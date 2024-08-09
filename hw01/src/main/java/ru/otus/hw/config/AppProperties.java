package ru.otus.hw.config;

import lombok.Data;

@Data
public class AppProperties implements TestFileNameProvider {
    private String testFileName;
}
