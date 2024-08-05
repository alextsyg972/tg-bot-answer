package org.example.spring.boot.tgbotanswers.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Long chatId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat", fetch = FetchType.EAGER)
    private List<Image> images;

    public void addImageToChat(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        image.setChat(this);
    }

    public Chat() {
    }

    public Chat(Long chatId) {
        this.chatId = chatId;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }
}
