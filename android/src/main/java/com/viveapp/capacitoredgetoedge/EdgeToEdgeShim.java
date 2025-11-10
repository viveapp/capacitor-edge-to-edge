package com.viveapp.capacitoredgetoedge;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;

/**
 * Shim interface to abstract differences between native and plugin contexts
**/
public interface EdgeToEdgeShim {
    Activity getActivity();
    Context getContext();
    Window getWindow();
    View getTargetView();  // The view to apply content insets to
}

