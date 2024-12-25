package ru.otus.hw.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.records.Butterfly;
import ru.otus.hw.records.Cocoon;

@Slf4j
@Service
public class ButterflyServiceImpl implements ButterflyService {

    @Override
    public Butterfly fromCocoonToButterfly(Cocoon cocoon) {
        log.info("From cocoon to butterfly {}", cocoon.name());
        return new Butterfly(cocoon.name());
    }
}
