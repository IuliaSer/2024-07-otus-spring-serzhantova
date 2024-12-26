package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.migration.AuthorMigration;
import ru.otus.hw.migration.BookMigration;
import ru.otus.hw.migration.CommentMigration;
import ru.otus.hw.migration.GenreMigration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final JobRepository jobRepository;

    private final AuthorMigration authorMigration;

    private final GenreMigration genreMigration;

    private final BookMigration bookMigration;

    private final CommentMigration commentMigration;

    @Bean
    public Job migrateDbJob(Step migrateAuthorStep, Step migrateGenreStep, Step migrateBookStep,
                            Step migrateCommentStep) {
        SimpleJobBuilder jobBuilder = new JobBuilder("migrateDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(authorMigration.createTableAuthors())
                .next(authorMigration.createSequenceForAuthorIds())
                .next(authorMigration.createTemporaryTableAuthorIds())
                .next(migrateAuthorStep)
                .next(genreMigration.createTableGenres())
                .next(genreMigration.createTemporaryTableGenreIds())
                .next(genreMigration.createSequenceForGenreIds())
                .next(migrateGenreStep)
                .next(bookMigration.createTableBooks())
                .next(bookMigration.createTemporaryTableBookIds())
                .next(bookMigration.createSequenceForBookIds())
                .next(migrateBookStep)
                .next(commentMigration.createTableComments())
                .next(commentMigration.createTemporaryTableCommentIds())
                .next(commentMigration.createSequenceForCommentIds())
                .next(migrateCommentStep);
        return cleanDb(jobBuilder);
    }

    public Job cleanDb(SimpleJobBuilder jobBuilder) {
        return jobBuilder
                .next(authorMigration.dropSequenceAuthorIds())
                .next(authorMigration.dropTemporaryAuthor())
                .next(genreMigration.dropSequenceGenreIds())
                .next(genreMigration.dropTemporaryGenre())
                .next(bookMigration.dropSequenceBookIds())
                .next(bookMigration.dropTemporaryBook())
                .next(commentMigration.dropSequenceCommentIds())
                .next(commentMigration.dropTemporaryComment())
                .build();
    }
}
