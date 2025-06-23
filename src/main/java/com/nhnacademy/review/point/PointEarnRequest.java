package com.nhnacademy.review.point;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PointEarnRequest {
    private Long userId;
    private int totalPrice;
    private PointPolicyType type;
}