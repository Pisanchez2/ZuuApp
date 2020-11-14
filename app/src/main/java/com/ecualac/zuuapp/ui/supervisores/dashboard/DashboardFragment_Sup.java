package com.ecualac.zuuapp.ui.supervisores.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ecualac.zuuapp.PubVariable;
import com.ecualac.zuuapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment_Sup extends Fragment {
    private static final String TAG = "Dashboard";
    GeckoSession session = new GeckoSession();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        session.close();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard_sup, container, false);
        try{
            GeckoView view = root.findViewById(R.id.webView_dash_Sup);
            //GeckoSession session = new GeckoSession();
            GeckoRuntime runtime = GeckoRuntime.getDefault(requireContext());
            session.open(runtime);
            view.setSession(session);
            session.loadUri(PubVariable.postUrl+"/grafana/d/ocdejGTMk/queso?orgId=2&refresh=5s&from=now%2Fd&to=now");
        }catch (Exception e){
            Log.e(TAG, "onCreateView: ",e );
        }

        return root;
    }
}