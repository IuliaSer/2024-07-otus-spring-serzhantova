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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.migration.processors.AuthorProcessor;
import ru.otus.hw.properties.AppProperties;
import ru.otus.hw.repositories.AuthorRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorMigration {
    private final AppProperties appProperties;

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
    public TaskletStep createTemporaryTableAuthorIds() {
        return new StepBuilder("createTemporaryAuthorIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS temp_table_author_ids_mongo_to_postgres(
                                    id_mongo VARCHAR(255) NOT NULL UNIQUE, 
                                    id_postgres BIGINT NOT NULL UNIQUE)"""
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
                .pageSize(appProperties.getPageSize())
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public AuthorProcessor authorProcessor() {
        return new AuthorProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorInsertTempTable() {
        JdbcBatchItemWriter<AuthorDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO temp_table_author_ids_mongo_to_postgres(id_mongo, id_postgres)
                VALUES (:id, nextval('seq_authors_ids'))""");
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorJdbcBatchItemWriter() {
        JdbcBatchItemWriter<AuthorDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO authors(id, full_name) VALUES
                ((SELECT id_postgres FROM temp_table_author_ids_mongo_to_postgres WHERE id_mongo = :id), :fullName)""");
        return writer;
    }

    @Bean
    public CompositeItemWriter<AuthorDto> compositeAuthorWriter(JdbcBatchItemWriter<AuthorDto> authorInsertTempTable,
            JdbcBatchItemWriter<AuthorDto> authorJdbcBatchItemWriter) {

        CompositeItemWriter<AuthorDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(authorInsertTempTable, authorJdbcBatchItemWriter));
        return writer;
    }
    
    @Bean
    public Step migrateAuthorStep(RepositoryItemReader<Author> reader, AuthorProcessor authorProcessor,
                                  CompositeItemWriter<AuthorDto> writer) {
        return new StepBuilder("migrateAuthorStep", jobRepository)
                .<Author, AuthorDto>chunk(appProperties.getChankSize(), platformTransactionManager)
                .reader(reader)
                .processor(authorProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropTemporaryAuthor() {
        return new StepBuilder("dropTemporaryAuthor", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_author_ids_mongo_to_postgres");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
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
