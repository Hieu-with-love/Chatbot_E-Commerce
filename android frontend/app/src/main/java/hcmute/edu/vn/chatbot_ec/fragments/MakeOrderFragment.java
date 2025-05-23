package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.CartAdapter;
import hcmute.edu.vn.chatbot_ec.model.CartItem;

public class MakeOrderFragment extends Fragment {

    public MakeOrderFragment() {
        // Required empty public constructor
    }
    private static final String ARG_SELECTED = "selected_items";

    private RecyclerView orderItemsRecyclerView;
    private List<CartItem> orderItems;
    Button orderButton;

    MaterialToolbar toolbar;
    // Factory method to create OrderFragment with args
    public static OrderFragment newInstance(ArrayList<CartItem> selectedItems) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SELECTED, selectedItems);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_make_order, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SELECTED)) {
            Serializable ser = args.getSerializable(ARG_SELECTED);
            //noinspection unchecked
            orderItems = (ArrayList<CartItem>) ser;
        } else {
            orderItems = new ArrayList<>();
        }

        toolbar = view.findViewById(R.id.toolbar);

        orderButton = view.findViewById(R.id.placeOrderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment successFragment = new OrderSuccessFragment();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, successFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        orderItemsRecyclerView = view.findViewById(R.id.orderItemsRecyclerView);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        CartAdapter adapter = new CartAdapter(orderItems,null);
        orderItemsRecyclerView.setAdapter(adapter);
    }
}