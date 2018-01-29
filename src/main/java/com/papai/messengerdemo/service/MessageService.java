package com.papai.messengerdemo.service;

import com.papai.messengerdemo.domain.Message;
import com.papai.messengerdemo.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FanoutExchange fanout;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<Message> listAllMessages() {
        return messageRepository.findAll();
    }

    public Message save(Message message) {
        Message persistedMessage =  messageRepository.save(message);
        log.info("Successfully saved message, publishing to listeners: \"{}\"", persistedMessage.getContent());
        rabbitTemplate.convertAndSend(fanout.getName(), "", persistedMessage.getContent());
        return persistedMessage;
    }

    /**
     * This method will be called if a new message is published by the queue.
     * @param message message published by the queue
     */
    @RabbitListener(queues = "#{queue.name}")
    public void receiveMessage(String message) {
        log.info("New message received from queue, publishing through WebSocket: \"{}\"", message);
        simpMessagingTemplate.convertAndSend("/messenger", new Message(message));
    }
}
