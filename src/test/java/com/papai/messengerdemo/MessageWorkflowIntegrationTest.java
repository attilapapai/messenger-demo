package com.papai.messengerdemo;

import com.papai.messengerdemo.domain.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageWorkflowIntegrationTest {

    private static final String MESSAGES_ENDPOINT = "/messages";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void exchangeMessagesTest() {

        // when initialized, messages should be empty
        ResponseEntity<List<Message>> emptyResponse = restTemplate.exchange(
            MESSAGES_ENDPOINT,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Message>>() {}
        );
        assertThat(emptyResponse.getBody()).isEmpty();

        // after posting a message we should receive back the persisted entity
        Message postedMessage = new Message("Hi!");
        ResponseEntity<Message> newMessageResponse =
            restTemplate.postForEntity(MESSAGES_ENDPOINT, postedMessage, Message.class);

        assertThat(newMessageResponse).isNotNull();
        assertThat(newMessageResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Message persistedMessage = newMessageResponse.getBody();
        assertThat(persistedMessage.getContent()).isEqualTo(postedMessage.getContent());
        assertThat(persistedMessage.getId()).isNotNull();

        // querying the list again, messages should contain one element
        ResponseEntity<List<Message>> oneMessageListResponse = restTemplate.exchange(
            MESSAGES_ENDPOINT,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Message>>() {}
        );

        List<Message> messages = oneMessageListResponse.getBody();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualToComparingFieldByField(persistedMessage);
    }
}
