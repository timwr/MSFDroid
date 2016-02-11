package com.msfdroid;

import android.test.AndroidTestCase;

import com.msfdroid.adapter.TerminalPresenter;
import com.msfdroid.model.RpcServer;
import com.msfdroid.model.Session;
import com.msfdroid.model.Terminal;

import java.util.List;

public class TerminalPresenterTest extends AndroidTestCase {

    private RpcServer rpcServer;

    public void testCreateConsole() throws Exception {
        if (rpcServer == null) {
            rpcServer = MsfTest.testLogin();
        }

        rpcServer.getRpc().updateModel();
        assertNotNull(rpcServer.getModel().getConsoles());
        assertNotNull(rpcServer.getModel().getJobs());
        assertNotNull(rpcServer.getModel().getSessions());
        TerminalPresenter terminalPresenter = new TerminalPresenter();
        terminalPresenter.setTerminal(rpcServer.getRpc(), null, Terminal.TYPE_CONSOLE);
        terminalPresenter.updateConsole();
        assertNotNull(terminalPresenter.getTerminal().id);
//        assertTrue(rpcServer.getModel().getSessions().size() > 0);
    }

    public void testSession() throws Exception {
        if (rpcServer == null) {
            rpcServer = MsfTest.testLogin();
        }

        rpcServer.getRpc().updateModel();
        TerminalPresenter terminalPresenter = new TerminalPresenter();
        List<Session> sessions = rpcServer.getModel().getSessions();
        assertNotNull(sessions);
        System.err.println("sessions " + sessions);
        Session session = sessions.get(0);
        terminalPresenter.setTerminal(rpcServer.getRpc(), session.id, session.type);
        terminalPresenter.updateConsole();
        terminalPresenter.sendCommand("id");
        terminalPresenter.updateConsole();
//        terminalPresenter.setSession(rpcServer.getRpc(), sessions.get(0));
//        terminalPresenter.updateConsole();
//        assertNotNull(terminalPresenter.getTerminal().id);
    }

}
