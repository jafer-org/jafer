package org.jafer.servlet.xslt;

import java.io.IOException;

import javax.servlet.*;
import org.w3c.dom.Document;
import javax.xml.transform.Templates;
import java.util.Map;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Iterator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;

public class XSLTRequestDispatcher implements RequestDispatcher {
    private static TransformerFactory transformerFactory;
    private static DocumentBuilder domBuilder;
    private static Map templatesMap;

    static {
        try {
            templatesMap = new Hashtable();
            transformerFactory = TransformerFactory.newInstance();
            domBuilder = DocumentBuilderFactory.newInstance().
                         newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    private ServletContext context;
    private Map localeResources;
    private Templates templates;

    private void constructor(ServletContext context, String xslUrl,
                             String resource, Locale locale) throws
            TransformerConfigurationException {
        try {
            localeResources = getLocaleResources(resource, locale);
        } catch (Exception ex) {
            localeResources = null;
        }
        this.context = context;
        String fullXsltPath = context.getRealPath(xslUrl);
        templates = (Templates) templatesMap.get(fullXsltPath);
        if (templates == null) {
            StreamSource xsltSource = new StreamSource(new File(fullXsltPath));
            templates = transformerFactory.newTemplates(xsltSource);
            templatesMap.put(xslUrl, templates);
        }
    }

    public XSLTRequestDispatcher(ServletContext context, String xslUrl) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, null, null);
    }

    public XSLTRequestDispatcher(ServletContext context, String xslUrl,
                                        String resource) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, Locale.getDefault());
    }

    public XSLTRequestDispatcher(ServletContext context, String xslUrl,
                                        String resource, String locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, new Locale(locale));
    }

    public XSLTRequestDispatcher(ServletContext context, String xslUrl,
                                        String resource, Locale locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, locale);
    }

    /**
     * Forwards a request from a servlet to another resource (servlet, JSP
     * file, or HTML file) on the server.
     *
     * @param request a {@link ServletRequest} object that represents the
     *   request the client makes of the servlet
     * @param response a {@link ServletResponse} object that represents the
     *   response the servlet returns to the client
     * @throws ServletException if the target resource throws this exception
     * @throws IOException if the target resource throws this exception
     * @todo Implement this javax.servlet.RequestDispatcher method
     */
    public void forward(ServletRequest request, ServletResponse response) throws
            ServletException, IOException {
    }

    /**
     * Includes the content of a resource (servlet, JSP page, HTML file) in
     * the response.
     *
     * @param request a {@link ServletRequest} object that contains the
     *   client's request
     * @param response a {@link ServletResponse} object that contains the
     *   servlet's response
     * @throws ServletException if the included resource throws this
     *   exception
     * @throws IOException if the included resource throws this exception
     * @todo Implement this javax.servlet.RequestDispatcher method
     */
    public void include(ServletRequest request, ServletResponse response) throws
            ServletException, IOException {
        Document xml = (Document) request.getAttribute(
                "org.jafer.servlet.xslt.xmlDocument");
        if (xml == null) {
            throw new ServletException("No XML Document");
        }
        Map xslParams = null;
        try {
            xslParams = (Map) request.getAttribute(
                    "org.jafer.servlet.xslt.xslParams");
        } catch (Exception ex1) {
            throw new ServletException("xslParams error", ex1);
        }
        Map urlParams = null;
        try {
            urlParams = (Map) request.getAttribute(
                    "org.jafer.servlets.xslt.urlParams");
        } catch (Exception ex2) {
            throw new ServletException("urlParams error", ex2);
        }

        try {
            processXSLT(response, xml, xslParams, urlParams);
        } catch (Exception ex) {
            new ServletException("XSLT Processing Exception", ex);
        }
    }

    private Map getLocaleResources(String resourceName, Locale locale) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(
                    resourceName,
                    locale);
            Map resources = new HashMap();
            for (Enumeration keys = resourceBundle.getKeys();
                                    keys.hasMoreElements(); ) {
                String key = (String) keys.nextElement();
                resources.put(key, resourceBundle.getObject(key));
            }
            return resources;
        } catch (java.util.MissingResourceException ex) {
            return null;
        }
    }

    private void processXSLT(ServletResponse response, Document xml,
                             Map xslParams,
                             Map urlParams) throws Exception {
        Transformer transformer = templates.newTransformer();

        if (xslParams == null) {
            xslParams = new Hashtable();
        }
        if (localeResources != null) {
            xslParams.putAll(localeResources);
        }

        for (Iterator names = xslParams.keySet().iterator(); names.hasNext(); ) {
            String paramName = (String) names.next();
            transformer.setParameter(paramName, xslParams.get(paramName));
        }
        StreamResult result = new StreamResult(response.getOutputStream());
        transformer.setOutputProperty("omit-xml-declaration", "true");
        transformer.transform(new DOMSource(xml), result);
    }
}
