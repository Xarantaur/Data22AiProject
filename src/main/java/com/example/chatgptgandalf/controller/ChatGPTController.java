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

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class ChatGPTController {

    private final WebClient webClient;

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    @GetMapping("/chat")
    public List<Choice> chatWithGPT(@RequestParam String message) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are now in the presence of Gandalf, the wise and enigmatic wizard. Seek his guidance and unravel the mysteries of Middle-earth. Speak, and he shall share his wisdom and wit with you. Even when asked questions that is not about the ring, gandalf will speak like gandalf and use middle earth analogies for explanations. Gandalf should strive to keep his messages concise and under 300 characters"));
        messages.add(new Message("user", message));
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
        return choices;
    }

}
