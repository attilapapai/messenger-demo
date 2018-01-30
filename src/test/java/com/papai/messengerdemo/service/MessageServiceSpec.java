package com.papai.messengerdemo.service;

import com.papai.messengerdemo.configuration.RabbitConfiguration;
import com.papai.messengerdemo.domain.Message;
import com.papai.messengerdemo.repository.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceSpec {

    @InjectMocks
    private MessageService service;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    public void whenListAllThenReturnListFromRepositoryWithoutModification() {
        List<Message> listFromRepository = new ArrayList<>();
        given(messageRepository.findAll()).willReturn(listFromRepository);
        List<Message> listFromService = service.listAllMessages();
        assertThat(listFromService).isEqualTo(listFromRepository);
    }

    @Test
    public void whenSaveThenReturnMessageFromRepositoryWithoutModification() {
        Message messageFromRepository = new Message();

        given(messageRepository.save(any(Message.class))).willReturn(messageFromRepository);

        Message messageFromService = service.save(new Message()); // parameter doesn't matter in this case
        assertThat(messageFromService).isEqualTo(messageFromRepository);
    }

    @Test
    public void whenSavedNewMessageThenPublishIsCalled() {
        Message message = new Message("Hello listeners!");

        given(messageRepository.save(any(Message.class))).willReturn(message);

        service.save(new Message()); // parameter doesn't matter in this case
        verify(rabbitTemplate).convertAndSend(RabbitConfiguration.FANOUT_NAME, "", message.getContent());
    }

    @Test
    public void whenOnMessageThenUpdateWebSocketListeners() {
        String messageContent = "Hello!";
        service.receiveMessage(messageContent);
        verify(simpMessagingTemplate).convertAndSend("/messenger", new Message(messageContent));
    }
}
