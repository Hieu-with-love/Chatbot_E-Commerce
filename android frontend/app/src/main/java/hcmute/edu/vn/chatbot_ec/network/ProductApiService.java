package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.Product;
import hcmute.edu.vn.chatbot_ec.response.PageResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductApiService {

    @GET("/api/v1/products")
    Call<PageResponse<ProductResponse>> getAllProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("direction") String direction
    );

    @GET("/api/v1/products/search")
    Call<PageResponse<ProductResponse>> searchProducts(
            @Query("query") String query,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("direction") String direction,
            @Query("keyword") String keyword
    );

}
