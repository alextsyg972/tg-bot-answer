package org.example.spring.boot.tgbotanswers.model;

import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends CrudRepository<Chat, Long> {

    Chat findByChatId(Long chatId);
}
