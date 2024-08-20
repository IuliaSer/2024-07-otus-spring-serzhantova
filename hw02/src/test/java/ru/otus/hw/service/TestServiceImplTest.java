package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.exceptions.WrongInputException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.otus.hw.Constants.TEST_ANSWER_1;
import static ru.otus.hw.Constants.TEST_ANSWER_2;
import static ru.otus.hw.Constants.TEST_QUESTION;

public class TestServiceImplTest {

    private final IOService ioService = mock(StreamsIOService.class);

    private final QuestionDao questionDao = mock(QuestionDao.class);

    private final Student student = mock(Student.class);

    private final TestService testService = new TestServiceImpl(ioService, questionDao);

    private List<Question> questions;

    @BeforeEach
    public void setup() {
        Answer answer1 = new Answer(TEST_ANSWER_1, true);
        Answer answer2 = new Answer(TEST_ANSWER_2, false);
        List<Answer> answers = List.of(answer1, answer2);
        questions = List.of(new Question(TEST_QUESTION, answers));
        when(questionDao.findAll()).thenReturn(questions);
    }

    @Test
    public void executeTestFor_Test_Valid() {
        when(ioService.readString()).thenReturn(TEST_ANSWER_1);

        assertEquals(questions, testService.executeTestFor(student).getAnsweredQuestions());
        assertEquals(1, testService.executeTestFor(student).getRightAnswersCount());
        verify(ioService, atLeast(3)).printLine(anyString());
        verify(ioService, atLeast(1)).printFormattedLine(anyString());
    }

    @Test
    public void executeTestFor_Test_InvalidInput() {
        when(ioService.readString()).thenReturn("wrong input");

        assertThrows(WrongInputException.class, () -> testService.executeTestFor(student));
        verify(ioService, atLeast(3)).printLine(anyString());
        verify(ioService, atLeast(1)).printFormattedLine(anyString());
    }
}
