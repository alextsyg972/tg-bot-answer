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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
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
                .filter(x -> messageText.equals(x.getKeyToImg()))
                .toList();
        if (!key.isEmpty()) {
            int index = new Random().nextInt(key.size());
            if (key.get(index).getPathToImg().contains(".png")) {
                return (T) SendPhoto
                        .builder()
                        .chatId(chatId)
                        .photo(new InputFile(new java.io.File(key.get(index).getPathToImg())))
                        .build();
            } else {
                return (T) SendAnimation
                        .builder()
                        .chatId(chatId)
                        .animation(new InputFile(new java.io.File(key.get(index).getPathToImg())))
                        .build();
            }
        }
        return null;
    }

    <T> T sendingMedia(long chatId, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup, int index) throws TelegramApiException {
        List<Image> listImages = imageRepository.getImagesByChat(chatRepository.findByChatId(chatId));
        List<Image> key = listImages.stream()
                .filter(x -> messageText.equals(x.getKeyToImg()))
                .toList();
        if (!key.isEmpty()) {
            if (key.get(index).getPathToImg().contains(".png")) {
                return (T) SendPhoto
                        .builder()
                        .chatId(chatId)
                        .photo(new InputFile(new java.io.File(key.get(index).getPathToImg())))
                        .replyMarkup(inlineKeyboardMarkup)
                        .build();
            } else {
                return (T) SendAnimation
                        .builder()
                        .chatId(chatId)
                        .animation(new InputFile(new java.io.File(key.get(index).getPathToImg())))
                        .replyMarkup(inlineKeyboardMarkup)
                        .build();
            }
        }
        return null;
    }

    InlineKeyboardMarkup showImgCallback(long chatId,int index, String key) {
        List<Image> imageList = imageRepository.getImagesByChatAndKeyToImg(chatRepository.findByChatId(chatId), key);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        if (index <= 0) {

            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            InlineKeyboardButton deleteButton = new InlineKeyboardButton();

            deleteButton.setText("Удалить");
            deleteButton.setCallbackData(deleteButton.getText());

            nextButton.setText("\u27A1");
            nextButton.setCallbackData(nextButton.getText() + "show");

            keyboardButtonsRow.add(deleteButton);
            keyboardButtonsRow.add(nextButton);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);

            return inlineKeyboardMarkup;
        } else if (imageList.size() - 1 == index) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            InlineKeyboardButton deleteButton = new InlineKeyboardButton();

            deleteButton.setText("Удалить");
            deleteButton.setCallbackData(deleteButton.getText());

            prevButton.setText("\u2B05");
            prevButton.setCallbackData(prevButton.getText() + "show");

            keyboardButtonsRow.add(prevButton);
            keyboardButtonsRow.add(deleteButton);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);

            return inlineKeyboardMarkup;
        }
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();

        prevButton.setText("\u2B05");
        prevButton.setCallbackData(prevButton.getText() + "show");
        deleteButton.setText("Удалить");
        deleteButton.setCallbackData(deleteButton.getText());

        nextButton.setText("\u27A1");
        nextButton.setCallbackData(nextButton.getText() + "show");

        keyboardButtonsRow.add(prevButton);
        keyboardButtonsRow.add(deleteButton);
        keyboardButtonsRow.add(nextButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    InlineKeyboardMarkup pagingImgKeyWords(long chatId, int start, int end) {
        List<Image> imageList = imageRepository.getImagesByChat(chatRepository.findByChatId(chatId));
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        if (imageList.size() >= start && start >= 0) {
            for (int i = start; i < imageList.size(); i++) {
                if (i >= end) break;
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(imageList.get(i).getKeyToImg());
                inlineKeyboardButton.setCallbackData(inlineKeyboardButton.getText());
                keyboardButtonsRow.add(inlineKeyboardButton);
            }
        }
        List<InlineKeyboardButton> pagButtons = new ArrayList<>();
        if (start < 0) {
            InlineKeyboardButton nextButton = new InlineKeyboardButton();
            nextButton.setText("\u27A1");
            nextButton.setCallbackData(nextButton.getText());
            pagButtons.add(nextButton);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);
            rowList.add(pagButtons);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);
            return inlineKeyboardMarkup;
        } else if (end > imageList.size()) {
            InlineKeyboardButton prevButton = new InlineKeyboardButton();
            prevButton.setText("\u2B05");
            prevButton.setCallbackData(prevButton.getText());
            pagButtons.add(prevButton);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);
            rowList.add(pagButtons);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);
            return inlineKeyboardMarkup;
        }
        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("\u2B05");
        prevButton.setCallbackData(prevButton.getText());

        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("\u27A1");
        nextButton.setCallbackData(nextButton.getText());

        pagButtons.add(prevButton);
        pagButtons.add(nextButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        rowList.add(pagButtons);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
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
