package com.firstproject.springredditclone.exceptions;

import com.firstproject.springredditclone.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SpringRedditException.class)
    public ResponseEntity<ApiResponse> springRedditExceptionHandler(SpringRedditException springRedditException)
    {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(springRedditException.getMessage());
        apiResponse.setSuccess(false);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }


    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse> postNotFoundException(PostNotFoundException postNotFoundException)
    {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(postNotFoundException.getMessage());
        apiResponse.setSuccess(false);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(SubredditNotFoundException.class)
    public ResponseEntity<ApiResponse> subredditNotFoundException(SubredditNotFoundException subredditNotFoundException)
    {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(subredditNotFoundException.getMessage());
        apiResponse.setSuccess(false);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


}
