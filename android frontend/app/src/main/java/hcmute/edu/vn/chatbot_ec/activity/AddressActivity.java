package hcmute.edu.vn.chatbot_ec.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.AddressAdapter;
import hcmute.edu.vn.chatbot_ec.fragments.AddEditAddressDialog;
import hcmute.edu.vn.chatbot_ec.network.AddressApiService;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.request.AddressRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener, AddEditAddressDialog.OnAddressSaveListener {

    private RecyclerView recyclerViewAddresses;
    private MaterialTextView textEmptyAddresses;
    private CircularProgressIndicator progressLoading;
    private AddressAdapter addressAdapter;
    private List<AddressResponse> addresses;
    private AddressApiService addressApiService;
    private UserApiService userApiService;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Initialize views
        recyclerViewAddresses = findViewById(R.id.recycler_view_addresses);
        textEmptyAddresses = findViewById(R.id.text_empty_addresses);
        progressLoading = findViewById(R.id.progress_loading);
        FloatingActionButton fabAddAddress = findViewById(R.id.fab_add_address);

        // Show loading state
        progressLoading.setVisibility(View.VISIBLE);
        recyclerViewAddresses.setVisibility(View.GONE);
        textEmptyAddresses.setVisibility(View.GONE);

        // Initialize API services
        addressApiService = ApiClient.getAddressApiService();
        userApiService = ApiClient.getUserApiService();

        // Initialize addresses list
        addresses = new ArrayList<>();

        // Setup RecyclerView (adapter will be set after fetching userId)
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB click listener
        fabAddAddress.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            AddEditAddressDialog dialog = AddEditAddressDialog.newInstance(null, this);
            dialog.show(getSupportFragmentManager(), "AddEditAddressDialog");
        });

        // Check token and fetch userId
        String token = TokenManager.getToken(this);
        if (token == null) {
            progressLoading.setVisibility(View.GONE);
            textEmptyAddresses.setText("Vui lòng đăng nhập để xem địa chỉ");
            textEmptyAddresses.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch userId and initialize adapter in callback
        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().getUserId();
                    // Initialize AddressAdapter with userId
                    addressAdapter = new AddressAdapter(addresses, AddressActivity.this);
                    recyclerViewAddresses.setAdapter(addressAdapter);
                    // Fetch addresses
                    fetchAddresses();
                } else {
                    progressLoading.setVisibility(View.GONE);
                    textEmptyAddresses.setText("Không thể lấy thông tin người dùng. Vui lòng thử lại.");
                    textEmptyAddresses.setVisibility(View.VISIBLE);
                    Toast.makeText(AddressActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                progressLoading.setVisibility(View.GONE);
                textEmptyAddresses.setText("Lỗi kết nối. Vui lòng kiểm tra mạng.");
                textEmptyAddresses.setVisibility(View.VISIBLE);
                Toast.makeText(AddressActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAddresses() {
        if (userId == null) return;
        progressLoading.setVisibility(View.VISIBLE);
        addressApiService.getAddressesByUserId(userId).enqueue(new Callback<List<AddressResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AddressResponse>> call, @NonNull Response<List<AddressResponse>> response) {
                progressLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    addresses = response.body();
                    addressAdapter.updateAddresses(addresses);
                    updateAddressVisibility();
                    if (!addresses.isEmpty()) {
                        Snackbar.make(recyclerViewAddresses, R.string.message_addresses_loaded, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.success))
                                .setTextColor(getResources().getColor(R.color.button_text))
                                .show();
                    }
                } else {
                    textEmptyAddresses.setText("Không thể tải danh sách địa chỉ. Vui lòng thử lại.");
                    updateAddressVisibility();
                    Snackbar.make(recyclerViewAddresses, R.string.error_load_addresses_failed, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.error))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AddressResponse>> call, @NonNull Throwable t) {
                progressLoading.setVisibility(View.GONE);
                textEmptyAddresses.setText("Lỗi kết nối. Vui lòng kiểm tra mạng.");
                updateAddressVisibility();
                Snackbar.make(recyclerViewAddresses, getString(R.string.error_network, t.getMessage()), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.error))
                        .setTextColor(getResources().getColor(R.color.button_text))
                        .show();
            }
        });
    }

    private void updateAddressVisibility() {
        if (addresses.isEmpty()) {
            recyclerViewAddresses.setVisibility(View.GONE);
            textEmptyAddresses.setVisibility(View.VISIBLE);
        } else {
            recyclerViewAddresses.setVisibility(View.VISIBLE);
            textEmptyAddresses.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(AddressResponse address) {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        AddEditAddressDialog dialog = AddEditAddressDialog.newInstance(address, this);
        dialog.show(getSupportFragmentManager(), "AddEditAddressDialog");
    }

    @Override
    public void onDelete(AddressResponse address) {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        progressLoading.setVisibility(View.VISIBLE);
        addressApiService.deleteAddress(address.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                progressLoading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    fetchAddresses();
                    Snackbar.make(recyclerViewAddresses, R.string.message_address_deleted, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.success))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                } else {
                    Snackbar.make(recyclerViewAddresses, R.string.error_delete_address_failed, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.error))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressLoading.setVisibility(View.GONE);
                Snackbar.make(recyclerViewAddresses, getString(R.string.error_network, t.getMessage()), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.error))
                        .setTextColor(getResources().getColor(R.color.button_text))
                        .show();
            }
        });
    }

    @Override
    public void onSelect(AddressResponse address) {
        Toast.makeText(this, "Địa chỉ đã chọn: " + address.getFullAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSave(AddressRequest request, Integer addressId) {
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        progressLoading.setVisibility(View.VISIBLE);
        if (addressId == null) {
            // Add new address
            addressApiService.addAddress(userId, request).enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddressResponse> call, @NonNull Response<AddressResponse> response) {
                    progressLoading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        fetchAddresses();
                        Snackbar.make(recyclerViewAddresses, R.string.message_address_added, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.success))
                                .setTextColor(getResources().getColor(R.color.button_text))
                                .show();
                    } else {
                        Snackbar.make(recyclerViewAddresses, R.string.error_add_address_failed, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.error))
                                .setTextColor(getResources().getColor(R.color.button_text))
                                .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddressResponse> call, @NonNull Throwable t) {
                    progressLoading.setVisibility(View.GONE);
                    Snackbar.make(recyclerViewAddresses, getString(R.string.error_network, t.getMessage()), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.error))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                }
            });
        } else {
            // Update existing address
            addressApiService.updateAddress(addressId, request).enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddressResponse> call, @NonNull Response<AddressResponse> response) {
                    progressLoading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        fetchAddresses();
                        Snackbar.make(recyclerViewAddresses, R.string.message_address_updated, Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.success))
                                .setTextColor(getResources().getColor(R.color.button_text))
                                .show();
                    } else {
                        Snackbar.make(recyclerViewAddresses, R.string.error_update_address_failed, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.error))
                                .setTextColor(getResources().getColor(R.color.button_text))
                                .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddressResponse> call, @NonNull Throwable t) {
                    progressLoading.setVisibility(View.GONE);
                    Snackbar.make(recyclerViewAddresses, getString(R.string.error_network, t.getMessage()), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.error))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                }
            });
        }
    }
}