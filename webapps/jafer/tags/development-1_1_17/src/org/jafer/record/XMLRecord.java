/**
 * JAFER Toolkit Project.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2001
 *  Company: Oxford University
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.record;

import asn1.*;
import org.w3c.dom.*;
import java.io.StringWriter;
import org.jafer.util.xml.XMLSerializer;
import org.jafer.exception.JaferException;

public class XMLRecord extends DataObject {

  private Node root;
  private String schema;
  private BEREncoding ber;
  public static final String NAMESPACE = "http://www.jafer.org/formats/xml";

  public XMLRecord(String dbName, BEREncoding ber) {

    super(dbName, ber);
    this.ber = ber;
  }

  public XMLRecord(Node root, String schema) {

    super(root, schema);
    /** @todo Why set the root here and in the superclass?
     * do we need db name set?*/
 //    this.root = root.getFirstChild();
    this.root = root;
    this.schema = schema;
  }

  public BEREncoding getBER() throws RecordException {

    if (this.ber != null)
      return this.ber;
    if (root == null)
      return null;

    try {
      ASN1External asn1External = new ASN1External();
      asn1External.c_singleASN1type = new ASN1GeneralString(processRecord(root));
      // set syntax:
      asn1External.s_direct_reference = new asn1.ASN1ObjectIdentifier(super.getRecordSyntax());
      this.ber = asn1External.ber_encode();
    } catch (ASN1Exception e) {
      /** @todo handle exception */
      // build diagnostic
      System.out.println(e.toString());
    } catch (JaferException j) {
      /** @todo handle exception */
      // build diagnostic
      System.out.println(j.toString());
    }

    return this.ber;
  }


  public Node getXML(Document document) throws RecordException {

    /** @todo

     Node node = super.getXML(document);

     if (node != null)
      return node;

     */

    if (document == null)
      return null;

    if (root != null) {
      if (document.equals(root.getOwnerDocument()))
	return root;
      else
	return document.importNode(root, true);
    }

    if (this.ber == null)
      return null;

    try {
      this.root = document.createElement("XMLRecord");
//    ((Element)root).setAttribute("xmlns", NAMESPACE);

      ASN1External asn1External = new ASN1External(this.ber, true);
      ASN1Any data = asn1External.c_singleASN1type;
      String s = ((ASN1GeneralString)data).get();
      Document d = org.jafer.util.xml.DOMFactory.parse(s);
      Node n = d.getDocumentElement();
      /////////////
      String namespace = n.getNamespaceURI();
      ((Element)root).setAttribute("xmlns", namespace);
      ////////////////
      root.appendChild(document.importNode(n, true));
    } catch (ASN1Exception ex) {
      String message = "ASN1Exception processing XMLRecord; " + ex.toString();
      throw new RecordException(message, ex);
    } catch (org.jafer.exception.JaferException ex) {
      String message = "JaferException processing XMLRecord; " + ex.toString();
      throw new RecordException(message, ex);
    }
    return root;
  }

  public int[] getRecordSyntax() {

/** @todo set this somewhere else/another way... */
    return new int[] {1,2,840,10003,5,109,10};
  }

  public String getRecordSchema() throws RecordException {

    if (schema != null)
      return schema;

    return super.getRecordSchema();
  }

  private String processRecord(Node node) throws JaferException {

    StringWriter writer = new StringWriter();
    XMLSerializer.out(node, true, writer);

    return writer.toString();
  }
}