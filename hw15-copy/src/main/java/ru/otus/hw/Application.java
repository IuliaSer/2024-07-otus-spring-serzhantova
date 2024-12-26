package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import ru.otus.hw.services.CaterpillarService;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        CaterpillarService caterpillarService = context.getBean(CaterpillarService.class);
        caterpillarService.startGenerateCaterpillars();
    }
}