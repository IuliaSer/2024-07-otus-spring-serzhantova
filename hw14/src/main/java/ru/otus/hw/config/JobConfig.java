package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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
        return new JobBuilder("migrateDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(authorMigration.createTableAuthors())
                .next(authorMigration.createSequenceForAuthorIds())
                .next(migrateAuthorStep)
                .next(authorMigration.dropSequenceAuthorIds())
                .next(genreMigration.createTableGenres())
                .next(genreMigration.createSequenceForGenreIds())
                .next(migrateGenreStep)
                .next(genreMigration.dropSequenceGenreIds())
                .next(bookMigration.createTableBooks())
                .next(bookMigration.createSequenceForBookIds())
                .next(migrateBookStep)
                .next(bookMigration.dropSequenceBookIds())
                .next(commentMigration.createTableComments())
                .next(commentMigration.createSequenceForCommentIds())
                .next(migrateCommentStep)
                .next(commentMigration.dropSequenceCommentIds())
                .build();
    }

}
