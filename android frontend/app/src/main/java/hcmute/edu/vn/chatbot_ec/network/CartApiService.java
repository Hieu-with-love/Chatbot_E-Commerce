package hcmute.edu.vn.chatbot_ec.network;

import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApiService {
    @GET("/api/v1/carts/{userId}")
    Call<CartResponse> getCart(@Path("userId") Integer userId);

    @POST("/api/v1/carts/{userId}/items")
    Call<CartResponse> addItemToCart(@Path("userId") Integer userId, @Body AddCartItemRequest request);

    @PUT("/api/v1/carts/{userId}/items/{itemId}")
    Call<CartResponse> updateItemInCart(@Path("userId") Integer userId, @Path("itemId") Integer itemId, @Body AddCartItemRequest request);

    @DELETE("/api/v1/carts/{userId}/items/{itemId}")
    Call<CartResponse> removeItemFromCart(@Path("userId") Integer userId, @Path("itemId") Integer itemId);

    @DELETE("/api/v1/carts/{userId}")
    Call<CartResponse> clearCart(@Path("userId") Integer userId);
}
