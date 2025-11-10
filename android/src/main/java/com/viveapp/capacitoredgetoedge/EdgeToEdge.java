package com.viveapp.capacitoredgetoedge;

import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class EdgeToEdge {
    @NonNull
    private final EdgeToEdgeConfig config;

    @NonNull
    private final EdgeToEdgeShim shim;

    private static final String STATUS_BAR_TAG = "status_bar_view_tag";
    private static final String NAVIGATION_BAR_TAG = "nav_bar_view_tag";

    private View statusBarView = null;
    private View navigationBarView = null;

    private int cachedStatusBarBackground;
    private int cachedNavigationBarBackground;

    public EdgeToEdge(@NonNull EdgeToEdgeShim shim, @NonNull EdgeToEdgeConfig config) {
        this.shim = shim;
        this.config = config;

        cachedStatusBarBackground     = config.getStatusBarBackgroundColor();
        cachedNavigationBarBackground = config.getNavigationBarBackgroundColor();

        Log.d("EdgeToEdge", "EdgeToEdge#EdgeToEdge");

        // Enable edge-to-edge display
        enable(shim.getTargetView());
    }

    /**
     * Enable edge-to-edge display mode
     */
    public void enable(View targetView) {
        Window window = shim.getWindow();

        /// Disable fitsSystemWindows on root to prevent double insets on Android 14-
        targetView.setFitsSystemWindows(false);

        // Set window flags for edge-to-edge
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(false);
        } else {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            // Android 9-10 (API 28-29)
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        applyContentInsets(targetView);
    }

    /**
     * Disable edge-to-edge display mode
     */
    public void disable(View targetView) {
        Window window = shim.getWindow();

        // Reverse window flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(true);
        } else {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            // Android 9-10 (API 28-29)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        removeContentInsets(targetView);

        /// Restore fitsSystemWindows for Android 14-
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            targetView.setFitsSystemWindows(true);
        }
    }

    /**
     * Get the applied content insets from a target view
     * @param targetView The view to read insets from
     * @return MarginLayoutParams containing the applied insets, or null if not available
     */
    public ViewGroup.MarginLayoutParams getInsets(View targetView) {
        ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return (ViewGroup.MarginLayoutParams) lp;
        }
        return null;
    }

    /**
     * Apply content insets to prevent content from being drawn under system bars
     * This method sets up both immediate inset application and a listener for dynamic updates
     * @param targetView The view to apply insets to (typically root or WebView parent)
     */
    public void applyContentInsets(View targetView) {
        /// Apply current insets immediately if available
        WindowInsetsCompat currentInsets = ViewCompat.getRootWindowInsets(targetView);
        if (currentInsets != null) {
            applyInsetsToView(targetView, currentInsets);
        } else {
            Log.w("EdgeToEdge", "No current insets available for initial application");
        }

        /// Set up listener for dynamic inset updates (orientation, keyboard, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(targetView, (v, windowInsets) -> {
            applyInsetsToView(v, windowInsets);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Remove content insets from a target view
     * @param targetView The view to remove insets from
     */
    public void removeContentInsets(View targetView) {
        ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.topMargin = 0;
            mlp.leftMargin = 0;
            mlp.rightMargin = 0;
            mlp.bottomMargin = 0;
            targetView.setLayoutParams(mlp);
        }
        // Remove listener
        ViewCompat.setOnApplyWindowInsetsListener(targetView, null);
    }

    public void setBackgroundColor(int parsedStatusBarColor, int parsedNavigationBarColor) {
        Log.d("EdgeToEdge", "setBackgroundColor within implementation");

        cachedStatusBarBackground     = parsedStatusBarColor;
        cachedNavigationBarBackground = parsedNavigationBarColor;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            setBackgroundColorNewWay(parsedStatusBarColor, parsedNavigationBarColor);
        } else {
            setBackgroundColorOldWay(parsedStatusBarColor, parsedNavigationBarColor);
        }
    }

    public void setCachedBackgroundColors() {
        Log.d("EdgeToEdge", "setCachedBackgroundColor(" + String.format("#%06X", 0xFFFFFF & cachedStatusBarBackground) + "," + String.format("#%06X", 0xFFFFFF & cachedNavigationBarBackground) + ")");
        this.setBackgroundColor(cachedStatusBarBackground, cachedNavigationBarBackground);
    }

    /**
     * This method creates and positions the status and navigation bar views
     * and sets up a listener for dynamic updates
     *
     * Use only for android version after {@link android.os.Build.VERSION_CODES#VANILLA_ICE_CREAM}.
     **/
    public void setupBarViews() {
        Log.d("EdgeToEdge", "setupBarViews");

        // Get decorView dynamically
        ViewGroup decorView = (ViewGroup) shim.getWindow().getDecorView();

        // Use decorView directly - it's always a FrameLayout and supports positioning
        ViewGroup containerForBars = decorView;

        // Remove any previously created bar views
        removeOldBarViews(containerForBars);

        // Get current insets to set initial heights
        WindowInsetsCompat currentInsets = ViewCompat.getRootWindowInsets(decorView);
        int initialStatusHeight = 0;
        int initialNavHeight = 0;

        if (currentInsets != null) {
            Insets statusBarInset = currentInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBarInset = currentInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            initialStatusHeight = statusBarInset.top;
            initialNavHeight = navBarInset.bottom;
            Log.d("EdgeToEdge", "Initial insets - status: " + initialStatusHeight + ", nav: " + initialNavHeight);
        } else {
            Log.w("EdgeToEdge", "No current insets available yet");
        }

        // Create layout params with gravity for positioning
        FrameLayout.LayoutParams statusParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                initialStatusHeight
        );
        statusParams.gravity = Gravity.TOP;

        FrameLayout.LayoutParams navigationParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                initialNavHeight
        );
        navigationParams.gravity = Gravity.BOTTOM;

        // Setup status bar view - add on top of all other views
        statusBarView = new View(shim.getContext());
        statusBarView.setTag(STATUS_BAR_TAG);
        statusBarView.setLayoutParams(statusParams);
        containerForBars.addView(statusBarView);  // Add at end = draw on top
        Log.d("EdgeToEdge", "Status bar view created with height: " + statusParams.height);

        // Setup navigation bar view - add on top of all other views
        navigationBarView = new View(shim.getContext());
        navigationBarView.setTag(NAVIGATION_BAR_TAG);
        navigationBarView.setLayoutParams(navigationParams);
        containerForBars.addView(navigationBarView);  // Add at end = draw on top
        Log.d("EdgeToEdge", "Navigation bar view created with height: " + navigationParams.height);

        // Set up insets listener for dynamic updates (orientation changes, keyboard, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(containerForBars, (v, listenerInsets) -> {
            int statusHeight = listenerInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationHeight = listenerInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            Log.d("EdgeToEdge", "Insets updated - status: " + statusHeight + ", nav: " + navigationHeight);

            // Update the heights
            statusParams.height = statusHeight;
            navigationParams.height = navigationHeight;

            statusBarView.requestLayout();
            navigationBarView.requestLayout();

            // Pass insets through so Compose Scaffold can also handle padding
            return listenerInsets;
        });
    }

    /*==============================================================================================
     * Private calls
     *==============================================================================================
     **/

    private void removeOldBarViews(ViewGroup container) {
        if (container == null) {
            return;
        }

        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            View child = container.getChildAt(i);
            if (child != null) {
                Object tag = child.getTag();
                if (STATUS_BAR_TAG.equals(tag) || NAVIGATION_BAR_TAG.equals(tag)) {
                    container.removeView(child);
                }
            }
        }
    }

    /**
     * This method is called only for Android versions before
     * {@link android.os.Build.VERSION_CODES#VANILLA_ICE_CREAM}.
     * Applications that target {@link android.os.Build.VERSION_CODES#VANILLA_ICE_CREAM} or higher
     * shall call {@link EdgeToEdge#setBackgroundColorNewWay(int, int)}
     **/
    private void setBackgroundColorOldWay(int parsedStatusBarColor, int parsedNavigationBarColor) {
        Window window = shim.getWindow();
        /// Clear any translucent flags to avoid overrides
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        /// Enable drawing system bar backgrounds
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(parsedStatusBarColor);
        window.setNavigationBarColor(parsedNavigationBarColor);
    }

    private void setBackgroundColorNewWay(int parsedStatusBarColor, int parsedNavigationBarColor) {
        Log.d("EdgeToEdge", "setBackgroundColorNewWay");
        statusBarView.setBackgroundColor(parsedStatusBarColor);
        navigationBarView.setBackgroundColor(parsedNavigationBarColor);
    }

    /**
     * Low-level method to apply insets as margins to a target view
     * @param targetView The view to apply insets to (must have MarginLayoutParams)
     * @param insets The WindowInsetsCompat containing inset values
     **/
    private void applyInsetsToView(View targetView, WindowInsetsCompat insets) {
        Insets systemBarsInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
        );
        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
        boolean keyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) targetView.getLayoutParams();
        mlp.bottomMargin = keyboardVisible ? imeInsets.bottom : systemBarsInsets.bottom;
        mlp.topMargin = systemBarsInsets.top;
        mlp.leftMargin = systemBarsInsets.left;
        mlp.rightMargin = systemBarsInsets.right;

        Log.d("EdgeToEdge", "top margin is" + systemBarsInsets.top);
        Log.d("EdgeToEdge", "bottom margin is" + systemBarsInsets.bottom);

        targetView.setLayoutParams(mlp);
    }
}