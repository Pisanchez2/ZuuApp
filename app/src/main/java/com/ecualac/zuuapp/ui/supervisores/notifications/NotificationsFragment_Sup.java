package com.ecualac.zuuapp.ui.supervisores.notifications;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ecualac.zuuapp.R;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class NotificationsFragment_Sup extends Fragment {
    GeckoSession session = new GeckoSession();

    private static final String TAG = "web";


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        session.close();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications_sup, container, false);
        try{
            GeckoView view = root.findViewById(R.id.webViewSup);
            //GeckoSession session = new GeckoSession();
            GeckoRuntime runtime = GeckoRuntime.getDefault(requireContext());
            session.open(runtime);
            view.setSession(session);
            session.loadUri("http://192.168.100.53/grafana/d/XaM_6Z2Gk/stock?orgId=1&refresh=5s&from=now%2Fd&to=now");
        }catch (Exception e){
            Log.e(TAG, "onCreateView: ",e );
        }

        return root;
    }

}