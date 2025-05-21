package hcmute.edu.vn.chatbot_ec.network;

import hcmute.edu.vn.chatbot_ec.request.LoginRequest;
import hcmute.edu.vn.chatbot_ec.request.RegisterRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("/api/v1/auth/register")
    Call<ResponseData> register(@Body RegisterRequest request);

    @POST("/api/v1/auth/login")
    Call<ResponseData> login(@Body LoginRequest request);
}
