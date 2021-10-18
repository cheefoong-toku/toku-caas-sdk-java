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

/*! Call Handle Controller Class */
/*!
 * This class use for both Programmable Call and Programmable VOIP service.  
 * The class provides the standard value used by the service and provides function 
 * to form the call control JSON response.
 */

public class CallController {

    /** \addtogroup <TTS_VOICE_TYPE Voice type
     * Voice type for PlayTTS()
     *  @{
     */
    public static String VOICE_FEMALE = "F"; //!< Female voice
    public static String VOICE_MALE = "M"; //!< Male voice
    /** @}*/
    
    /** \addtogroup <RINGTONE_TYPE Ringtone
     * Ringtone type for MakeCall()
     *  @{
     */
    public static String RINGTONE_AU = "AU"; //!< AU ringtone
    public static String RINGTONE_EU = "EU"; //!< EU ringtone
    public static String RINGTONE_JP = "JP"; //!< JP ringtone
    public static String RINGTONE_UK = "UK"; //!< UK ringtone
    public static String RINGTONE_US = "US"; //!< US ringtone
    /** @}*/
    
    /** \addtogroup <CALL_TYPE Call Type
     * call_type in event parameter
     *  @{
     */
    public static String CALL_TYPE_P2I = "P2I"; //!< Call type - Call from phone to VOIP
    public static String CALL_TYPE_I2P = "I2P"; //!< Call type - Call from VOIP to phone
    public static String CALL_TYPE_IP_FORWARD = "IP_FORWARD"; //!< Call type - VOIP call forward
    public static String CALL_TYPE_WS_FORWARD = "WS_FORWARD"; //!< Call type - Websocket/WebRTC call forward
    /** @}*/
    
    /** \addtogroup <CALL_STATUS Call Status
     * call_status in event parameter
     *  @{
     */
    public static String CALL_STATUS_SETUP = "SETUP"; //!< Call status event - Setup
    public static String CALL_STATUS_ALERT = "ALERT"; //!< Call status event - Alerting
    public static String CALL_STATUS_DISCONNECT = "DISCONNECT"; //!< Call status event - Disconnect
    public static String CALL_STATUS_DTMF = "DTMF"; //!< Call status event - DTMF
    public static String CALL_STATUS_SPEECH_TO_TEXT = "SPEECH_TO_TEXT";//!< Call status event - Speech to text
    public static String CALL_STATUS_RECORD = "RECORD"; //!< Call Status Event - Recording
    
    public static String CALL_STATUS_RECORD_START = "START_RECORD"; //!< Call Status Event - Start Recording
    public static String RECORD_END = "END_RECORD"; //!< Call Status Event - End Recording
    /** @}*/
    
    
    private static final Logger debug = Logger.getLogger(CallController.class.getName());

    private HttpServletRequest request;
    private HttpServletResponse response;

    private ArrayList m_FuncList = new ArrayList<Map>();

    //! Constructor
    /*!
      \param req Servlet HTTP request object
      \param resp Servlet HTTP response object
      \sa GetParam()
     */
    public CallController(HttpServletRequest req, HttpServletResponse resp) {
        request = req;
        response = resp;
    }

    //! Get specific event parameter
    /*!
      \param paramName event parameter's name
      \param defaultValue default value if specified parameter not found

      \return <em>String</em> Event parameter's value
      \sa See avaialble parameter for event: 
        <a href="https://apidocs.toku.co/Additional-Information-Toku-API/call-service-webhook/call-handle-webhook#call-handle-dtmf-parameters" target="_blank">DTMF</a>, 
        <a href="https://apidocs.toku.co/Additional-Information-Toku-API/call-service-webhook/call-status-others-webhook#callstatusevent" target="_blank">Call</a>, 
        <a href="https://apidocs.toku.co/Additional-Information-Toku-API/call-service-webhook/call-status-others-webhook#call-recording-callback-webhook" target="_blank">Recording</a>
     */
    public String GetParam(String paramName, String defaultValue) {
        String paramVal = request.getParameter(paramName);
        if (paramVal == null || "".equals(paramVal.trim())) {
            paramVal = defaultValue;
        }
        return paramVal;
    }
    
