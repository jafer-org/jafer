/**
 * JAFER Toolkit Poject.
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
 *
 *
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

import org.jafer.record.DataObject;
import org.jafer.conf.Config;
import org.jafer.util.xml.DOMFactory;

import asn1.BEREncoding;
import asn1.ASN1External;
import asn1.ASN1Exception;
import asn1.ASN1Integer;
import asn1.ASN1GeneralString;
import asn1.ASN1ObjectIdentifier;
import asn1.ASN1Boolean;
import asn1.ASN1GeneralizedTime;

import z3950.RS_generic.GenericRecord;
import z3950.RS_generic.TaggedElement;
import z3950.RS_generic.ElementData;
import z3950.v3.StringOrNumeric;
import z3950.v3.InternationalString;
import z3950.v3.IntUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class GRS1Record extends DataObject {

  private Node root;
  private BEREncoding ber;
  private String dbName;

  public GRS1Record(String dbName, BEREncoding ber) {

    super(dbName, ber);
    this.ber = ber;
  }

//  public GRS1Record(String dbName, Node root) {
//
//    super(dbName, root);
//    this.root = root.getFirstChild();
//  }

  public BEREncoding getBER() throws org.jafer.record.RecordException {

    if (ber != null)
      return ber;
    if (root == null)
      return null;

    String message;
    GenericRecord record = new GenericRecord();
    try {
      record.value = processElement(root.getChildNodes());
      ASN1External asn1External = new ASN1External();
      asn1External.c_singleASN1type = record;
      // set syntax
      asn1External.s_direct_reference = new asn1.ASN1ObjectIdentifier(super.getRecordSyntax());
      ber = asn1External.ber_encode();
    } catch (ASN1Exception e) {
      message = "ASN1Exception processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    } catch (NullPointerException e) {
      message = "NullPointerException processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    } catch (StringIndexOutOfBoundsException e) {
      message = "StringIndexOutOfBoundsException processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    }
    return ber;
  }

  private TaggedElement[] processElement(NodeList list) throws NullPointerException, StringIndexOutOfBoundsException{

    String tagName, tagType, tagValue, contentType, content;
    TaggedElement[] taggedElements = new TaggedElement[list.getLength()];

    for (int i = 0; i < list.getLength(); i++) {
      Element e = (Element)list.item(i);

      tagName = e.getTagName();
      int last = tagName.lastIndexOf("_", tagName.length() - 1);
      tagType = tagName.substring(tagName.lastIndexOf("_", last - 1) + 1, last);
      tagValue = tagName.substring(last + 1);
      contentType = e.getAttribute("type");
      content = Config.getValue(e);

      TaggedElement taggedElement = new TaggedElement();
      taggedElement.s_tagType = getASN1Integer(tagType);
      taggedElement.s_tagValue = getStringOrNumeric(tagValue);
      taggedElement.s_content = getElementData(contentType, content);

      if (e.hasChildNodes() && e.getFirstChild().getNodeType() != Node.TEXT_NODE)
        taggedElement.s_content.c_subtree = processElement(e.getChildNodes());

      taggedElements[i] = taggedElement;
    }
    return taggedElements;
  }

  public Node getXML(Document document) throws RecordException {

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

    String message;
    ASN1External asn1External = null;
    try{
      asn1External = new ASN1External(getBER(), true);
      GenericRecord record = new GenericRecord(asn1External.c_singleASN1type.ber_encode(), true);
      root = document.createElement("GRS1Record");
      processElement(record.value, root, document);
    } catch (ASN1Exception e) {
      message = "ASN1Exception processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    } catch (NullPointerException e) {
      message = "NullPointerException processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    } catch (IOException e) {
      message = "IOException processing GRS1Record; " + e.toString();
      throw new RecordException(message, e);
    }
    return root;
  }

  private void processElement(TaggedElement[] t, Node x, Document d) throws IOException {

    for(int n = 0; n < t.length; n++) {

      String parentTagName = "";
      if (!((Element)x).getTagName().equalsIgnoreCase("GRS1Record"))
        parentTagName = ((Element)x).getTagName();// + "_";

      Element e = d.createElement(parentTagName +
                        "_" + getTag(t[n].s_tagType) +
                        "_" + getTag(t[n].s_tagValue));

      if(t[n].s_content.c_subtree != null)
        processElement(t[n].s_content.c_subtree, e, d);

      else if (t[n].s_content.c_string != null) {
        e.appendChild(d.createTextNode(
                      t[n].s_content.c_string.value.get()));
        e.setAttribute("type", "String");
      }
      else if (t[n].s_content.c_oid != null) {
        e.appendChild(d.createTextNode(
                      Config.convertSyntax(t[n].s_content.c_oid.get())));
        e.setAttribute("type", "OID");
      }
      else if (t[n].s_content.c_numeric != null) {
        e.appendChild(d.createTextNode(
                      Integer.toString(t[n].s_content.c_numeric.get())));
        e.setAttribute("type", "Integer");
      }
      else if (t[n].s_content.c_trueOrFalse != null) {
        e.appendChild(d.createTextNode(
                      t[n].s_content.c_trueOrFalse.get() ? "true" : "false"));
        e.setAttribute("type", "Boolean");
      }
      else if (t[n].s_content.c_date != null) {
        e.appendChild(d.createTextNode(
                      t[n].s_content.c_date.get()));
        e.setAttribute("type", "Date");
      }
      else if (t[n].s_content.c_intUnit != null) {
        e.appendChild(d.createTextNode(
                      Integer.toString(t[n].s_content.c_intUnit.s_value.get())));
        e.setAttribute("type", "intUnit"); /** @todo need expansion to get unit type */
      }

      x.appendChild(e);
    }
  }

  private ElementData getElementData(String contentType, String content) {

    ElementData elementData = new ElementData();

    if (contentType.equalsIgnoreCase("String"))
      elementData.c_string = getInternationalString(content);
    else if (contentType.equalsIgnoreCase("Integer"))
      elementData.c_numeric = getASN1Integer(content);
    else if (contentType.equalsIgnoreCase("OID"))
      elementData.c_oid = getASN1ObjectIdentifier(content);
    else if (contentType.equalsIgnoreCase("Boolean"))
      elementData.c_trueOrFalse = getASN1Boolean(content);
    else if (contentType.equalsIgnoreCase("Date"))
      elementData.c_date = getASN1GeneralizedTime(content);
    else if (contentType.equalsIgnoreCase("intUnit"))
      elementData.c_intUnit = getIntUnit(content);

    return elementData;
  }

  private StringOrNumeric getStringOrNumeric(String s) {

    StringOrNumeric sOr = new StringOrNumeric();
    try {
      int i = Integer.parseInt(s);
      sOr.c_numeric = getASN1Integer(s);
      return sOr;
    } catch (NumberFormatException ex) {
      sOr.c_string = getInternationalString(s);
      return sOr;
    }
  }

  private InternationalString getInternationalString(String s) {

    InternationalString is = new InternationalString();
    is.value = new ASN1GeneralString(s);
    return is;
  }

  private ASN1Integer getASN1Integer(String s) {

    return new ASN1Integer(Integer.parseInt(s));
  }

  private ASN1ObjectIdentifier getASN1ObjectIdentifier(String s) {

    return new ASN1ObjectIdentifier(Config.convertSyntax(s));
  }

  private ASN1Boolean getASN1Boolean(String s) {

    return new ASN1Boolean(Boolean.getBoolean(s));
  }

  private ASN1GeneralizedTime getASN1GeneralizedTime(String s) {

    return new ASN1GeneralizedTime(s);
  }

  private IntUnit getIntUnit(String s) {

    IntUnit iu = new IntUnit();
    iu.s_value = getASN1Integer(s);
    return iu;
  }

  private String getTag(StringOrNumeric sOn) throws IOException {

    if (sOn.c_numeric != null)
      return Integer.toString(sOn.c_numeric.get());
    else
      return javaIdentifierTag(sOn.c_string.value.get());
  }

  private String getTag(ASN1Integer i) {

    return Integer.toString(i.get());
  }

  private String javaIdentifierTag(String s) throws IOException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    for(int i = 0; i < s.length(); i++)
      if(s.charAt(i) != '_' && Character.isJavaIdentifierPart(s.charAt(i)))
        out.write(s.charAt(i));
    out.close();

    return out.toString();
  }

  public String toString() {

    return root.toString();
  }
}