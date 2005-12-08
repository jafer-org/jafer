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

import org.jafer.util.Config;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

// http://www.crxnet.com/zjava.html
import asn1.BEREncoding;
import asn1.ASN1External;
import asn1.ASN1Exception;
import z3950.v3.DefaultDiagFormat;

public class DataObject {

  private String dbName, schema;
  private BEREncoding ber;
  private Node root;
  private int[] syntax;

  public DataObject() {}

  public DataObject(String dbName, BEREncoding ber) {

    this.dbName = dbName;
    this.ber = ber;
  }

  public DataObject(String dbName, Node root) {

    this.dbName = dbName;
    this.root = root;
  }

  public DataObject(String dbName, Node root, String syntax) {

    this.dbName = dbName;
    this.root = root;
    this.syntax = Config.convertSyntax(syntax);
  }

  public DataObject(Node root, String schema) {

/** @todo for XML records...
     * has implications for methods that call DataObject.getRecordSyntax()?
     * (CT) */
    this.root = root;
    this.schema = schema;
  }

  public int[] getRecordSyntax() throws RecordException {

    /** @todo provide method (getRecordType?) which gets the syntax for DataObjects that hold BER, or a
     *  schema/namespace for XML records.
     * (recordsyntax and OID's aren't really relevant to XML records).
     * (CT)
     */

    if (syntax != null)
      return syntax;

    if (root != null) {
      if (((Element)root).hasAttribute("syntax"))
        this.syntax = Config.convertSyntax(((Element)root).getAttribute("syntax"));
      else throw new RecordException("Syntax attribute not found in XMLRecord root");
    } else if (ber != null) {
      try{
        ASN1External asn1External = new ASN1External(ber, true);
        syntax = asn1External.s_direct_reference.get();
      } catch (ASN1Exception e1) {
        try {
          DefaultDiagFormat defaultDiagFormat = new DefaultDiagFormat(ber, true);
          syntax = defaultDiagFormat.s_diagnosticSetId.get();
        } catch (ASN1Exception e2) {
          throw new RecordException("ASN1Exception processing record; " + e2.toString(), e2);
        }
      }
    } else throw new RecordException("NULL recordSyntax");

    return syntax;
  }

  public String getRecordSchema() throws RecordException {

    if (root == null)
        throw new RecordException("XMLRecord root is NULL");

    if (((Element)root).hasAttribute("schema"))
      return ((Element)root).getAttribute("schema");
    else throw new RecordException("schema attribute not found in XMLRecord root");
  }


  public BEREncoding getBER() throws RecordException {

    return ber;
  }

  public Node getXML(Document document) throws RecordException {

    if (root == null)
        throw new RecordException("XMLRecord root is NULL");
//      return null;

    if (document == null)
        throw new RecordException("DOM Document is NULL");

    if (root.getOwnerDocument() == null)
      return document.importNode(root, true);

    if (root.getOwnerDocument() == (document))
      return root;
    else
      return document.importNode(root, true);
  }

/** @todo Don't really need to pass/use the document do we?
   * Here's another version of the method for testing... (CT) */
    public Node getXML() {

      return root;// even if it is null...
  }

  public String getDatabaseName() {

    return dbName;
  }
}