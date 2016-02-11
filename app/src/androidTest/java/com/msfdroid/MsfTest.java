package com.msfdroid;

import android.test.AndroidTestCase;

import com.msfdroid.model.DefaultRpcServer;
import com.msfdroid.model.RpcServer;

public class MsfTest extends AndroidTestCase {

    protected RpcServer rpcServer;

    public static RpcServer testLogin() throws Exception {
        Msf msf = new Msf();
        RpcServer rpcServer = DefaultRpcServer.createDefaultRpcServer();
        msf.msfServerList.serverList.add(rpcServer);
        assertTrue(rpcServer.status == RpcServer.STATUS_NEW);
        msf.msfServerList.connectServer(rpcServer);
        assertTrue(rpcServer.status == RpcServer.STATUS_CONNECTED);
        return rpcServer;
    }

    public void testLoginAndGenerateModels() throws Exception {
        if (rpcServer == null) {
            rpcServer = testLogin();
        }

        rpcServer.getRpc().updateModel();
        assertNotNull(rpcServer.getModel().getConsoles());
        assertNotNull(rpcServer.getModel().getJobs());
        assertNotNull(rpcServer.getModel().getSessions());
        assertTrue(rpcServer.getModel().getSessions().size() > 0);
    }


//    public void skipTestLoginTwoServers() {
//        Msf msf = new Msf();
//        msf.addRpcServer(DefaultRpcServer.createDefaultRpcServer());
//        MsfRpc.connect(msf.getServerList().get(0));
//        msf.addRpcServer(DefaultRpcServer.createDefaultRpcServer("10.0.2.2"));
//        MsfRpc.connect(msf.getServerList().get(1));
//    }

}
