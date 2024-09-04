package org.example.spring.boot.tgbotanswers.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Long chatId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat", fetch = FetchType.EAGER)
    private List<Image> images;



    public Chat(Long id, Long chatId) {
        this.id = id;
        this.chatId = chatId;
    }

    public void addImageToChat(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        image.setChat(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setChat(null);
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
