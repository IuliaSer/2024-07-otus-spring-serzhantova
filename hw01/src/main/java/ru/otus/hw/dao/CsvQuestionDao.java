package ru.otus.hw.dao;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final int AMOUNT_OF_LINES_TO_SKIP = 1;

    private static final char COLUMN_SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (CSVReader csvReader = initializeCSVReader()) {
            CsvToBean<QuestionDto> csv = new CsvToBeanBuilder<QuestionDto>(csvReader)
                    .withType(QuestionDto.class)
                    .build();
            List<QuestionDto> questionsDtos = csv.parse();
            return questionsDtos
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception e) {
            throw new QuestionReadException(String.format("Could,t parse csv file. Error: %s", e.getMessage()), e);
        }
    }

    private CSVReader initializeCSVReader() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName());
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return new CSVReaderBuilder(reader)
                    .withSkipLines(AMOUNT_OF_LINES_TO_SKIP)
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator(COLUMN_SEPARATOR)
                            .build())
                    .build();
        } else {
            throw new QuestionReadException("Couldn't load test csv file as inputStream");
        }
    }

}