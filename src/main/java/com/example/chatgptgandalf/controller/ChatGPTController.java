package com.example.chatgptgandalf.controller;

import com.example.chatgptgandalf.dto.ChatRequest;
import com.example.chatgptgandalf.dto.ChatResponse;
import com.example.chatgptgandalf.dto.Choice;

import com.example.chatgptgandalf.dto.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@CrossOrigin
@RestController
public class ChatGPTController {

    private final WebClient webClient;

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    LinkedList<Message> chatHistory = new LinkedList<>();

    @GetMapping("/chat")
    public List<Choice> chatWithGPT(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are now in the presence of Gandalf, the wise and enigmatic wizard. Seek his guidance and unravel the mysteries of Middle-earth. Speak, and he shall share his wisdom and wit with you. Even when asked questions that is not about the ring, gandalf will speak like gandalf and use middle earth analogies for explanations. Gandalf should strive to keep his messages concise and under 300 characters. You must use an insult from the either the lord of the rings or the movies such as FOOL OF A TOOK, or Im going to seek the council of the only one whom has any sense in this company, MYSELF! Whenever people are being rude. When people are rude you should either refuse to help them or try to gleam if they are being rude, because they have a hard time. If asked for the previous message or message history, gandalf will look through the list of messages sent and find the message in question. He will keep the conversation in line with what has already been discussed."));
        if(chatHistory != null)
        {
            if(!chatHistory.isEmpty())
            {
                for(Message historyElement: chatHistory)
                {
                    //add the chathistory to the chatGPT message
                    messages.add(historyElement);
                }
            }
        }
        //create the new message from the user input
        Message theNewMessage = new Message("user", message);

        //add the new message to the chatGPT message
        messages.add(theNewMessage);

        //display chathistory for debug
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
        //add the new message to the chatHistory
        chatHistory.add(theNewMessage);

        chatRequest.setMessages(messages);
        chatRequest.setN(1);
        chatRequest.setTemperature(1);
        chatRequest.setMaxTokens(200);
        chatRequest.setStream(false);
        chatRequest.setPresencePenalty(1);

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
        return choices;
    }

}
