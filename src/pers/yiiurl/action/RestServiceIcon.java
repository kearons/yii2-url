package pers.yiiurl.action;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public enum RestServiceIcon {
    Web("w"),
    Console("c");

    private final String icon;

    RestServiceIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon(int i) {
        return this.icon;
    }
    public Icon getIcon() {
        return IconLoader.findIcon("/icons/"+this.icon+".png");
    }
}
