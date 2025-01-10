package ru.otus.hw.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.records.Caterpillar;
import ru.otus.hw.services.ButterflyService;
import ru.otus.hw.services.CocoonService;
import org.springframework.messaging.Message;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> caterpillarChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> butterFlyChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100)
                .maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow butterFlyFlow(ButterflyService butterflyService, CocoonService cocoonService) {
        return IntegrationFlow
                .from(caterpillarChannel())
                .split()
                .<Caterpillar>log(LoggingHandler.Level.INFO, "Caterpillar", Message::getPayload)
                .handle(cocoonService, "fromCaterpillarToCocoon")
//                .channel("fromCocoonToButterfly")
                .handle(butterflyService, "fromCocoonToButterfly")
                .aggregate()
                .channel(butterFlyChannel())
                .get();
    }
}