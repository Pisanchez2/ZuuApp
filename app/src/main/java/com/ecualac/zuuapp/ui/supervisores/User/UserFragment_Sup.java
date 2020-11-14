package com.ecualac.zuuapp.ui.supervisores.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;

public class UserFragment_Sup extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user_sup, container, false);
        TextView username = root.findViewById(R.id.user_name_sup);
        TextView useremail = root.findViewById(R.id.user_email_sup);
        username.setText(PubVariable.username);
        useremail.setText(PubVariable.email);

        return root;
    }
}