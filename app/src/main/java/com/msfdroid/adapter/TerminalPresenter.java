package com.msfdroid.adapter;

import android.os.Handler;

import com.msfdroid.model.Terminal;
import com.msfdroid.rpc.Async;
import com.msfdroid.rpc.RpcConnection;
import com.msfdroid.rpc.RpcConstants;
import com.msfdroid.rpc.RpcException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TerminalPresenter {

    private static final long POLLING_INTERVAL = 8000;

    private Handler handler = new Handler();
    private List<UpdateListener> listeners = new LinkedList<>();
    private Terminal terminal;
    private RpcConnection rpcConnection;
    private StringBuilder commandList = new StringBuilder();
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

    public void setTerminal(RpcConnection rpc, String id, int type) {
        rpcConnection = rpc;
        terminal = new Terminal();
        terminal.id = id;
        terminal.type = type;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    private void updateListeners() {
        for (UpdateListener listener : listeners) {
            listener.onUpdated();
        }
    }

    public void sendCommand(String command) {
        commandList.append(command);
    }

    private void updateSync() {
        try {
            updateConsole();
        } catch (RpcException e) {
            e.printStackTrace();
        }
        updateListeners();
    }

    public void updateConsole() throws RpcException {
        if (terminal.id == null) {
            HashMap<String, String> consoleInfo = (HashMap<String, String>) rpcConnection.execute(RpcConstants.CONSOLE_CREATE);
            terminal.id = consoleInfo.get("id");
        }

        String writeCommand = null;
        if (commandList.length() > 0) {
            if (terminal.type == Terminal.TYPE_CONSOLE) {
                writeCommand = RpcConstants.CONSOLE_WRITE;
            } else if (terminal.type == Terminal.TYPE_SHELL) {
                writeCommand = RpcConstants.SESSION_SHELL_WRITE;
            } else {
                writeCommand = RpcConstants.SESSION_METERPRETER_WRITE;
            }
            String command = commandList.toString();
            commandList.setLength(0);
            Object result = rpcConnection.execute(writeCommand, new Object[]{terminal.id, command});
            terminal.text.append(terminal.prompt);
            terminal.text.append(command);
        }

        String readCommand = null;
        if (terminal.type == Terminal.TYPE_CONSOLE) {
            readCommand = RpcConstants.CONSOLE_READ;
        } else if (terminal.type == Terminal.TYPE_SHELL) {
            readCommand = RpcConstants.SESSION_SHELL_READ;
        } else {
            readCommand = RpcConstants.SESSION_METERPRETER_READ;
        }
        HashMap<String, Object> consoleObject = (HashMap<String, Object>) rpcConnection.execute(readCommand, new Object[]{terminal.id});
        String prompt = (String) consoleObject.get("prompt");
        if (prompt != null) {
            prompt = prompt.replaceAll("\\x01|\\x02", "");
            terminal.prompt = prompt;
        }

        // Fix MSFRPC bugs
        if ("meterpreter >".equals(terminal.prompt) && terminal.type == Terminal.TYPE_CONSOLE) {
            terminal.prompt = "msf > ";
        }
        if (terminal.prompt == null && terminal.type == Terminal.TYPE_METERPRETER) {
            terminal.prompt = "meterpreter > ";
        }

        if (terminal.prompt == null) {
            terminal.prompt = "";
        }

        String data = (String) consoleObject.get("data");
        if (data != null) {
            terminal.text.append(data);
        }

        Boolean busy = (Boolean) consoleObject.get("busy");
        if (busy != null && busy) {
            refreshAfterInterval(100);
        } else {
            if (writeCommand != null) {
                refreshAfterInterval(100);
            } else {
                refreshAfterInterval(POLLING_INTERVAL);
            }
        }
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
