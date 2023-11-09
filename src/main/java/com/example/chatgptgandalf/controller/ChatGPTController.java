package com.example.chatgptgandalf.controller;

import com.example.chatgptgandalf.dto.ChatRequest;
import com.example.chatgptgandalf.dto.ChatResponse;
import com.example.chatgptgandalf.dto.Choice;

import com.example.chatgptgandalf.dto.Message;
import com.example.chatgptgandalf.service.WizardSelector;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.TimeUnit;

import java.util.*;

@CrossOrigin
@RestController
public class ChatGPTController {

    private final WebClient webClient;

    private static final int MAX_REQUESTS = 5; // Maximum number of requests
    private static final long TIME_FRAME = 1; // Over how many minutes
    private static final long RATE_LIMIT = TimeUnit.MINUTES.toMillis(TIME_FRAME) / MAX_REQUESTS; // Calculate requests allowed pr min into RATE_LIMIT
    private static long lastRequestTime = 0; // Var to store the time of the last request

    private String formerWizard = "";

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    //linked list to hold the chathistory
    // Uses linkedList data structure to avoid needing to move all element index after deleting elements
    LinkedList<Message> chatHistory = new LinkedList<>();

    @GetMapping("/chat")
    public ResponseEntity<List<Choice>> chatWithGPT(@RequestParam String message, @RequestParam String chosenwizard) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        List<Message> messages = new ArrayList<>();
        WizardSelector wizardSelector = new WizardSelector(chosenwizard);

        //if the selected wizard is another than the former wizard, delete the chathistory
        if(!chosenwizard.equals(formerWizard) && chatHistory != null && !formerWizard.isEmpty())
        {
            //only clear the chathistory if it is not allready empty
            if(!chatHistory.isEmpty())
            {
                chatHistory.clear();
            }
        }

        messages.add(new Message("system", wizardSelector.getChosenpersonality()));

        System.out.println(wizardSelector.getChosenpersonality());

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRequestTime < RATE_LIMIT) {
            // Rate limit exceeded
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        // Update last request time
        lastRequestTime = currentTime;

        //Go through all elements in chathistory and add them to the messages sent to chatGPT api if they exist to give chatGPT a memory of the conversation
        if (chatHistory != null) {
            if (!chatHistory.isEmpty()) {
                for (Message historyElement : chatHistory) {
                    messages.add(historyElement);
                }
            }
        }

        //if the new message sent by the user is too long, cut it down in size to 300 tokens
        if (message.length() > 300) {
            message = message.substring(0, 300);
        }

        //construct a new message from the user input
        Message theNewMessage = new Message("user", message);
        messages.add(theNewMessage);
        chatHistory.add(theNewMessage);

        //append messages to the chatRequest and set chatGPT settings
        chatRequest.setMessages(messages);
        chatRequest.setN(1);
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(200);
        chatRequest.setStream(false);
        chatRequest.setPresencePenalty(1);

        //Post to chatGPT api
        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth("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"))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> choices = response.getChoices();
        Message assistantResponse = choices.get(0).getMessage();
        chatHistory.add(assistantResponse);

        //If the chathistory is longer than 10 messages, delete the two oldest entries to conserve token ussage
        if (chatHistory.size() >= 10) {
            chatHistory.removeFirst();
            chatHistory.removeFirst();
        }

        formerWizard = chosenwizard;

        return ResponseEntity.ok(choices);
    }




    //DEBUG TOOLS, TO BE DELETED


    /*display chathistory for debug
    if(chatHistory != null)
    {
        if(!chatHistory.isEmpty())
        {
            for (Message chatelement: chatHistory) {
                System.out.println(chatelement.getRole());
                System.out.println(chatelement.getContent());
            };
        }
    }

     */

}