    //! Get event parameter - call_cause
    /*!
      \return <em>string</em> event 'call_cause' parameter
      \sa GetParam()
     */
    public String GetCallCause() {
        return GetParam("call_cause", "");
    }
    
    //! Get event parameter - call_status
    /*!
      \return <em>String</em> event 'call_status' parameter
      \sa GetParam()
     */
    public String GetCallStatus() {
        return GetParam("call_status", "");
    }
    
    //! Get event parameter - call_connected
    /*!
      \return <em>String</em> event 'call_connected' parameter
      \sa GetParam()
     */
    boolean IsCallConnected() {
        return GetParam("call_connected", "false").toUpperCase() == "TRUE"?true: false;
    }
    
    //! Get event parameter - dtmf
    /*!
      \return <em>String</em> event 'dtmf' parameter
      \sa GetParam()
     */
    public String GetDTMF() {
        return GetParam("dtmf", "");
    }
    
    //! Get event parameter - voip_login
    /*!
      \return <em>String</em> event 'voip_caller_number' parameter
      \sa GetParam()
     */
    public String GetSIPLogin() {
        return "sip:" + GetParam("voip_login", "");
    }
    
    //! Get event parameter - call_type
    /*!
      \return <em>String</em> event 'call_type' parameter
      \sa
     */
    public String GetCallType() {
        return GetParam("call_type", "PSTN");
    }
    
    //! Get event parameter - calling_party
    /*!
      \return <em>String</em> event 'calling_party' parameter
      \sa GetParam()
     */
    public String GetCallingParty() {
        return GetParam("calling_party", "");
    }
    
    //! Get event parameter - called_party
    /*!
      \return <em>String</em> event 'called_party' parameter
      \sa GetParam()
     */
    public String GetCalledParty() {
        return GetParam("called_party", "");
    }
    
    //! Get event parameter - voip_caller_number
    /*!
      \return <em>String</em> event 'called_party' parameter
      \sa GetParam()
     */
    public String GetSIPCallerNumber() {
        return GetParam("voip_caller_number", "");
    }
    
    //! Get event parameter - number
    /*!
      \return <em>String</em> event 'number' parameter
      \sa GetParam()
     */
    public String GetPhoneNumber() {
        return GetParam("number", "");
    }
    
    //! Get event parameter - calling party number based on difference call type
    /*!
      \return <em>String</em> event 'calling_party' parameter
      \sa GetParam()
     */
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
    
