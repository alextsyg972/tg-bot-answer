package org.example.spring.boot.tgbotanswers.service;

import org.example.spring.boot.tgbotanswers.config.BotConfig;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.Image;
import org.example.spring.boot.tgbotanswers.model.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class AnswerBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(AnswerBot.class);
    private ChatRepository chatRepository;
    private ChatService chatService;
    private final BotConfig botConfig;
    private ImageRepository imageRepository;


    public AnswerBot(@Value("${bot.key}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "welcome"));
        listOfCommands.add(new BotCommand("/register", "register chat"));
        listOfCommands.add(new BotCommand("/add", "add to"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
//            log.error("Error setting bot's command list: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().isReply()) {
            String messageText = update.getMessage().getText();
            if (messageText.contains("@kowern_bot")) {
                switch (messageText) {
                    case "/start@kowern_bot" -> sendMessage(chatId, chatService.startCommandReceived(chatId));
                    case "/register@kowern_bot" -> sendMessage(chatId, chatService.registerUser(chatId));
                    case "/add@kowern_bot" -> sendMessage(chatId, "Reply -> add image and message");
                }
                return;
            }
            try {
                sendingPhoto(chatId, messageText);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.getMessage().isReply() & update.getMessage().hasPhoto()) {
            addImg(update.getMessage().getCaption(), update);
        } else if (update.getMessage().isReply() & update.getMessage().hasAnimation()) {
            addImg(update.getMessage().getAnimation(), update.getMessage().getChatId());
            sendMessage(chatId, "ответь на это сообщение для добавления слова на которое будет реагировать бот");
        } else if (update.getMessage().isReply() & update.getMessage().hasText()) {
            chatService.cringe(chatId, update.getMessage().getText());
            sendMessage(chatId, "++?");
        }
    }
//    private void addGif(Animation animation, Long chatId) {
//        try {
//            GetFile getFile = new GetFile(animation.getFileId());
//            File file = execute(getFile);
//            downloadFile(file, new java.io.File("/root/testAppJar/photos/" + file.getFileId() + ".gif"));
//            sendMessage(chatId, "ответь на это сообщение для добавления слова на которое будет реагировать бот");
//            chatService.addGifToChat(chatId,chatId.toString(), file);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

    //Png
    private void addImg(String keyword, Update update) {
        if (keyword != null) {
            Message message = update.getMessage();
            try {
                GetFile getFile = new GetFile(message.getPhoto().get(2).getFileId());
                File file = execute(getFile); //tg file obj
                downloadFile(file, new java.io.File("/root/testAppJar/photos/" + file.getFileId() + ".png"));
                chatService.addImgToChat(message.getChatId(), keyword, file);
                sendMessage(message.getChatId(), "Успешно добавлено");
            } catch (IndexOutOfBoundsException e) {
                log.error("Ошибка при загрузке файла из чата", e);
                sendMessage(message.getChatId(), "Неправильный размер изображения");
            } catch (TelegramApiException x) {
                log.error(x.toString());
                sendMessage(message.getChatId(), "Ошибка при обработке фото");
            }
        }
    }

    //Gif
    private void addImg(Animation animation, Long chatId) {
        try {
            GetFile getFile = new GetFile(animation.getFileId());
            File file = execute(getFile); //tg file obj
            downloadFile(file, new java.io.File("/root/testAppJar/photos/" + file.getFileId() + ".gif"));
            chatService.addGifToChat(chatId, chatId.toString(), file);
            sendMessage(chatId, "Успешно добавлено");
        } catch (TelegramApiException x) {
            log.error(x.toString());
            sendMessage(chatId, "Ошибка при обработке фото");
        }
    }

    private void sendingPhoto(long chatId, String messageText) throws TelegramApiException {
        List<Image> list = imageRepository.getImagesByChat(chatRepository.findByChatId(chatId));
        List<Image> key = list.stream()
                .filter(x -> messageText.contains(x.getKeyToImg()))
                .toList();
        if (!key.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(key.size());
            if (key.get(index).getPathToImg().contains(".png")) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(new java.io.File(key.get(index).getPathToImg())));
                execute(sendPhoto);
            } else {
                SendAnimation sendAnimation = new SendAnimation();
                sendAnimation.setChatId(chatId);
                sendAnimation.setAnimation(new InputFile(new java.io.File(key.get(index).getPathToImg())));
                execute(sendAnimation);
            }
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Autowired
    private void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Autowired
    private void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }
}
