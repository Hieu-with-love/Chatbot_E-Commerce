package hcmute.edu.vn.chatbot_ec.api;

import hcmute.edu.vn.chatbot_ec.model.ApiResponse;
import hcmute.edu.vn.chatbot_ec.model.ChatSessionResponse;
import hcmute.edu.vn.chatbot_ec.model.MessageResponse;
import hcmute.edu.vn.chatbot_ec.model.MessageSendRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApiService {
    @POST("chat/start")
    Call<ApiResponse<ChatSessionResponse>> startChatSession();

    @POST("chat/send")
    Call<ApiResponse<MessageResponse>> sendMessage(@Body MessageSendRequest request);

    @GET("chat/session/{sessionId}")
    Call<ApiResponse<ChatSessionResponse>> getChatSession(@Path("sessionId") String sessionId);

    @POST("chat/session/{sessionId}/end")
    Call<ApiResponse<Void>> endChatSession(@Path("sessionId") String sessionId);
} 