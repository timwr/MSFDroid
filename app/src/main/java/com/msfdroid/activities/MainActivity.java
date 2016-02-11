package com.msfdroid.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.util.KeyboardUtil;
import com.msfdroid.Msf;
import com.msfdroid.MsfServerList;
import com.msfdroid.R;
import com.msfdroid.adapter.ModelAdapter;
import com.msfdroid.adapter.ModelPresenter;
import com.msfdroid.fragments.TerminalFragment;
import com.msfdroid.model.Console;
import com.msfdroid.model.RpcServer;
import com.msfdroid.model.Session;
import com.msfdroid.model.Terminal;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ModelPresenter.UpdateListener {

    private Drawer drawer;
    private AccountHeader accountHeader;

    private ModelPresenter modelPresenter;
    private ModelAdapter modelAdapter;
    private MsfServerList msfServerList;
    private int rpcServerId;
    private RpcServer rpcServer;
    private HashMap<Integer, Object> menuMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withProfileImagesVisible(false)
                .withCurrentProfileHiddenInList(true)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile.getIdentifier() == ModelAdapter.ID_ADD_NEW_SERVER) {
                            startActivity(new Intent(MainActivity.this, ServerActivity.class));
                        } else if (profile.getIdentifier() == ModelAdapter.ID_MODIFY_SERVER) {
                            startActivity(new Intent(MainActivity.this, ServerActivity.class));
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return onMenuItemClick(position);
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withHasStableIds(false)
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        drawer.keyboardSupportEnabled(this, true);

        msfServerList = Msf.get().msfServerList;
        rpcServerId = getIntent().getIntExtra(MsfServerList.RPC_SERVER_ID, 0);
        rpcServer = msfServerList.getRpcServer(rpcServerId);
        modelAdapter = new ModelAdapter();
        modelPresenter = new ModelPresenter();
        modelPresenter.setConnection(rpcServer.getRpc());

        if (savedInstanceState == null) {
            selectFragment(null, Terminal.TYPE_CONSOLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        modelPresenter.addListener(this);
        modelPresenter.update();
        updateView();
    }

    private void updateView() {
        modelAdapter.updateHeader(accountHeader, rpcServerId, msfServerList);
        menuMap = modelAdapter.updateView(drawer, rpcServer);
    }

    @Override
    protected void onStop() {
        modelPresenter.removeListener(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = drawer.saveInstanceState(outState);
        outState = accountHeader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private boolean onMenuItemClick(int position) {
        if (position == ModelAdapter.ID_NEW_CONSOLE) {
            selectFragment(null, Terminal.TYPE_CONSOLE);
            return false;
        }
        if (menuMap == null) {
            return false;
        }
        Object menuItem = menuMap.get(position);
        if (menuItem instanceof Console) {
            Console console = (Console) menuItem;
            selectFragment(console.id, Terminal.TYPE_CONSOLE);
        } else if (menuItem instanceof Session) {
            Session session = (Session) menuItem;
            selectFragment(session.id, session.type);
        }
        return false;
    }

    private void selectFragment(String id, int type) {
        Fragment consoleFragment = TerminalFragment.newInstance(rpcServerId, id, type);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, consoleFragment).commit();
        setTitle("Console");
    }

    @Override
    public void onUpdated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        });
    }
}
