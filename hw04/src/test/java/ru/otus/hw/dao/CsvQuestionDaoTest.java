package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ru.otus.hw.Constants.TEST_ANSWER_1;
import static ru.otus.hw.Constants.TEST_ANSWER_2;
import static ru.otus.hw.Constants.TEST_QUESTION;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private QuestionDao questionDao;

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