package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.Image;
import org.example.spring.boot.tgbotanswers.model.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class MediaService {

    private ImageRepository imageRepository;
    private ChatRepository chatRepository;

    @Autowired
    public MediaService(ImageRepository imageRepository, ChatRepository chatRepository) {
        this.imageRepository = imageRepository;
        this.chatRepository = chatRepository;
    }

    <T> T sendingMedia(long chatId, String messageText) throws TelegramApiException {
        List<Image> listImages = imageRepository.getImagesByChat(chatRepository.findByChatId(chatId));
        List<Image> key = listImages.stream()
                .filter(x -> messageText.contains(x.getKeyToImg()))
                .toList();
        if (!key.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(key.size());
            if (key.get(index).getPathToImg().contains(".png")) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(new java.io.File(key.get(index).getPathToImg())));
                return (T) sendPhoto;
            } else {
                SendAnimation sendAnimation = new SendAnimation();
                sendAnimation.setChatId(chatId);
                sendAnimation.setAnimation(new InputFile(new java.io.File(key.get(index).getPathToImg())));
                return (T) sendAnimation;
            }
        }
        return null;
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
