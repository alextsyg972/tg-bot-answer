package org.example.spring.boot.tgbotanswers.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.boot.tgbotanswers.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
    int start = 0;
    int end = 3;
    LocalDateTime localDateTime = LocalDateTime.now();
    int index;
    String keyword;


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
        String respond;
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().isReply()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            if (messageText.contains("@kowern_bot")) {
                switch (messageText) {
                    case "/start@kowern_bot" ->
                            chatService.sendMessage(chatId, chatService.startCommandReceived(chatId));
                    case "/register@kowern_bot" -> chatService.sendMessage(chatId, chatService.registerUser(chatId));
                    case "/add@kowern_bot" -> chatService.sendMessage(chatId, "Reply -> Добавить изображение с сообщением|Gif");
                    case "/delete@kowern_bot" -> {
                        chatService.sendMessage(chatId, mediaService.pagingImgKeyWords(chatId, start, end));
                        start = 0;
                        end = 3;
                        index = 0;
                    }
                }
                return;
            }
            if (LocalDateTime.now().getMinute() - localDateTime.getMinute() >= 3) {
                sendMedia(chatId, messageText);
                localDateTime = LocalDateTime.now();
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            Integer message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chatIdCallback = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals("\u2B05")) {
                try {
                    InlineKeyboardMarkup inlineKeyboardMarkup = mediaService.pagingImgKeyWords(chatIdCallback, start - 3, end - 3);
                    if (inlineKeyboardMarkup != null) {
                        execute(EditMessageText.builder().chatId(chatIdCallback).messageId(message_id).text("Выберите необходимое слово").replyMarkup(inlineKeyboardMarkup).build());
                        start = start - 3;
                        end = end - 3;
                    }
                } catch (TelegramApiException e) {
                    log.error("RunTimeEx when send edited msg", e);
                }
            } else if (callBackData.equals("\u27A1")) {
                try {
                    InlineKeyboardMarkup inlineKeyboardMarkup = mediaService.pagingImgKeyWords(chatIdCallback, start + 3, end + 3);
                    if (inlineKeyboardMarkup != null) {
                        execute(EditMessageText.builder().chatId(chatIdCallback).messageId(message_id).text("Выберите необходимое слово").replyMarkup(mediaService.pagingImgKeyWords(chatIdCallback, start + 3, end + 3)).build());
                        start = start + 3;
                        end = end + 3;
                    }
                } catch (TelegramApiException e) {
                    log.error("RunTimeEx when send edited msg", e);
                }
            } else {
                if (callBackData.equals("\u2B05show")) {
                    index--;
                    sendMedia(chatIdCallback, keyword, mediaService.showImgCallback(chatIdCallback, index, keyword), index);
                } else if (callBackData.equals("\u27A1show")) {
                    index = index + 1;
                    sendMedia(chatIdCallback, keyword, mediaService.showImgCallback(chatIdCallback, index, keyword), index);
                } else if (callBackData.equals("Удалить")) {
                    mediaService.deleteImgFromChat(chatIdCallback, index);
                    chatService.sendMessage(chatIdCallback, "Успешно удалено");
                } else {
                    sendMedia(chatIdCallback, callBackData, mediaService.showImgCallback(chatIdCallback, index, callBackData), index);
                    keyword = callBackData;
                }
            }
        } else if (update.getMessage().isReply() & update.getMessage().hasPhoto()) {
            respond = addImg(update.getMessage().getCaption(), update);
            chatService.sendMessage(update.getMessage().getChatId(), respond);
        } else if (update.getMessage().isReply() & update.getMessage().hasAnimation()) {
            respond = addGif(update.getMessage().getAnimation(), update.getMessage().getChatId());
            chatService.sendMessage(update.getMessage().getChatId(), respond);
        } else if (update.getMessage().isReply() & update.getMessage().hasText()) {
            mediaService.updateKeyToImg(update.getMessage().getChatId(), update.getMessage().getText());
            chatService.sendMessage(update.getMessage().getChatId(), "Готово");
        }
    }

    private <T> void sendMedia(long chatId, String messageText) {
        try {
            T t = mediaService.sendingMedia(chatId, messageText);
            if (t instanceof SendPhoto) execute((SendPhoto) t);
            else if (t instanceof SendAnimation) execute((SendAnimation) t);
        } catch (TelegramApiException e) {
            log.error("Error sending Media", e);
        }
    }

    private <T> void sendMedia(long chatId, String messageText, InlineKeyboardMarkup inlineKeyboardMarkup, int index) {
        try {
            T t = mediaService.sendingMedia(chatId, messageText, inlineKeyboardMarkup, index);
            if (t instanceof SendPhoto) execute((SendPhoto) t);
            else if (t instanceof SendAnimation) execute((SendAnimation) t);
        } catch (TelegramApiException e) {
            log.error("Error sending Media", e);
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
            return "Gif анимация успешно добавлена";
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
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Autowired
    private void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

}
