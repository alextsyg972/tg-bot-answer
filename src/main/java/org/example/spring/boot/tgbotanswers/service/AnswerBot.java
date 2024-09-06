package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.config.BotConfig;
import org.example.spring.boot.tgbotanswers.handler.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AnswerBot extends TelegramLongPollingBot {
    private ChatService chatService;
    private MediaService mediaService;
    private final BotConfig botConfig;
    LocalDateTime localDateTime = LocalDateTime.now();
    private CallbackHandler callbackHandler;
    private String respond;
    private Integer messageId;

    public AnswerBot(@Value("${bot.key}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "welcome"));
        listOfCommands.add(new BotCommand("/register", "register chat"));
        listOfCommands.add(new BotCommand("/add", "add to"));
        listOfCommands.add(new BotCommand("/delete", "delete img"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (!update.getMessage().isReply()) {
                    switch (messageText) {
                        case "/start@kowern_bot" ->
                                chatService.sendMessage(chatId, chatService.startCommandReceived(chatId));
                        case "/register@kowern_bot" ->
                                chatService.sendMessage(chatId, chatService.registerUser(chatId));
                        case "/add@kowern_bot" ->
                                messageId = chatService.sendMessage(chatId, "Reply -> Добавить изображение с сообщением|Gif");
                        case "/delete@kowern_bot" -> {
                            chatService.sendMessage(chatId, mediaService.pagingImgKeyWords(chatId, 0, 3));
                            callbackHandler.deleteCommandInit();
                        }
                    }
                }
                if (LocalDateTime.now().getMinute() - localDateTime.getMinute() >= 1) {
                System.out.println("im here");
                sendMedia(chatId, messageText);
                localDateTime = LocalDateTime.now();
                }
            }
            if (update.getMessage().isReply() && update.getMessage().getReplyToMessage().getMessageId().equals(messageId)) {
                if (update.getMessage().hasPhoto()) {
                    respond = addImg(update.getMessage().getCaption(), update);
                    chatService.sendMessage(update.getMessage().getChatId(), respond);
                } else if (update.getMessage().hasAnimation()) {
                    respond = addGif(update.getMessage().getAnimation(), update.getMessage().getChatId());
                    messageId = chatService.sendMessage(update.getMessage().getChatId(), respond);
                } else if (update.getMessage().hasText()) {
                    mediaService.updateKeyToImg(update.getMessage().getChatId(), update.getMessage().getText());
                    chatService.sendMessage(update.getMessage().getChatId(), "Текст закреплен за Gif");
                }
            }

        }
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().contains("main")) {
                try {
                    execute(callbackHandler.deleteCallbackHandler(update));
                } catch (TelegramApiException e) {
                    log.error("Error execute deleteCallbackHandler {}", e.getMessage());
                }
            } else {
                sendMedia(update);
            }
        }
    }

    private <T> void sendMedia(long chatId, String messageText) {
        System.out.println("inside sendMedia");
        try {
            T t = mediaService.sendingMedia(chatId, messageText);
            if (t instanceof SendPhoto) execute((SendPhoto) t);
            else if (t instanceof SendAnimation) execute((SendAnimation) t);
        } catch (TelegramApiException e) {
            log.error("Error sending Media", e);
        }
    }

    private <T> void sendMedia(Update update) {
        try {
            T t = callbackHandler.deleteCallbackShow(update);
            if (t instanceof SendPhoto) execute((SendPhoto) t);
            else if (t instanceof SendAnimation) execute((SendAnimation) t);
        } catch (TelegramApiException e) {
            log.error("Error sending Media = {}", e.getMessage());
        } catch (NullPointerException x) {
            log.error("callbackHandler.deleteCallBackShow returning null = {}", x.getMessage());
        }
    }


    //Png
    private String addImg(String keyword, Update update) {
        if (keyword != null) {
            Message message = update.getMessage();
            try {
                GetFile getFile = new GetFile(message.getPhoto().get(2).getFileId());
                File file = execute(getFile); //tg file obj
                downloadFile(file, new java.io.File("/root/testAppJar/photos/" + file.getFileId() + ".png"));
                mediaService.addImgToChat(message.getChatId(), keyword, file);
                return "Изображение успешно добавлено";
            } catch (IndexOutOfBoundsException e) {
                log.error("Ошибка при загрузке изображения из чата", e);
                return "Ошибка при загрузке изображения из чата";
            } catch (TelegramApiException x) {
                log.error(x.toString());
                return "Ошибка при загрузке изображения из чата";
            }
        }
        return "Нет сообщения";
    }

    //Gif
    private String addGif(Animation animation, Long chatId) {
        try {
            GetFile getFile = new GetFile(animation.getFileId());
            File file = execute(getFile); //tg file obj
            downloadFile(file, new java.io.File("/root/testAppJar/photos/" + file.getFileId() + ".gif"));
            mediaService.addGifToChat(chatId, chatId.toString(), file);
            return "Gif анимация успешно добавлена, ответь на это сообщение чтобы указать на какое слово бот будет реагировать";
        } catch (TelegramApiException x) {
            log.error(x.toString());
            return "Ошибка при обработке gif";
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Autowired
    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Autowired
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    private void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

}
