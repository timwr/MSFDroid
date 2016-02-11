package com.msfdroid.model;

public class Terminal extends RpcObject {

    public final static int TYPE_CONSOLE = 0;
    public final static int TYPE_SHELL = Session.SESSION_TYPE_SHELL;
    public final static int TYPE_METERPRETER = Session.SESSION_TYPE_METERPRETER;

    public int type;
    public String id;
    public String prompt;
    public StringBuffer text = new StringBuffer();

}
