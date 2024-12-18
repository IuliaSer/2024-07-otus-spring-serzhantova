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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.migration.processors.AuthorProcessor;
import ru.otus.hw.repositories.AuthorRepository;

import javax.sql.DataSource;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AuthorMigration {
    private static final int CHUNK_SIZE = 5;

    private final AuthorRepository mongoAuthorRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final DataSource dataSource;

    @Bean
    public TaskletStep createTableAuthors() {
        return new StepBuilder("createTableAuthors", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS authors(
                                    id BIGINT PRIMARY KEY,
                                    full_name VARCHAR(255) NOT NULL)"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createSequenceForAuthorIds() {
        return new StepBuilder("createSequenceForAuthorIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_authors_ids");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<Author> authorReader() {
        return new RepositoryItemReaderBuilder<Author>()
                .name("authorReader")
                .repository(mongoAuthorRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public AuthorProcessor authorProcessor() {
        return new AuthorProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorJdbcBatchItemWriter() {
        JdbcBatchItemWriter<AuthorDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO authors(id, full_name) VALUES (nextval('seq_authors_ids'), :fullName)");
        return writer;
    }

    @Bean
    public Step migrateAuthorStep(RepositoryItemReader<Author> reader, AuthorProcessor authorProcessor,
                                  JdbcBatchItemWriter<AuthorDto> writer) {
        return new StepBuilder("migrateAuthorStep", jobRepository)
                .<Author, AuthorDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(authorProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropSequenceAuthorIds() {
        return new StepBuilder("dropSequenceAuthorIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_authors_ids");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }
}
