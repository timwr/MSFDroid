package com.msfdroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.msfdroid.Msf;
import com.msfdroid.MsfServerList;
import com.msfdroid.R;
import com.msfdroid.adapter.ServerListAdapter;
import com.msfdroid.model.RpcServer;

import java.util.List;

public class ServerActivity extends Activity implements MsfServerList.UpdateListener {

    private ListView listviewServers;
    private ServerListAdapter listAdapter;

    private boolean firstLaunch = true;
    private MsfServerList msfServerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        listviewServers = (ListView) findViewById(R.id.listview_servers);

        msfServerList = Msf.get().msfServerList;

        final List<RpcServer> serverList = msfServerList.getServerList();
        listAdapter = new ServerListAdapter(this, serverList);
        listviewServers.setAdapter(listAdapter);
        listviewServers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RpcServer rpcServer = msfServerList.getRpcServer(i);
                if (rpcServer.status == RpcServer.STATUS_CONNECTED && rpcServer.rpcConnection != null) {
                    Intent intent = new Intent(ServerActivity.this, MainActivity.class);
                    intent.putExtra(MsfServerList.RPC_SERVER_ID, i);
                    startActivity(intent);
                    finish();
                } else if (rpcServer.status != RpcServer.STATUS_CONNECTING) {
                    if (rpcServer.rpcPassword != null || rpcServer.rpcToken != null) {
                        msfServerList.connectAsync(rpcServer);
                    } else {
                        startServerDetailActivity(i);
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            firstLaunch = false;
        }
    }

    private void updateView() {
        listAdapter.updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        msfServerList.addListener(this);
        updateView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        msfServerList.removeListener(this);
    }

    @Override
    public void onUpdated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        });

        if (firstLaunch && msfServerList.getServerList().size() == 0) {
            firstLaunch = false;
            startServerDetailActivity(MsfServerList.RPC_SERVER_ID_NEW);
        }
    }

    public void clickAddServer(View view) {
        startServerDetailActivity(MsfServerList.RPC_SERVER_ID_NEW);
    }

    public void startServerDetailActivity(int rpcServer) {
        Intent intent = new Intent(this, ServerDetailActivity.class);
        intent.putExtra(MsfServerList.RPC_SERVER_ID, rpcServer);
        startActivity(intent);
    }
}

