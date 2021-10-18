package Toku.CaaS.V1.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Toku.CaaS.V1.CallHandle.CallController;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@WebServlet(name = "TestClass", urlPatterns = {"/toku_caas_response_sample"})

/*! Servlet Test Class */
/*!
 * Servlet test class, simply reponse play TTS JSON to the webhook.
 */
public class TestClass extends HttpServlet {
    
    private static final Logger debug = Logger.getLogger(TestClass.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        
        try (PrintWriter out = response.getWriter()) {

            HashMap<String,Object> extra = new HashMap<>();

            CallController callCtrl=new CallController(request, response);
            
            extra.put("ext",1);//for testing only
            
            callCtrl.PrintAllParam();
            
            callCtrl.
                    PlayTTS("Test message", "en", CallController.VOICE_FEMALE, 0, false, 0, null, extra).
                    MakeCall("[called_party]", "[calling_party]", false, false,"http://test.com", "GET", true, "AU", false, null).
                    Response();
            
        }
        catch (Exception ex)
        {
            debug.log(Level.INFO,"Exception");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
