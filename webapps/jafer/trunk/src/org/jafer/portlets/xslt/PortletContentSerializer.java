/**
 * Copyright � 2002 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or withoutu
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 *
 * Derived from work by Michael Ivanov under the JA-SIG Collaborative license
 *
 *
 */



package org.jafer.portlets.xslt;

import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;
import java.util.Map;
import org.xml.sax.helpers.AttributesImpl;
import java.util.List;
import java.io.IOException;
import org.apache.xml.serialize.OutputFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.XHTMLSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.xml.serialize.Method;

import org.jafer.portlets.*;

public class PortletContentSerializer extends XHTMLSerializer {

    private RenderResponse response;
    private PortletURL actionUrl;
    private PortletURL renderUrl;
    private Map urlParams;
    //private String actionData;
    // Link mode
    private boolean actionMode;
    private AttributesImpl actionAtts;
    private List actionElements;

    private boolean renderMode;
    private AttributesImpl renderAtts;
    private List renderElements;


    public PortletContentSerializer(RenderResponse response, Map urlParams) throws
            IOException {
        super();

        this.response = response;
        this.urlParams = urlParams;

        this.setOutputCharStream(response.getWriter());
        OutputFormat format = new OutputFormat(Method.XML, null, true);
        format.setOmitDocumentType(true);
        format.setOmitXMLDeclaration(true);
        format.setPreserveSpace(false);
        format.setIndenting(true);
        this.setOutputFormat(format);

        actionMode = false;
        actionElements = new ArrayList();
        renderMode = false;
        renderElements = new ArrayList();
    }

    private PortletURL createActionURL() {
        PortletURL actionUrl = response.createActionURL();
        if (urlParams != null && !urlParams.isEmpty()) {
            for (Iterator params = urlParams.keySet().iterator();
                                   params.hasNext(); ) {
                String paramName = (String) params.next();
                String paramValue = (String) urlParams.get(paramName);
                if (paramName != null && paramValue != null)
                    actionUrl.setParameter(paramName, paramValue);
            }
        }
        return actionUrl;
    }

    private PortletURL createRenderURL() {
        PortletURL renderUrl = response.createRenderURL();
        if (urlParams != null && !urlParams.isEmpty()) {
            for (Iterator params = urlParams.keySet().iterator();
                                   params.hasNext(); ) {
                String paramName = (String) params.next();
                String paramValue = (String) urlParams.get(paramName);
                if (paramName != null && paramValue != null)
                    renderUrl.setParameter(paramName, paramValue);
            }
        }
        return renderUrl;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        boolean actionLink = qName.equalsIgnoreCase("a") &&
                             "actionURL".equals(atts.getValue("href"));
        boolean renderLink = qName.equalsIgnoreCase("a") &&
                             "renderURL".equals(atts.getValue("href"));
        if (actionLink) {
            actionAtts = new AttributesImpl(atts);
            actionMode = true;
            if (actionUrl == null)
                actionUrl = createActionURL();
        } else if (renderLink) {
            renderAtts = new AttributesImpl(atts);
            renderMode = true;
            if (renderUrl == null)
                renderUrl = createRenderURL();
        } else if (qName.equalsIgnoreCase("form") &&
                   "actionURL".equals(atts.getValue("action"))) {
            AttributesImpl attsImpl = new AttributesImpl(atts);
            attsImpl.removeAttribute(attsImpl.getIndex("action"));
            attsImpl.addAttribute(uri, "action", "action", "CDATA",
                                  StringEscapeUtils.unescapeHtml(
                    createActionURL().toString()));
            super.startElement(uri, localName, qName, attsImpl);
        } else if (qName.equalsIgnoreCase("portlet:param")) {
            if (actionMode) {
                if (actionUrl == null)
                    actionUrl = createActionURL();
                actionUrl.setParameter(atts.getValue("name"),
                                       atts.getValue("value"));
            } else if (renderMode) {
                if (renderUrl == null)
                    renderUrl = createRenderURL();
                renderUrl.setParameter(atts.getValue("name"),
                                       atts.getValue("value"));
            }
        } else if (actionMode) {
            AttributesImpl attsImpl = new AttributesImpl(atts);
            if (attsImpl.getIndex("src") > 0) {
                String encodedUrl = response.encodeURL(attsImpl.
                        getValue("src"));
                attsImpl.removeAttribute(attsImpl.getIndex("src"));
                attsImpl.addAttribute(uri, "src", "src", "CDATA",
                                      StringEscapeUtils.unescapeHtml(encodedUrl));
            }
            actionElements.add(SAXElement.createStartElement(uri,
                    localName, qName, new AttributesImpl(attsImpl)));
        } else if (renderMode) {
            AttributesImpl attsImpl = new AttributesImpl(atts);
            if (attsImpl.getIndex("src") > 0) {
                String encodedUrl = response.encodeURL(attsImpl.
                        getValue("src"));
                attsImpl.removeAttribute(attsImpl.getIndex("src"));
                attsImpl.addAttribute(uri, "src", "src", "CDATA",
                                      StringEscapeUtils.unescapeHtml(encodedUrl));
            }
            renderElements.add(SAXElement.createStartElement(uri,
                    localName, qName, new AttributesImpl(attsImpl)));
        } else {
            AttributesImpl attsImpl = new AttributesImpl(atts);
            if (attsImpl.getIndex("src") > 0) {
                String encodedUrl = response.encodeURL(attsImpl.getValue("src"));
                attsImpl.removeAttribute(attsImpl.getIndex("src"));
                attsImpl.addAttribute(uri, "src", "src", "CDATA",
                                      StringEscapeUtils.unescapeHtml(encodedUrl));
            }
            super.startElement(uri, localName, qName, attsImpl);
        }
    }

