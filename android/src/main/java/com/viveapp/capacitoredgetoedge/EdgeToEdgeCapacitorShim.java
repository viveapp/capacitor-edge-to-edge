package com.viveapp.capacitoredgetoedge;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.getcapacitor.Plugin;

/**
 * Plugin implementation of E2EShim for Capacitor plugin context
 */
public class EdgeToEdgeCapacitorShim implements EdgeToEdgeShim {
    private final Plugin plugin;

    public EdgeToEdgeCapacitorShim(@NonNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Activity getActivity() {
        return plugin.getActivity();
    }

    @Override
    public Context getContext() {
        return plugin.getContext();

    }

    @Override
    public Window getWindow() {
        return getActivity().getWindow();
    }

    @Override
    public View getTargetView() {
        // Plugin: Use WebView's parent
        View webView = plugin.getBridge().getWebView();
        return (ViewGroup) webView.getParent();
    }
}

