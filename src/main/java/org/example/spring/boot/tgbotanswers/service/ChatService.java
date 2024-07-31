package org.example.spring.boot.tgbotanswers.service;

import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
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

    void check(Update update) {
        List<PhotoSize> photoSizeList = update.getMessage().getPhoto();
        int count = 1;
        for (PhotoSize photoSize : photoSizeList) {
            GetFile getFile = new GetFile(photoSizeList.get(photoSizeList.size() - 1).getFileId());
            System.out.println(getFile.getFileId());
                        try {
                File file = answerBot.execute(getFile); //tg file obj
                answerBot.downloadFile(file, new java.io.File("C:\\gigaPhotos" + count + ".png"));
                count++;
            } catch (TelegramApiException e) {
//                log.error(e.toString());
                e.printStackTrace();
            }
        }
    }

}
