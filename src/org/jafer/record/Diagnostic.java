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
import org.jafer.util.Config;

import asn1.BEREncoding;
import asn1.ASN1Exception;
import z3950.v3.DefaultDiagFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Diagnostic extends DataObject {

  private static final String JAFER_OID = "1.2.840.10003.4.1000.176.1";

  public Diagnostic(String dbName, BEREncoding ber) {

    super(dbName, ber);
  }

  public Diagnostic(String dbName, Node root) {

    super(dbName, root, JAFER_OID);
  }

  private DefaultDiagFormat getDefaultDiagFormat() throws RecordException {

    /** @todo what if the BER is null, ie constructor used root? */
    DefaultDiagFormat defaultDiagFormat = null;
    try {
      defaultDiagFormat = new DefaultDiagFormat(getBER(), true);
    } catch (ASN1Exception e) {
      throw new RecordException("ASN1Exception processing Diagnostic; " + e.toString(), e);
    }

    return defaultDiagFormat;
  }

  public int getCondition() throws RecordException {

    int condition = 0;
    try {
      condition = getDefaultDiagFormat().s_condition.get();
    } catch (NullPointerException e) {}

    return condition;
  }

  public String getAddInfo() throws RecordException {

    String addInfo = "";
    try {
      if (getDefaultDiagFormat().s_addinfo != null && getDefaultDiagFormat().s_addinfo.c_v2Addinfo != null)
        addInfo = getDefaultDiagFormat().s_addinfo.c_v2Addinfo.get();
      else if (getDefaultDiagFormat().s_addinfo != null && getDefaultDiagFormat().s_addinfo.c_v3Addinfo != null)
        addInfo = getDefaultDiagFormat().s_addinfo.c_v3Addinfo.value.get();
    } catch (NullPointerException e) {}

   return addInfo;
  }

  public Node getXML(Document document) throws RecordException {

    Node root = document.createElement("diagnostic");
    Node condition = document.createElement("condition");
    condition.appendChild(document.createTextNode(String.valueOf(getCondition())));
    root.appendChild(condition);

    if (getAddInfo() != null) {
      Node additionalInfo = document.createElement("additionalInformation");
      additionalInfo.appendChild(document.createTextNode(getAddInfo()));
      root.appendChild(additionalInfo);
    }
    return root;
  }

  public BEREncoding getBER() throws RecordException {

    if (super.getBER() != null)
      return super.getBER();
    else
      return null;
    /** @todo if getXML(Document) != null; generate diagnostic (wrapped as BER) from XML */
  }

  public String toString() {

    String s = null;
    try {
      s = "Diagnostic " + getCondition() + " - ";
      s += Config.getBib1Diagnostic(getCondition()) + " ";
      if (!getAddInfo().equals(""))
	s += "(" + Config.getBib1DiagnosticAddInfo(getCondition()) + ": "+ getAddInfo() + ")";
//      s += getAddInfo().equals("") ? "" : "; additional info: " + getAddInfo();
    } catch (RecordException e) {}
    return s;
  }
}
