package ru.otus.hw.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties
public class AppProperties {

    @Value("${chank-size}")
    private int chankSize;

    @Value("${page-size}")
    private int pageSize;
}
