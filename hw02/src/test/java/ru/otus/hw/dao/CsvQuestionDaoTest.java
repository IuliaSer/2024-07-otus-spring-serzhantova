package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.otus.hw.Constants.TEST_ANSWER_1;
import static ru.otus.hw.Constants.TEST_ANSWER_2;
import static ru.otus.hw.Constants.TEST_QUESTION;

public class CsvQuestionDaoTest {

    private TestFileNameProvider testFileNameProvider = mock(TestFileNameProvider.class);

    private QuestionDao questionDao = new CsvQuestionDao(testFileNameProvider);

    @Test
    public void findAllTest_Valid() {
        Answer answer1 = new Answer(TEST_ANSWER_1, true);
        Answer answer2 = new Answer(TEST_ANSWER_2, false);
        List<Answer> answers = List.of(answer1, answer2);
        List<Question> questions = List.of(new Question(TEST_QUESTION, answers));
        when(testFileNameProvider.getTestFileName()).thenReturn("test.csv");

        assertEquals(questions, questionDao.findAll());
    }

    @Test
    public void findAllTest_Invalid() {
        when(testFileNameProvider.getTestFileName()).thenReturn("wrongPath.csv");

        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }
}
