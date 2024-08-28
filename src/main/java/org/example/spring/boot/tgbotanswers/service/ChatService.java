package org.example.spring.boot.tgbotanswers.service;

import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.Image;
import org.example.spring.boot.tgbotanswers.model.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.File;

import java.util.List;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ImageRepository imageRepository;
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    public ChatService(ChatRepository chatRepository, ImageRepository imageRepository) {
        this.chatRepository = chatRepository;
        this.imageRepository = imageRepository;
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
    void addImgToChat(long chatId, String keyword, File file) {
        Image image = new Image(keyword, "/root/testAppJar/photos/" + file.getFileId() + ".png");
        Chat chat = chatRepository.findByChatId(chatId);
        chat.addImageToChat(image);
        chatRepository.save(chat);
        log.info("added new img to chat{}", chatId);
    }
    void addGifToChat(long chatId, String keyword, File file) {
        Image image = new Image(keyword, "/root/testAppJar/photos/" + file.getFileId() + ".gif");
        Chat chat = chatRepository.findByChatId(chatId);
        chat.addImageToChat(image);
        chatRepository.save(chat);
        log.info("added new gif to chat{}", chatId);
    }
    void cringe(Long chatId, String keyword) {
        Chat chat = chatRepository.findByChatId(chatId);
        List<Image> image = imageRepository.getImagesByChatAndKeyToImg(chat, chatId.toString());
        image.get(0).setKeyToImg(keyword);
        imageRepository.save(image.get(0));
    }

}
