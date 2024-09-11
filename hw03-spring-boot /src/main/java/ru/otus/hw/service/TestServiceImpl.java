package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService localizedIOService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("TestService.answer.the.questions");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            askQuestion(question);
            int amountOfAnswers = question.answers().size();
            int studentAnswer = localizedIOService.readIntForRangeWithPromptLocalized(1, amountOfAnswers,
                    "TestService.answer.the.prompt", "TestService.answer.error.message");
            Answer answer = getCorrespondingAnswer(question, studentAnswer);
            testResult.applyAnswer(question, answer.isCorrect());
        }
        return testResult;
    }

    private void askQuestion(Question question) {
        localizedIOService.printFormattedLine("%s%n", question.text());

        int i = 1;
        for (Answer answer: question.answers()) {
            localizedIOService.printFormattedLine("%d. %s", i++, answer.text());
        }
    }

    private Answer getCorrespondingAnswer(Question question, int studentAnswer) {
        return question.answers().get(studentAnswer - 1);
    }

}
