package com.msfdroid.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Session extends RpcObject {
    public final static int SESSION_TYPE_SHELL = 1;
    public final static int SESSION_TYPE_METERPRETER = 2;

    public String id;
    public Map fields;
    public int type;
    public String description;

    public Session(String id, Map fields) {
        this.id = id;
        this.fields = fields;
    }

    public static List<Session> getList(Object o) {
        List<Session> sessions = new ArrayList<Session>();
        Map<String, Map> map = (Map<String, Map>) o;
        for (String key : map.keySet()) {
            Map<String, String> fields = (Map<String, String>) map.get(key);
            Session session = new Session(key, fields);
            String type = fields.get("type");
            if ("shell".equals(type)) {
                session.type = SESSION_TYPE_SHELL;
            } else {
                session.type = SESSION_TYPE_METERPRETER;
            }
            session.description = fields.get("desc");
            sessions.add(session);
        }
        return sessions;
    }

    @Override
    public String toString() {
        return id + fields;
    }
}
