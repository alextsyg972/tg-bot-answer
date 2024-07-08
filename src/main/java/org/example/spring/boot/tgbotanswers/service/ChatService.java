package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class ChatService {
    private ChatRepository chatRepository;

    String startCommandReceived(Long chatId) {
        String answer = "Я пишу пасты и отвечаю на ваши сообщения в случайное время";
        if (chatRepository.findByChatId(chatId) == null) {
            Chat chat = new Chat();
            chat.setChatId(chatId);
            chatRepository.save(chat);
            log.info("create new Entity: Chat() with chatId = {}", chatId);
        }
        return answer;
    }

    void check(Update update) {
        if (update.getMessage().hasPhoto()) {
            List<PhotoSize> photoSizeList = update.getMessage().getPhoto();
            int count = 1;

                GetFile getFile = new GetFile(photoSizeList.get(photoSizeList.size()-1).getFileId());
                try {
                    File file = answerBot.execute(getFile);
                    answerBot.downloadFile(file, new java.io.File("photos/photo" + count + ".p"));
                    count++;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

        }
    }

    @Autowired
    private void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
}
