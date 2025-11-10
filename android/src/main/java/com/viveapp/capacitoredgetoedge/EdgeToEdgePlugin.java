package com.viveapp.capacitoredgetoedge;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;



@CapacitorPlugin(name = "EdgeToEdge")
public class EdgeToEdgePlugin extends Plugin {
    private static final String ERROR_COLOR_MISSING = "color must be provided.";
    private static final String TAG = "EdgeToEdge";

    private EdgeToEdge implementation;

    @Override
    public void load() {
        EdgeToEdgeConfig config = getEdgeToEdgeConfig();
        EdgeToEdgeShim shim = new EdgeToEdgeCapacitorShim(this);
        implementation = new EdgeToEdge(shim, config);

        View webView = getBridge().getWebView();
        ViewGroup root = (ViewGroup) webView.getParent();

        ///  Wait for Android view to initialise and setup bar views then
        root.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                ///  Enable inset dispatching on root
                root.setFitsSystemWindows(Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    implementation.setupBarViews();
                }

                root.post(() -> applyColorsWithRetry(root));

                ViewCompat.requestApplyInsets(root);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {}
        });
    }

    @PluginMethod
    public void enable(PluginCall call) {
        getActivity()
                .runOnUiThread(() -> {
                    try {
                        ViewGroup root = (ViewGroup) getBridge().getWebView().getParent();

                        implementation.enable(root);

                        /// Disable contrast enforcement (scrim) on API 29+
                        Window window = getActivity().getWindow();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            window.setStatusBarContrastEnforced(false);
                            window.setNavigationBarContrastEnforced(false);
                        }

                        /// Re-trigger insets after changes
                        ViewCompat.requestApplyInsets(root);

                        call.resolve();
                    } catch (Exception exception) {
                        call.reject(exception.getMessage());
                    }
                });
    }

    @PluginMethod
    public void disable(PluginCall call) {
        getActivity()
                .runOnUiThread(() -> {
                    try {
                        // Remove content insets
                        View webView = getBridge().getWebView();
                        ViewGroup root = (ViewGroup) webView.getParent();

                        implementation.disable(root);

                        call.resolve();
                    } catch (Exception exception) {
                        call.reject(exception.getMessage());
                    }
                });
    }

    @PluginMethod
    public void getInsets(PluginCall call) {
        try {
            View webView = getBridge().getWebView();
            ViewGroup root = (ViewGroup) webView.getParent();
            ViewGroup.MarginLayoutParams insets = implementation.getInsets(root);

            if (insets == null) {
                call.reject("No insets available");
                return;
            }

            JSObject result = new JSObject();
            result.put("bottom", insets.bottomMargin);
            result.put("left", insets.leftMargin);
            result.put("right", insets.rightMargin);
            result.put("top", insets.topMargin);
            call.resolve(result);
        } catch (Exception exception) {
            call.reject(exception.getMessage());
        }
    }

    @PluginMethod
    public void setBackgroundColor(PluginCall call) {
        String statusBarColor     = call.getString("statusBarColor");
        String navigationBarColor = call.getString("navigationBarColor");

        if (statusBarColor == null && navigationBarColor == null) {
            call.reject(ERROR_COLOR_MISSING);
            return;
        }

        getActivity()
                .runOnUiThread(() -> {
                    try {
                        int parsedStatusBarColor     = Color.parseColor(statusBarColor);
                        int parsedNavigationBarColor = Color.parseColor(navigationBarColor);

                        implementation.setBackgroundColor(parsedStatusBarColor, parsedNavigationBarColor);

                        call.resolve();
                    } catch (Exception exception) {
                        call.reject(exception.getMessage());
                    }
                });
    }

    public void applyColorsWithRetry(View root) {
        Activity activity = getActivity();
        if (activity == null || !activity.hasWindowFocus()) {
            /// Retry once after a short delay (catches most focus lags)
            root.postDelayed(() -> applyColorsWithRetry(root), 100);
            return;
        }

        implementation.setCachedBackgroundColors();
    }

    private EdgeToEdgeConfig getEdgeToEdgeConfig() {
        EdgeToEdgeConfig config = new EdgeToEdgeConfig();

        try {
            String statusBarBackgroundColor = getConfig().getString("statusBarBackgroundColor");
            if (statusBarBackgroundColor != null) {
                config.setStatusBarBackgroundColor(Color.parseColor(statusBarBackgroundColor));
            }

            String navigationBarBackgroundColor = getConfig().getString("navigationBarBackgroundColor");
            if (navigationBarBackgroundColor != null) {
                config.setNavigationBarBackgroundColor(Color.parseColor(navigationBarBackgroundColor));
            }
        } catch (Exception exception) {
            Logger.error(TAG, "Set config failed.", exception);
        }
        return config;
    }
}
