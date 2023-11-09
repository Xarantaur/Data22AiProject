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
    public void performRequest() {
        ResponseEntity<?> response = null;
        lastRequestTime = 30;
        long currentTime = 50;
        if (currentTime - lastRequestTime < RATE_LIMIT) {
            response = ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        // Simulate processing the request
        assertEquals("This is the result", HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    }

    @Test
    public void rateLimitTest() {
        int MAX_REQUESTS = 5;
        long TIME_FRAME = 1;
        long RATE_LIMIT;
        long expected_time = 12000;

        RATE_LIMIT = TimeUnit.MINUTES.toMillis(TIME_FRAME) / MAX_REQUESTS;

        assertEquals("This should be...", expected_time, RATE_LIMIT);

    }

}