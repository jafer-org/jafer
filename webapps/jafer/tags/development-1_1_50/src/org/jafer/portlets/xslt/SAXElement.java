package org.jafer.portlets.xslt;

import org.xml.sax.Attributes;

public class SAXElement {

    public static final String START_MODE = "start";
    public static final String END_MODE = "end";
    public static final String DATA_MODE = "data";

    private String mode;
    private String uri;
    private String localName;
    private String qName;
    private Attributes atts;

    // characters data
    private String data;

    protected SAXElement(String mode, String uri, String localName,
                         String qName, Attributes atts) {
        this.mode = mode;
        this.localName = localName;
        this.qName = qName;
        this.atts = atts;
        data = null;
    }

    protected SAXElement(String data) {
        this.mode = DATA_MODE;
        this.data = data;
        localName = null;
        qName = null;
        atts = null;
    }

    public static SAXElement createStartElement(String uri, String localName,
                                                String qName, Attributes atts) {
        return new SAXElement(START_MODE, uri, localName, qName, atts);
    }

    public static SAXElement createEndElement(String uri, String localName,
                                              String qName) {
        return new SAXElement(END_MODE, uri, localName, qName, null);
    }

    public static SAXElement createDataElement(String data) {
        return new SAXElement(data);
    }

    public String getMode() {
        return mode;
    }

    public String getUri() {
        return uri;
    }

    public String getLocalName() {
        return localName;
    }

    public String getQName() {
        return qName;
    }

    public Attributes getAttributes() {
        return atts;
    }

    public String toString() {

        if (data != null)
            return data;

        if (mode.equals(START_MODE))
            return "<" + qName + ">";
        else if (mode.equals(END_MODE))
            return "<" + qName + "/>";

        return null;
    }
}
