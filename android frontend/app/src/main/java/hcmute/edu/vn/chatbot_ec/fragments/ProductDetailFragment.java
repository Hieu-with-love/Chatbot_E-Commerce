package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import hcmute.edu.vn.chatbot_ec.MainActivity;
import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.ProductImageAdapter;
import hcmute.edu.vn.chatbot_ec.model.Product;

public class ProductDetailFragment extends Fragment {

    TextView productDetailName, productDetailCategory, productDetailPrice, productDetailStock, productDetailDescription;
    Button buttonAddToCart, buttonPurchase;

    ImageButton backButton;

    private ImageView imgMainPreview;
    private RecyclerView rvImageGallery;
    private boolean isShowingCustomImage = false;
    private String thumbnailUrl;
    private String currentShownUrl;
    Product product;

    public ProductDetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        imgMainPreview = view.findViewById(R.id.img_main_preview);
        rvImageGallery = view.findViewById(R.id.rv_image_gallery);

        productDetailName = view.findViewById(R.id.product_detail_name);
        productDetailCategory = view.findViewById(R.id.product_detail_category);
        productDetailPrice = view.findViewById(R.id.product_detail_price);
        productDetailStock = view.findViewById(R.id.product_detail_stock);
        productDetailDescription = view.findViewById(R.id.product_detail_description);

        buttonAddToCart = view.findViewById(R.id.button_add_to_cart);
        buttonPurchase = view.findViewById(R.id.button_purchase);

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        product = (Product) getArguments().getSerializable("product");
        setupProductDetails();

        return view;
    }

    private void setupProductDetails() {
        Bundle args = getArguments();
        if (args == null) return;

        String name = args.getString("product_name");
        String category = args.getString("product_category");
        String price = args.getString("product_price");
        String description = args.getString("product_description");
        String thumbnailUrl = args.getString("product_thumbnail_url");
        ArrayList<String> imageUrls = args.getStringArrayList("product_images");

        productDetailName.setText(name);
        productDetailCategory.setText(category);
        productDetailPrice.setText(price);
        productDetailDescription.setText(description);

        this.thumbnailUrl = thumbnailUrl;
        this.currentShownUrl = thumbnailUrl;

        Glide.with(requireContext())
                .load(thumbnailUrl)
                .into(imgMainPreview);

        // Setup image gallery
        ProductImageAdapter adapter = new ProductImageAdapter(imageUrls, url -> {
            if (!url.equals(currentShownUrl)) {
                Glide.with(requireContext())
                        .load(url)
                        .into(imgMainPreview);
                currentShownUrl = url;
                isShowingCustomImage = true;
            }
        });

        rvImageGallery.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvImageGallery.setAdapter(adapter);

        imgMainPreview.setOnClickListener(v -> {
            if (isShowingCustomImage) {
                Glide.with(requireContext())
                        .load(thumbnailUrl)
                        .into(imgMainPreview);
                isShowingCustomImage = false;
                currentShownUrl = thumbnailUrl;
            }
        });
    }

}