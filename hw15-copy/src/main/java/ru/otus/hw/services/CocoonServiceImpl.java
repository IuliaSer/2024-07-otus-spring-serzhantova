package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.records.Caterpillar;
import ru.otus.hw.records.Cocoon;

@Slf4j
@RequiredArgsConstructor
@Service
public class CocoonServiceImpl implements CocoonService {

    @Override
    public Cocoon fromCaterpillarToCocoon(Caterpillar caterpillar) {
        log.info("From caterpillar to cocoon {}", caterpillar.name());
        return new Cocoon(caterpillar.name());
    }
}
