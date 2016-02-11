package com.msfdroid.model;

import android.content.Intent;

public class Plugin extends RpcObject {
    public String id;
    public boolean enabled;
    public String description;
    public Intent intent;

    public Plugin(String id, String description, Intent intent) {
        this.id = id;
        this.description = description;
        this.intent = intent;
    }
}