    //! Print paramter into debug log for debugging purpose
    /*!
     */
    public void PrintAllParam() {
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();

            debug.log(Level.INFO, "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }
    }
    
    //! Call handle function, play text to speech
    /*!
      \param message Text to speech message to be played.
      \param language Specified langauge to be used for the message, see <a href="https://apidocs.toku.co/Additional-Information-Toku-API/text-to-speech-tts-support-toku" target="_blank">Languages supported</a>.
      \param voice Voice type, see @ref TTS_VOICE_TYPE.
      \param dtmf Set number of DTMF to be capture, valid range between 0 ~ 20, set to 0 if no DTMF input needed to be captured.
      \param handle_interrupt Set to true if need to capture DTMF, a webhook will be send to call handle callback URL, an empty will be sent if no DTMF captured as long as this parameter set to true.
      \param replay Number of times the message will be replay, usually replay will be used if <em>dtmf</em> > 0, the message will be replay if no DTMF captured.
      \param no_dtmf_message Play difference message if <em>dtmf</em> > 0 and no DTMF captured.
      \param extra Adding additional paramter into call handle command
      \return <em>class</em> CallController object instance
      \sa PlayUrl()
    */
    public CallController PlayTTS(String message, String language, String voice, int dtmf,
            boolean handle_interrupt, int replay, String no_dtmf_message,
            Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();
        
        if (no_dtmf_message==null) no_dtmf_message=message;
        
        p.put("function", "PlayTTS");
        p.put("message", message);
        p.put("language", language);
        p.put("voice", voice);
        p.put("dtmf", dtmf);
        
        p.put("handle_interrupt", handle_interrupt);
        p.put("replay", replay);
        p.put("no_dtmf_message", no_dtmf_message);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommands(p);
    }
    
    //! Call handle function, play system media
    /*!
      \param name System wave file, only "beep" are available currently
      \param dtmf Set number of DTMF to be capture, valid range between 0 ~ 20, set to 0 if no DTMF input needed to be captured. 
      \param handle_interrupt Set to true if need to capture DTMF, a webhook will be send to call handle callback URL, an empty will be sent if no DTMF captured as long as this parameter set to true.
      \param replay Number of times the message will be replay, usually replay will be used if <em>dtmf</em> > 0, the message will be replay if no DTMF captured. 
      \param no_dtmf_value Play difference system wave file if <em>dtmf</em> > 0 and no DTMF captured.
      \param extra Adding additional paramter into call handle command
      \return <em>class</em> CallController object instance
      \sa PlayTTS(), PlayURL()
     */
    public CallController PlaySystem(String name, int dtmf, 
            boolean handle_interrupt, int replay, String no_dtmf_value,
            Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();
        
        if (no_dtmf_value==null) no_dtmf_value=name;
        
        p.put("function", "PlayFile");
        p.put("type", "system");
        p.put("value", name);
        p.put("dtmf", dtmf);
        
        p.put("handle_interrupt", handle_interrupt);
        p.put("replay", replay);
        p.put("no_dtmf_value", no_dtmf_value);
        
        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommands(p);
    }
    
    //! Call handle function, play media file from media storage (not open for public)
    /*!
      \param name Storage wave file name
      \param dtmf Set number of DTMF to be capture, valid range between 0 ~ 20, set to 0 if no DTMF input needed to be captured. 
      \param handle_interrupt Set to true if need to capture DTMF, a webhook will be send to call handle callback URL, an empty will be sent if no DTMF captured as long as this parameter set to true.
      \param replay Number of times the message will be replay, usually replay will be used if <em>dtmf</em> > 0, the message will be replay if no DTMF captured. 
      \param no_dtmf_value Play difference wave file if <em>dtmf</em> > 0 and no DTMF captured.
      \param extra Adding additional paramter into call handle command
      \return <em>class</em> CallController object instance
      \sa PlayTTS(), PlayURL()
     */
    public CallController PlayFile(String name, int dtmf, 
            boolean handle_interrupt, int replay, String no_dtmf_value,
            Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();
        
        if (no_dtmf_value==null) no_dtmf_value=name;
        
        p.put("function", "PlayFile");
        p.put("type", "file");
        p.put("value", name);
        p.put("dtmf", dtmf);
        
        p.put("handle_interrupt", handle_interrupt);
        p.put("replay", replay);
        p.put("no_dtmf_value", no_dtmf_value);
        
        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommands(p);
    }
    
    //! Call handle function, play media via given URL
    /*!
      \param url Public accessible media file via URL
      \param dtmf Set number of DTMF to be capture, valid range between 0 ~ 20, set to 0 if no DTMF input needed to be captured. 
      \param handle_interrupt Set to true if need to capture DTMF, a webhook will be send to call handle callback URL, an empty will be sent if no DTMF captured as long as this parameter set to true.
      \param replay Number of times the message will be replay, usually replay will be used if <em>dtmf</em> > 0, the message will be replay if no DTMF captured. 
      \param no_dtmf_value Play difference message if <em>dtmf</em> > 0 and no DTMF captured.
      \param extra Adding additional paramter into call handle command
      \return <em>class</em> CallController object instance
      \sa PlayTTS(), PlayFile()
     */
    public CallController PlayUrl(String url, int dtmf, 
            boolean handle_interrupt, int replay, String no_dtmf_value,
            Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();
       
        if (no_dtmf_value==null) no_dtmf_value=url;
        
        p.put("function", "PlayFile");
        p.put("type", "url");
        p.put("value", url);
        p.put("dtmf", dtmf);
        
        p.put("handle_interrupt", handle_interrupt);
        p.put("replay", replay);
        p.put("no_dtmf_value", no_dtmf_value);
        
        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommands(p);
    }
    
    //! Call handle function, make outbound call
    /*!
    \param called_party Called party number.
    \param calling_party Calling party number.
    \param handle_interrupt Set to true if need to receive B party disconnect event, a webhook will be sent to call handle callback URL.
    \param record_call Set to true if need to record the call conversation.
    
    \param record_callback_url Record record callback URL.
    \param record_callback_method Record callback method.
    \param record_wait_sync Set to true if need to download the recording file uppon receive the recording callback webhook, this is to ensure the recording event to be sent only when the media file are ready to download.
    \param ringtone Ringtone see @ref RINGTONE_TYPE
    \param record_split Set to true if need to record call conversation into sperate recording file.

    
    \param extra Addition parameter to be added into the MakeCall call handle command see <a href="https://apidocs.toku.co/Additional-Information-Toku-API/call-service-webhook/call-handle-command#callhandlecommands1-1" target="_blank">MakeCall</a>
    \return <em>class</em> CallController object instance
    */
    public CallController MakeCall(String called_party, String calling_party, boolean handle_interrupt, boolean record_call,
            String record_callback_url, String record_callback_method, boolean record_wait_sync, String ringtone, boolean record_split,
            Map<String, Object> extra) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (calling_party == null) {
            calling_party = GetDefaultCallingParty();
        }

        p.put("function", "MakeCall");
        p.put("called_party", called_party);
        p.put("calling_party", calling_party);
        p.put("handle_interrupt", handle_interrupt);
        p.put("record_call", record_call);
        
        p.put("record_callback_url", record_callback_url);
        p.put("record_callback_method", record_callback_method);
        p.put("record_wait_sync", record_wait_sync);
        p.put("ringtone", ringtone);
        p.put("record_split", record_split);

        if (extra != null) {
            p.putAll(extra);
        }

        return AddCommands(p);
    }
    
