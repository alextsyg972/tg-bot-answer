package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageRepository extends CrudRepository<Image,Long> {

    List<Image> getImagesByChat(Chat chat);

    List<Image> getImagesByChatAndKeyToImg(Chat chat, String keyToImg);

//    List<Image>
}
