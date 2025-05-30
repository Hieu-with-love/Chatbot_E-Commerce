package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.ChatSessionDto;
import android.hcmute.edu.vn.chatbot_spring.dto.request.*;
import android.hcmute.edu.vn.chatbot_spring.dto.response.*;
import android.hcmute.edu.vn.chatbot_spring.model.Product;
import android.hcmute.edu.vn.chatbot_spring.service.ChatbotService;
import android.hcmute.edu.vn.chatbot_spring.service.OrderService;
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
    private final OrderService orderService;

    @PostMapping("/process-product")
    public ResponseEntity<?> searchProducts(@RequestBody ProductSearchRequest req) {
        try{
            List<Product> products = productService.searchProducts(req);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/check-orders/{userId}")
    public ResponseEntity<?> checkUserOrders(@PathVariable("userId") Integer userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(
                ResponseData.builder()
                        .status(200)
                        .message("Get all orders of user successfully")
                        .data(orders)
                        .build()
        );
    }

    @PostMapping("/process-consultant")
    public ResponseEntity<?> searchProducts(@RequestBody ProductConsultantReq req) {
        try{
            List<Product> products = productService.searchProductsBySize(req);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/start")
    public ResponseEntity<?> startChatSession(@RequestBody ChatSessionStartRequest request) {
        try {
            ChatSessionResponse session = chatbotService.startChatSession(request);
            chatbotService.updateChatSession(session.getId());
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
            ChatSessionResponse session = chatbotService.getChatSessionResponseById(sessionId);
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

    @GetMapping("session/get")
    public ResponseEntity<?> getChatSession(@RequestBody ChatSessionRequest request) {
        return ResponseEntity.ok(chatbotService.getChatSession(request));
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

    @PostMapping("session/{sessionId}/summary")
    public ResponseEntity<?> updateSummaryChatSession(@PathVariable Integer sessionId,
                                                      @RequestBody SummaryRequest request) {
        try {
            ChatSessionResponse session = chatbotService.updateSummaryChatSession(sessionId, request);
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

    @GetMapping("/session/exists")
    public ResponseEntity<?> checkExistsAnyChatSessionByToken(@RequestParam String token) {
        try {
            ChatSessionResponse session = chatbotService.existsAnyChatSessionByToken(token);
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Check chat session existence successfully")
                            .data(session)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.builder()
                            .status(400)
                            .message("Failed to check chat session existence: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get a chat session with all related messages by sessionId and userId
     * 
     * @param sessionId The session ID
     * @param userId The user ID
     * @return Response containing the session with all messages
     */
    @GetMapping("/session/{sessionId}/user/{userId}")
    public ResponseEntity<?> getChatSessionWithMessages(
            @PathVariable Integer sessionId,
            @PathVariable Integer userId) {
        try {
            ChatSessionResponse session =
                    chatbotService.getChatSessionWithMessages(sessionId, userId);
            
            return ResponseEntity.ok(
                    ResponseData.builder()
                            .status(200)
                            .message("Chat session with messages retrieved successfully")
                            .data(session)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.builder()
                            .status(404)
                            .message("Failed to retrieve chat session: " + e.getMessage())
                            .build());
        }
    }
}
