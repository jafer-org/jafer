/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
 * University. This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jafer.sru.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jafer.sru.SRUException;
import org.jafer.sru.bridge.SRUtoSRWBridge;

/**
 * This class represents the SRU processing servlet
 */
public class SRUServlet extends HttpServlet
{

    /**
     * Stores a reference to the web service URL.
     */
    private String serviceURL = null;

    /**
     * Initialises the servlet
     * 
     * @param config The servlet configuration information
     */
    public void init(ServletConfig config)
    {
        serviceURL = config.getInitParameter("serviceURL");
        
    }

    /**
     * This method processes a put request on the SRUServer. It does nothing
     * other than forward the request to the post method
     * 
     * @param request the HttpServletRequest object that contains the request
     *        the client made of the servlet
     * @param response the HttpServletResponse object that contains the response
     *        the servlet returns to the client
     * @throws ServletException
     * @throws IOException
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
    {
        // forward request on for doGet to process
        doPost(request, response);
    }

    /**
     * This method processes a get request on the SRUServer. It does nothing
     * other than forward the request to the post method
     * 
     * @param request the HttpServletRequest object that contains the request
     *        the client made of the servlet
     * @param response the HttpServletResponse object that contains the response
     *        the servlet returns to the client
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
    {
        // forward request on for doGet to process
        doPost(request, response);
    }

    /**
     * This method processes a post request on the SRUServer. It does nothing
     * other than forward the request to the DoGet method
     * 
     * @param request the HttpServletRequest object that contains the request
     *        the client made of the servlet
     * @param response the HttpServletResponse object that contains the response
     *        the servlet returns to the client
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
    {
        try
        {
            // This is created each time to avoid sychronisation issues.
            SRUtoSRWBridge bridge = new SRUtoSRWBridge(serviceURL);

            // get all the request paramaters and place them in a hash map
            // would just simply pass request param map but this contains string arrays
            // which the bridge class is not expecting
            HashMap requestMap = new HashMap();
            Enumeration enumeration = request.getParameterNames();
            while(enumeration.hasMoreElements())
            {
                String key = (String) enumeration.nextElement();
                requestMap.put(key,request.getParameter(key));
            }
            
            // get all the params inthe request and call the bridge
            String operationResponse = bridge.processRequest(requestMap);

            // set the response type to be XML and output the response
            response.setContentType("text/xml");
            // obtain the writer to write the response
            PrintWriter out = response.getWriter();
            out.println(operationResponse);
            out.close();
        }
        catch (MalformedURLException exc)
        {
            request.setAttribute("errormsg", "SRU configuration failure contact system administrator with these details:<br><br> "
                    + exc);
            // forward to standard error page
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
        catch (SRUException exc)
        {
            request.setAttribute("errormsg", "SRU configuration failure contact system administrator with these details:<br><br> "
                    + exc);
            // forward to standard error page
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
