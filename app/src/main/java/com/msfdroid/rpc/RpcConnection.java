package com.msfdroid.rpc;

// Credits to scriptjunkie and rsmudge

import com.msfdroid.model.MsfModel;
import com.msfdroid.model.RpcServer;

public class RpcConnection implements RpcConstants {

    private MsfRpc msfRpc = new MsfRpc();
    private MsfModel msfModel = new MsfModel();

    public void connect(RpcServer rpcServer) throws RpcException {
        msfRpc = new MsfRpc();
        msfRpc.createURL(rpcServer.rpcHost, rpcServer.rpcPort, rpcServer.ssl);
        if (rpcServer.rpcToken != null) {
            msfRpc.checkToken(rpcServer.rpcToken);
        } else {
            rpcServer.rpcToken = msfRpc.connect(rpcServer.rpcUser, rpcServer.rpcPassword);
        }
    }

    public void updateModel() throws RpcException {
        for (String command : new String[]{CONSOLE_LIST, JOB_LIST, SESSION_LIST}) {
            Object object = msfRpc.execute(command);
            msfModel.updateModel(command, object);
        }
    }

    public MsfModel getModel() {
        return msfModel;
    }

    public Object execute(String command) throws RpcException {
        return msfRpc.execute(command);
    }

    public Object execute(String command, Object[] args) throws RpcException {
        return msfRpc.execute(command, args);
    }

}
