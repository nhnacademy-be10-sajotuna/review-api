package com.nhnacademy.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "book-api")
public interface BookClient {

    @PostMapping("/api/books/{isbn}/review")
    void notifyBookReviewCreated(@PathVariable String isbn, @RequestBody double rating);
}
