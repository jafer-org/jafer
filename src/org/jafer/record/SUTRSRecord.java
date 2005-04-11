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

import asn1.*;
import org.w3c.dom.*;
import z3950.RS_SUTRS.*;


public class SUTRSRecord extends DataObject {

  private Node root;
  private BEREncoding ber;

  public SUTRSRecord(String dbName, BEREncoding ber) {

    super(dbName, ber);
    this.ber = ber;
  }

  public SUTRSRecord(String dbName, Node root) {

    super(dbName, root);
    this.root = root.getFirstChild();
  }

  public BEREncoding getBER() throws org.jafer.record.RecordException {

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
    }
    return this.ber;
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

    try {
      root = document.createElement("SUTRSRecord");
/** @todo: have to go via ASN1External? */
      ASN1External asn1External = new ASN1External(this.ber, true);
      processRecord(document, asn1External);
    }
    catch (ASN1Exception ex) {
      String message = "ASN1Exception processing SUTRSRecord; " + ex.toString();
      throw new RecordException(message, ex);
    }
    return root;
  }

  private String processRecord(Node node) {
/** @todo make more efficient? */
    String data = "";
    Node child;
    NodeList list = node.getChildNodes();
    for (int i=0; i<list.getLength(); i++) {
      child = list.item(i);
      if (child.getNodeType() == Node.TEXT_NODE)
	data += list.item(i).getNodeValue();
      else
	data += processRecord(child);
    }

    return data + '\n';
  }


  private void processRecord(Document document, ASN1External asn1External) throws ASN1Exception {
/** @todo have to go via SutrsRecord? */
    String data = "";
    SutrsRecord record = new SutrsRecord(
      asn1External.c_singleASN1type.ber_encode(), true);
      if (record.value != null)
        if (record.value.value != null)
          data = record.value.value.get();

    int start = 0, end = data.indexOf(10);
    while (end != -1) {
      String substring = data.substring(start, end);
      addNewLine(document, substring);
      start = end + 1;
      end = data.indexOf(10, start);
    }
  }

  private void addNewLine(Document document, String data) {

    if (!data.equals("")) {
      Node line = document.createElement("line");
      line.appendChild(document.createTextNode(data));
      root.appendChild(line);
    }
  }
}