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
import ru.otus.hw.dto.ShortCommentDto;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.migration.processors.CommentProcessor;
import ru.otus.hw.properties.AppProperties;
import ru.otus.hw.repositories.CommentRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMigration {
    private final AppProperties appProperties;

    private final CommentRepository mongoCommentRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final DataSource dataSource;

    @Bean
    public TaskletStep createTableComments() {
        return new StepBuilder("createTableComments", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS comments(
                                    id BIGINT PRIMARY KEY,
                                    message VARCHAR(255) NOT NULL,
                                    book_id  BIGINT,
                                    FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE)"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createTemporaryTableCommentIds() {
        return new StepBuilder("createTemporaryCommentIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS temp_table_comment_ids_mongo_to_postgres(
                                    id_mongo VARCHAR(255) NOT NULL UNIQUE, 
                                    id_postgres BIGINT NOT NULL UNIQUE)"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createSequenceForCommentIds() {
        return new StepBuilder("createSequenceForCommentIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_comment_ids");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<Comment> commentReader() {
        return new RepositoryItemReaderBuilder<Comment>()
                .name("commentReader")
                .repository(mongoCommentRepository)
                .methodName("findAll")
                .pageSize(appProperties.getPageSize())
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public CommentProcessor commentProcessor() {
        return new CommentProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<ShortCommentDto> commentInsertTempTable() {
        JdbcBatchItemWriter<ShortCommentDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO temp_table_comment_ids_mongo_to_postgres(id_mongo, id_postgres) 
                VALUES (:id, nextval('seq_comment_ids'))""");
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<ShortCommentDto> commentJdbcBatchItemWriter() {
        JdbcBatchItemWriter<ShortCommentDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO comments(id, message, book_id) 
                VALUES(
                (SELECT id_postgres FROM temp_table_comment_ids_mongo_to_postgres WHERE id_mongo = :id), 
                :message,
                (SELECT id_postgres FROM temp_table_book_ids_mongo_to_postgres WHERE id_mongo = :bookId))""");
        return writer;
    }

    @Bean
    public CompositeItemWriter<ShortCommentDto> compositeCommentWriter(
            JdbcBatchItemWriter<ShortCommentDto> commentInsertTempTable,
            JdbcBatchItemWriter<ShortCommentDto> commentJdbcBatchItemWriter) {

        CompositeItemWriter<ShortCommentDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(commentInsertTempTable, commentJdbcBatchItemWriter));
        return writer;
    }

    @Bean
    public Step migrateCommentStep(RepositoryItemReader<Comment> reader, CommentProcessor commentProcessor,
                                  CompositeItemWriter<ShortCommentDto> writer) {
        return new StepBuilder("migrateCommentStep", jobRepository)
                .<Comment, ShortCommentDto>chunk(appProperties.getChankSize(), platformTransactionManager)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropTemporaryComment() {
        return new StepBuilder("dropTemporaryComment", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_comment_ids_mongo_to_postgres");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep dropSequenceCommentIds() {
        return new StepBuilder("dropSequenceCommentIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_comments_ids");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }
}
