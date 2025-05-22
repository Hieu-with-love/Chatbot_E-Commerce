package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductApiService {
    @GET("/api/v1/products")
    Call<List<Product>> getAllProducts();

    @GET("/api/v1/products/search")
    Call<List<Product>> searchProducts(@Query("query") String query);
}
