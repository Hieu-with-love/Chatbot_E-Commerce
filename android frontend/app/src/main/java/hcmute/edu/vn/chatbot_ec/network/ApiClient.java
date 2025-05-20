package hcmute.edu.vn.chatbot_ec.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    // Thay BASE_URL bằng URL thực tế của Spring Boot backend
    private static final String BASE_URL = "http://10.0.2.2:9080/";

    public static AuthApiService getAuthApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthApiService.class);
    }
}
