package org.jasig.portal.portlet.xslt;

import javax.portlet.*;
import java.io.*;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

import org.jasig.portal.portlet.xslt.serialize.*;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


import javax.xml.parsers.*;
import javax.xml.transform.*;

public class XsltPortlet extends GenericPortlet {

  private static TransformerFactory transformerFactory;
  protected static DocumentBuilder domBuilder;
  private static Map templatesMap;
  static {
    try {
    templatesMap = new Hashtable();
   	transformerFactory = TransformerFactory.newInstance();
   	domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch ( Exception e ) {
  	  throw new RuntimeException(e.toString());
    }
   }

  public XsltPortlet() {
    super();
  }

   private static class SAXElement {

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

	 	protected SAXElement ( String mode, String uri, String localName, String qName, Attributes atts ) {
	 		this.mode = mode;
	 		this.localName = localName;
	 		this.qName = qName;
	 		this.atts = atts;
	 		data = null;
	 	}

	 	protected SAXElement ( String data ) {
	 		this.mode = DATA_MODE;
	 		this.data = data;
	 		localName = null;
	 		qName = null;
	 		atts = null;
	 	}

	 	public static SAXElement createStartElement ( String uri, String localName, String qName, Attributes atts ) {
	 	   return new SAXElement(START_MODE,uri,localName,qName,atts);
	 	}

	 	public static SAXElement createEndElement ( String uri, String localName, String qName ) {
		   return new SAXElement(END_MODE,uri,localName,qName,null);
		}

	 	public static SAXElement createDataElement ( String data ) {
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

	 	  if ( data != null )
	 	  	return data;

	 	  if ( mode.equals(START_MODE) )
	 	  	return "<" + qName + ">";
	 	  else if ( mode.equals(END_MODE) )
	 	  	return "<" + qName + "/>";

	 	  return null;
	 	}
   }

  private class PortletContentSerializer extends XHTMLSerializer {

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


	 public PortletContentSerializer(RenderResponse response, Map urlParams ) throws IOException {
                super(response.getWriter(),null);
		setDisableOutputEscaping(true);
		this.response = response;
		this.urlParams = urlParams;
		actionMode = false;
		actionElements = new ArrayList();
                renderMode = false;
                renderElements = new ArrayList();
	 }

	 private PortletURL createActionURL() {
	   PortletURL actionUrl = response.createActionURL();
	   if ( urlParams != null && !urlParams.isEmpty() ) {
	   	for ( Iterator params = urlParams.keySet().iterator(); params.hasNext(); ) {
	   		String paramName = (String) params.next();
	   		String paramValue = (String) urlParams.get(paramName);
	   		if ( paramName != null && paramValue != null )
	   		 actionUrl.setParameter(paramName,paramValue);
	   	}
	   }
	      return actionUrl;
	 }

         private PortletURL createRenderURL() {
           PortletURL renderUrl = response.createRenderURL();
           if ( urlParams != null && !urlParams.isEmpty() ) {
                for ( Iterator params = urlParams.keySet().iterator(); params.hasNext(); ) {
                        String paramName = (String) params.next();
                        String paramValue = (String) urlParams.get(paramName);
                        if ( paramName != null && paramValue != null )
                         renderUrl.setParameter(paramName,paramValue);
                }
           }
              return renderUrl;
         }

	 public void startElement (String uri, String localName, String qName,  Attributes atts) throws SAXException {

	 	boolean actionLink = qName.equalsIgnoreCase("a") && "actionURL".equals(atts.getValue("href"));
                boolean renderLink = qName.equalsIgnoreCase("a") && "renderURL".equals(atts.getValue("href"));
	 	if ( actionLink ) {
		  actionAtts = new AttributesImpl(atts);
		  actionMode = true;
                  if ( actionUrl == null )
                    actionUrl = createActionURL();
		} else if ( renderLink ) {
                  renderAtts = new AttributesImpl(atts);
                  renderMode = true;
                  if ( renderUrl == null )
                    renderUrl = createRenderURL();
		} else if ( qName.equalsIgnoreCase("form") && "actionURL".equals(atts.getValue("action")) ) {
		   AttributesImpl attsImpl = new AttributesImpl(atts);
		   attsImpl.removeAttribute(attsImpl.getIndex("action"));
		   attsImpl.addAttribute(uri,"action","action","CDATA",createActionURL().toString());
	 	   super.startElement(uri,localName,qName,attsImpl);
		} else if ( qName.equalsIgnoreCase("portlet:param") ) {
		   if ( actionMode ) {
		    if ( actionUrl == null )
		      actionUrl = createActionURL();
		    actionUrl.setParameter(atts.getValue("name"),atts.getValue("value"));
		   } else if ( renderMode ) {
                    if ( renderUrl == null )
                      renderUrl = createRenderURL();
                    renderUrl.setParameter(atts.getValue("name"),atts.getValue("value"));
                   }
		} else if ( actionMode ) {
			actionElements.add(SAXElement.createStartElement(uri,localName,qName,new AttributesImpl(atts)));
                } else if ( renderMode ) {
                        renderElements.add(SAXElement.createStartElement(uri,localName,qName,new AttributesImpl(atts)));
                } else {
                  AttributesImpl attsImpl = new AttributesImpl(atts);
                  if (attsImpl.getIndex("src") > 0) {
                    String encodedUrl = response.encodeURL(attsImpl.getValue("src"));
                    attsImpl.removeAttribute(attsImpl.getIndex("src"));
                    attsImpl.addAttribute(uri, "src", "src", "CDATA", encodedUrl);
                  }
                  super.startElement(uri,localName,qName,attsImpl);
                }
	 }

         public void characters(char c[], int start, int length) throws SAXException {

           if (actionMode) {
             actionElements.add(SAXElement.createDataElement(new String(c, start,
                 length)));
           } else if (renderMode) {
             renderElements.add(SAXElement.createDataElement(new String(c, start,
                 length)));
           }
           else {
             super.characters(c, start, length);
           }
         }

	 public void endElement (String uri, String localName, String qName) throws SAXException {

           if (actionMode && qName.equalsIgnoreCase("a")) {
             actionAtts.removeAttribute(actionAtts.getIndex("href"));
             actionAtts.addAttribute(uri, "href", "href", "CDATA",
                                     (actionUrl != null) ? actionUrl.toString() : "");
             super.startElement(uri, localName, qName, actionAtts);
             for (int i = 0; i < actionElements.size(); i++) {
               SAXElement element = (SAXElement) actionElements.get(i);
               if (SAXElement.START_MODE.equals(element.getMode()))
                 super.startElement(element.getUri(), element.getLocalName(),
                                    element.getQName(), element.getAttributes());
               else if (SAXElement.DATA_MODE.equals(element.getMode())) {
                 char[] chars = element.toString().toCharArray();
                 super.characters(chars, 0, chars.length);
               }
               else if (SAXElement.END_MODE.equals(element.getMode()))
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
                                     (renderUrl != null) ? renderUrl.toString() : "");
             super.startElement(uri, localName, qName, renderAtts);
             for (int i = 0; i < renderElements.size(); i++) {
               SAXElement element = (SAXElement) renderElements.get(i);
               if (SAXElement.START_MODE.equals(element.getMode()))
                 super.startElement(element.getUri(), element.getLocalName(),
                                    element.getQName(), element.getAttributes());
               else if (SAXElement.DATA_MODE.equals(element.getMode())) {
                 char[] chars = element.toString().toCharArray();
                 super.characters(chars, 0, chars.length);
               }
               else if (SAXElement.END_MODE.equals(element.getMode()))
                 super.endElement(element.getUri(), element.getLocalName(),
                                  element.getQName());
             }
             super.endElement(uri, localName, qName);
             renderUrl = null;
             renderMode = false;
             renderElements.clear();
           }
           else if (!qName.equalsIgnoreCase("portlet:param")) {
             if (actionMode)
               actionElements.add(SAXElement.createEndElement(uri, localName, qName));
             else if (renderMode)
               renderElements.add(SAXElement.createEndElement(uri, localName, qName));
             else
               super.endElement(uri, localName, qName);
           }
	 }

  }

  protected void processXSLT ( String xslUrl, Document xml,  RenderResponse response, Map xslParams, Map urlParams ) throws Exception {
  	response.setContentType("text/html");
    Templates templates = (Templates) templatesMap.get(xslUrl);
    if ( templates == null ) {
      StreamSource xsltSource = new StreamSource(this.getClass().getResourceAsStream(xslUrl));
      templates = transformerFactory.newTemplates(xsltSource);
      templatesMap.put(xslUrl,templates);
    }

    Transformer transformer = templates.newTransformer();

    // Adding parameters
    if ( xslParams != null ) {
     for ( Iterator names = xslParams.keySet().iterator(); names.hasNext(); ) {
      String paramName = (String) names.next();
      transformer.setParameter(paramName,xslParams.get(paramName));
     }
    }

    if (urlParams != null) {
      try {
        PortletContentSerializer filter = new PortletContentSerializer(response,
            urlParams);
        SAXResult result = new SAXResult(filter);
        transformer.transform(new DOMSource(xml), result);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
      }
    } else {
    PrintWriter writer = response.getWriter();
      transformer.transform( new DOMSource(xml), new StreamResult(writer));
    }
  }

  protected void processXSLT ( String xslUrl, Document xml,  RenderResponse response, Map xslParams ) throws Exception {
    processXSLT( xslUrl, xml, response, xslParams, null);
  }
}
