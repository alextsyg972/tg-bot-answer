package org.example.spring.boot.tgbotanswers.config;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.service.AnswerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Arrays;

@Component
@Slf4j
public class BotInitializer {

    private final AnswerBot bot;

    @Autowired
    public BotInitializer(AnswerBot bot) {
        this.bot = bot;
    }

    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Error occured: {}", e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
