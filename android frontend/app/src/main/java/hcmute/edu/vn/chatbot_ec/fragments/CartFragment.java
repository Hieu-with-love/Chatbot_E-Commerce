package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.CartAdapter;
import hcmute.edu.vn.chatbot_ec.model.CartItem;
import hcmute.edu.vn.chatbot_ec.model.Product;

public class CartFragment extends Fragment {

    RecyclerView rvCartItems;
    TextView tvTotalPrice;
    Button buttonCheckout;

    public CartFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCartItems = view.findViewById(R.id.rv_cart_items);
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        buttonCheckout = view.findViewById(R.id.button_checkout);

        //test data
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Wireless Headphones", "Audio", new BigDecimal("99.99"), null, "Clothes"));
        products.add(new Product(2, "Smart Watch", "Wearables", new BigDecimal("129.99"), null, "General"));
        products.add(new Product(3, "Bluetooth Speaker", "Audio", new BigDecimal("49.99"), null, "Electronics"));
        products.add(new Product(4, "Running Shoes", "Footwear", new BigDecimal("89.99"), null, "Clothes"));
        products.add(new Product(5, "Laptop Backpack", "Accessories", new BigDecimal("59.99"), null, "General"));
        products.add(new Product(6, "Fitness Tracker", "Wearables", new BigDecimal("79.99"), null, "General"));
        products.add(new Product(7, "LED Desk Lamp", "Lighting", new BigDecimal("39.99"), null, "Home"));
        products.add(new Product(8, "Gaming Mouse", "Peripherals", new BigDecimal("29.99"), null, "Electronics"));
        products.add(new Product(9, "Noise Cancelling Earbuds", "Audio", new BigDecimal("109.99"), null, "Electronics"));
        products.add(new Product(10, "Cotton Hoodie", "Apparel", new BigDecimal("45.99"), null, "Clothes"));

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(1, products.get(0), 2));
        cartItems.add(new CartItem(2, products.get(6), 1));
        cartItems.add(new CartItem(3, products.get(4), 3));
        cartItems.add(new CartItem(4, products.get(9), 1));
        cartItems.add(new CartItem(5, products.get(3), 2));

        CartAdapter adapter = new CartAdapter(cartItems);
        rvCartItems.setAdapter(adapter);

        // Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        String totalPriceStr = "Total: $" + totalPrice.toString();
        tvTotalPrice.setText(totalPriceStr);

        return view;
    }
}