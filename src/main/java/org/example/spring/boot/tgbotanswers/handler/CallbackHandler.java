package org.example.spring.boot.tgbotanswers.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.service.ChatService;
import org.example.spring.boot.tgbotanswers.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class CallbackHandler {

    private MediaService mediaService;
    private ChatService chatService;
    private int start = 0;
    private int end = 3;
    private String keyword;
    private int index;

    @Autowired
    public CallbackHandler(MediaService mediaService, ChatService chatService) {
        this.mediaService = mediaService;
        this.chatService = chatService;
    }

    public void deleteCommandInit() {
        start = 0;
        end = 3;
        index = 0;
    }

    public EditMessageText deleteCallbackHandler(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        Integer message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chatIdCallback = update.getCallbackQuery().getMessage().getChatId();
        if (callBackData.equals("\u2B05main")) {
            start = start - 3;
            end = end - 3;
            InlineKeyboardMarkup inlineKeyboardMarkup = mediaService.pagingImgKeyWords(chatIdCallback, start, end);
            if (inlineKeyboardMarkup != null) {
                return EditMessageText
                        .builder()
                        .chatId(chatIdCallback)
                        .messageId(message_id)
                        .text("Выберите необходимое слово")
                        .replyMarkup(inlineKeyboardMarkup)
                        .build();
            }
        } else if (callBackData.equals("\u27A1main")) {
            start = start + 3;
            end = end + 3;
            InlineKeyboardMarkup inlineKeyboardMarkup = mediaService.pagingImgKeyWords(chatIdCallback, start, end);
            if (inlineKeyboardMarkup != null) {
                return EditMessageText
                        .builder()
                        .chatId(chatIdCallback)
                        .messageId(message_id).text("Выберите необходимое слово")
                        .replyMarkup(mediaService.pagingImgKeyWords(chatIdCallback, start, end))
                        .build();
            }
        }
        return null;
    }

    public <T> T deleteCallbackShow(Update update) throws TelegramApiException {
        String callBackData = update.getCallbackQuery().getData();
        long chatIdCallback = update.getCallbackQuery().getMessage().getChatId();
        switch (callBackData) {
            case "\u2B05show" -> {
                index--;
                return mediaService.sendingMedia(chatIdCallback, keyword, mediaService.showImgCallback(chatIdCallback, index, keyword), index);
            }
            case "\u27A1show" -> {
                index++;
                return mediaService.sendingMedia(chatIdCallback, keyword, mediaService.showImgCallback(chatIdCallback, index, keyword), index);
            }
            case "Удалить" -> {
                mediaService.deleteImgFromChat(chatIdCallback, index);
                chatService.sendMessage(chatIdCallback, "Успешно удалено");
            }
            default -> {
                keyword = callBackData;
                return mediaService.sendingMedia(chatIdCallback, keyword, mediaService.showImgCallback(chatIdCallback, index, keyword), index);
            }
        }
        return null;
    }
}