package org.jafer.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.io.InputStream;
import java.net.URL;
import java.beans.XMLDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.jafer.util.xml.DOMFactory;
import org.jafer.util.xml.XMLTransformer;
import java.io.ByteArrayOutputStream;
import org.jafer.util.xml.XMLSerializer;
import java.io.ByteArrayInputStream;

public class CompressedXMLDecoder {
  private InputStream in;

  public CompressedXMLDecoder(InputStream in) {
    this.in = in;
  }

  public Object readObject() {
    try {
      URL resource;
      Document docin = DOMFactory.parse(in);
      Node xmlIn = docin.getDocumentElement();
      resource =  this.getClass().getClassLoader().getResource("org/jafer/xsl/beans/bean-decode.xsl");
      Node xml = XMLTransformer.transform(xmlIn, resource);
      ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
      XMLSerializer.out(xml, true, bytesout);
      XMLDecoder fromXml = new XMLDecoder(new ByteArrayInputStream(bytesout.toByteArray()));
      return fromXml.readObject();
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void close() {
    try {
      in.close();
    } catch (Exception ex) {
      /**@todo: ignore this?
       */
    }
    in = null;
    System.gc();
  }
}
