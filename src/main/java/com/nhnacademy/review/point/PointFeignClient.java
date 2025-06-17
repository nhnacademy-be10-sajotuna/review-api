package com.nhnacademy.review.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-api")
public interface PointFeignClient {
    @PostMapping("/api/points/review")
    void earnPointsByType(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("type") String type // enum의 name() 값 사용
    );
}