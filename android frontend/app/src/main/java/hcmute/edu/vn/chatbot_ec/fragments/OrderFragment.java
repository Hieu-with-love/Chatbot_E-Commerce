package hcmute.edu.vn.chatbot_ec.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.OrderAdapter;
import hcmute.edu.vn.chatbot_ec.network.ApiClient;
import hcmute.edu.vn.chatbot_ec.network.OrderApiService;
import hcmute.edu.vn.chatbot_ec.network.UserApiService;
import hcmute.edu.vn.chatbot_ec.response.OrderResponse;
import hcmute.edu.vn.chatbot_ec.response.UserResponse;
import hcmute.edu.vn.chatbot_ec.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private TextView textEmptyOrders;
    private ProgressBar progressBar;
    private OrderAdapter orderAdapter;
    private List<OrderResponse> orders;
    private UserApiService userApiService;
    private OrderApiService orderApiService;
    private Integer userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Initialize views
        recyclerViewOrders = view.findViewById(R.id.recycler_view_orders);
        textEmptyOrders = view.findViewById(R.id.text_empty_orders);
        progressBar = view.findViewById(R.id.progress_bar);
        userApiService = ApiClient.getUserApiService();
        orderApiService = ApiClient.getOrderApiService();
        orders = new ArrayList<>();

        // Initialize RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter();
        recyclerViewOrders.setAdapter(orderAdapter);
        recyclerViewOrders.setContentDescription(getString(R.string.orders_list_description));

        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);
        textEmptyOrders.setVisibility(View.GONE);

        // Check token and fetch userId
        String token = TokenManager.getToken(getContext());
        if (token == null) {
            if (isAdded() && getContext() != null) {
                progressBar.setVisibility(View.GONE);
                textEmptyOrders.setVisibility(View.VISIBLE);
                textEmptyOrders.setText(R.string.please_login);
                Toast.makeText(getContext(), R.string.please_login, Toast.LENGTH_SHORT).show();
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
                        fetchOrders(userId);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        textEmptyOrders.setVisibility(View.VISIBLE);
                        textEmptyOrders.setText(R.string.error_user_info);
                        Toast.makeText(getContext(), R.string.error_user_info, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    textEmptyOrders.setVisibility(View.VISIBLE);
                    textEmptyOrders.setText(R.string.connection_error);
                    Toast.makeText(getContext(), getString(R.string.connection_error_message, t.getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void fetchOrders(Integer userId) {
        if (userId == null || !isAdded()) return;
        orderApiService.getOrdersByUserId(userId).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        orders.clear();
                        orders.addAll(response.body());
                        orderAdapter.submitList(orders);
                        updateOrderVisibility();
                    } else {
                        textEmptyOrders.setVisibility(View.VISIBLE);
                        textEmptyOrders.setText(R.string.error_load_orders);
                        Toast.makeText(getContext(), R.string.error_load_orders, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    progressBar.setVisibility(View.GONE);
                    textEmptyOrders.setVisibility(View.VISIBLE);
                    textEmptyOrders.setText(R.string.connection_error);
                    Toast.makeText(getContext(), getString(R.string.connection_error_message, t.getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateOrderVisibility() {
        if (orders.isEmpty()) {
            recyclerViewOrders.setVisibility(View.GONE);
            textEmptyOrders.setVisibility(View.VISIBLE);
            textEmptyOrders.setText(R.string.no_orders);
        } else {
            recyclerViewOrders.setVisibility(View.VISIBLE);
            textEmptyOrders.setVisibility(View.GONE);
        }
    }
}