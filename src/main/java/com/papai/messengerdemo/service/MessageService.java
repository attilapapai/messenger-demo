package com.papai.messengerdemo.service;

import com.papai.messengerdemo.domain.Message;
import com.papai.messengerdemo.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<Message> listAllMessages() {
        return messageRepository.findAll();
    }

    public Message save(Message message) {
        Message persistedMessage =  messageRepository.save(message);
        log.info("Successfully saved message, publishing to listeners: \"{}\"", persistedMessage.getContent());
        redisTemplate.convertAndSend(topic.getTopic(), persistedMessage.getContent());
        return persistedMessage;
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String content = new String(message.getBody());
        log.info("New message received from Redis, publishing through WebSocket: \"{}\"", content);
        simpMessagingTemplate.convertAndSend("/messenger", new Message(content));
    }
}
