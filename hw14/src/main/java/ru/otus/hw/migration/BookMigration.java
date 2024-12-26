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
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.migration.processors.BookProcessor;
import ru.otus.hw.properties.AppProperties;
import ru.otus.hw.repositories.BookRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMigration {
    private final AppProperties appProperties;

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
    public TaskletStep createTemporaryTableBookIds() {
        return new StepBuilder("createTemporaryBookIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS temp_table_book_ids_mongo_to_postgres(
                                    id_mongo VARCHAR(255) NOT NULL UNIQUE, 
                                    id_postgres BIGINT NOT NULL UNIQUE)"""
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
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_book_ids");
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
                .pageSize(appProperties.getPageSize())
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public BookProcessor bookProcessor() {
        return new BookProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<ShortBookDto> bookInsertTempTable() {
        JdbcBatchItemWriter<ShortBookDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO temp_table_book_ids_mongo_to_postgres(id_mongo, id_postgres)
                VALUES (:id, nextval('seq_book_ids'))""");
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<ShortBookDto> bookJdbcBatchItemWriter() {
        JdbcBatchItemWriter<ShortBookDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                        INSERT INTO books(id, title, author_id, genre_id) 
                        VALUES (
                        (SELECT id_postgres FROM temp_table_book_ids_mongo_to_postgres WHERE id_mongo = :id),  
                        :title, 
                        (SELECT id_postgres FROM temp_table_author_ids_mongo_to_postgres WHERE id_mongo = :authorId), 
                        (SELECT id_postgres FROM temp_table_genre_ids_mongo_to_postgres WHERE id_mongo = :genreId))""");
        return writer;
    }

    @Bean
    public CompositeItemWriter<ShortBookDto> compositeBookWriter(
            JdbcBatchItemWriter<ShortBookDto> bookInsertTempTable,
            JdbcBatchItemWriter<ShortBookDto> bookJdbcBatchItemWriter) {

        CompositeItemWriter<ShortBookDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(bookInsertTempTable, bookJdbcBatchItemWriter));
        return writer;
    }

    @Bean
    public Step migrateBookStep(RepositoryItemReader<Book> reader, BookProcessor bookProcessor,
                                 CompositeItemWriter<ShortBookDto> writer) {
        return new StepBuilder("migrateBookStep", jobRepository)
                .<Book, ShortBookDto>chunk(appProperties.getChankSize(), platformTransactionManager)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropTemporaryBook() {
        return new StepBuilder("dropTemporaryBook", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_book_ids_mongo_to_postgres");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
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
