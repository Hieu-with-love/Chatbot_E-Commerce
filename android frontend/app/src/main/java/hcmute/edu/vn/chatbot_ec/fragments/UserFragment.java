package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.UserDetailResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.service.AuthenticationService;
import hcmute.edu.vn.chatbot_ec.utils.AuthUtils;
import hcmute.edu.vn.chatbot_ec.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";

    private ImageView imageAvatar;
    private TextView textFullName;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textVerifiedStatus;
    private MaterialButton buttonEditProfile;
    private ExtendedFloatingActionButton fabAddress;
    private ExtendedFloatingActionButton fabOrderHistory;
    private MaterialButton buttonLogout;      private ProgressBar progressBar;
    private UserApiService userApiService;
    
    // Broadcast receiver for logout events
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {        @Override
        public void onReceive(Context context, Intent intent) {
            if (AuthenticationService.ACTION_USER_LOGOUT.equals(intent.getAction())) {
                handleLogoutBroadcast();
            }
        }
    };

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
        fabOrderHistory = view.findViewById(R.id.fab_order_history);        buttonLogout = view.findViewById(R.id.button_logout);
        progressBar = view.findViewById(R.id.progress_bar);
        userApiService = ApiClient.getUserApiService();

        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        hideUserInfo();        // Check if user is authenticated and load user info
        if (!AuthUtils.validateAndHandleToken(getContext(), true)) {
            progressBar.setVisibility(View.GONE);
            showNotLoggedInState();
            return view;
        }

        // Get user info directly from SessionManager
        SessionManager.UserInfo userInfo = SessionManager.getUserInfo(getContext());
        if (userInfo != null) {
            progressBar.setVisibility(View.GONE);
            displayUserInfoFromSession(userInfo);
        } else {
            // Fallback: If no user info in session, try to fetch it
            // This should rarely happen if authentication flow is correct
            fetchUserFromAPI();
        }

        // Handle button clicks
        buttonEditProfile.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Chuyển đến màn hình chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to EditProfileFragment
            }
        });

        fabAddress.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Fragment addressFragment = new AddressFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, addressFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getContext(), "Chuyển đến màn hình địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            }
        });

        fabOrderHistory.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                Fragment orderFragment = new OrderFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, orderFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getContext(), "Chuyển đến màn hình lịch sử mua hàng", Toast.LENGTH_SHORT).show();
            }
        });        // Handle logout button click
        buttonLogout.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                // Use enhanced logout with broadcast
                AuthUtils.logoutWithBroadcast(getContext(), true, "User initiated logout");
            }        });

        return view;
    }
      private void fetchUserFromAPI() {
        // Fallback method to fetch user info from API if not available in session
        String token = SessionManager.getToken(getContext());
        if (token == null) {
            progressBar.setVisibility(View.GONE);
            showNotLoggedInState();
            return;
        }

        userApiService.getMe(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        Integer userId = response.body().getUserId();
                        fetchUserDetails(userId);
                    } else if (response.code() == 401) {
                        // Handle unauthorized response
                        AuthUtils.handleUnauthorizedResponse(getContext());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        showNotLoggedInState();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                    showNotLoggedInState();
                }
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();        // Register for logout broadcasts
        if (getContext() != null) {
            IntentFilter filter = new IntentFilter(AuthenticationService.ACTION_USER_LOGOUT);
            ContextCompat.registerReceiver(getContext(), logoutReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        // Unregister broadcast receiver
        if (getContext() != null) {
            try {
                getContext().unregisterReceiver(logoutReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver was not registered
            }
        }
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
                    } else if (response.code() == 401) {
                        // Handle unauthorized response
                        AuthUtils.handleUnauthorizedResponse(getContext());
                        showNotLoggedInState();
                    } else {
                        Toast.makeText(getContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                        showNotLoggedInState();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    showNotLoggedInState();
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
        fabAddress.setVisibility(View.VISIBLE);        fabOrderHistory.setVisibility(View.VISIBLE);
        buttonLogout.setVisibility(View.VISIBLE);
    }

    private void displayUserInfoFromSession(SessionManager.UserInfo userInfo) {
        textFullName.setText(userInfo.fullName);
        textEmail.setText(getString(R.string.email_label, userInfo.email));
        
//        // For phone, we might not have it in SessionManager, so handle gracefully
//        if (userInfo.phone != null && !userInfo.phone.isEmpty()) {
//            textPhone.setText(getString(R.string.phone_label, userInfo.phone));
//        } else {
//            textPhone.setText(getString(R.string.phone_label, "Chưa cập nhật"));
//        }
        
        // For verified status, we might not have it in SessionManager
        textVerifiedStatus.setText("Đã xác thực"); // Default to verified for now
        textVerifiedStatus.setSelected(true);
        
        imageAvatar.setContentDescription(getString(R.string.avatar_description, userInfo.fullName));

        // Load default avatar since we don't have avatar URL in SessionManager
        Glide.with(this)
                .load(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(imageAvatar);

        // Show user info views
        textFullName.setVisibility(View.VISIBLE);
        textEmail.setVisibility(View.VISIBLE);
        textPhone.setVisibility(View.VISIBLE);
        textVerifiedStatus.setVisibility(View.VISIBLE);
        buttonEditProfile.setVisibility(View.VISIBLE);
        fabAddress.setVisibility(View.VISIBLE);
        fabOrderHistory.setVisibility(View.VISIBLE);
        buttonLogout.setVisibility(View.VISIBLE);
    }

    private void hideUserInfo() {
        textFullName.setVisibility(View.GONE);
        textEmail.setVisibility(View.GONE);
        textPhone.setVisibility(View.GONE);
        textVerifiedStatus.setVisibility(View.GONE);
        buttonEditProfile.setVisibility(View.GONE);
        fabAddress.setVisibility(View.GONE);
        fabOrderHistory.setVisibility(View.GONE);
        buttonLogout.setVisibility(View.GONE);
    }

    private void showNotLoggedInState() {
        hideUserInfo();
        textFullName.setVisibility(View.VISIBLE);
        textFullName.setText("Chưa đăng nhập");
        textEmail.setVisibility(View.VISIBLE);
        textEmail.setText("Vui lòng đăng nhập để xem thông tin cá nhân");
        
        // Show default avatar
        Glide.with(this)
                .load(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(imageAvatar);
        
        // Update button text to show login option
        buttonEditProfile.setVisibility(View.VISIBLE);
        buttonEditProfile.setText("Đăng nhập");
        buttonEditProfile.setOnClickListener(v -> {
            if (isAdded() && getContext() != null) {
                // Navigate to login - you can implement this based on your navigation structure
                Toast.makeText(getContext(), "Chuyển đến màn hình đăng nhập", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to LoginActivity
            }
        });
    }

    private void handleLogoutBroadcast() {
        if (isAdded() && getContext() != null) {
            progressBar.setVisibility(View.GONE);
            showNotLoggedInState();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }
}