package org.example.spring.boot.tgbotanswers.model;

import jakarta.persistence.*;

@Entity
@Table
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String keyToImg;
    @Column
    private String pathToImg;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    private Chat chat;


    public Image() {
    }

    public Image(String keyToImg, String pathToImg) {
        this.keyToImg = keyToImg;
        this.pathToImg = pathToImg;
    }

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

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", keyToImg='" + keyToImg + '\'' +
                ", pathToImg='" + pathToImg + '\'' +
                ", chat=" + chat +
                '}';
    }
}
