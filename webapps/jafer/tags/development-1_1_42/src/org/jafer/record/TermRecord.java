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

package org.jafer.record;
import org.jafer.util.xml.DOMFactory;
//import org.jafer.util.Bib1DiagMessages;

import asn1.BEREncoding;
import asn1.ASN1Exception;
import z3950.v3.TermInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class TermRecord extends DataObject {

  private Node root;
  private TermInfo termInfo;

  public TermRecord(BEREncoding ber) {

    super("", ber);
  }

  public Node getXML(Document document) throws RecordException {

    root = document.createElement("term");
    try {
      termInfo = new TermInfo(getBER(), true);
    } catch (ASN1Exception e) {
      throw new RecordException("ASN1Exception processing record; " + e.toString(), e);
    }
    if (termInfo.s_term!=null) {
      Element el = document.createElement("term");
      el.appendChild(document.createTextNode(termInfo.s_term.c_general.get()));
      root.appendChild(el);
    }
    if (termInfo.s_displayTerm!=null) {
      Element el = document.createElement("display");
      el.appendChild(document.createTextNode(termInfo.s_displayTerm.value.toString()));
      root.appendChild(el);
    }
    if (termInfo.s_globalOccurrences!=null) {
      Element el = document.createElement("occurences");
      el.appendChild(document.createTextNode(Integer.toString(termInfo.s_globalOccurrences.get())));
      root.appendChild(el);
    }
/*
    if (termInfo.s_suggestedAttributes == null) {
      for (int n = 0; n < termInfo.s_suggestedAttributes.value.length; n++) {
//        termInfo.s_suggestedAttributes.value[n].s_attributeSet;
//        termInfo.s_suggestedAttributes.value[n].s_attributeType;
//        termInfo.s_suggestedAttributes.value[n].s_attributeValue;
      }
    }
*/
/**
 * @todo:
 *     termInfo.s_byAttributes
 *     termInfo.s_alternativeTerm
 */
    return root;
  }

  public String toString() {

    return "";
//      return root.toString();
  }
}