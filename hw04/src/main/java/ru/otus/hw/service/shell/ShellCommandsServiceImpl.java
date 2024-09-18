package ru.otus.hw.service.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Commands for test app")
@RequiredArgsConstructor
public class ShellCommandsServiceImpl implements ShellCommandsService {

    private final TestRunnerService testRunnerService;

    @ShellMethod(key = {"test", "start_test", "run_test"})
    @Override
    public void startTest() {
        testRunnerService.run();
    }
}
