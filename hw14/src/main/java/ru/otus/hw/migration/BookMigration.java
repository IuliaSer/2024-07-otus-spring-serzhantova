package ru.otus.hw.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.postgres.ShortBookDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.migration.processors.BookProcessor;
import ru.otus.hw.repositories.BookRepository;

import javax.sql.DataSource;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class BookMigration {
    private static final int CHUNK_SIZE = 5;

    private final BookRepository mongoBookRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final DataSource dataSource;

    @Bean
    public TaskletStep createTableBooks() {
        return new StepBuilder("createTableBooks", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS books (
                                       id        BIGINT PRIMARY KEY,
                                       title     VARCHAR(255),
                                       author_id BIGINT,
                                       genre_id  BIGINT,
                                       FOREIGN KEY (author_id) REFERENCES authors (id) ON DELETE CASCADE,
                                       FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
                                   );"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createSequenceForBookIds() {
        return new StepBuilder("createSequenceForBookIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_books_ids");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<Book> bookReader() {
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(mongoBookRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public BookProcessor bookProcessor() {
        return new BookProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<ShortBookDto> bookJdbcBatchItemWriter() {
        JdbcBatchItemWriter<ShortBookDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO books(id, title, author_id, genre_id) 
                VALUES (nextval('seq_books_ids'), :title, :authorId, :genreId)""");
        return writer;
    }

    @Bean
    public Step migrateBookStep(RepositoryItemReader<Book> reader, BookProcessor bookProcessor,
                                 JdbcBatchItemWriter<ShortBookDto> writer) {
        return new StepBuilder("migrateBookStep", jobRepository)
                .<Book, ShortBookDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropSequenceBookIds() {
        return new StepBuilder("dropSequenceBookIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_books_ids");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }
}
