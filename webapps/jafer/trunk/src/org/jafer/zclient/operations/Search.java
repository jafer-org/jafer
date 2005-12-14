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
import org.jafer.record.Diagnostic;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryException;
import org.jafer.exception.JaferException;

import org.w3c.dom.Node;
import z3950.v3.*;
import asn1.*;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Search {

  private ZSession session;
  private PDUDriver pduDriver;
  private static int refId = 0;
  private static Logger logger;

  public Search(ZSession session) {

    this.session = session;
    this.pduDriver = session.getPDUDriver();
    this.logger = Logger.getLogger("org.jafer.zclient");
  }

  public int[] search(Object queryObject, String[] databases, String resultSetName)
    throws JaferException, ConnectionException {

    if (queryObject instanceof RPNQuery)
      return search((RPNQuery)queryObject, databases, resultSetName);
    else if (queryObject instanceof Node)
      return search((Node)queryObject, databases, resultSetName);
    else
      throw new QueryException("Query type: "+ queryObject.getClass().getName() +" not supported", 107, "");
  }

  public int[] search(Node domQuery, String[] databases, String resultSetName)
      throws JaferException, ConnectionException {

    JaferQuery jaferQuery = new JaferQuery(domQuery);
    return search(jaferQuery.toRPNQuery().getQuery(), databases, resultSetName);
  }


  public int[] search(RPNQuery rpnQuery, String[] databases, String resultSetName)
      throws JaferException, ConnectionException {

    SearchRequest search = new SearchRequest();

    try {
      search.s_query = new Query();
      search.s_query.c_type_1 = rpnQuery;
      search.s_smallSetUpperBound = new ASN1Integer(0);
      search.s_largeSetLowerBound = new ASN1Integer(1);
      search.s_mediumSetPresentNumber = new ASN1Integer(0);
      search.s_replaceIndicator = new ASN1Boolean(true);

      search.s_resultSetName = new InternationalString();
      search.s_resultSetName.value = new ASN1GeneralString(resultSetName);
      // s_referenceId is optional
      search.s_referenceId = new ReferenceId();
      search.s_referenceId.value = new ASN1OctetString("org.jafer.zclient.search_" + ++refId);

      DatabaseName dbs[] = new DatabaseName[databases.length];
      for (int n = 0; n < databases.length; n++) {
	dbs[n] = new DatabaseName();
	dbs[n].value = new InternationalString();
	dbs[n].value.value = new ASN1GeneralString(databases[n]);
      }
      search.s_databaseNames = dbs;
    } catch (NullPointerException e) {
      String message = "Search: NullPointer Error" + "(" + e.toString() + ")";
      throw new JaferException(message, e);
    }

    PDU pduResponse = new PDU();
    pduResponse.c_searchRequest = search;
    pduDriver.sendPDU(pduResponse);

    PDU pduRequest = pduDriver.getPDU();
    SearchResponse response = pduRequest.c_searchResponse;
    if (response.s_searchStatus != null && response.s_searchStatus.get() == false) {
      String message = "";
      Diagnostic diagnostic = null;
      /** @todo handle mutliple diagnostics? */
      if (response.s_records.c_nonSurrogateDiagnostic != null) {
	try {
	  diagnostic = new Diagnostic(databases[0], response.s_records.c_nonSurrogateDiagnostic.ber_encode());
	  message = diagnostic.toString();
	} catch (Exception e) {
	  message = "diagnostic not available - ASN1Exception processing diagnostic (" + e.toString() + ")";
	}
      }
      throw new JaferException(message, diagnostic);
    }

    if (response.s_additionalSearchInfo != null && response.s_additionalSearchInfo.value[0] != null) {
      OtherInformation1 info = response.s_additionalSearchInfo.value[0];
      ASN1Sequence targetSeq = (ASN1Sequence)info.s_information.c_externallyDefinedInfo.c_singleASN1type;
      ASN1Any[] targets = targetSeq.get();
      DatabaseName dbName;
      int [] results = new int[targets.length];

      for (int i=0; i<targets.length; i++) {
	try {
	  ASN1Sequence target = (ASN1Sequence)targets[i];
	  ASN1Any[] details = target.get();
	  dbName = new DatabaseName(details[0].ber_encode(), false);
	  if (! dbName.value.value.get().equalsIgnoreCase(databases[i])) {
	    String message = "database name listed in additional search info doesn't match database name in names set.";
	    /** @todo diag condition in exception? */
            logger.log(Level.WARNING, message);
//	    throw new JaferException(message);
	  }
          if (details[1] instanceof ASN1Integer)
            results[i] = ((ASN1Integer)details[1]).get();

	} catch (ASN1Exception ex) {
	  String message = "Error in accessing additional search info.";
	  results[i] = -1;
          logger.log(Level.WARNING, message);
	}
      }

      return results;
    }

    return new int[]{response.s_resultCount.get()};
  }
}
