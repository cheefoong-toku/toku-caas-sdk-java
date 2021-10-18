[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/cheefoong-toku/toku-caas-sdk/blob/main/LICENSE)

![Toku](https://caasuser.tokuworld.com/images/logo-inverse.png)

## Documentation
You can find the online document [here](https://apidocs.toku.co/).


## Installation
You can download or clone from [github](https://github.com/cheefoong-toku/toku-caas-sdk-java).
The library files are stored in "<root path>/java/Toku/".

## Dependencies
Download dependencies library and add into your java project:
* Json library [json-simple-1.1.1.jar](https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar)

## Quick Start - Call Handle
#### Import class
Import call controller class in your servlet class.

```java
import Toku.CaaS.V1.CallHandle.CallController;
```

#### Instance
Create a new call controller instance in your servelet request handling function
```java
CallController callCtrl=new CallController(request, response);
```

#### Call Handle Command
Use the call controller to send call handle command such as PlayTTS command.
```java
callCtrl.PlayTTS("Test message 1", "en", "f", 0, null).Response();
```
You can also send multiple command.
```java
callCtrl.PlayTTS("Test message 1", "en", "f", 0, null).
         PlayTTS("Test message 2", "en", "f", 0, null).
         Response();
```

#### Sample Code

```java
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
            CallController callCtrl=new CallController(request, response);
            callCtrl.PlayTTS("Test message", "en", CallController.VOICE_FEMALE, 0, false, 0, null, null).Response();
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

```
