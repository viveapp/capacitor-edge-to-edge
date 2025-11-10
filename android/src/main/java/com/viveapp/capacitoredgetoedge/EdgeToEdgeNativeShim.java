package com.viveapp.capacitoredgetoedge;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

/**
 * Native implementation for testing early android versions
**/
public class EdgeToEdgeNativeShim implements EdgeToEdgeShim {
    private final Activity activity;

    public EdgeToEdgeNativeShim(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public Context getContext() {
        return activity;
    }

    @Override
    public Window getWindow() {
        return activity.getWindow();
    }

    @Override
    public View getTargetView() {
        // Native: Use activity's content root (android.R.id.content)
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        return decorView.findViewById(android.R.id.content);
    }
}

