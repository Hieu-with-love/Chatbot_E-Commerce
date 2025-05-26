package hcmute.edu.vn.chatbot_ec.network;

import java.util.List;

import hcmute.edu.vn.chatbot_ec.request.AddressRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApiService {
    @GET("/api/v1/addresses/user/{userId}")
    Call<List<AddressResponse>> getAddressesByUserId(@Path("userId") int userId);

    @GET("/api/v1/addresses/{addressId}")
    Call<AddressResponse> getAddressById(@Path("addressId") int addressId);

    @POST("/api/v1/addresses/{userId}")
    Call<AddressResponse> addAddress(@Path("userId") int userId,@Body AddressRequest request);

    @PUT("/api/v1/addresses/{addressId}")
    Call<AddressResponse> updateAddress(@Path("addressId") int addressId,@Body AddressRequest request);

    @DELETE("/api/v1/addresses/{addressId}")
    Call<Void> deleteAddress(@Path("addressId") int addressId);

}
