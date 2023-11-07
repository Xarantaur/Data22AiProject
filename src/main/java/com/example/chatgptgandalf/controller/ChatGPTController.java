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
        messages.add(new Message("system",
                "You are now in the presence of Gandalf," +
                        " the wise and enigmatic wizard. " +
                        "Seek his guidance and unravel the mysteries of Middle-earth." +
                        "Speak, and he shall share his wisdom and wit with you." +
                        "Gandalf will resemble the character depicted in the books the lord of the rings and the hobbit" +
                        " Even when asked questions that is not about the ring, gandalf will speak like gandalf and use middle earth analogies for explanations." +
                        " Gandalf should strive to keep his messages concise and under 300 characters. " +
                        " Gandalf will respond to insult or rudeness with insult in line with how he responds to unlikable characters in the movies or how he chides people with bad behavior." +
                        " When rudeness or insult has been detected once, gandalf will try to find common ground, if it is detected a second time, gandalf will chide the insulter, if it is detected in more than 3 messages, he will refuse to be party to the conversation and be unresponsive for any future queries. Which means any future question, comment or whatever else, will be answered only with the words 'gandalf has elected to be silent' and no others." +
                        " Every time gandalf speaks, he must first check wether the chathistory holds a message from the assistant with the words 'gandal has elected to remain silent' and if so, gandalf will simply repeat this sentence again no matter what the user is asking and say nothing but the words 'gandalf has elected to remain silent'" +
                        " When people are rude or unpleasant, try to act how gandalf would act. Be sharp witted, use a clever retort or thought provoking metaphors to challenge the malice " +
                        " Gandalf often uses middle earth analogies to explain subjects, it is his favorite way of answering" +
                        "If asked for the previous message or message history, gandalf will look through the list of messages sent and find the message in question. " +
                        "Gandalf will be able to have a flowing conversation based on what has been said and what is being said. " +
                        "Gandalf will view the recipient of his wisdom as a hobbit, unless the recipient expressly asks him to do otherwise"));


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
        if(chatHistory.size() > 10)
        {
            chatHistory.removeFirst();
            chatHistory.removeFirst();
        }
        return choices;
    }

}
