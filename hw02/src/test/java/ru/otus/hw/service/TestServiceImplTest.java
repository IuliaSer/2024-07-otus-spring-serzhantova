package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.otus.hw.Constants.TEST_ANSWER_1;
import static ru.otus.hw.Constants.TEST_ANSWER_2;
import static ru.otus.hw.Constants.TEST_QUESTION;

public class TestServiceImplTest {

    private IOService ioService;

    private QuestionDao questionDao;

    private TestService testService;

    private Student student;

    private List<Question> questions;

    @BeforeEach
    public void setup() {
        ioService = mock(StreamsIOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
        student = new Student("Ivan", "Ivanov");
        Answer answer1 = new Answer(TEST_ANSWER_1, true);
        Answer answer2 = new Answer(TEST_ANSWER_2, false);
        List<Answer> answers = List.of(answer1, answer2);
        questions = List.of(new Question(TEST_QUESTION, answers));
        when(questionDao.findAll()).thenReturn(questions);
    }

    @Test
    public void executeTestFor_Test_oneCorrectAnswer() {
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).thenReturn(1);

        assertEquals(questions, testService.executeTestFor(student).getAnsweredQuestions());
        assertEquals(1, testService.executeTestFor(student).getRightAnswersCount());
        verify(ioService, atLeast(2)).printLine(anyString());
        verify(ioService, atLeast(1)).printFormattedLine(anyString());
    }

    @Test
    public void executeTestFor_Test_zeroCorrectAnswer() {
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).thenReturn(2);

        assertEquals(0, testService.executeTestFor(student).getRightAnswersCount());
        verify(ioService, atLeast(1)).printLine(anyString());
        verify(ioService, atLeast(1)).printFormattedLine(anyString());
    }
}