package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageRepository extends CrudRepository<Image,Long> {

    List<Image> getImagesByChat(Chat chat);

    List<Image> getImagesByChatAndKeyToImg(Chat chat, String keyToImg);

//    List<Image>
}
