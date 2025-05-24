package hcmute.edu.vn.chatbot_ec.network;

import hcmute.edu.vn.chatbot_ec.request.LoginRequest;
import hcmute.edu.vn.chatbot_ec.request.RegisterRequest;
import hcmute.edu.vn.chatbot_ec.response.ResponseData;
import hcmute.edu.vn.chatbot_ec.response.AuthResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("/api/v1/auth/register")
    Call<ResponseData> register(@Body RegisterRequest request);

    /**
     * Login endpoint that returns JWT token
     * @param request LoginRequest with username/email and password
     * @return AuthResponse containing JWT token and related information
     */
    @POST("/api/v1/auth/login")
    Call<ResponseData<String>> login(@Body LoginRequest request);
    
    /**
     * Test endpoint for validating JWT token authentication
     * This can be used to verify if the user's token is still valid
     * @return ResponseData with user information if authenticated
     */
    @GET("/api/v1/auth/me")
    Call<ResponseData<UserResponse>> getCurrentUser();
}