    //! Call handle function, delay next call handle command by given duration (secs)
    /*!
      \param duration Duration in seconds.
      \return <em>class</em> CallController object instance
      \sa GetParam()
     */
    public CallController Sleep(int duration) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (duration < 0) {
            duration = 1;
        }

        p.put("function", "Sleep");
        p.put("duration", duration);
        return AddCommands(p);

    }
    
    //! Call handle function, drap session with specified duration (secs)
    /*!
      \param duration Duration in seconds.
      \sa
     */
    public CallController DropSession(int duration) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (duration < 0) {
            duration = 1;
        }

        p.put("function", "DropSession");
        p.put("duration", duration);

        return AddCommands(p);
    }
    
    //! Call handle function, speech to text
    /*!
      \param language Output language of speech to text
      \param handle_interrupt A speech to text event will be sent if set to true.
      \param duration Speech to text recording duration in seconds.
      \return <em>class</em> CallController object instance
      \sa
     */
    public CallController SpeechToText(String language, boolean handle_interrupt, int duration) {
        LinkedHashMap p = new LinkedHashMap<String, Object>();

        if (duration < 0) {
            duration = 1;
        }

        p.put("function", "SpeechToText");
        p.put("duration", duration);
        p.put("handle_interrupt", handle_interrupt);
        p.put("language", duration);

        return AddCommands(p);
    }

    //! Add command into command response array
    /*!
      \param param Call handle command hash map, see <a href="https://apidocs.toku.co/Additional-Information-Toku-API/call-service-webhook/call-handle-command" target="_blank">Call Handle Command</a>
      \return <em>class</em> CallController object instance 
      \sa Response()
     */
    public CallController AddCommands(LinkedHashMap param) {
        m_FuncList.add(param);
        return this;
    }
    
    //! Print out call handle command
    /*!
      \return <em>class</em> CallController object instance
      \sa AddCommands()
     */
    public void Response() throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(toJsonString());
        }
    }
    
    //! Return JSON call handle command list
    /*!
      \return <em>String</em> JSON call handle command list
      \sa AddCommands()
     */
    public String toJsonString() {
        Map<String, ArrayList> resultMap = new HashMap<String, ArrayList>();

        resultMap.put("commands", m_FuncList);
        JSONObject res = new JSONObject(resultMap);
        return res.toJSONString();
    }
}
