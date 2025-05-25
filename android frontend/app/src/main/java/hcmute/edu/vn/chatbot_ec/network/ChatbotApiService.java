package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.ApiResponse;
import hcmute.edu.vn.chatbot_ec.model.Message;
import hcmute.edu.vn.chatbot_ec.request.ChatSessionRequest;
import hcmute.edu.vn.chatbot_ec.request.MessageRequest;
import hcmute.edu.vn.chatbot_ec.request.MessageSendRequest;
import hcmute.edu.vn.chatbot_ec.response.ChatSessionResponse;
import hcmute.edu.vn.chatbot_ec.response.MessageResponse;
import hcmute.edu.vn.chatbot_ec.request.ChatSessionStartRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatbotApiService {
    
    @POST("/api/v1/chatbot/start")
    Call<ResponseData<ChatSessionResponse>> startChatSession(@Body ChatSessionStartRequest request);
    
    @POST("/api/v1/chatbot/send")
    Call<ResponseData<MessageResponse>> processMessage(@Body MessageSendRequest request);
    
    @GET("/api/v1/chatbot/session/{sessionId}")
    Call<ResponseData<ChatSessionResponse>> getChatSessionById(@Path("sessionId") Integer sessionId);
    
    @GET("/api/v1/chatbot/sessions/user/{userId}")
    Call<ResponseData<List<ChatSessionResponse>>> getChatSessionsByUserId(@Path("userId") Integer userId);
    
    @PUT("/api/v1/chatbot/session/{sessionId}/end")
    Call<ResponseData<ChatSessionResponse>> endChatSession(@Path("sessionId") Integer sessionId);
    
    @POST("chat/message/save")
    Call<ResponseData<Message>> saveMessage(@Body Message message);

    @GET("/api/v1/chatbot/session/exists")
    Call<ResponseData<ChatSessionResponse>> checkExistsChatSession(@Query("token") String token);

    @POST("/api/v1/chatbot/session/get")
    Call<ResponseData<ChatSessionResponse>> getChatSession(@Body ChatSessionRequest request);
    
    @GET("/api/v1/chatbot/session/{sessionId}/user/{userId}")
    Call<ResponseData<ChatSessionResponse>> getChatSessionWithMessages(
            @Path("sessionId") Integer sessionId,
            @Path("userId") Integer userId);
}
