package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.AddressAdapter;
import hcmute.edu.vn.chatbot_ec.network.AddressApiService;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.request.AddressRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressFragment extends Fragment implements AddressAdapter.OnAddressActionListener, AddEditAddressDialog.OnAddressSaveListener {

    private RecyclerView recyclerViewAddresses;
    private com.google.android.material.textview.MaterialTextView textEmptyAddresses;
    private CircularProgressIndicator progressLoading;
    private AddressAdapter addressAdapter;
    private List<AddressResponse> addresses;
    private AddressApiService addressApiService;
    private int userId = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        recyclerViewAddresses = view.findViewById(R.id.recycler_view_addresses);
        textEmptyAddresses = view.findViewById(R.id.text_empty_addresses);
        progressLoading = view.findViewById(R.id.progress_loading);
        FloatingActionButton fabAddAddress = view.findViewById(R.id.fab_add_address);

        addresses = new ArrayList<>();
        addressAdapter = new AddressAdapter(addresses, this);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddresses.setAdapter(addressAdapter);

        addressApiService = ApiClient.getAddressApiService();

        fabAddAddress.setOnClickListener(v -> {
            AddEditAddressDialog dialog = AddEditAddressDialog.newInstance(null, this);
            dialog.show(getParentFragmentManager(), "AddEditAddressDialog");
        });

        fetchAddresses();

        return view;
    }

    private void fetchAddresses() {
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
                    Snackbar.make(recyclerViewAddresses, R.string.error_load_addresses_failed, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.error))
                            .setTextColor(getResources().getColor(R.color.button_text))
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AddressResponse>> call, @NonNull Throwable t) {
                progressLoading.setVisibility(View.GONE);
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
        AddEditAddressDialog dialog = AddEditAddressDialog.newInstance(address, this);
        dialog.show(getParentFragmentManager(), "AddEditAddressDialog");
    }

    @Override
    public void onDelete(AddressResponse address) {
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
        Toast.makeText(getContext(), "Địa chỉ đã chọn: " + address.getFullAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSave(AddressRequest request, Integer addressId) {
        progressLoading.setVisibility(View.VISIBLE);
        if (addressId == null) {
            // Thêm địa chỉ mới
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
            // Cập nhật địa chỉ
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