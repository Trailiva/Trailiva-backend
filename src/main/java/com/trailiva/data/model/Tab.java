package com.trailiva.data.model;

import org.aspectj.apache.bcel.generic.Tag;

public enum Tab {
    PENDING("pending"),
    IN_PROGRESS("progress"),
    COMPLETED("completed");

    private final String tab;

    Tab(String tag) {
        this.tab = tag;
    }

    public static Tab tabMapper(String tabRequest){
        for (Tab tab : Tab.values()) {
            if (tab.getTab().equals(tabRequest))
                return tab;
        }
        return PENDING;
    }

    public String getTab() {
        return tab;
    }
}
