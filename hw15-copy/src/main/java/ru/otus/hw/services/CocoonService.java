package ru.otus.hw.services;

import ru.otus.hw.records.Caterpillar;
import ru.otus.hw.records.Cocoon;

public interface CocoonService {
    Cocoon fromCaterpillarToCocoon(Caterpillar caterpillar);
}
