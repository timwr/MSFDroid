package com.msfdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.msfdroid.R;
import com.msfdroid.model.RpcServer;

public class RpcServerView extends FrameLayout {

    private TextView textviewName;
    private TextView textviewStatus;
    private FrameLayout layoutStatus;

    public RpcServerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public RpcServerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RpcServerView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_item_server, null);
        textviewName = (TextView) view.findViewById(R.id.textview_name);
        textviewStatus = (TextView) view.findViewById(R.id.textview_status);
        layoutStatus = (FrameLayout) view.findViewById(R.id.layout_status);
        addView(view);
    }

    public void updateView(RpcServer rpcServer) {
        textviewName.setText(rpcServer.getRpcServerName());
        textviewStatus.setText(rpcServer.getStatusString());
    }
}
