package com.msfdroid.model;

import com.msfdroid.rpc.RpcConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsfModel implements RpcConstants {

    private List<Console> consoles;

    private List<Job> jobs;
    private Map<String, Object> jobmap;

    private Map<String, Map> sessionMap;
    private List<Session> sessions;

    private Module module = new Module();

    private List<Plugin> pluginlist;
    private List<String> plugins;

//    pluginlist = new ArrayList<Plugin>();
//    pluginlist.add(new Plugin("gcm_notify", "New session notification", new Intent()));
//    pluginlist.add(new Plugin("auto_add_route", "Auto route new sessions", null));

    public List<Job> getJobs() {
        List<Job> list = new ArrayList<Job>();
        Map<String, Object> jobMap = jobmap;
        for (String key : jobMap.keySet()) {
            Object job = jobMap.get(key);
            String consoleResult = String.valueOf(job);
            Job l = new Job();
            l.id = key;
            l.name = consoleResult;
            list.add(l);
        }
        return list;
    }

    public Object updateModel(String cmd, Object object) {
        if (cmd.equals(CONSOLE_LIST)) {
            HashMap<String, List> consoleMap = (HashMap<String, List>) object;
            List consoleList = consoleMap.get("consoles");
            consoles = new ArrayList<>();
            for (Object item : consoleList) {
                HashMap<String, String> consoleResult = (HashMap<String, String>) item;
                Console console = new Console();
                console.id = consoleResult.get("id");
                consoles.add(console);
            }
        } else if (cmd.equals(RpcConstants.PLUGIN_LOADED)) {
            plugins = (List<String>) ((Map) object).get("plugins");
            for (Plugin plugin : pluginlist) {
                plugin.enabled = plugins.contains(plugin.id);
            }
        } else if (cmd.equals(RpcConstants.MODULE_POST) || cmd.equals(RpcConstants.MODULE_AUXILIARY) || cmd.equals(RpcConstants.MODULE_EXPLOITS) || cmd.equals(RpcConstants.MODULE_PAYLOADS)) {
            object = ((Map) object).get("modules");
        } else if (cmd.equals(RpcConstants.MODULE_INFO)) {
            module.info = (Map<String, Map<String, Object>>) object;
        } else if (cmd.equals(RpcConstants.MODULE_COMPATIBLE_PAYLOADS)) {
            module.payloads = (List<String>) ((Map) object).get("payloads");
        } else if (cmd.equals(RpcConstants.MODULE_OPTIONS)) {
            ModuleOption.addModuleOptions(module, object);
        } else if (cmd.equals(SESSION_LIST)) {
            sessionMap = (Map<String, Map>) object;
            sessions = Session.getList(object);
        } else if (cmd.equals(RpcConstants.JOB_LIST)) {
            jobmap = (Map) object;
        } else if (cmd.equals(RpcConstants.JOB_INFO)) {
            Map map = (Map) object;
            Object id = map.get("jid");
            jobmap.put(String.valueOf(id), map);
        }

        return object;
    }

    public List<Console> getConsoles() {
        return consoles;
    }

    public List<Session> getSessions() {
        return sessions;
    }
}
