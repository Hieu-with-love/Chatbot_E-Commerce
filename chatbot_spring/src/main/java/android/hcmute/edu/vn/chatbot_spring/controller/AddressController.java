package android.hcmute.edu.vn.chatbot_spring.controller;

import android.hcmute.edu.vn.chatbot_spring.dto.request.AddressRequest;
import android.hcmute.edu.vn.chatbot_spring.dto.response.AddressResponse;
import android.hcmute.edu.vn.chatbot_spring.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/{userId}")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Integer userId,
                                                        @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponse>> getAddressesByUserId(@PathVariable Integer userId) {
        List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Integer addressId) {
        AddressResponse response = addressService.getAddressById(addressId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Integer addressId,
                                                        @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.updateAddress(addressId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}