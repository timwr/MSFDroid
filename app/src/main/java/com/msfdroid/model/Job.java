package com.msfdroid.model;

public class Job extends RpcObject {
    public String id;
    public String name;

    @Override
    public String toString() {
        return name;
    }

}
