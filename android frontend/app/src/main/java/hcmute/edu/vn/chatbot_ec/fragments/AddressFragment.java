package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.adapter.AddressAdapter;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;

public class AddressFragment extends Fragment {

    private RecyclerView recyclerViewAddresses;
    private TextView textEmptyAddresses;
    private AddressAdapter addressAdapter;
    private List<AddressResponse> addresses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        // Initialize views
        recyclerViewAddresses = view.findViewById(R.id.recycler_view_addresses);
        textEmptyAddresses = view.findViewById(R.id.text_empty_addresses);

        // Initialize data and adapter
        addresses = createFakeAddresses();
        addressAdapter = new AddressAdapter(addresses);

        // Setup RecyclerView
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddresses.setAdapter(addressAdapter);

        // Update visibility
        updateAddressVisibility();

        return view;
    }

    private List<AddressResponse> createFakeAddresses() {
        List<AddressResponse> fakeAddresses = new ArrayList<>();

        // Fake Address 1
        AddressResponse address1 = new AddressResponse();
        address1.setId(1);
        address1.setRecipientName("Nguyễn Văn A");
        address1.setFullAddress("123 Đường Láng, Đống Đa, Hà Nội");
        address1.setPhone("0123456789");
        fakeAddresses.add(address1);

        // Fake Address 2
        AddressResponse address2 = new AddressResponse();
        address2.setId(2);
        address2.setRecipientName("Trần Thị B");
        address2.setFullAddress("456 Nguyễn Trãi, Thanh Xuân, Hà Nội");
        address2.setPhone("0987654321");
        fakeAddresses.add(address2);

        // Fake Address 3
        AddressResponse address3 = new AddressResponse();
        address3.setId(3);
        address3.setRecipientName("Lê Văn C");
        address3.setFullAddress("789 Phạm Văn Đồng, Cầu Giấy, Hà Nội");
        address3.setPhone("0912345678");
        fakeAddresses.add(address3);

        return fakeAddresses;
    }

    private void updateAddressVisibility() {
        if (addresses.isEmpty()) {
            recyclerViewAddresses.setVisibility(View.GONE);
            textEmptyAddresses.setVisibility(View.VISIBLE);
        } else {
            recyclerViewAddresses.setVisibility(View.VISIBLE);
            textEmptyAddresses.setVisibility(View.GONE);
        }
    }
}