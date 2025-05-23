package hcmute.edu.vn.chatbot_ec.network;


import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("/api/v1/users/me")
    Call<UserResponse> getMe(@Query("token") String token);
}
