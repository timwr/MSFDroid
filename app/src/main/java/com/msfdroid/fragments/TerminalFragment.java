package com.msfdroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.msfdroid.Msf;
import com.msfdroid.MsfServerList;
import com.msfdroid.R;
import com.msfdroid.adapter.TerminalPresenter;
import com.msfdroid.model.RpcServer;
import com.msfdroid.model.Terminal;

public class TerminalFragment extends Fragment implements TerminalPresenter.UpdateListener {

    private static final String ID = "id";
    private static final String TYPE = "type";
    private ScrollView scrollviewConsole;
    private TextView textviewConsole;
    private TextView textviewPrompt;
    private EditText edittextInput;
    private TerminalPresenter terminalPresenter;

    public static TerminalFragment newInstance(int rpcServerId, String id, int type) {
        TerminalFragment terminalFragment = new TerminalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MsfServerList.RPC_SERVER_ID, rpcServerId);
        bundle.putString(ID, id);
        bundle.putInt(TYPE, type);
        terminalFragment.setArguments(bundle);
        return terminalFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);
        scrollviewConsole = (ScrollView) view.findViewById(R.id.scrollview_console);
        textviewConsole = (TextView) view.findViewById(R.id.textview_console);
        textviewPrompt = (TextView) view.findViewById(R.id.textview_prompt);
        textviewPrompt.setText("Loading...");
        edittextInput = (EditText) view.findViewById(R.id.edittext_input);
        edittextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    String text = edittextInput.getText().toString() + "\n";
                    writeCommand(text);
                    edittextInput.setText("");
                    return true;
                }
                return false;
            }
        });

        Bundle bundle = getArguments();
        String id = bundle.getString(ID);
        int type = bundle.getInt(TYPE);
        int rpcServerId = bundle.getInt(MsfServerList.RPC_SERVER_ID);
        RpcServer rpcServer = Msf.get().msfServerList.getRpcServer(rpcServerId);

        System.err.println("console ID " + id);
        terminalPresenter = new TerminalPresenter();
        terminalPresenter.setTerminal(rpcServer.getRpc(), id, type);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (terminalPresenter.getTerminal().type == Terminal.TYPE_METERPRETER) {
            menu.clear();
            MenuItem fav = menu.add(0, 0, 0, "webcam_snap");
            fav.setIcon(android.R.drawable.ic_menu_camera);
        } else {
            menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            edittextInput.setText("webcam_snap -p /var/www/html/c.jpeg -i 2");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        terminalPresenter.addListener(this);
    }

    @Override
    public void onStop() {
        terminalPresenter.removeListener(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContent();
    }

    private void updateView() {
        updateView(terminalPresenter.getTerminal());
    }

    @Override
    public void onUpdated() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        };
        getActivity().runOnUiThread(runnable);
    }

    private void updateContent() {
        terminalPresenter.update();
    }

    private void updateScroll() {
        scrollviewConsole.post(new Runnable() {
            @Override
            public void run() {
                scrollviewConsole.fullScroll(ScrollView.FOCUS_DOWN);
                edittextInput.requestFocus();
            }
        });
    }

    private void updateView(Terminal terminal) {
        textviewPrompt.setText(terminal.prompt);
        textviewConsole.setText(terminal.text);
        updateScroll();
    }

    private void writeCommand(String command) {
        terminalPresenter.sendCommand(command);
        terminalPresenter.update();
    }

}
