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
import ru.otus.hw.dto.postgres.CommentDto;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.migration.processors.CommentProcessor;
import ru.otus.hw.repositories.CommentRepository;

import javax.sql.DataSource;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class CommentMigration {
    private static final int CHUNK_SIZE = 5;

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
    public TaskletStep createSequenceForCommentIds() {
        return new StepBuilder("createSequenceForCommentIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_comments_ids");
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
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public CommentProcessor commentProcessor() {
        return new CommentProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CommentDto> commentJdbcBatchItemWriter() {
        JdbcBatchItemWriter<CommentDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                        INSERT INTO comments(id, message, book_id) 
                        VALUES (nextval('seq_comments_ids'), :message, :bookId)""");
        return writer;
    }

    @Bean
    public Step migrateCommentStep(RepositoryItemReader<Comment> reader, CommentProcessor commentProcessor,
                                  JdbcBatchItemWriter<CommentDto> writer) {
        return new StepBuilder("migrateCommentStep", jobRepository)
                .<Comment, CommentDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
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
