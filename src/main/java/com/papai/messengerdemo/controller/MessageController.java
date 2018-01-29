package com.papai.messengerdemo.controller;

import com.papai.messengerdemo.domain.Message;
import com.papai.messengerdemo.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;


    @GetMapping
    public ResponseEntity<List<Message>> listAllMessages() {
        log.info("Listing all messages");
        List<Message> messages =  messageService.listAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Message> addMessage(@Validated @RequestBody Message message) {
        log.info("Received message: {}", message);
        Message persistedMessage = messageService.save(message); // should have ID
        return new ResponseEntity<>(persistedMessage, HttpStatus.CREATED);
    }
}
