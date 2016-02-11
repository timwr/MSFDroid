package com.msfdroid.adapter;

import android.os.Handler;

import com.msfdroid.rpc.Async;
import com.msfdroid.rpc.RpcConnection;
import com.msfdroid.rpc.RpcException;

import java.util.LinkedList;
import java.util.List;

public class ModelPresenter {

    private static final long POLLING_INTERVAL = 15000;

    private Handler handler = new Handler();
    private List<UpdateListener> listeners = new LinkedList<>();
    private RpcConnection rpcConnection;
    private Runnable updateHandler = new Runnable() {
        @Override
        public void run() {
            update();
        }
    };

    public void addListener(UpdateListener listener) {
        listeners.add(listener);
    }

    private void refreshAfterInterval(long pollingInterval) {
        handler.removeCallbacksAndMessages(null);
        if (listeners.size() > 0) {
            handler.postDelayed(updateHandler, pollingInterval);
        }
    }

    public void removeListener(UpdateListener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void setConnection(RpcConnection rpc) {
        rpcConnection = rpc;
    }

    private void updateListeners() {
        for (UpdateListener listener : listeners) {
            listener.onUpdated();
        }
    }

    private void updateSync() {
        try {
            rpcConnection.updateModel();
        } catch (RpcException e) {
            e.printStackTrace();
        }
        updateListeners();
        refreshAfterInterval(POLLING_INTERVAL);
    }

    public void update() {
        new Async() {
            @Override
            protected Void doInBackground(Void... arg0) {
                updateSync();
                return super.doInBackground(arg0);
            }
        }.execute();
    }

    public interface UpdateListener {
        void onUpdated();
    }

}
