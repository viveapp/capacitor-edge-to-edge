package com.viveapp.capacitoredgetoedge;

import android.graphics.Color;

public class EdgeToEdgeConfig {
  private int statusBarBackgroundColor = Color.WHITE;
  private int navigationBarBackgroundColor = Color.WHITE;

  public int getStatusBarBackgroundColor() {
    return this.statusBarBackgroundColor;
  }

  public void setStatusBarBackgroundColor(int statusBarBackgroundColor) {
    this.statusBarBackgroundColor = statusBarBackgroundColor;
  }

  public int getNavigationBarBackgroundColor() {
    return this.navigationBarBackgroundColor;
  }

  public void setNavigationBarBackgroundColor(int navigationBarBackgroundColor) {
    this.navigationBarBackgroundColor = navigationBarBackgroundColor;
  }
}
