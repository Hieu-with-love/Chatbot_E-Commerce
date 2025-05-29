package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddressRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.AddressResponse;
import android.hcmute.edu.vn.chatbot_spring.exception.ResourceNotFoundException;
import android.hcmute.edu.vn.chatbot_spring.model.Address;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.AddressRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.AddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AddressResponse createAddress(Integer userId, AddressRequest request) {
        // Kiểm tra người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + userId));

        // Tạo địa chỉ mới
        Address address = Address.builder()
                .recipientName(request.getRecipientName())
                .fullAddress(request.getFullAddress())
                .phone(request.getPhone())
                .user(user)
                .build();

        // Lưu địa chỉ
        address = addressRepository.save(address);
        return convertToResponse(address);
    }

    @Override
    public List<AddressResponse> getAddressesByUserId(Integer userId) {
        // Kiểm tra người dùng
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + userId));

        // Lấy danh sách địa chỉ
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public AddressResponse getAddressById(Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ: " + addressId));
        return convertToResponse(address);
    }

    @Transactional
    @Override
    public AddressResponse updateAddress(Integer addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ: " + addressId));

        // Cập nhật thông tin địa chỉ
        address.setRecipientName(request.getRecipientName());
        address.setFullAddress(request.getFullAddress());
        address.setPhone(request.getPhone());

        // Lưu địa chỉ
        address = addressRepository.save(address);
        return convertToResponse(address);
    }

    @Transactional
    @Override
    public void deleteAddress(Integer addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ: " + addressId));
        addressRepository.delete(address);
    }

    // Chuyển đổi Address entity thành AddressResponse
    private AddressResponse convertToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setRecipientName(address.getRecipientName());
        response.setFullAddress(address.getFullAddress());
        response.setPhone(address.getPhone());
        response.setUserId(address.getUser().getId());
        return response;
    }
}