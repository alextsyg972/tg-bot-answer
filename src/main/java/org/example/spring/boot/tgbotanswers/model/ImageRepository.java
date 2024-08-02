package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image,Long> {
}
