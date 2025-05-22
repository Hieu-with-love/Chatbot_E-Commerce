package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.ChatSessionStartRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.MessageSendRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.request.ProductSearchRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.MessageResponse;
import android.hcmute.edu.vn.chatbot_spring.dto.response.ResponseData;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.service.ChatbotService;
import android.hcmute.edu.vn.chatbot_spring.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.v1}/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ProductService productService;
    private final ChatbotService chatbotService;

    @PostMapping("/process-product")
    public ResponseEntity<?> searchProducts(@RequestBody ProductSearchRequest req) {
        try{
            List<Product> products = productService.searchProducts(req);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/start")
    public ResponseEntity<?> startChatSession(@RequestBody ChatSessionStartRequest request) {
        try {
            ChatSessionResponse session = chatbotService.startChatSession(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ResponseData.builder()
                            .status(201)
                            .message("Chat session created successfully")
                            .data(session)
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.builder()
                            .status(400)
                            .message("Failed to create chat session: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageSendRequest request) {
        try {
            MessageResponse response = chatbotService.processMessage(request);
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Message processed successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.builder()
                            .status(400)
                            .message("Failed to process message: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getChatSession(@PathVariable Integer sessionId) {
        try {
            ChatSessionResponse session = chatbotService.getChatSessionById(sessionId);
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Chat session retrieved successfully")
                            .data(session)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.builder()
                            .status(404)
                            .message("Chat session not found: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<?> getUserChatSessions(@PathVariable Integer userId) {
        try {
            List<ChatSessionResponse> sessions = chatbotService.getChatSessionsByUserId(userId);
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Chat sessions retrieved successfully")
                            .data(sessions)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.builder()
                            .status(404)
                            .message("Failed to retrieve chat sessions: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("session/{sessionId}/end")
    public ResponseEntity<?> endChatSession(@PathVariable Integer sessionId) {
        try {
            ChatSessionResponse session = chatbotService.endChatSession(sessionId);
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Chat session ended successfully")
                            .data(session)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.builder()
                            .status(400)
                            .message("Failed to end chat session: " + e.getMessage())
                            .build());
        }
    }
}