    public void characters(char[] c, int start, int length) throws SAXException {

        if (actionMode) {
            actionElements.add(SAXElement.createDataElement(new String(c, start,
                    length)));
        } else if (renderMode) {
            renderElements.add(SAXElement.createDataElement(new String(c, start,
                    length)));
        } else {
            super.characters(c, start, length);
        }
    }

    public void endElement(String uri, String localName, String qName) throws
            SAXException {

        if (actionMode && qName.equalsIgnoreCase("a")) {
            actionAtts.removeAttribute(actionAtts.getIndex("href"));
            actionAtts.addAttribute(uri, "href", "href", "CDATA",
                                    (actionUrl != null) ?
                                    StringEscapeUtils.
                                    unescapeHtml(actionUrl.toString()) : "");
            super.startElement(uri, localName, qName, actionAtts);
            for (int i = 0; i < actionElements.size(); i++) {
                SAXElement element = (SAXElement) actionElements.get(i);
                if (SAXElement.START_MODE.equals(element.getMode()))
                    super.startElement(element.getUri(), element.getLocalName(),
                                       element.getQName(),
                                       element.getAttributes());
                else if (SAXElement.DATA_MODE.equals(element.getMode())) {
                    char[] chars = element.toString().toCharArray();
                    super.characters(chars, 0, chars.length);
                } else if (SAXElement.END_MODE.equals(element.getMode()))
                    super.endElement(element.getUri(), element.getLocalName(),
                                     element.getQName());
            }
            super.endElement(uri, localName, qName);
            actionUrl = null;
            actionMode = false;
            actionElements.clear();
        } else if (renderMode && qName.equalsIgnoreCase("a")) {
            renderAtts.removeAttribute(renderAtts.getIndex("href"));
            renderAtts.addAttribute(uri, "href", "href", "CDATA",
                                    (renderUrl != null) ?
                                    StringEscapeUtils.
                                    unescapeHtml(renderUrl.toString()) : "");
            super.startElement(uri, localName, qName, renderAtts);
            for (int i = 0; i < renderElements.size(); i++) {
                SAXElement element = (SAXElement) renderElements.get(i);
                if (SAXElement.START_MODE.equals(element.getMode()))
                    super.startElement(element.getUri(), element.getLocalName(),
                                       element.getQName(),
                                       element.getAttributes());
                else if (SAXElement.DATA_MODE.equals(element.getMode())) {
                    char[] chars = element.toString().toCharArray();
                    super.characters(chars, 0, chars.length);
                } else if (SAXElement.END_MODE.equals(element.getMode()))
                    super.endElement(element.getUri(), element.getLocalName(),
                                     element.getQName());
            }
            super.endElement(uri, localName, qName);
            renderUrl = null;
            renderMode = false;
            renderElements.clear();
        } else if (!qName.equalsIgnoreCase("portlet:param")) {
            if (actionMode)
                actionElements.add(SAXElement.createEndElement(uri, localName,
                        qName));
            else if (renderMode)
                renderElements.add(SAXElement.createEndElement(uri, localName,
                        qName));
            else
                super.endElement(uri, localName, qName);
        }
    }
}
