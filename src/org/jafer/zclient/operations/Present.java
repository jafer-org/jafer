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

import org.jafer.zclient.ZSession;
import org.jafer.transport.PDUDriver;
import org.jafer.record.DataObject;
import org.jafer.record.Diagnostic;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Vector;

import z3950.v3.*;
import asn1.*;

public class Present {

  private static Logger logger;
  private ZSession session;
  private PDUDriver pduDriver;

  public Present(ZSession session) {

    logger = Logger.getLogger("org.jafer.zclient");
    this.session = session;
    this.pduDriver = session.getPDUDriver();
  }

  public  Vector present(int nRecord, int nRecords, int[] recordOID, String eSpec, String resultSetName)
      throws PresentException, ConnectionException {

    Vector dataObjects = new Vector();
    PresentRequest pr = new PresentRequest();

    pr.s_resultSetId = new ResultSetId();
    pr.s_resultSetId.value = new InternationalString();
    pr.s_resultSetId.value.value = new ASN1GeneralString(resultSetName);
    pr.s_resultSetStartPoint = new ASN1Integer(nRecord);
    pr.s_numberOfRecordsRequested = new ASN1Integer(nRecords);
    pr.s_recordComposition = new PresentRequest_recordComposition();
    pr.s_recordComposition.c_simple = new ElementSetNames();
    pr.s_recordComposition.c_simple.c_genericElementSetName =
      new InternationalString();
    pr.s_recordComposition.c_simple.c_genericElementSetName.value =
      new ASN1GeneralString(eSpec);

    pr.s_preferredRecordSyntax = new ASN1ObjectIdentifier(recordOID);

    PDU pduResponse = new PDU();
    pduResponse.c_presentRequest = pr;
    pduDriver.sendPDU(pduResponse);
    PDU pduRequest = pduDriver.getPDU();

    PresentResponse response = pduRequest.c_presentResponse;
    if (response == null) {
        throw new ConnectionException("Scan failed");
    }
    String dbName = org.jafer.zclient.ZClient.DEFAULT_DATABASE_NAME;
    int nReturned = 0, status = 0;
    if (response.s_numberOfRecordsReturned != null)
      nReturned = response.s_numberOfRecordsReturned.get();
    if (response.s_presentStatus.value != null)
      status = response.s_presentStatus.value.get();
    String message = null;

	int records = nRecords > response.s_records.c_responseRecords.length
		? response.s_records.c_responseRecords.length : nRecords;

    if (status == PresentStatus.E_success) {

      for (int n = 0; n < records; n++) {
        NamePlusRecord nr = response.s_records.c_responseRecords[n];
        if (nr.s_name != null)
          dbName = nr.s_name.value.value.get();

        try {

          if (nr.s_record.c_retrievalRecord != null){
            dataObjects.add(new DataObject(dbName,
              nr.s_record.c_retrievalRecord.ber_encode()));

          } else if  (nr.s_record.c_surrogateDiagnostic != null) {
              dataObjects.add(new DataObject(dbName,
                nr.s_record.c_surrogateDiagnostic.c_defaultFormat.ber_encode()));
              message = "Session Present (Record " + (nRecord + n) + "): " + new Diagnostic(dbName,
                nr.s_record.c_surrogateDiagnostic.c_defaultFormat.ber_encode()).toString();
              logger.log(Level.WARNING, message);
          }
        } catch (ASN1Exception e) {
          message = "Record(s) not available: ASN1Exception processing record(s); " + e.toString();
          throw new PresentException(status, nReturned, message, e);
        }
      }
    } else
        throw getPresentException(status, nReturned, dbName, response);

    return dataObjects;
  }

  private PresentException getPresentException(int status, int nReturned, String dbName, PresentResponse response) {

    String message = null;
    PresentException presentException = null;
    Diagnostic[] diagnostics = null;

    if (status == 1)
      message = "Some records were not returned (request was terminated by access control)";
    else if (status == 2)
      message = "Some records were not returned (message size is too small)";
    else if (status == 3)
      message = "Some records were not returned (request was terminated by resource control, at origin request)";
    else if (status == 4)
      message = "Some records were not returned (request was terminated by resource control, by the target)";
    else if (status == 5)
      message = "no records were returned (one or more non-surrogate diagnostics were returned)";

    try {
      if (response.s_records.c_multipleNonSurDiagnostics != null) {
        diagnostics = new Diagnostic[response.s_records.c_multipleNonSurDiagnostics.length];
        for (int i = 0; i < response.s_records.c_multipleNonSurDiagnostics.length; i++)
          diagnostics[i] = new Diagnostic(dbName,
            response.s_records.c_multipleNonSurDiagnostics[i].c_defaultFormat.ber_encode());

      } else if (response.s_records.c_nonSurrogateDiagnostic != null) {
          diagnostics = new Diagnostic[1];
          diagnostics[0] = new Diagnostic(dbName,
            response.s_records.c_nonSurrogateDiagnostic.ber_encode());
      }

      if (diagnostics != null)/** @todo went into constant looping here... */
        presentException =  new PresentException(status, nReturned, diagnostics, message);
      else
        presentException =  new PresentException(status, nReturned, message);

    } catch (ASN1Exception e) {
      message = "Diagnostic(s) not available: ASN1Exception processing diagnostic(s); " + e.toString();
      presentException =  new PresentException(status, nReturned, message, e);
    }

    return presentException;
  }
}
