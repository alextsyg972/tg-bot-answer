package org.example.spring.boot.tgbotanswers.service;

import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

//@Slf4j
@Service
public class ChatService {
    private AnswerBot answerBot;
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    public ChatService(AnswerBot answerBot, ChatRepository chatRepository) {
        this.answerBot = answerBot;
        this.chatRepository = chatRepository;
    }

    String startCommandReceived(Long chatId) {
        String answer = "Я пишу пасты и отвечаю на ваши сообщения в случайное время";
        if (chatRepository.findByChatId(chatId) == null) {
            Chat chat = new Chat();
            chat.setChatId(chatId);
            chatRepository.save(chat);
//            log.info("create new Entity: Chat() with chatId = {}", chatId);
        }
        return answer;
    }

    String registerUser(long chatId) {
        Chat chat = new Chat();
        chat.setChatId(chatId);
        chatRepository.save(chat);
        return "Chat registered";
//        log.info("user saved{}", user);
    }
    String addImgToChat(long chatId, String keyword, File file) {
        Image image = new Image("da", "C:\\gigaPhotos\\photos\\" + file.getFileId() + ".png");
        Chat chat = chatRepository.findByChatId(chatId);
        chat.addImageToChat(image);
        chatRepository.save(chat);
        return "Success add";
    }

}
