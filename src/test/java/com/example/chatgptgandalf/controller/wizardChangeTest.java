package com.example.chatgptgandalf.controller;

import com.example.chatgptgandalf.dto.Message;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

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

}