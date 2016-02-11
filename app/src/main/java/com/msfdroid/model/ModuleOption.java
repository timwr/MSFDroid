package com.msfdroid.model;

import java.util.List;
import java.util.Map;

public class ModuleOption {
    public String name;
    public String desc;
    public boolean required;
    public boolean advanced;
    public boolean evasion;
    public String type;
    public Object defaultValue;
    public Object value;
    public List<String> enums;

    /*

{SSLVersion=
{advanced=true, desc=Specify the version of SSL that should be used (accepted: SSL2, SSL3, TLS1), default=SSL3, type=enum, required=false, evasion=false, enums=[SSL2, SSL3, TLS1]}
, RPORT=
{advanced=false, desc=The target port, default=514, type=port, required=true, evasion=false}
, SSLVerifyMode=
{advanced=true, desc=SSL verification method (accepted: CLIENT_ONCE, FAIL_IF_NO_PEER_CERT, NONE, PEER), default=PEER, type=enum, required=false, evasion=false, enums=[CLIENT_ONCE, FAIL_IF_NO_PEER_CERT, NONE, PEER]}
, SSLCipher=
{advanced=true, desc=String for SSL cipher - "DHE-RSA-AES256-SHA" or "ADH", required=false, type=string, evasion=false}
, VERBOSE=
{advanced=true, default=false, desc=Enable detailed status messages, required=false, type=bool, evasion=false}
, WfsDelay=
{advanced=true, default=0, desc=Additional delay when waiting for a session, type=integer, required=false, evasion=false}
, SSL=
{advanced=true, desc=Negotiate SSL for outgoing connections, default=false, type=bool, required=false, evasion=false}
, ConnectTimeout=
{advanced=true, default=10, desc=Maximum number of seconds to establish a TCP connection, type=integer, required=true, evasion=false}
, WORKSPACE=
{advanced=true, desc=Specify the workspace for this module, type=string, required=false, evasion=false}
, TCP::send_delay=
{advanced=false, desc=Delays inserted before every send.  (0 = disable), default=0, required=false, type=integer, evasion=true}
, EnableContextEncoding=
{advanced=true, desc=Use transient context when encoding payloads, default=false, type=bool, required=false, evasion=false}
, Proxies=
{advanced=true, desc=Use a proxy chain, type=string, required=false, evasion=false}
, CHOST=
{advanced=true, desc=The local client address, required=false, type=address, evasion=false}
, DisablePayloadHandler=
{advanced=true, default=false, desc=Disable the handler code for the selected payload, required=false, type=bool, evasion=false}
, RHOST=
{advanced=false, desc=The target address, required=true, type=address, evasion=false}
, TCP::max_send_size=
{advanced=false, default=0, desc=Maxiumum tcp segment size.  (0 = disable), required=false, type=integer, evasion=true}
, CPORT=
{advanced=true, desc=The local client port, type=port, required=false, evasion=false}
, ContextInformationFile=
{advanced=true, desc=The information file that contains context information, type=path, required=false, evasion=false}
}
*/
    public static void addModuleOptions(Module module, Object object) {
        module.options.clear();
        Map<String, Map<String, Object>> moduleoptions = (Map<String, Map<String, Object>>) object;
        for (String key : moduleoptions.keySet()) {
            Map<String, Object> opt = moduleoptions.get(key);
            ModuleOption moduleOption = new ModuleOption();

            moduleOption.name = key;
            moduleOption.desc = (String) opt.get("desc");
            moduleOption.type = (String) opt.get("type");
            moduleOption.advanced = Boolean.TRUE.equals(opt.get("advanced"));
            moduleOption.required = Boolean.TRUE.equals(opt.get("required"));
            moduleOption.evasion = Boolean.TRUE.equals(opt.get("evasion"));
            moduleOption.defaultValue = opt.get("default");
            moduleOption.value = moduleOption.defaultValue;
            if ("enum".equals(moduleOption.type)) {
                moduleOption.enums = (List<String>) opt.get("enums");
            }
            if (moduleOption.advanced || !moduleOption.required) {
                module.options.add(moduleOption);
            } else {
                module.options.add(0, moduleOption);
            }
        }
    }
}
