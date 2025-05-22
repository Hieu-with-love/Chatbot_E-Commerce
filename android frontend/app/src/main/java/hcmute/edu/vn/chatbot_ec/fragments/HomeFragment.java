package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ProductAdapter;
import hcmute.edu.vn.chatbot_ec.model.Product;
import hcmute.edu.vn.chatbot_ec.model.ProductImage;


public class HomeFragment extends Fragment {

    RecyclerView rvProducts;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvProducts = view.findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //Test data
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

        ProductAdapter adapter = new ProductAdapter(products, product -> {
            Bundle bundle = new Bundle();
            bundle.putString("product_name", product.getName());
            bundle.putString("product_category", product.getCategoryName());
            bundle.putString("product_price", product.getPrice().toString());
            bundle.putString("product_description", product.getDescription());
            bundle.putString("product_thumbnail_url", product.getThumbnailUrl());

            // Convert List<ProductImage> to List<String>
            ArrayList<String> imageUrls = new ArrayList<>();
            for (ProductImage image : product.getProductImages()) {
                imageUrls.add(image.getUrl());
            }

            bundle.putStringArrayList("product_images", imageUrls);
            bundle.putString("product_id", String.valueOf(product.getId()));


            ProductDetailFragment detailFragment = new ProductDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Để có thể quay lại
                    .commit();
        });
        rvProducts.setAdapter(adapter);

        return view;
    }
}