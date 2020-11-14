package com.ecualac.zuuapp.ui.supervisores.Crema;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class CreamFragment_Sup extends Fragment {
    private static final String TAG = "Dashboard";
    GeckoSession session = new GeckoSession();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        session.close();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cream_sup, container, false);
        try{
            GeckoView view = root.findViewById(R.id.webView_cream_Sup);
            //GeckoSession session = new GeckoSession();
            GeckoRuntime runtime = GeckoRuntime.getDefault(requireContext());
            session.open(runtime);
            view.setSession(session);
            session.loadUri(PubVariable.postUrl+"/grafana/d/3ZioeMoGz/crema?orgId=2&refresh=5s&from=now%2Fd&to=now");
        }catch (Exception e){
            Log.e(TAG, "onCreateView: ",e );
        }

        return root;
    }
}