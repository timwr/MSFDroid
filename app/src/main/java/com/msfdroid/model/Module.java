package com.msfdroid.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Module extends RpcObject {
    public String type;
    public String name;
    public String description;
    public List<String> payloads;
    public List<ModuleOption> options = new ArrayList<ModuleOption>();
    public Map<String, Map<String, Object>> info;

    @Override
    public String toString() {
        return name;
    }

}
