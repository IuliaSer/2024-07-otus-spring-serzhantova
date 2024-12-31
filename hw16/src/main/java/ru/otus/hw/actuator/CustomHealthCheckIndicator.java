package ru.otus.hw.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthCheckIndicator implements HealthIndicator {

    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long freeMemoryInMB = convertMemoryFromBytesToMb(freeMemory);

        if (freeMemoryInMB < 10) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Заканчивается память!")
                    .build();
        } else {
            return Health.up().withDetail("message", "Все отлично!").build();
        }
    }

    private long convertMemoryFromBytesToMb(long memory) {
        return memory / (1024 * 1024);
    }
}
