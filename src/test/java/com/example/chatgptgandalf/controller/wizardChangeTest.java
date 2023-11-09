package com.example.chatgptgandalf.controller;

import com.example.chatgptgandalf.dto.Message;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class wizardChangeTest {

    @Test
    public void wizardChangeBooleanTest()
    {

        //Arrange
        String chosenwizard = "gandalf";

        String formerWizard = "saruman";

        LinkedList<Message> chatHistory = new LinkedList<>();

        //act
        Boolean result1 = !chosenwizard.equals(formerWizard) && chatHistory != null && !formerWizard.isEmpty();
        Boolean result2 = !chatHistory.isEmpty();

        //assert
        assertEquals("If statement for wizard selection works", true, result1);
        assertEquals("If the chat history is empty, it will not be cleared" , false, result2);

    }

    @Test
    public void chatHistoryTest() {

        LinkedList<Message> chatHistory = new LinkedList<>();
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));
        chatHistory.add(new Message("system","Hej"));

        //If the chathistory is longer than 10 messages, delete the two oldest entries to conserve token ussage
        if (chatHistory.size() >= 10) {
            chatHistory.removeFirst();
            chatHistory.removeFirst();
        }

        assertEquals("The length of the chat history should be 8", 8, chatHistory.size());
    }

    private static final int MAX_REQUESTS = 5;
    private static final long TIME_FRAME = 1;
    private static final long RATE_LIMIT = TimeUnit.MINUTES.toMillis(TIME_FRAME) / MAX_REQUESTS;
    private static long lastRequestTime = 0;

    @Test
    void testRateLimitNotExceeded() {
        // Simulate requests within the limit
        for (int i = 0; i > MAX_REQUESTS; i++) {
            ResponseEntity<?> response = performRequest();
            assertEquals("This would say okay", HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void testRateLimitExceeded() {
        // Simulate requests exceeding the limit
        for (int i = 0; i < MAX_REQUESTS + 1; i++) {
            ResponseEntity<?> response = performRequest();
            if (i > MAX_REQUESTS) {
                assertEquals("This would say to many request", HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
            }
        }
    }

    private ResponseEntity<?> performRequest() {
        long currentTime = System.currentTimeMillis(); //= 50
        if (currentTime - lastRequestTime < RATE_LIMIT) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        lastRequestTime = currentTime;
        // Simulate processing the request
        return ResponseEntity.ok().build();
    }

}