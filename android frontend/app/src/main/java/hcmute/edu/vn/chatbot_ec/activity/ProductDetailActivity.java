package hcmute.edu.vn.chatbot_ec.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Consumer;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ProductImageAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.CartApiService;
import hcmute.edu.vn.chatbot_ec.network.ProductApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.request.AddCartItemRequest;
import hcmute.edu.vn.chatbot_ec.response.CartResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductImageResponse;
import hcmute.edu.vn.chatbot_ec.response.ProductResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
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
    private BigDecimal productPrice;
    private String productColor;
    private String productSize;
    private CartApiService cartApiService;
    private UserApiService userApiService;
    private ProductApiService productApiService;
    private Integer userId;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0"); // For formatting price

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_detail);

        // Initialize UI components
        viewPagerImages = findViewById(R.id.view_pager_images);
        productDetailName = findViewById(R.id.product_detail_name);
        productDetailCategory = findViewById(R.id.product_detail_category);
        productDetailPrice = findViewById(R.id.product_detail_price);
        productDetailStock = findViewById(R.id.product_detail_stock);
        productDetailDescription = findViewById(R.id.product_detail_description);
        productDetailColor = findViewById(R.id.product_detail_color);
        productDetailSize = findViewById(R.id.product_detail_size);
        buttonAddToCart = findViewById(R.id.button_add_to_cart);
        buttonPurchase = findViewById(R.id.button_purchase);
        backButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progress_bar);
        cartApiService = ApiClient.getCartApiService();
        userApiService = ApiClient.getUserApiService();
        productApiService = ApiClient.getProductApiService();

        // Handle Back button
        backButton.setOnClickListener(v -> finish());

        // Get product ID from Intent
        productId = getIntent().getIntExtra("product_id", -1);
        Log.d("ProductDetailActivity", "Received product ID: " + productId);
        if (productId == -1) {
            Toast.makeText(this, "Không có ID sản phẩm", Toast.LENGTH_SHORT).show();
            productDetailName.setText("Không có dữ liệu sản phẩm");
            buttonAddToCart.setEnabled(false);
            buttonPurchase.setEnabled(false);
            return;
        }

        // Fetch userId and product details
        fetchUserIdAndProductDetails();
    }

    private void fetchUserIdAndProductDetails() {
        String token = TokenManager.getToken(this);
        if (token == null) {
            Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
            productDetailName.setText("Vui lòng đăng nhập");
            buttonAddToCart.setEnabled(false);
            buttonPurchase.setEnabled(false);
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Fetch user info
        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().getUserId();
                    fetchProductDetails();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductDetailActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    productDetailName.setText("Không thể lấy thông tin người dùng");
                    buttonAddToCart.setEnabled(false);
                    buttonPurchase.setEnabled(false);
                    showRetryDialogUser();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                productDetailName.setText(R.string.error_network);
                buttonAddToCart.setEnabled(false);
                buttonPurchase.setEnabled(false);
                showRetryDialogUser();
            }
        });
    }

    private void fetchProductDetails() {
        productApiService.getProductById(productId).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse product = response.body();
                    setupProductDetails(product);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể lấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                    productDetailName.setText("Không có dữ liệu sản phẩm");
                    buttonAddToCart.setEnabled(false);
                    buttonPurchase.setEnabled(false);
                    showRetryDialogProduct();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                productDetailName.setText(R.string.error_network);
                buttonAddToCart.setEnabled(false);
                buttonPurchase.setEnabled(false);
                showRetryDialogProduct();
            }
        });
    }

    private void showRetryDialogUser() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.network_error_retry)
                .setPositiveButton(R.string.retry, (dialog, which) -> fetchUserIdAndProductDetails())
                .setNegativeButton(R.string.button_cancel, (dialog, which) -> finish())
                .show();
    }

    private void showRetryDialogProduct() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.network_error_retry)
                .setPositiveButton(R.string.retry, (dialog, which) -> fetchProductDetails())
                .setNegativeButton(R.string.button_cancel, (dialog, which) -> finish())
                .show();
    }

    private void setupProductDetails(ProductResponse product) {
        productName = product.getName();
        String description = product.getDescription();
        productPrice = product.getPrice();
        thumbnailUrl = product.getThumbnailUrl();
        Integer categoryId = product.getCategoryId();
        stock = product.getStock() != null ? product.getStock() : 0;
        productColor = product.getColor();
        productSize = product.getSize();
        ArrayList<String> imageUrls = new ArrayList<>();
        if (product.getProductImages() != null) {
            for (ProductImageResponse image : product.getProductImages()) {
                imageUrls.add(image.getImageUrl());
            }
        }

        productDetailName.setText(productName != null ? productName : "Không xác định");
        productDetailCategory.setText(categoryId != null ? "Danh mục: " + categoryId : "Không xác định");
        productDetailPrice.setText(productPrice != null ? decimalFormat.format(productPrice) + " VNĐ" : "0 VNĐ");
        productDetailStock.setText(stock != null ? "Tồn kho: " + stock : "Hết hàng");
        productDetailDescription.setText(description != null ? description : "Không có mô tả");
        productDetailColor.setText(productColor != null ? "Màu sắc: " + productColor : "Màu sắc: Không xác định");
        productDetailSize.setText(productSize != null ? "Kích thước: " + productSize : "Kích thước: Không xác định");

        // Disable buttons if out of stock
        boolean isInStock = stock != null && stock > 0;
        buttonAddToCart.setEnabled(isInStock && userId != null);
        buttonPurchase.setEnabled(isInStock && userId != null);

        // Setup ViewPager2 for product images
        if (!imageUrls.isEmpty()) {
            imageUrls.add(0, thumbnailUrl);
            ProductImageAdapter adapter = new ProductImageAdapter(imageUrls);
            viewPagerImages.setAdapter(adapter);
        } else {
            ArrayList<String> defaultImages = new ArrayList<>();
            defaultImages.add(thumbnailUrl != null ? thumbnailUrl : "");
            ProductImageAdapter adapter = new ProductImageAdapter(defaultImages);
            viewPagerImages.setAdapter(adapter);
        }

        // Handle Add to Cart button
        buttonAddToCart.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isInStock) {
                Toast.makeText(this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            addToCart(productId, 1);
        });

        // Handle Purchase button
        buttonPurchase.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isInStock) {
                Toast.makeText(this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            showQuantityDialog(stock, quantity -> {
                Toast.makeText(this, "Đã chọn " + quantity + " sản phẩm", Toast.LENGTH_SHORT).show();
                navigateToCheckout(quantity);
            });
        });
    }

    private void showQuantityDialog(int maxQuantity, Consumer<Integer> onQuantitySelected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn số lượng");

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_quantity_picker, null);
        builder.setView(dialogView);

        TextView productInfo = dialogView.findViewById(R.id.product_info);
        TextView priceInfo = dialogView.findViewById(R.id.price_info);
        TextView stockInfo = dialogView.findViewById(R.id.stock_info);
        NumberPicker picker = dialogView.findViewById(R.id.quantity_picker);

        productInfo.setText("Sản phẩm: " + (productName != null ? productName : "Không xác định"));
        priceInfo.setText(productPrice != null ? "Giá: " + decimalFormat.format(productPrice) + " VNĐ" : "0 VNĐ");
        priceInfo.setTextColor(getResources().getColor(R.color.text_accent));
        stockInfo.setText("Tồn kho: " + maxQuantity + " sản phẩm");
        picker.setMinValue(1);
        picker.setMaxValue(maxQuantity);
        picker.setValue(1);
        picker.setWrapSelectorWheel(false);

        picker.setOnValueChangedListener((picker1, oldVal, newVal) -> {
            try {
                if (productPrice != null) {
                    BigDecimal totalPrice = productPrice.multiply(new BigDecimal(newVal));
                    priceInfo.setText("Tổng: " + decimalFormat.format(totalPrice) + " VNĐ");
                } else {
                    priceInfo.setText("Giá: Không xác định");
                }
            } catch (Exception e) {
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void navigateToCheckout(int quantity) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("product_id", productId);
        intent.putExtra("quantity", quantity);
        intent.putExtra("user_id", userId);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_price", productPrice != null ? productPrice.toString() : "0");
        startActivity(intent);
        Toast.makeText(this, "Chuyển đến màn hình thanh toán", Toast.LENGTH_SHORT).show();
    }

    private void addToCart(int productId, int quantity) {
        if (userId == null) return;
        AddCartItemRequest request = new AddCartItemRequest(productId, quantity);
        cartApiService.addItemToCart(userId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}