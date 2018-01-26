package com.papai.messengerdemo.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String content;

    public Message() {} // JPA

    public Message(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
            Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", content='" + content + '\'' +
            '}';
    }
}
