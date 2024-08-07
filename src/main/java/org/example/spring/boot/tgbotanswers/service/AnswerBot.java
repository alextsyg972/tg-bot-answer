package org.example.spring.boot.tgbotanswers.service;

import org.example.spring.boot.tgbotanswers.config.BotConfig;
import org.example.spring.boot.tgbotanswers.model.Chat;
import org.example.spring.boot.tgbotanswers.model.ChatRepository;
import org.example.spring.boot.tgbotanswers.model.Image;
import org.example.spring.boot.tgbotanswers.model.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

//@Slf4j
@Component
public class AnswerBot extends TelegramLongPollingBot {
    ChatRepository chatRepository;
    ChatService chatService;
    final BotConfig botConfig;
    ImageRepository imageRepository;


    public AnswerBot(@Value("${bot.key}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "welcome"));
        listOfCommands.add(new BotCommand("/register", "register chat"));
        listOfCommands.add(new BotCommand("/add", "add to"));
        listOfCommands.add(new BotCommand("/today", "Oh"));
        listOfCommands.add(new BotCommand("/set", "set"));
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
            System.out.println(messageText);
            switch (messageText) {
                case "/start@kowern_bot" -> sendMessage(chatId, chatService.startCommandReceived(chatId));
                case "/register@kowern_bot" -> sendMessage(chatId, chatService.registerUser(chatId));
                case "/add@kowern_bot" ->
                        sendMessage(chatId, "Ответь на это сообщение словом на которое бот должен реагировать");
            }
        } else if (update.getMessage().isReply()) {
            System.out.println("dasdasd");
            addImg(update.getMessage().getChatId(), update);
            System.out.println("dasdasd");
        }
    }

    void addImg(long chatId, Update update) {
        System.out.println("dadasdad");
        sendMessage(chatId, "Введите на какое слово будет реагировать бот");
        String keyword = update.getMessage().getReplyToMessage().getText();
        if (keyword != null) {
            System.out.println(keyword);
            check(update, keyword);
        }

    }


    void check(Update update, String keyword) {
        Message message = update.getMessage();
        GetFile getFile = new GetFile(message.getPhoto().get(2).getFileId());
        try {
            File file = execute(getFile); //tg file obj
            downloadFile(file, new java.io.File("C:\\gigaPhotos\\photos\\" + file.getFileId() + ".png"));
            sendMessage(message.getChatId(), chatService.addImgToChat(message.getChatId(), keyword, file));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
