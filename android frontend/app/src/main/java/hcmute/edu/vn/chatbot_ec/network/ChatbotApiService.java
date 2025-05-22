package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.ChatSessionResponse;
import hcmute.edu.vn.chatbot_ec.model.ChatSessionStartRequest;
import hcmute.edu.vn.chatbot_ec.model.Message;
import hcmute.edu.vn.chatbot_ec.model.MessageResponse;
import hcmute.edu.vn.chatbot_ec.request.MessageRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatbotApiService {
    
    @POST("/api/v1/chatbot/start")
    Call<ChatSessionResponse> startChatSession(@Body ChatSessionStartRequest request);
    
    @POST("/api/v1/chatbot/send")
    Call<MessageResponse> processMessage(@Body MessageRequest request);
    
    @GET("/api/v1/chatbot/session/{sessionId}")
    Call<ChatSessionResponse> getChatSessionById(@Path("sessionId") Integer sessionId);
    
    @GET("/api/v1/chatbot/sessions/user/{userId}")
    Call<List<ChatSessionResponse>> getChatSessionsByUserId(@Path("userId") Integer userId);
    
    @PUT("/api/v1/chatbot/session/{sessionId}/end")
    Call<ChatSessionResponse> endChatSession(@Path("sessionId") Integer sessionId);
    
    @POST("chat/message/save")
    Call<Message> saveMessage(@Body Message message);
}
