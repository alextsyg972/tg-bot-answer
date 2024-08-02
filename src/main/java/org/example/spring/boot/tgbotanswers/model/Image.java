package org.example.spring.boot.tgbotanswers.model;

import jakarta.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String keyToImg;
    @Column
    private String pathToImg;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Chat chat;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getKeyToImg() {
        return keyToImg;
    }

    public void setKeyToImg(String keyToImg) {
        this.keyToImg = keyToImg;
    }

    public String getPathToImg() {
        return pathToImg;
    }

    public void setPathToImg(String pathToImg) {
        this.pathToImg = pathToImg;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
