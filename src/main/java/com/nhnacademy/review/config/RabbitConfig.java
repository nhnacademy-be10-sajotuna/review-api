package com.nhnacademy.review.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public Queue pointQueue() {
        return new Queue(queueName, true); // durable
    }

    @Bean
    public DirectExchange pointExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding pointBinding(Queue pointQueue, DirectExchange pointExchange) {
        return BindingBuilder.bind(pointQueue).to(pointExchange).with(routingKey);
    }
}
