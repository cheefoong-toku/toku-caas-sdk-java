package Toku.CaaS.V1.CallHandle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

public class CallController {

    public static int STATE_NONE = 0;
    public static int CUSTOM_STATE_START = 100;

    public static String VOICE_FEMALE = "F";
    public static String VOICE_MALE = "M";

    public static String RINGTONE_AU = "AU";
    public static String RINGTONE_EU = "EU";
    public static String RINGTONE_JP = "JP";
    public static String RINGTONE_UK = "UK";
    public static String RINGTONE_US = "US";

    public static String CALL_TYPE_P2I = "P2I";
    public static String CALL_TYPE_I2P = "I2P";
    public static String CALL_TYPE_IP_FORWARD = "IP_FORWARD";
    public static String CALL_TYPE_WS_FORWARD = "WS_FORWARD";//websocket/webrtc forward

    public static String CALL_STATUS_SETUP = "SETUP";
    public static String CALL_STATUS_ALERT = "ALERT";
    public static String CALL_STATUS_DISCONNECT = "DISCONNECT";
    public static String CALL_STATUS_DTMF = "DTMF";
    public static String CALL_STATUS_SPEECH_TO_TEXT = "SPEECH_TO_TEXT";
    public static String CALL_STATUS_RECORD = "RECORD";

    private static final Logger debug = Logger.getLogger(CallController.class.getName());

    private HttpServletRequest request;
    private HttpServletResponse response;

    private ArrayList m_FuncList = new ArrayList<Map>();

    public CallController(HttpServletRequest req, HttpServletResponse resp) {
        request = req;
        response = resp;
    }

    public String getParam(String paramName, String defaultValue) {
        String paramVal = request.getParameter(paramName);
        if (paramVal == null || "".equals(paramVal.trim())) {
            paramVal = defaultValue;
        }
        return paramVal;
    }
    
    public String GetCallCause() {
        return getParam("call_cause", "");
    }
    
    public String GetCallStatus() {
        return getParam("call_status", "");
    }
    
    boolean IsCallConnected() {
        return getParam("call_connected", "false").toUpperCase() == "TRUE"?true: false;
    }
        
    public String GetDTMF() {
        return getParam("dtmf", "");
    }

    public String GetSIPLogin() {
        return "sip:" + getParam("voip_login", "");
    }

    public String GetCallType() {
        return getParam("call_type", "PSTN");
    }

    public String GetCallingParty() {
        return getParam("calling_party", "");
    }

    public String GetCalledParty() {
        return getParam("called_party", "");
    }

    public String GetSIPCallerNumber() {
        return getParam("voip_caller_number", "");
    }

    public String GetPhoneNumber() {
        return getParam("number", "");
    }

    public String GetDefaultCallingParty() {
        if (GetCallType() == CallController.CALL_TYPE_P2I) {
            return GetCallingParty();
        } else if (GetCallType() == CallController.CALL_TYPE_IP_FORWARD) {
            String sipCli = GetSIPCallerNumber();
            if (sipCli.length() > 0) {
                return sipCli;
            }

            return GetCallingParty();
        } else {
            return GetPhoneNumber();
        }
    }

    public void PrintAllParam() {
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();

            debug.log(Level.INFO, "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }
    }

    public CallController AddCommand(LinkedHashMap p) {
        m_FuncList.add(p);
        return this;
    }

    public CallController PlayTTS(String message, String language, String voice, int dtmf, Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        p.put("function", "PlayTTS");
        p.put("message", message);
        p.put("language", language);
        p.put("voice", voice);
        p.put("dtmf", dtmf);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommand(p);
    }
    
    public CallController PlaySystem(String name, int dtmf, Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        p.put("function", "PlayFile");
        p.put("type", "system");
        p.put("value", name);
        p.put("dtmf", dtmf);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommand(p);
    }
    
    public CallController PlayFile(String name, int dtmf, Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        p.put("function", "PlayFile");
        p.put("type", "file");
        p.put("value", name);
        p.put("dtmf", dtmf);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommand(p);
    }
    
    public CallController PlayUrl(String url, int dtmf, Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        p.put("function", "PlayFile");
        p.put("type", "url");
        p.put("value", url);
        p.put("dtmf", dtmf);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommand(p);
    }
    
    public CallController MakeCall(String called_party, String calling_party, boolean handle_interrupt, boolean record_call, Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (calling_party == null) {
            calling_party = GetDefaultCallingParty();
        }

        p.put("function", "MakeCall");
        p.put("called_party", called_party);
        p.put("calling_party", calling_party);
        p.put("handle_interrupt", handle_interrupt);
        p.put("record_call", record_call);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommand(p);
    }

    public CallController Sleep(int duration) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (duration < 0) {
            duration = 1;
        }

        p.put("function", "Sleep");
        p.put("duration", duration);
        return AddCommand(p);

    }

    public CallController DropSession(int duration) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (duration < 0) {
            duration = 1;
        }

        p.put("function", "DropSession");
        p.put("duration", duration);

        return AddCommand(p);
    }

    public void Response() throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(toJsonString());
        }
    }
    
    public String toJsonString() {
        Map<String, ArrayList> resultMap = new HashMap<String, ArrayList>();

        resultMap.put("commands", m_FuncList);
        JSONObject res = new JSONObject(resultMap);
        return res.toJSONString();
    }
}
