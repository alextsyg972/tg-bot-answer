package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class ChatService {
    private ChatRepository chatRepository;
    private AnswerBot answerBot;


    @Autowired
    @Lazy
    public ChatService(ChatRepository chatRepository, AnswerBot answerBot) {
        this.chatRepository = chatRepository;
        this.answerBot = answerBot;
    }

    String startCommandReceived(Long chatId) {
        String answer = "Отправляю смешные картинки в зависимости от ваших сообщений\n" +
                "\n" +
                "/register@kowern_bot - зарегистрировать чат \n" +
                "/add@kowern_bot - добавление изображения для ответов бота";
        log.info("start command received{}", chatId);
        return answer;
    }

    String registerUser(long chatId) {
        if (chatRepository.findByChatId(chatId) != null) {
            log.info("chat already registered" + chatId);
            return "Чат уже зарегистрирован";
        }
        Chat chat = new Chat();
        chat.setChatId(chatId);
        chatRepository.save(chat);
        log.info("created new Entity, chatId={}", chatId);
        return "Зарегистрировал";
    }

    void sendMessage(long chatId, String textToSend) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(textToSend)
                .build();
        try {
            answerBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    void sendMessage(long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text("Выберите необходимое слово")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            answerBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }


}
