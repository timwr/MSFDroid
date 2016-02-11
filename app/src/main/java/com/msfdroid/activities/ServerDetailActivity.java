package com.msfdroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.msfdroid.Msf;
import com.msfdroid.MsfServerList;
import com.msfdroid.R;
import com.msfdroid.model.RpcServer;
import com.msfdroid.view.RpcServerView;

public class ServerDetailActivity extends Activity implements MsfServerList.UpdateListener {

    private EditText edittextIp;
    private EditText edittextPort;
    private EditText edittextUser;
    private EditText edittextPass;
    private CheckBox checkboxSsl;

    private MsfServerList msfServerList;
    private int rpcServerId;
    private RpcServer rpcServer;
    private RpcServerView rpcServerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        edittextIp = (EditText) findViewById(R.id.edittext_ip);
        edittextPort = (EditText) findViewById(R.id.edittext_port);
        edittextUser = (EditText) findViewById(R.id.edittext_user);
        edittextPass = (EditText) findViewById(R.id.edittext_pass);
        checkboxSsl = (CheckBox) findViewById(R.id.checkbox_ssl);
        rpcServerView = (RpcServerView) findViewById(R.id.rpcserverview_server);


        msfServerList = Msf.get().msfServerList;
        rpcServerId = getIntent().getIntExtra(MsfServerList.RPC_SERVER_ID, -1);
        if (rpcServerId == MsfServerList.RPC_SERVER_ID_NEW) {
            rpcServer = new RpcServer();
        } else {
            rpcServer = msfServerList.getRpcServer(rpcServerId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        msfServerList.addListener(this);
        updateView(rpcServer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        msfServerList.removeListener(this);
    }

    @Override
    public void onUpdated() {
        if (rpcServer.status == RpcServer.STATUS_AUTHORISED) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MsfServerList.RPC_SERVER_ID, rpcServerId);
            startActivity(intent);
            finish();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView(rpcServer);
            }
        });
    }

    private void updateView(RpcServer rpcServer) {
        if (rpcServerId != MsfServerList.RPC_SERVER_ID_NEW) {
            rpcServerView.updateView(rpcServer);
        }
        edittextIp.setText(rpcServer.rpcHost);
        edittextUser.setText(rpcServer.rpcUser);
        edittextPort.setText(String.valueOf(rpcServer.rpcPort));
        edittextPass.setText(rpcServer.rpcPassword);
        checkboxSsl.setChecked(rpcServer.ssl);
    }

    /*
    private void showProgress() {
        new AlertDialog.Builder(this).setMessage("Connection error").show();

        Uri uri = intent.getData();
        if (uri != null) {
            String user = uri.getUserInfo();
            String host = uri.getHost();

            int port = uri.getPort();
            if (port == -1) {
                port = 55553;
            }
            edittextIp.setText(host);
            edittextUser.setText(user);
            edittextPort.setText(String.valueOf(port));
            edittextPass.setText(intent.getStringExtra(MsfController.PASSWORD));

            if (intent.getBooleanExtra(MsfController.CONNECT, false)) {
                connect(null);
            }
        }
    }
        */


    private void updateRpcServer() {
        rpcServer.rpcHost = edittextIp.getText().toString();
        rpcServer.rpcUser = edittextUser.getText().toString();
        rpcServer.rpcPassword = edittextPass.getText().toString();
        rpcServer.rpcPort = Integer.valueOf(edittextPort.getText().toString());
        rpcServer.ssl = checkboxSsl.isChecked();
        if (rpcServerId == MsfServerList.RPC_SERVER_ID_NEW) {
            msfServerList.serverList.add(rpcServer);
            rpcServerId = msfServerList.getServerList().size() - 1;
        }
    }

    public void connect(View view) {
        updateRpcServer();
        updateView(rpcServer);
        msfServerList.saveServerList();
        msfServerList.connectAsync(rpcServer);
    }

    public void update(View view) {
        updateRpcServer();
        updateView(rpcServer);
        msfServerList.saveServerList();
    }

}

