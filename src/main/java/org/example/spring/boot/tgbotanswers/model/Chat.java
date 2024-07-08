package org.example.spring.boot.tgbotanswers.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long chatId;

    @ElementCollection
    Map<String, String> hashMap = new HashMap<>();


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Map<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
