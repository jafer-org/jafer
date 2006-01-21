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
package org.jafer.zclient.operations;

import org.jafer.transport.ConnectionException;

import org.jafer.exception.JaferException;
import org.jafer.transport.PDUDriver;
import org.jafer.util.Config;
import org.jafer.record.TermRecord;
import org.jafer.zclient.ZSession;
import org.jafer.query.converter.RPNQueryConverter;

import java.util.Vector;
import org.w3c.dom.Node;

import asn1.ASN1GeneralString;
import asn1.ASN1Integer;
import asn1.ASN1ObjectIdentifier;
import z3950.v3.AttributeSetId;
import z3950.v3.AttributesPlusTerm;
import z3950.v3.DatabaseName;
import z3950.v3.InternationalString;
import z3950.v3.PDU;
import z3950.v3.ScanRequest;
import z3950.v3.ScanResponse;

public class Scan {

  private ZSession session;
  private PDUDriver pduDriver;

  public Scan(ZSession session) {

    this.session = session;
    this.pduDriver = session.getPDUDriver();
  }

/**
 * @todo: handle more that bib1?
 * handle surrogate diags
 * can throw JaferException containing diagnostic
 */
  public Vector scan(String[] databases, int nTerms, int step, int position, Node term) throws JaferException, ConnectionException {

    return scan(databases, nTerms, step, position, RPNQueryConverter.processConstraintModelNode(term).c_op.c_attrTerm);
  }

  public Vector scan(String[] databases, int nTerms, int step, int position, Object termObject) throws JaferException, ConnectionException {

    if (!(termObject instanceof AttributesPlusTerm))
      throw new JaferException("termObject is not of type z3950.v3.AttributesPlusTerm");
    AttributesPlusTerm term = (AttributesPlusTerm)termObject;
    Vector terms = new Vector();
    ScanRequest sr = new ScanRequest();

    sr.s_attributeSet = new AttributeSetId();
    sr.s_attributeSet.value = new ASN1ObjectIdentifier(Config.convertSyntax(Config.getAttributeSetSyntax()));
    DatabaseName[] databaseNames = new DatabaseName[databases.length];
    for (int n = 0; n < databases.length; n++) {
      databaseNames[n] = new DatabaseName();
      databaseNames[n].value = new InternationalString();
      databaseNames[n].value.value = new ASN1GeneralString(databases[n]);
    }

    sr.s_databaseNames = databaseNames;

    sr.s_numberOfTermsRequested = new ASN1Integer(nTerms);
    sr.s_stepSize = new ASN1Integer(step);
    sr.s_preferredPositionInResponse = new ASN1Integer(position);
    sr.s_termListAndStartPoint = term;

    PDU pduResponse = new PDU();
    pduResponse.c_scanRequest = sr;
    pduDriver.sendPDU(pduResponse);
    PDU pduRequest = pduDriver.getPDU();
    ScanResponse response = pduRequest.c_scanResponse;
    if (response == null) {
        throw new ConnectionException("Scan failed");
    }
    if (response.s_scanStatus.get() == 0) {
      for (int n=0; n < response.s_entries.s_entries.length; n++)
        try {
          terms.add(new TermRecord(response.s_entries.s_entries[n].c_termInfo.ber_encode()));
//        response.s_entries.s_entries[0].c_termInfo.
        } catch (Exception e) {
/** @todo handle exception and surrogate diags
 *  can throw JaferException containing diagnostic*/
        }
    }

    return terms;
  }
}
