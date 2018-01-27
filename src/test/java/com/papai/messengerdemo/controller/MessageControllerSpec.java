package com.papai.messengerdemo.controller;

import com.papai.messengerdemo.domain.Message;
import com.papai.messengerdemo.repository.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerSpec {

    private static final String MESSAGES_ENDPOINT = "/messages";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private MessageRepository messageRepository;

    @Test
    public void whenNoMessagesAreStoredThenResponseMessageListIsEmpty() {
        given(messageRepository.findAll()).willReturn(emptyList());
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            MESSAGES_ENDPOINT,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Message>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void whenMessagesArePresentThenResponseMessageListIsTheSame() {
        List<Message> storedMessages = Arrays.asList(new Message("Hello"), new Message("Bye"));
        given(messageRepository.findAll()).willReturn(storedMessages);
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            MESSAGES_ENDPOINT,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Message>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSameElementsAs(storedMessages);
    }

    @Test
    public void whenPostingMessageWithContentNullThenBadRequest() {
        Message message = new Message(); // "content": null in json
        ResponseEntity<Message> response = restTemplate.postForEntity(MESSAGES_ENDPOINT, message, Message.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenPostingMessageWithContentEmptyThenBadRequest() {
        Message message = new Message(""); // "content": "" in json
        ResponseEntity<Message> response = restTemplate.postForEntity(MESSAGES_ENDPOINT, message, Message.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * The purpose of this test to check that the REST layer doesn't change anything when returning
     * successfully created objects, thus the content of the posted message in this test doesn't matter.
     */
    @Test
    public void whenPostingMessageThenReturnPersistedMessage() {
        Message returnedMessageFromDb = new Message("Im coming from the db!");
        given(messageRepository.save(any(Message.class))).willReturn(returnedMessageFromDb);

        ResponseEntity<Message> response =
            restTemplate.postForEntity(MESSAGES_ENDPOINT, new Message("Hello"), Message.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(returnedMessageFromDb);
    }
}
