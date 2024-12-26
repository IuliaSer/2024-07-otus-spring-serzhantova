package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.records.Butterfly;
import ru.otus.hw.records.Caterpillar;

import java.util.List;

@MessagingGateway
public interface CaterpillarGateway {

    @Gateway(requestChannel = "caterpillarChannel", replyChannel = "butterFlyChannel")
    List<Butterfly> process(List<Caterpillar> caterpillars);
}
