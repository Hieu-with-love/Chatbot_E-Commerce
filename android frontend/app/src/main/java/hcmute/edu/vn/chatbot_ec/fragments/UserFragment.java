package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.activity.AddressActivity;
import hcmute.edu.vn.chatbot_ec.activity.OrderActivity;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private ImageView imageAvatar;
    private TextView textFullName;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textVerifiedStatus;
    private MaterialButton buttonEditProfile;
    private ExtendedFloatingActionButton fabAddress;
    private ExtendedFloatingActionButton fabOrderHistory;
    private ProgressBar progressBar;
    private UserApiService userApiService;
    private Integer userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize views
        imageAvatar = view.findViewById(R.id.image_avatar);
        textFullName = view.findViewById(R.id.text_full_name);
        textEmail = view.findViewById(R.id.text_email);
        textPhone = view.findViewById(R.id.text_phone);
        textVerifiedStatus = view.findViewById(R.id.profile_verified_status);
        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        fabAddress = view.findViewById(R.id.fab_address);
        fabOrderHistory = view.findViewById(R.id.fab_order_history);
        progressBar = view.findViewById(R.id.progress_bar);
        userApiService = ApiClient.getUserApiService();

        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        hideUserInfo();

        // Check token and fetch userId
        String token = TokenManager.getToken(getContext());
        if (token == null) {
            if (isAdded() && getContext() != null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem thông tin", Toast.LENGTH_SHORT).show();
                // Optionally redirect to login screen
                // Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                // startActivity(loginIntent);
                // getActivity().finish();
            }
            return view;
        }

        // Fetch userId
        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        userId = response.body().getUserId();
                        fetchUserDetails(userId);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle button clicks
        buttonEditProfile.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Chuyển đến màn hình chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to EditProfileFragment
            }
        });

        fabAddress.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Intent intent = new Intent(getContext(), AddressActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Chuyển đến màn hình địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            }
        });

        fabOrderHistory.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Chuyển đến màn hình lịch sử mua hàng", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchUserDetails(Integer userId) {
        if (userId == null || !isAdded()) return;
        userApiService.getUserById(userId).enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        UserDetailResponse user = response.body();
                        displayUserInfo(user);
                    } else {
                        Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUserInfo(UserDetailResponse user) {
        textFullName.setText(user.getFullName());
        textEmail.setText(getString(R.string.email_label, user.getEmail()));
        textPhone.setText(getString(R.string.phone_label, user.getPhone()));
        textVerifiedStatus.setText(user.isVerified() ? R.string.verified : R.string.unverified);
        textVerifiedStatus.setSelected(user.isVerified());
        imageAvatar.setContentDescription(getString(R.string.avatar_description, user.getFullName()));

        // Load avatar with Glide
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(imageAvatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(imageAvatar);
        }

        // Show user info views
        textFullName.setVisibility(View.VISIBLE);
        textEmail.setVisibility(View.VISIBLE);
        textPhone.setVisibility(View.VISIBLE);
        textVerifiedStatus.setVisibility(View.VISIBLE);
        buttonEditProfile.setVisibility(View.VISIBLE);
        fabAddress.setVisibility(View.VISIBLE);
        fabOrderHistory.setVisibility(View.VISIBLE);
    }

    private void hideUserInfo() {
        textFullName.setVisibility(View.GONE);
        textEmail.setVisibility(View.GONE);
        textPhone.setVisibility(View.GONE);
        textVerifiedStatus.setVisibility(View.GONE);
        buttonEditProfile.setVisibility(View.GONE);
        fabAddress.setVisibility(View.GONE);
        fabOrderHistory.setVisibility(View.GONE);
    }
}