package hcmute.edu.vn.chatbot_ec.network;


import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("/api/v1/users/me")
    Call<UserResponse> getMe(@Query("token") String token);

    @GET("/api/v1/users/{id}")
    Call<UserDetailResponse> getUserById(@Path("id") Integer id);
}
