package com.msfdroid.model;

import android.net.Uri;

public class SavedRpcServer {

    public String name;
    public boolean ssl;
    public String rpcHost;
    public String rpcToken;
    public String rpcUser;
    public int rpcPort;

    public SavedRpcServer() {
        rpcHost = "";
        rpcUser = "msf";
        rpcPort = 55553;
    }

    public static String serverString(SavedRpcServer savedRpcServer) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(savedRpcServer.ssl ? "msfs" : "msf");
        stringBuffer.append("://");
        stringBuffer.append(savedRpcServer.rpcUser);
        stringBuffer.append("@");
        stringBuffer.append(savedRpcServer.rpcHost);
        stringBuffer.append(":");
        stringBuffer.append(savedRpcServer.rpcPort);
        return stringBuffer.toString();
    }

    public static SavedRpcServer fromString(String uriString) {
        Uri uri = Uri.parse(uriString);
        SavedRpcServer savedRpcServer = new SavedRpcServer();
        savedRpcServer.ssl = true;
        String scheme = uri.getScheme();
        if (scheme != null && scheme.length() > 1 && !scheme.endsWith("s")) {
            savedRpcServer.ssl = false;
        }
        savedRpcServer.rpcHost = uri.getHost();
        savedRpcServer.rpcUser = uri.getUserInfo();
        int port = uri.getPort();
        if (port == -1) {
            port = 55553;
        }
        savedRpcServer.rpcPort = port;
        return savedRpcServer;
    }

    public String getRpcServerName() {
        if (this.name != null) {
            return name;
        }
        return serverString(this);
    }


}
