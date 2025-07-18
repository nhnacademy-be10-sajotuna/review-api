package com.nhnacademy.review;

import com.nhnacademy.review.point.PointEarnRequest;
import com.nhnacademy.review.point.PointPolicyType;
import com.nhnacademy.review.service.PointMessageProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointMessageProducerTest {

    @InjectMocks
    private PointMessageProducer pointMessageProducer;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("포인트 적립 요청 메시지 전송 테스트")
    void sendPointEarnRequest() {
        // Arrange (준비)
        // @Value로 주입되는 필드 값을 ReflectionTestUtils를 사용해 강제로 설정
        ReflectionTestUtils.setField(pointMessageProducer, "exchangeName", "point-exchange");
        ReflectionTestUtils.setField(pointMessageProducer, "routingKey", "point.earn");

        PointEarnRequest request = new PointEarnRequest(1L, PointPolicyType.REVIEW);

        // Act (실행)
        pointMessageProducer.sendPointEarnRequest(request);

        // Assert (검증)
        // rabbitTemplate의 convertAndSend 메서드가 정확한 파라미터로 1번 호출되었는지 검증
        verify(rabbitTemplate, times(1)).convertAndSend("point-exchange", "point.earn", request);
    }
}