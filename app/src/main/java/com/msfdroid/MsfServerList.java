package com.msfdroid;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msfdroid.model.DefaultRpcServer;
import com.msfdroid.model.RpcServer;
import com.msfdroid.model.SavedRpcServer;
import com.msfdroid.rpc.Async;
import com.msfdroid.rpc.LoginException;
import com.msfdroid.rpc.RpcConnection;
import com.msfdroid.rpc.RpcException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MsfServerList {

    public static final String RPC_SERVER_ID = "rpc_server_id";
    public static final int RPC_SERVER_ID_NEW = -1;
    private static final String MSF_MAIN = "msfMain";
    private static final String MSF_RPC_SESSIONS = "msfRpcSessions";
    public List<RpcServer> serverList = new ArrayList<RpcServer>();
    private Gson gson;
    private SharedPreferences preferences;
    private List<UpdateListener> listeners = new LinkedList<>();

    public MsfServerList() {
        gson = new Gson();
        preferences = MsfApplication.getApplication().getSharedPreferences(MSF_MAIN, Context.MODE_PRIVATE);

        new Async() {
            @Override
            protected Void doInBackground(Void... arg0) {
                loadSavedServerList();
                updateListeners();
                return super.doInBackground(arg0);
            }
        }.execute();
    }

    public List<RpcServer> getServerList() {
        return serverList;
    }

    public RpcServer getRpcServer(int rpcServerId) {
        return serverList.get(rpcServerId);
    }

    public void addListener(UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(UpdateListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        List<UpdateListener> updateList = new ArrayList<UpdateListener>(listeners);
        for (UpdateListener listener : updateList) {
            listener.onUpdated();
        }
    }

    public void loadSavedServerList() {
        String jsonSessions = preferences.getString(MSF_RPC_SESSIONS, null);
        if (jsonSessions != null) {
            Type listType = new TypeToken<ArrayList<SavedRpcServer>>() {
            }.getType();
            List<SavedRpcServer> savedRpcServers = gson.fromJson(jsonSessions, listType);
            List<RpcServer> rpcServers = new ArrayList<>();
            for (SavedRpcServer savedRpcServer : savedRpcServers) {
                RpcServer rpcServer = new RpcServer();
                rpcServer.ssl = savedRpcServer.ssl;
                rpcServer.name = savedRpcServer.name;
                rpcServer.rpcToken = savedRpcServer.rpcToken;
                rpcServer.rpcUser = savedRpcServer.rpcUser;
                rpcServer.rpcHost = savedRpcServer.rpcHost;
                rpcServer.rpcPort = savedRpcServer.rpcPort;
                if (savedRpcServer.rpcToken != null) {
                    rpcServer.status = RpcServer.STATUS_AUTHORISED;
                }
                rpcServers.add(rpcServer);
            }
            serverList.addAll(rpcServers);
        }

        if (serverList.size() == 0 && BuildConfig.DEBUG) {
//            RpcServer emulator = DefaultRpcServer.createDefaultRpcServer("10.0.2.2");
            RpcServer autoConnect = DefaultRpcServer.createDefaultRpcServer();
            serverList.add(autoConnect);
            connectAsync(autoConnect);
        }
    }

    public void saveServerList() {
        Gson gson = new Gson();
        List<SavedRpcServer> savedRpcServers = new ArrayList<>();
        for (RpcServer rpcServer : serverList) {
            SavedRpcServer savedRpcServer = new SavedRpcServer();
            savedRpcServer.ssl = rpcServer.ssl;
            savedRpcServer.name = rpcServer.name;
            savedRpcServer.rpcToken = rpcServer.rpcToken;
            savedRpcServer.rpcUser = rpcServer.rpcUser;
            savedRpcServer.rpcHost = rpcServer.rpcHost;
            savedRpcServer.rpcPort = rpcServer.rpcPort;
            savedRpcServers.add(savedRpcServer);
        }
        String jsonSession = gson.toJson(savedRpcServers);
        preferences.edit().putString(MSF_RPC_SESSIONS, jsonSession).apply();
    }

    public void connectServer(RpcServer rpcServer) {
        rpcServer.status = RpcServer.STATUS_CONNECTING;
        updateListeners();
        rpcServer.rpcConnection = new RpcConnection();
        try {
            rpcServer.rpcConnection.connect(rpcServer);
            if (rpcServer.rpcToken == null) {
                rpcServer.status = RpcServer.STATUS_AUTHORISATION_FAILED;
                updateListeners();
            } else {
                rpcServer.status = RpcServer.STATUS_CONNECTED;
                updateListeners();
                saveServerList();
            }
        } catch (LoginException e) {
            rpcServer.status = RpcServer.STATUS_AUTHORISATION_FAILED;
            updateListeners();
        } catch (RpcException e) {
            rpcServer.status = RpcServer.STATUS_CONNECTION_FAILED;
            updateListeners();
        }
    }

    public void connectAsync(final RpcServer rpcServer) {
        new Async() {
            @Override
            protected Void doInBackground(Void... arg0) {
                connectServer(rpcServer);
                return super.doInBackground(arg0);
            }
        }.execute();
    }

    public interface UpdateListener {
        void onUpdated();
    }
}
