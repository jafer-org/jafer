package org.jafer.portlets.xslt;

import java.io.IOException;

import javax.servlet.*;
import org.w3c.dom.Document;
import javax.xml.transform.Templates;
import javax.portlet.RenderResponse;
import javax.xml.transform.sax.SAXResult;
import java.util.Map;
import javax.xml.transform.dom.DOMSource;
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
import javax.portlet.PortletContext;
import javax.xml.transform.*;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

public class XSLTPortletRequestDispatcher implements PortletRequestDispatcher {
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

    private PortletContext context;
    private Map localeResources;
    private Templates templates;

    /**
     * Creates a XSLT based Dispatcher using the specified XSLT transform file
     * note: for render Urls generate XML of the following form:<br>
     *
     *       &lt;a href="renderURL"&gt;
     *         &lt;portlet:param name="action" value="item" /&gt;
     *      &lt;/a&gt;
     *
     * <br>and for action Urls generate XML of the following form:<br>
     *
     *       &lt;form name="form" method="post" action="actionURL"&gt;
     *        ...
     *       &lt;/form&gt;
     *
     * <br>the dispatcher handles encoding these and any other URLs in the generated fragment according to JSR168/WSRP
     *
     * @param context PortletContext
     * @param xslUrl String
     * @param resource String
     * @param locale Locale
     * @throws TransformerConfigurationException
     */
    private void constructor(PortletContext context, String xslUrl,
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

    /**
     * Creates a XSLT based Dispatcher using the specified XSLT transform file
     * note: for render Urls generate XML of the following form:<br>
     *
     *       &lt;a href="renderURL"&gt;
     *         &lt;portlet:param name="action" value="item" /&gt;
     *      &lt;/a&gt;
     *
     * <br>and for action Urls generate XML of the following form:<br>
     *
     *       &lt;form name="form" method="post" action="actionURL"&gt;
     *        ...
     *       &lt;/form&gt;
     *
     * <br>the dispatcher handles encoding these and any other URLs in the generated fragment according to JSR168/WSRP
     *
     * @param context PortletContext
     * @param xslUrl String
     * @throws TransformerConfigurationException
     */
    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, null, null);
    }

    /**
     * Creates a XSLT based Dispatcher using the specified XSLT transform file
     * note: for render Urls generate XML of the following form:<br>
     *
     *       &lt;a href="renderURL"&gt;
     *         &lt;portlet:param name="action" value="item" /&gt;
     *      &lt;/a&gt;
     *
     * <br>and for action Urls generate XML of the following form:<br>
     *
     *       &lt;form name="form" method="post" action="actionURL"&gt;
     *        ...
     *       &lt;/form&gt;
     *
     * <br>the dispatcher handles encoding these and any other URLs in the generated fragment according to JSR168/WSRP
     *
     * @param context PortletContext
     * @param xslUrl String
     * @param resource String
     * @throws TransformerConfigurationException
     */
    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, Locale.getDefault());
    }

    /**
     * Creates a XSLT based Dispatcher using the specified XSLT transform file
     * note: for render Urls generate XML of the following form:<br>
     *
     *       &lt;a href="renderURL"&gt;
     *         &lt;portlet:param name="action" value="item" /&gt;
     *      &lt;/a&gt;
     *
     * <br>and for action Urls generate XML of the following form:<br>
     *
     *       &lt;form name="form" method="post" action="actionURL"&gt;
     *        ...
     *       &lt;/form&gt;
     *
     * <br>the dispatcher handles encoding these and any other URLs in the generated fragment according to JSR168/WSRP
     *
     * @param context PortletContext
     * @param xslUrl String
     * @param resource String
     * @param locale String
     * @throws TransformerConfigurationException
     */
    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource, String locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, new Locale(locale));
    }

    /**
     * Creates a XSLT based Dispatcher using the specified XSLT transform file
     * note: for render Urls generate XML of the following form:<br>
     *
     *       &lt;a href="renderURL"&gt;
     *         &lt;portlet:param name="action" value="item" /&gt;
     *      &lt;/a&gt;
     *
     * <br>and for action Urls generate XML of the following form:<br>
     *
     *       &lt;form name="form" method="post" action="actionURL"&gt;
     *        ...
     *       &lt;/form&gt;
     *
     * <br>the dispatcher handles encoding these and any other URLs in the generated fragment according to JSR168/WSRP
     *
     * @param context PortletContext
     * @param xslUrl String
     * @param resource String
     * @param locale Locale
     * @throws TransformerConfigurationException
     */
    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource, Locale locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, locale);
    }


    /**
     * Passes the portletServlet to dispatch via an XSLT transform
     * the renderRequest should have the following attributes set (by using
     * renderRequest.setAttribute)
     * org.jafer.portlets.xslt.xmlDocument - XML input to XSLT Transform (as a
     * DOM Document)
     * org.jafer.portlets.xslt.xslParams - Map of parameters to pass to the XSLT
     * transform
     * org.jafer.portlets.xslt.urlParams - Map of parameters to add to portlet
     * action and renderUrls
     *
     * @param renderRequest RenderRequest
     * @param renderResponse RenderResponse
     * @throws PortletException
     * @throws IOException
     */
    public void include(RenderRequest renderRequest,
                        RenderResponse renderResponse) throws PortletException,
            IOException {
        Document xml = (Document) renderRequest.getAttribute(
                "org.jafer.portlets.xslt.xmlDocument");
        if (xml == null) {
            throw new PortletException("No XML Document");
        }
        Map xslParams = null;
        try {
            xslParams = (Map) renderRequest.getAttribute(
                    "org.jafer.portlets.xslt.xslParams");
        } catch (Exception ex1) {
            throw new PortletException("xslParams error", ex1);
        }
        Map urlParams = null;
        try {
            urlParams = (Map) renderRequest.getAttribute(
                    "org.jafer.portlets.xslt.urlParams");
        } catch (Exception ex2) {
            throw new PortletException("urlParams error", ex2);
        }

        try {
            processXSLT(renderResponse, xml, xslParams, urlParams);
        } catch (Exception ex) {
            new PortletException("XSLT Processing Exception", ex);
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

    private void processXSLT(RenderResponse response, Document xml,
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

        PortletContentSerializer filter = new PortletContentSerializer(response,
                urlParams);
        SAXResult result = new SAXResult(filter);
        transformer.setOutputProperty("omit-xml-declaration", "true");
        transformer.transform(new DOMSource(xml), result);
    }
}
