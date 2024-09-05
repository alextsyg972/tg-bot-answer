package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ImageRepository extends CrudRepository<Image,Long> {

    List<Image> getImagesByChat(Chat chat);

    @Query(value = "select distinct img.key_to_img from image img where chat_id = ?1", nativeQuery = true)
    List<String> getKeyToImgByChat(Long id);


    List<Image> getImagesByChatAndKeyToImg(Chat chat, String keyToImg);
}
