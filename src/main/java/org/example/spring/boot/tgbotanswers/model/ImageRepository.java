package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageRepository extends CrudRepository<Image,Long> {

    List<Image> getImagesByChat(Chat chat);

}
