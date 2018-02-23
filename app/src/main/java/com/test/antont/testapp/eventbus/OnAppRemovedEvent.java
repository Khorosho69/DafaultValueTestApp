package com.test.antont.testapp.eventbus;

public class OnAppRemovedEvent {

    private String mPackageName;

    public OnAppRemovedEvent(String packageName) {
        this.mPackageName = packageName;
    }

    public String getPackageName() {
        return mPackageName;
    }
}
