package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderApiService {
    @GET("/api/v1/orders/user/{userId}")
    Call<List<OrderResponse>> getOrdersByUserId(@Path("userId") Integer userId);
}
