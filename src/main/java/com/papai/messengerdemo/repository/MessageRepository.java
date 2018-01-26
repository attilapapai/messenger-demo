package com.papai.messengerdemo.repository;

import com.papai.messengerdemo.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
