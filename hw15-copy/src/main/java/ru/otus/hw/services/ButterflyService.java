package ru.otus.hw.services;

import ru.otus.hw.records.Butterfly;
import ru.otus.hw.records.Cocoon;

public interface ButterflyService {
    Butterfly fromCocoonToButterfly(Cocoon cocoon);
}
