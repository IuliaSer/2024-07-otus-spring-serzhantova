package service;

import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestServiceImplTest {
    private static final String TEST_ANSWER_1 = "answer1";

    private static final String TEST_ANSWER_2 = "answer2";

    private static final String TEST_QUESTION = "Question?";

    private final IOService ioService = mock(StreamsIOService.class);

    private final QuestionDao questionDao = mock(QuestionDao.class);

    private final TestService testService = new TestServiceImpl(ioService, questionDao);

    @Test
    public void executeTest_Test() {
        Answer answer1 = new Answer(TEST_ANSWER_1, true);
        Answer answer2 = new Answer(TEST_ANSWER_2, false);
        List<Answer> answers = List.of(answer1, answer2);
        List<Question> questions = List.of(new Question(TEST_QUESTION, answers));
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService, atLeast(5)).printLine(anyString());
        verify(ioService).printFormattedLine(anyString());
    }

}
