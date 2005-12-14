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

    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, null, null);
    }

    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, Locale.getDefault());
    }

    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource, String locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, new Locale(locale));
    }

    public XSLTPortletRequestDispatcher(PortletContext context, String xslUrl,
                                        String resource, Locale locale) throws
            TransformerConfigurationException {
        constructor(context, xslUrl, resource, locale);
    }

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
