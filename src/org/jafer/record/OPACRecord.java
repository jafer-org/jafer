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

import asn1.BEREncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import z3950.RS_opac.HoldingsRecord;
import z3950.RS_opac.HoldingsAndCircData;
import asn1.ASN1Any;
import java.lang.reflect.Field;


public class OPACRecord extends DataObject {
  private Node root;
  private BEREncoding ber;

  public static final String OPAC_NAMESPACE = "http://www.jafer.org/formats/opac";

  public OPACRecord(String dbName, BEREncoding ber) {

    super(dbName, ber);
    this.ber = ber;
  }

  public OPACRecord(String dbName, Node root) {

    super(dbName, root);
    this.root = root;
  }

  public BEREncoding getBER() throws org.jafer.record.RecordException {
    /**@todo: Override this org.jafer.record.DataObject method*/
    return super.getBER();
  }

  public Node getXML(Document document) throws org.jafer.record.RecordException {

    if (document == null)
        return null;
    if (root != null)
        if (document.equals(root.getOwnerDocument()))
            return root;
        else
            return document.importNode(root, true);
    if (this.ber == null)
        return null;

    String message;
  // using namespaces on all elements:
    root = document.createElementNS(OPAC_NAMESPACE, "OPACRecord");
//    ((Element)root).setAttribute("xmlns", OPAC_NAMESPACE);

    z3950.RS_opac.OPACRecord opacRec = new z3950.RS_opac.OPACRecord();

    try {
      asn1.ASN1External ext = new asn1.ASN1External(this.getBER(), true);
      opacRec = new z3950.RS_opac.OPACRecord(ext.c_singleASN1type.ber_encode(), true);
    } catch (asn1.ASN1Exception e) {
      //@todo: Catch this
      e.printStackTrace();
    }

    if (opacRec.s_bibliographicRecord != null) {
      try {
        MARCRecord bibRec = new MARCRecord(this.getDatabaseName(), opacRec.s_bibliographicRecord.ber_encode());
	Node bib = document.createElementNS(OPAC_NAMESPACE, "bibliographic");
        bib.appendChild(bibRec.getXML(document));
        //@todo: Convert MARC to MODs if set?
        root.appendChild(bib);
      } catch (asn1.ASN1Exception e) {
        //@todo: Catch this
        e.printStackTrace();
      }
    }

    HoldingsRecord[] holdRecs = opacRec.s_holdingsData;
    if (holdRecs != null) {
      Node holding, holdings = document.createElementNS(OPAC_NAMESPACE, "holdings");
      for (int n = 0; n < holdRecs.length; n++) {
        holding = document.createElementNS(OPAC_NAMESPACE, "holdingRecord");
        if (holdRecs[n].c_marcHoldingsRecord != null) {
          try {
            MARCRecord holdRec = new MARCRecord(this.getDatabaseName(), holdRecs[n].c_marcHoldingsRecord.ber_encode());
            holding.appendChild(holdRec.getXML(document));
          } catch (asn1.ASN1Exception e) {
            //@todo: Catch this
            e.printStackTrace();
          }
        } else
          processHoldingsAndCirc(holding, holdRecs[n].c_holdingsAndCirc);
        holdings.appendChild(holding);
      }
      root.appendChild(holdings);
    }
    return root;
  }

  private void processHoldingsAndCirc(Node node, HoldingsAndCircData data) {

    Node el = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, "holdingsAndCirc");

    if (data.s_circulationData != null) {
      Node circ, circs = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, "circRecords");
      for (int n=0; n < data.s_circulationData.length; n++) {
	circ = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, "circulationData");
	processAny(circ, data.s_circulationData[n]);
	circs.appendChild(circ);
      }
      el.appendChild(circs);
    }

    if (data.s_volumes != null) {
      Node vol, vols = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, "volumes");
      for (int n=0; n < data.s_volumes.length; n++) {
	vol = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, "volume");
	processAny(vol, data.s_volumes[n]);
	vols.appendChild(vol);
      }
      el.appendChild(vols);
    }
    processAny(el, data);
    node.appendChild(el);
  }

  private void processAny(Node node, ASN1Any data) {

    Node element, text = null;
    String name;
    Field field[] = data.getClass().getFields();

    for (int n=0; n < field.length; n++) {
      try {
	if (field[n].getName().startsWith("s_") && field[n].get(data) != null) {
	  name = field[n].getName().substring(2);
	  element = node.getOwnerDocument().createElementNS(OPAC_NAMESPACE, name);
	  if (field[n].getType().isInstance(new z3950.v3.InternationalString()))
	    text = node.getOwnerDocument().createTextNode(((z3950.v3.InternationalString)field[n].get(data)).value.get());
	  else if (field[n].getType().isInstance(new asn1.ASN1Boolean(true)))
	    text = node.getOwnerDocument().createTextNode(Boolean.toString(((asn1.ASN1Boolean)field[n].get(data)).get()));
	  else if (field[n].getType().isInstance(new asn1.ASN1Integer(0)))
	    text = node.getOwnerDocument().createTextNode(Integer.toString(((asn1.ASN1Integer)field[n].get(data)).get()));
	  element.appendChild(text);
	  node.appendChild(element);
	}
      } catch (java.lang.IllegalAccessException e) {
	//@todo: Catch this
	e.printStackTrace();
      }
    }
  }
}