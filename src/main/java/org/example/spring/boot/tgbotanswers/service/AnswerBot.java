package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.config.BotConfig;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AnswerBot extends TelegramLongPollingBot {
    ChatRepository chatRepository;
    ChatService chatService;
    final BotConfig botConfig;

    public AnswerBot(@Value("${bot.key}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "welcome"));
        botCommandList.add(new BotCommand("/addImg", "welcome"));
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "start/@" -> {
                    sendMessage(chatId,chatService.startCommandReceived(chatId));
                }
                case "addImg/@" {
                    if (update.getMessage().hasPhoto()) {
                        GetFile getFile = new GetFile()
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {

            log.error("error occurred:{}", e.getMessage());
        }
    }

    @Autowired
    private void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Autowired
    private void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
}
