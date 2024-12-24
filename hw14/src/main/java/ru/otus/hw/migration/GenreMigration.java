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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.migration.processors.GenreProcessor;
import ru.otus.hw.properties.AppProperties;
import ru.otus.hw.repositories.GenreRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreMigration {
    private final AppProperties appProperties;

    private final GenreRepository mongoGenreRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final DataSource dataSource;

    @Bean
    public TaskletStep createTableGenres() {
        return new StepBuilder("createTableGenres", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS genres(
                                    id BIGINT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL)"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createTemporaryTableGenreIds() {
        return new StepBuilder("createTemporaryGenreIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            """
                                    CREATE TABLE IF NOT EXISTS temp_table_genre_ids_mongo_to_postgres(
                                    id_mongo VARCHAR(255) NOT NULL UNIQUE, 
                                    id_postgres BIGINT NOT NULL UNIQUE)"""
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep createSequenceForGenreIds() {
        return new StepBuilder("createSequenceForGenreIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_genre_ids");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<Genre> genreReader() {
        return new RepositoryItemReaderBuilder<Genre>()
                .name("genreReader")
                .repository(mongoGenreRepository)
                .methodName("findAll")
                .pageSize(appProperties.getPageSize())
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public GenreProcessor genreProcessor() {
        return new GenreProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<GenreDto> genreInsertTempTable() {
        JdbcBatchItemWriter<GenreDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO temp_table_genre_ids_mongo_to_postgres(id_mongo, id_postgres)
                VALUES (:id, nextval('seq_genre_ids'))""");
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<GenreDto> genreJdbcBatchItemWriter() {
        JdbcBatchItemWriter<GenreDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("""
                INSERT INTO genres(id, name) VALUES
                ((SELECT id_postgres FROM temp_table_genre_ids_mongo_to_postgres WHERE id_mongo = :id), :name)""");
        return writer;
    }

    @Bean
    public CompositeItemWriter<GenreDto> compositeGenreWriter(JdbcBatchItemWriter<GenreDto> genreInsertTempTable,
                                                                JdbcBatchItemWriter<GenreDto> genreJdbcBatchItemWriter) {

        CompositeItemWriter<GenreDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(genreInsertTempTable, genreJdbcBatchItemWriter));
        return writer;
    }

    @Bean
    public Step migrateGenreStep(RepositoryItemReader<Genre> reader, GenreProcessor genreProcessor,
                                  CompositeItemWriter<GenreDto> writer) {
        return new StepBuilder("migrateGenreStep", jobRepository)
                .<Genre, GenreDto>chunk(appProperties.getChankSize(), platformTransactionManager)
                .reader(reader)
                .processor(genreProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskletStep dropTemporaryGenre() {
        return new StepBuilder("dropTemporaryGenre", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_genre_ids_mongo_to_postgres");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    @Bean
    public TaskletStep dropSequenceGenreIds() {
        return new StepBuilder("dropSequenceGenreIds", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_genres_ids");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }
}
