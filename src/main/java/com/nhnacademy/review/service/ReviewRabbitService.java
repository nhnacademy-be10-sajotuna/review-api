//package com.nhnacademy.review.service;
//
//import com.nhnacademy.review.point.PointEarnRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ReviewRabbitService {
//    @Value("${rabbitmq.exchange.name}")
//    private String exchangeName;
//    @Value("${rabbitmq.routing.key}")
//    private String routingKey;
//    private final RabbitTemplate rabbitTemplate;
//
//    public void sendPointEarnMessage(PointEarnRequest message) {
//        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
//    }
//}
