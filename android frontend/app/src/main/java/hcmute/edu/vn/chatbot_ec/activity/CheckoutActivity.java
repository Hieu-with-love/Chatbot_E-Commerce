package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.AddressAdapter;
import hcmute.edu.vn.chatbot_ec.adapter.CheckoutAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AddressApiService;
import hcmute.edu.vn.chatbot_ec.network.OrderApiService;
import hcmute.edu.vn.chatbot_ec.request.CreateOrderRequest;
import hcmute.edu.vn.chatbot_ec.request.PurchaseProductRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import hcmute.edu.vn.chatbot_ec.response.CartItemResponse;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private TextView productNameTextView, productPriceTextView, quantityTextView, totalPriceTextView, selectedAddressTextView;
    private MaterialCardView singleProductContainer, addressCard;
    private RecyclerView recyclerViewItems;
    private MaterialButton confirmPurchaseButton, addNewAddressButton;
    private ImageButton backButton;
    private ProgressBar progressBar;
    private AddressApiService addressApiService;
    private OrderApiService orderApiService;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productPrice;
    private List<CartItemResponse> cartItems;
    private List<AddressResponse> addresses;
    private AddressResponse selectedAddress;
    private boolean isCartCheckout;

    private static final int REQUEST_CODE_ADDRESS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize UI components
        singleProductContainer = findViewById(R.id.single_product_container);
        recyclerViewItems = findViewById(R.id.recycler_view_items);
        productNameTextView = findViewById(R.id.checkout_product_name);
        productPriceTextView = findViewById(R.id.checkout_product_price);
        quantityTextView = findViewById(R.id.checkout_quantity);
        totalPriceTextView = findViewById(R.id.checkout_total_price);
        selectedAddressTextView = findViewById(R.id.selected_address_text);
        addressCard = findViewById(R.id.address_card);
        confirmPurchaseButton = findViewById(R.id.button_confirm_purchase);
        addNewAddressButton = findViewById(R.id.button_add_new_address);
        backButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progress_bar);
        addressApiService = ApiClient.getAddressApiService();
        orderApiService = ApiClient.getOrderApiService();

        // Get data from Intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        isCartCheckout = intent.getBooleanExtra("is_cart_checkout", false);
        cartItems = intent.getParcelableArrayListExtra("cart_items");

        if (isCartCheckout && cartItems != null) {
            // Cart checkout: use RecyclerView
            singleProductContainer.setVisibility(View.GONE);
            recyclerViewItems.setVisibility(View.VISIBLE);
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            CheckoutAdapter adapter = new CheckoutAdapter(cartItems);
            recyclerViewItems.setAdapter(adapter);
            updateTotalPrice();
        } else {
            // Single product checkout
            singleProductContainer.setVisibility(View.VISIBLE);
            recyclerViewItems.setVisibility(View.GONE);
            productId = intent.getIntExtra("product_id", -1);
            quantity = intent.getIntExtra("quantity", 1);
            productName = intent.getStringExtra("product_name");
            productPrice = intent.getStringExtra("product_price");

            productNameTextView.setText(productName != null ? productName : "Không xác định");
            productPriceTextView.setText(productPrice != null ? productPrice + " VNĐ" : "0 VNĐ");
            quantityTextView.setText("Số lượng: " + quantity);
            updateTotalPrice();
        }

        // Handle Back button
        backButton.setOnClickListener(v -> finish());

        // Handle Address selection
        addressCard.setOnClickListener(v -> showAddressSelectionDialog());

        // Handle Add New Address button
        addNewAddressButton.setOnClickListener(v -> {
            Intent intentAddress = new Intent(this, AddressActivity.class);
            startActivityForResult(intentAddress, REQUEST_CODE_ADDRESS);
        });

        // Handle Confirm Purchase button
        confirmPurchaseButton.setOnClickListener(v -> {
            if (selectedAddress == null) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isCartCheckout) {
                createCartOrder(selectedAddress.getId());
            } else {
                purchaseSingleProduct(productId, quantity, selectedAddress.getId());
            }
        });

        // Fetch addresses
        fetchAddresses();
    }

    private void updateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        if (isCartCheckout && cartItems != null) {
            for (CartItemResponse item : cartItems) {
                if (item.getPrice() != null && item.getQuantity() > 0) {
                    total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                }
            }
        } else {
            try {
                String cleanPrice = productPrice != null ? productPrice.replaceAll("[^0-9]", "") : "0";
                double price = Double.parseDouble(cleanPrice);
                total = new BigDecimal(price * quantity);
            } catch (NumberFormatException e) {
                totalPriceTextView.setText("Tổng cộng: Không xác định");
                return;
            }
        }
        totalPriceTextView.setText(String.format("Tổng cộng: %s VNĐ", total.toString()));
    }

    private void fetchAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        confirmPurchaseButton.setEnabled(false);
        addressApiService.getAddressesByUserId(userId).enqueue(new Callback<List<AddressResponse>>() {
            @Override
            public void onResponse(Call<List<AddressResponse>> call, Response<List<AddressResponse>> response) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    addresses = response.body();
                    // Select the first address by default if none is selected
                    if (selectedAddress == null && !addresses.isEmpty()) {
                        selectedAddress = addresses.get(0);
                        updateSelectedAddressText();
                    }
                } else {
                    selectedAddress = null;
                    selectedAddressTextView.setText("Chưa có địa chỉ. Vui lòng thêm địa chỉ mới.");
                    Toast.makeText(CheckoutActivity.this, "Bạn chưa có địa chỉ. Vui lòng thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AddressResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                selectedAddressTextView.setText("Lỗi khi tải địa chỉ. Vui lòng thử lại.");
                Toast.makeText(CheckoutActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectedAddressText() {
        if (selectedAddress != null) {
            String addressString = selectedAddress.getRecipientName() != null ?
                    selectedAddress.getRecipientName() + " - " + selectedAddress.getFullAddress() + " - " + selectedAddress.getPhone() :
                    "Địa chỉ không xác định";
            selectedAddressTextView.setText(addressString);
        } else {
            selectedAddressTextView.setText("Chọn địa chỉ giao hàng");
        }
    }

    private void showAddressSelectionDialog() {
        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có địa chỉ. Vui lòng thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
            Intent intentAddress = new Intent(this, AddressActivity.class);
            startActivityForResult(intentAddress, REQUEST_CODE_ADDRESS);
            return;
        }

        AddressSelectionDialog dialog = new AddressSelectionDialog(addresses, selectedAddress, address -> {
            selectedAddress = address;
            updateSelectedAddressText();
        });
        dialog.show(getSupportFragmentManager(), "AddressSelectionDialog");
    }

    private void purchaseSingleProduct(int productId, int quantity, int addressId) {
        PurchaseProductRequest request = new PurchaseProductRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        request.setAddressId(addressId);

        progressBar.setVisibility(View.VISIBLE);
        confirmPurchaseButton.setEnabled(false);
        orderApiService.purchaseOrder(userId, request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CheckoutActivity.this, "Mua hàng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(CheckoutActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CheckoutActivity.this, "Không thể thực hiện mua hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCartOrder(int addressId) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(addressId);

        progressBar.setVisibility(View.VISIBLE);
        confirmPurchaseButton.setEnabled(false);
        orderApiService.createOrder(userId, request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CheckoutActivity.this, "Mua hàng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(CheckoutActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CheckoutActivity.this, "Không thể thực hiện mua hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                confirmPurchaseButton.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADDRESS && resultCode == RESULT_OK) {
            fetchAddresses(); // Refresh address list after adding/editing in AddressActivity
        }
    }

    // Address Selection Dialog
    public static class AddressSelectionDialog extends DialogFragment implements AddressAdapter.OnAddressActionListener {
        private List<AddressResponse> addresses;
        private AddressResponse selectedAddress;
        private OnAddressSelectedListener listener;

        public interface OnAddressSelectedListener {
            void onAddressSelected(AddressResponse address);
        }

        public AddressSelectionDialog(List<AddressResponse> addresses, AddressResponse selectedAddress, OnAddressSelectedListener listener) {
            this.addresses = addresses;
            this.selectedAddress = selectedAddress;
            this.listener = listener;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_address_selection, container, false);

            RecyclerView recyclerView = view.findViewById(R.id.recycler_view_addresses);
            TextView emptyTextView = view.findViewById(R.id.text_empty_addresses);
            MaterialButton addAddressButton = view.findViewById(R.id.button_add_address);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            AddressAdapter adapter = new AddressAdapter(addresses, this);
            recyclerView.setAdapter(adapter);

            if (addresses.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }

            addAddressButton.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddressActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);
                dismiss();
            });

            return view;
        }

        @Override
        public void onEdit(AddressResponse address) {
            Intent intent = new Intent(getContext(), AddressActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            dismiss();
        }

        @Override
        public void onDelete(AddressResponse address) {
            Intent intent = new Intent(getContext(), AddressActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            dismiss();
        }

        @Override
        public void onSelect(AddressResponse address) {
            listener.onAddressSelected(address);
            dismiss();
        }
    }
}