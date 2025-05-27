package hcmute.edu.vn.chatbot_ec.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ProductImageAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.AddressApiService;
import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.network.OrderApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.request.PurchaseProductRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private TextView productDetailName, productDetailCategory, productDetailPrice, productDetailStock,
            productDetailDescription, productDetailColor, productDetailSize;
    private MaterialButton buttonAddToCart, buttonPurchase;
    private ImageButton backButton;
    private ViewPager2 viewPagerImages;
    private ProgressBar progressBar;
    private String thumbnailUrl;
    private Integer productId;
    private Integer stock;
    private String productName;
    private String productPrice;
    private String productColor;
    private String productSize;
    private CartApiService cartApiService;
    private OrderApiService orderApiService;
    private UserApiService userApiService;
    private AddressApiService addressApiService;
    private Integer userId;

    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Khởi tạo các thành phần giao diện
        viewPagerImages = view.findViewById(R.id.view_pager_images);
        productDetailName = view.findViewById(R.id.product_detail_name);
        productDetailCategory = view.findViewById(R.id.product_detail_category);
        productDetailPrice = view.findViewById(R.id.product_detail_price);
        productDetailStock = view.findViewById(R.id.product_detail_stock);
        productDetailDescription = view.findViewById(R.id.product_detail_description);
        productDetailColor = view.findViewById(R.id.product_detail_color);
        productDetailSize = view.findViewById(R.id.product_detail_size);
        buttonAddToCart = view.findViewById(R.id.button_add_to_cart);
        buttonPurchase = view.findViewById(R.id.button_purchase);
        backButton = view.findViewById(R.id.back_button);
        progressBar = view.findViewById(R.id.progress_bar);
        cartApiService = ApiClient.getCartApiService();
        orderApiService = ApiClient.getOrderApiService();
        userApiService = ApiClient.getUserApiService();
        addressApiService = ApiClient.getAddressApiService();

        // Xử lý nút Back
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Lấy userId và thiết lập chi tiết sản phẩm
        fetchUserIdAndSetupProduct();

        return view;
    }

    private void fetchUserIdAndSetupProduct() {
        String token = TokenManager.getToken(getContext());
        if (token == null) {
            if (isAdded()) {
                Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                productDetailName.setText("Vui lòng đăng nhập");
                buttonAddToCart.setEnabled(false);
                buttonPurchase.setEnabled(false);
            }
            return;
        }

        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        userId = response.body().getUserId();
                        setupProductDetails();
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        productDetailName.setText("Không thể lấy thông tin người dùng");
                        buttonAddToCart.setEnabled(false);
                        buttonPurchase.setEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    productDetailName.setText(R.string.error_network);
                    buttonAddToCart.setEnabled(false);
                    buttonPurchase.setEnabled(false);
                }
            }
        });
    }

    private void setupProductDetails() {
        Bundle args = getArguments();
        if (args == null) {
            productDetailName.setText("Không có dữ liệu sản phẩm");
            buttonAddToCart.setEnabled(false);
            buttonPurchase.setEnabled(false);
            return;
        }

        productId = args.getInt("product_id", -1);
        productName = args.getString("product_name");
        String description = args.getString("product_description");
        productPrice = args.getString("product_price");
        thumbnailUrl = args.getString("product_thumbnail_url");
        int categoryId = args.getInt("product_category_id", -1);
        stock = args.getInt("product_stock", 0);
        productColor = args.getString("product_color");
        productSize = args.getString("product_size");
        ArrayList<String> imageUrls = args.getStringArrayList("product_images");

        productDetailName.setText(productName != null ? productName : "Không xác định");
        productDetailCategory.setText(categoryId != -1 ? "Danh mục: " + categoryId : "Không xác định");
        productDetailPrice.setText(productPrice != null ? productPrice + " VNĐ" : "0 VNĐ");
        productDetailStock.setText(stock != null ? "Tồn kho: " + stock : "Hết hàng");
        productDetailDescription.setText(description != null ? description : "Không có mô tả");
        productDetailColor.setText(productColor != null ? "Màu sắc: " + productColor : "Màu sắc: Không xác định");
        productDetailSize.setText(productSize != null ? "Kích thước: " + productSize : "Kích thước: Không xác định");

        // Vô hiệu hóa nút nếu hết hàng
        boolean isInStock = stock != null && stock > 0;
        buttonAddToCart.setEnabled(isInStock && userId != null);
        buttonPurchase.setEnabled(isInStock && userId != null);

        // Thiết lập ViewPager2 cho ảnh sản phẩm
        if (imageUrls != null) {
            imageUrls.add(0, thumbnailUrl); // Thêm thumbnail vào đầu danh sách
            ProductImageAdapter adapter = new ProductImageAdapter(imageUrls);
            viewPagerImages.setAdapter(adapter);
        } else {
            ArrayList<String> defaultImages = new ArrayList<>();
            defaultImages.add(thumbnailUrl != null ? thumbnailUrl : "");
            ProductImageAdapter adapter = new ProductImageAdapter(defaultImages);
            viewPagerImages.setAdapter(adapter);
        }

        // Xử lý nút Thêm vào giỏ hàng
        buttonAddToCart.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isInStock) {
                Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            addToCart(productId, 1);
        });

        // Xử lý nút Mua ngay
        buttonPurchase.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isInStock) {
                Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            showQuantityDialog(stock, quantity -> {
                Toast.makeText(getContext(), "Đã chọn " + quantity + " sản phẩm", Toast.LENGTH_SHORT).show();
                fetchAndSelectAddress(quantity);
            });
        });
    }

    private void showQuantityDialog(int maxQuantity, Consumer<Integer> onQuantitySelected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn số lượng");

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_quantity_picker, null);
        builder.setView(dialogView);

        TextView productInfo = dialogView.findViewById(R.id.product_info);
        TextView priceInfo = dialogView.findViewById(R.id.price_info);
        TextView stockInfo = dialogView.findViewById(R.id.stock_info);
        NumberPicker picker = dialogView.findViewById(R.id.quantity_picker);

        productInfo.setText("Sản phẩm: " + (productName != null ? productName : "Không xác định"));
        priceInfo.setText("Giá: " + (productPrice != null ? productPrice + " VNĐ" : "0 VNĐ"));
        priceInfo.setTextColor(requireContext().getResources().getColor(R.color.text_accent));
        stockInfo.setText("Tồn kho: " + maxQuantity + " sản phẩm");
        picker.setMinValue(1);
        picker.setMaxValue(maxQuantity);
        picker.setValue(1);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener((picker1, oldVal, newVal) -> {
            try {
                String cleanPrice = productPrice != null ? productPrice.replaceAll("[^0-9]", "") : "0";
                double price = Double.parseDouble(cleanPrice);
                priceInfo.setText("Tổng: " + (price * newVal) + " VNĐ");
            } catch (NumberFormatException e) {
                priceInfo.setText("Giá: Không xác định");
            }
        });

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            int selectedQuantity = picker.getValue();
            onQuantitySelected.accept(selectedQuantity);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(R.color.accent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(R.color.text_secondary));
    }

    private void fetchAndSelectAddress(int quantity) {
        progressBar.setVisibility(View.VISIBLE);
        buttonPurchase.setEnabled(false);
        addressApiService.getAddressesByUserId(userId).enqueue(new Callback<List<AddressResponse>>() {
            @Override
            public void onResponse(Call<List<AddressResponse>> call, Response<List<AddressResponse>> response) {
                progressBar.setVisibility(View.GONE);
                buttonPurchase.setEnabled(true);
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        showAddressSelectionDialog(response.body(), quantity);
                    } else {
                        Toast.makeText(getContext(), "Bạn chưa có địa chỉ. Vui lòng thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                        if (isAdded() && getContext() != null) {
                            Fragment addressFragment = new AddressFragment();
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, addressFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            Toast.makeText(getContext(), "Chuyển đến màn hình địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AddressResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonPurchase.setEnabled(true);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAddressSelectionDialog(List<AddressResponse> addresses, int quantity) {
        String[] addressStrings = new String[addresses.size()];
        for (int i = 0; i < addresses.size(); i++) {
            AddressResponse address = addresses.get(i);
            addressStrings[i] = address.getFullAddress() != null ?
                    address.getRecipientName() + " - " + address.getFullAddress() + " - " + address.getPhone() :
                    "Địa chỉ " + (i + 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn địa chỉ giao hàng");
        builder.setItems(addressStrings, (dialog, which) -> {
            int selectedAddressId = addresses.get(which).getId();
            purchaseNow(productId, quantity, selectedAddressId);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Thêm địa chỉ mới", (dialog, which) -> {
            if (isAdded() && getContext() != null) {
                Fragment addressFragment = new AddressFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addressFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getContext(), "Chuyển đến màn hình địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void purchaseNow(int productId, int quantity, int addressId) {
        PurchaseProductRequest request = new PurchaseProductRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        request.setAddressId(addressId);

        progressBar.setVisibility(View.VISIBLE);
        buttonPurchase.setEnabled(false);
        orderApiService.purchaseOrder(userId, request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                progressBar.setVisibility(View.GONE);
                buttonPurchase.setEnabled(true);
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "Mua hàng thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(getContext(), "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Không thể thực hiện mua hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonPurchase.setEnabled(true);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToCart(int productId, int quantity) {
        if (userId == -1) return;
        AddCartItemRequest request = new AddCartItemRequest(productId, quantity);
        cartApiService.addItemToCart(userId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}