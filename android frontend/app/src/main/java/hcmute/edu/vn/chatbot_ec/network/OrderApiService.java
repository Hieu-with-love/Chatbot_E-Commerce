package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.request.CreateOrderRequest;
import hcmute.edu.vn.chatbot_ec.request.PurchaseProductRequest;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApiService {
    @GET("/api/v1/orders/user/{userId}")
    Call<List<OrderResponse>> getOrdersByUserId(@Path("userId") Integer userId);

    @POST("/api/v1/orders/{userId}")
    Call<OrderResponse> createOrder(@Path("userId") Integer userId, @Body CreateOrderRequest orderRequest);

    @POST("/api/v1/orders/purchase/{userId}")
    Call<OrderResponse> purchaseOrder(@Path("userId") Integer userId, @Body PurchaseProductRequest orderRequest);
}
