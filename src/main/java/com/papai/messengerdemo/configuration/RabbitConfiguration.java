package com.papai.messengerdemo.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

    public static final String FANOUT_NAME = "messenger-demo";

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange(FANOUT_NAME);
    }

    @Bean
    public Queue queue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding(FanoutExchange fanout, Queue queue) {
        return BindingBuilder.bind(queue).to(fanout);
    }
}
