package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.NoButterflyException;
import ru.otus.hw.records.Butterfly;
import ru.otus.hw.records.Caterpillar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaterpillarServiceImpl implements CaterpillarService {
    private static final String[] TYPES = {"Голубянка", "Махаон", "Капустница", "Монарх", "Адмирал", "Траурница",
            "Шашечница"};

    private final CaterpillarGateway caterpillarGateway;

    @Override
    public void startGenerateCaterpillars() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int num = i + 1;

            pool.execute(() -> {
                List<Caterpillar> caterpillars = generateCaterpillars();
                log.info("{}, Caterpillars are born: {}", num,
                        caterpillars.stream().map(Caterpillar::name).collect(Collectors.joining(",")));

                List<Butterfly> butterfly = caterpillarGateway.process(caterpillars);

                if (isEmpty(butterfly)) {
                    throw new NoButterflyException("Butterfly list is null or empty after processing caterpillars.");
                }
                log.info("{}, Caterpillars transformed to butterflies: {}", num, butterfly.stream()
                        .map(Butterfly::name).collect(Collectors.joining(",")));
            });
        }
    }

    private static List<Caterpillar> generateCaterpillars() {
        List<Caterpillar> caterpillars = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(1, 5); ++i) {
            caterpillars.add(new Caterpillar(TYPES[random.nextInt(0, TYPES.length)]));
        }
        return caterpillars;
    }
}
