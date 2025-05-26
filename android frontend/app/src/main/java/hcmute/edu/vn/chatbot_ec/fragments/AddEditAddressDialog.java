package hcmute.edu.vn.chatbot_ec.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import hcmute.edu.vn.chatbot_ec.R;
import hcmute.edu.vn.chatbot_ec.request.AddressRequest;
import hcmute.edu.vn.chatbot_ec.response.AddressResponse;

public class AddEditAddressDialog extends DialogFragment {

    private static final String ARG_ADDRESS = "address";
    private AddressResponse address;
    private OnAddressSaveListener listener;

    public interface OnAddressSaveListener {
        void onSave(AddressRequest request, Integer addressId);
    }

    public static AddEditAddressDialog newInstance(AddressResponse address, OnAddressSaveListener listener) {
        AddEditAddressDialog dialog = new AddEditAddressDialog();
        dialog.listener = listener;
        Bundle args = new Bundle();
        args.putSerializable(ARG_ADDRESS, address);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Đặt kích thước dialog
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_edit_address, container, false);

        TextInputLayout layoutRecipientName = view.findViewById(R.id.layout_recipient_name);
        TextInputEditText editRecipientName = view.findViewById(R.id.edit_recipient_name);
        TextInputLayout layoutFullAddress = view.findViewById(R.id.layout_full_address);
        TextInputEditText editFullAddress = view.findViewById(R.id.edit_full_address);
        TextInputLayout layoutPhone = view.findViewById(R.id.layout_phone);
        TextInputEditText editPhone = view.findViewById(R.id.edit_phone);
        MaterialButton buttonSave = view.findViewById(R.id.button_save);
        MaterialButton buttonCancel = view.findViewById(R.id.button_cancel);

        if (getArguments() != null) {
            address = (AddressResponse) getArguments().getSerializable(ARG_ADDRESS);
            if (address != null) {
                editRecipientName.setText(address.getRecipientName());
                editFullAddress.setText(address.getFullAddress());
                editPhone.setText(address.getPhone());
            }
        }

        buttonSave.setOnClickListener(v -> {
            String recipientName = editRecipientName.getText().toString().trim();
            String fullAddress = editFullAddress.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            boolean isValid = true;
            if (recipientName.isEmpty()) {
                layoutRecipientName.setError(getString(R.string.error_recipient_name_required));
                isValid = false;
            } else {
                layoutRecipientName.setError(null);
            }
            if (fullAddress.isEmpty()) {
                layoutFullAddress.setError(getString(R.string.error_address_required));
                isValid = false;
            } else {
                layoutFullAddress.setError(null);
            }
            if (phone.isEmpty()) {
                layoutPhone.setError(getString(R.string.error_phone_required));
                isValid = false;
            } else if (!phone.matches("^\\+?[0-9]{10,13}$")) {
                layoutPhone.setError(getString(R.string.error_phone_invalid));
                isValid = false;
            } else {
                layoutPhone.setError(null);
            }

            if (isValid) {
                AddressRequest request = new AddressRequest();
                request.setRecipientName(recipientName);
                request.setFullAddress(fullAddress);
                request.setPhone(phone);
                listener.onSave(request, address != null ? address.getId() : null);
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return view;
    }
}