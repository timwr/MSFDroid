package com.msfdroid.model;

import com.msfdroid.R;
import com.msfdroid.rpc.RpcConnection;

public class RpcServer extends SavedRpcServer {

    public static final int STATUS_NEW = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTION_FAILED = 2;
    public static final int STATUS_AUTHORISED = 3;
    public static final int STATUS_CONNECTED = 4;
    public static final int STATUS_AUTHORISATION_FAILED = 5;

    public int status;
    public String rpcPassword;
    public RpcConnection rpcConnection;

    public int getStatusString() {
        if (status == RpcServer.STATUS_NEW) {
            return R.string.rpc_status_new;
        } else if (status == RpcServer.STATUS_CONNECTING) {
            return R.string.rpc_status_connecting;
        } else if (status == RpcServer.STATUS_AUTHORISED) {
            return R.string.rpc_status_authorised;
        } else if (status == RpcServer.STATUS_CONNECTED) {
            return R.string.rpc_status_connected;
        } else if (status == RpcServer.STATUS_CONNECTION_FAILED) {
            return R.string.rpc_status_connection_failed;
        } else {
            return R.string.rpc_status_connection_failed;
        }
    }

    public RpcConnection getRpc() {
        return rpcConnection;
    }

    public MsfModel getModel() {
        return rpcConnection.getModel();
    }

}
