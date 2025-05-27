package android.hcmute.edu.vn.chatbot_spring.service;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddressRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse createAddress(Integer userId, AddressRequest request);
    List<AddressResponse> getAddressesByUserId(Integer userId);
    AddressResponse getAddressById(Integer addressId);
    AddressResponse updateAddress(Integer addressId, AddressRequest request);
    void deleteAddress(Integer addressId);
}