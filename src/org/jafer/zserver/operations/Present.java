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
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */



package org.jafer.zserver.operations;

import org.jafer.transport.ConnectionException;

import org.jafer.transport.PDUDriver;
import org.jafer.util.Config;
import org.jafer.exception.JaferException;
import org.jafer.record.*;
import org.jafer.util.xml.*;
import org.jafer.zserver.Session;

import java.util.logging.*;

import org.w3c.dom.*;
import z3950.v3.*;
import asn1.*;

/**
 * <p>Runs a Z39.50 Present on requested resultSet stored in associated Session.
 * Uses locking on resultSets (databeans) if client has requested concurrent operations.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class Present extends Operation {

  private static RecordFactory recordFactory;
  private PDU pduRequest;
  private PDU pduResponse;

  static {
    recordFactory = new RecordFactory();
  }

  public Present(Session session, PDU pduRequest) {

    super(session, "present");
    this.pduRequest = pduRequest;
    this.pduResponse = new PDU();
  }

  public PDU runOp() throws Exception {

    int start, total;
    int[] preferredSyntax;

    start = pduRequest.c_presentRequest.s_resultSetStartPoint.get();
    total = pduRequest.c_presentRequest.s_numberOfRecordsRequested.get();
    preferredSyntax = pduRequest.c_presentRequest.s_preferredRecordSyntax.get();

    pduResponse.c_presentResponse = new PresentResponse();
    pduResponse.c_presentResponse.s_referenceId = pduRequest.c_presentRequest.s_referenceId;
    pduResponse.c_presentResponse.s_nextResultSetPosition = new ASN1Integer(0);
    pduResponse.c_presentResponse.s_numberOfRecordsReturned = new ASN1Integer(0);
    pduResponse.c_presentResponse.s_records = new Records();
    pduResponse.c_presentResponse.s_presentStatus = new PresentStatus();

    Present(start, total, preferredSyntax);

    return pduResponse;
  }

  public PDU Present(int start, int total, int[] syntax) throws Exception {

//    String requestedRecordSyntax, recordSyntax, recordSchema = null;
    String requestedRecordSyntax, recordSyntax;
   String targetRecordSchema = null, recordSchema = null;
    try {
      requestedRecordSyntax = Config.convertSyntax(syntax);
//      recordSchema = Config.getRecordSerializerTargetSchema(requestedRecordSyntax);
      targetRecordSchema = Config.getRecordSerializerTargetSchema(requestedRecordSyntax);
      /** @todo we're not using recordSyntax! */
      recordSyntax = Config.getRecordSyntax(targetRecordSchema);/** @todo take out after debug... */
    }
    catch (JaferException ex) {
      logger.log(Level.WARNING, getName() + " " + ex.toString(), ex);
      /** @todo handle exception */
    }

    String resultSetName;
    if (pduRequest.c_presentRequest.s_resultSetId.value == null)
      resultSetName = "default";
    else
      resultSetName = pduRequest.c_presentRequest.s_resultSetId.value.value.get();
    org.jafer.interfaces.Present databean = getDatabean(resultSetName);
    databean.setRecordSchema(targetRecordSchema);

    String databaseName;
    Node recordRoot;
    DataObject dataObject;
    BEREncoding ber;
    NamePlusRecord record;
    NamePlusRecord[] records = new NamePlusRecord[total];
    int recNo = start;
    while (recNo < start + total) {
      try {
        databean.setRecordCursor(recNo);
        databaseName = databean.getCurrentDatabase();
        recordRoot = databean.getCurrentRecord().getRoot();
        //////
        recordSchema = recordRoot.getFirstChild().getNamespaceURI();
        recordSyntax = Config.getRecordSyntax(recordSchema);
        //////
        dataObject = new DataObject(databaseName, recordRoot, recordSyntax);
        ber = (BEREncoding)recordFactory.getBER(dataObject, DOMFactory.newDocument(), databean.getRecordCursor());

        record = new NamePlusRecord();
        record.s_record = new NamePlusRecord_record();
        record.s_name = new DatabaseName();
        record.s_name.value = new InternationalString();
        record.s_name.value.value = new ASN1GeneralString(databaseName);
	/** @todo ber may be null: */
	record.s_record.c_retrievalRecord = new ASN1External(ber, true);
        records[recNo - start] = record;
        recNo++;
      } catch (JaferException ex) {
        try {
          getSession().freeDatabean(resultSetName);
	  break;
        } catch (JaferException e) {
          /** @todo return error diagnostic - error locking on bean */
          logger.log(Level.SEVERE, getName() + " " + e.toString());
        }
        logger.log(Level.WARNING, getName() + " recordNumber " + (recNo) + ": " + ex.toString(), ex);
        /**@todo: Create surrogate diagnostic*/
      } catch (ASN1Exception ex) {
	/** @todo if ASN1Exception is thrown, is it caught by this? */
          try {
            getSession().freeDatabean(resultSetName);
          } catch (JaferException e) {
            /** @todo return error diagnostic - error locking on bean */
            logger.log(Level.SEVERE, getName() + " " + e.toString());
          }
          logger.log(Level.WARNING, getName() + " recordNumber " + (recNo) + ": " + ex.toString(), ex);
          /**@todo: Create surrogate diagnostic*/
        }
    }

    pduResponse.c_presentResponse.s_records.c_responseRecords = records;
    pduResponse.c_presentResponse.s_nextResultSetPosition = new ASN1Integer(recNo);
    pduResponse.c_presentResponse.s_numberOfRecordsReturned = new ASN1Integer(recNo - start);
    if (recNo - start == total)
      pduResponse.c_presentResponse.s_presentStatus.value = new ASN1Integer(pduResponse.c_presentResponse.s_presentStatus.E_success);
    else if (recNo - start == 0) {
      pduResponse.c_presentResponse.s_presentStatus.value = new ASN1Integer(pduResponse.c_presentResponse.s_presentStatus.E_failure);
      pduResponse.c_presentResponse.s_records.c_responseRecords = null;
    } else {
      pduResponse.c_presentResponse.s_presentStatus.value = new ASN1Integer(pduResponse.c_presentResponse.s_presentStatus.E_partial_1);
    }

    return pduResponse;
  }

  private org.jafer.interfaces.Present getDatabean(String resultSetName) throws OperationException {

    org.jafer.interfaces.Present databean;
    if (getSession().containsDatabean(resultSetName))
      databean = (org.jafer.interfaces.Present)getSession().getDatabean(resultSetName);
    else
      // diagnostic - specified result set does not exist
      throw new OperationException(
      getName() + " " + Config.getBib1Diagnostic(30), getDiagnostic(30, null));

    try {
      getSession().lockDatabean(resultSetName);
    } catch (Exception ex) {
      // diagnostic - result set is in use
      throw new OperationException(
          getName() + " " + Config.getBib1Diagnostic(28) + "; " + ex.toString(), getDiagnostic(28, null), ex);
    }
    return databean;
  }

  public PDU getDiagnostic(int condition, String addInfo) {

    pduResponse.c_presentResponse.s_records = new Records();
    pduResponse.c_presentResponse.s_records.c_nonSurrogateDiagnostic = getDiagnostic(new DefaultDiagFormat(), condition, addInfo);
    pduResponse.c_presentResponse.s_presentStatus.value = new ASN1Integer(pduResponse.c_presentResponse.s_presentStatus.E_failure);
//    pduResponse.c_presentResponse.s_numberOfRecordsReturned = new ASN1Integer(1);
//    pduResponse.c_presentResponse.s_nextResultSetPosition = new ASN1Integer(1);
    return pduResponse;
  }
}
