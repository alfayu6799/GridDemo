package com.example.griddemo;

import android.widget.RelativeLayout;

import java.io.Serializable;

public class Function implements Serializable {
    String name;
    int icon;
    RelativeLayout layout;

    public Function() {
    }

    public Function(String name) {
        this.name = name;
    }

    public Function(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public Function(String name, int icon, RelativeLayout layout) {
        this.name = name;
        this.icon = icon;
        this.layout = layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public void setLayout(RelativeLayout layout) {
        this.layout = layout;
    }
}
