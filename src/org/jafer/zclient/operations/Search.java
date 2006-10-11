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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jafer.exception.JaferException;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryException;
import org.jafer.record.Diagnostic;
import org.jafer.transport.ConnectionException;
import org.jafer.transport.PDUDriver;
import org.jafer.zclient.SearchResult;
import org.jafer.zclient.ZSession;
import org.w3c.dom.Node;

import z3950.v3.DatabaseName;
import z3950.v3.InternationalString;
import z3950.v3.OtherInformation1;
import z3950.v3.PDU;
import z3950.v3.Query;
import z3950.v3.RPNQuery;
import z3950.v3.ReferenceId;
import z3950.v3.SearchRequest;
import z3950.v3.SearchResponse;
import asn1.ASN1Any;
import asn1.ASN1Boolean;
import asn1.ASN1Exception;
import asn1.ASN1GeneralString;
import asn1.ASN1Integer;
import asn1.ASN1OctetString;
import asn1.ASN1Sequence;

public class Search
{

    private PDUDriver pduDriver;

    private static int refId = 0;

    private static Logger logger;

    public Search(ZSession session)
    {
        this.pduDriver = session.getPDUDriver();
        logger = Logger.getLogger("org.jafer.zclient");
    }

    public SearchResult[] search(Object queryObject, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {

        if (queryObject instanceof RPNQuery)
            return search((RPNQuery) queryObject, databases, resultSetName);
        else if (queryObject instanceof Node)
            return search((Node) queryObject, databases, resultSetName);
        else if (queryObject instanceof JaferQuery)
            return search((JaferQuery) queryObject, databases, resultSetName);
        else if (queryObject instanceof CQLQuery)
            return search((CQLQuery) queryObject, databases, resultSetName);
        else
            throw new QueryException("Query type: " + queryObject.getClass().getName()
                    + " not supported", 107, "");
    }

    public SearchResult[] search(Node domQuery, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {
        JaferQuery jaferQuery = new JaferQuery(domQuery);
        return search(jaferQuery.toRPNQuery().getQuery(), databases, resultSetName);
    }

    public SearchResult[] search(JaferQuery jaferQuery, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {
        return search(jaferQuery.toRPNQuery().getQuery(), databases, resultSetName);
    }

    public SearchResult[] search(CQLQuery cqlQuery, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {
        return search(cqlQuery.toRPNQuery().getQuery(), databases, resultSetName);
    }

    public SearchResult[] search(RPNQuery rpnQuery, String[] databases, String resultSetName)
            throws JaferException, ConnectionException
    {

        SearchRequest search = new SearchRequest();

        try
        {
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
            for (int n = 0; n < databases.length; n++)
            {
                dbs[n] = new DatabaseName();
                dbs[n].value = new InternationalString();
                dbs[n].value.value = new ASN1GeneralString(databases[n]);
            }
            search.s_databaseNames = dbs;
        }
        catch (NullPointerException e)
        {
            String message = "Search: NullPointer Error" + "(" + e.toString() + ")";
            throw new JaferException(message, e);
        }

        PDU pduResponse = new PDU();
        pduResponse.c_searchRequest = search;
        pduDriver.sendPDU(pduResponse);

        PDU pduRequest = pduDriver.getPDU();
        SearchResponse response = pduRequest.c_searchResponse;
        if (response == null)
        {
            throw new ConnectionException("Search failed");
        }
        if (response.s_searchStatus != null && response.s_searchStatus.get() == false)
        {
            String message = "";
            Diagnostic diagnostic = null;
            /** @todo handle mutliple diagnostics? */
            if (response.s_records.c_nonSurrogateDiagnostic != null)
            {
                try
                {
                    diagnostic = new Diagnostic(databases[0],
                            response.s_records.c_nonSurrogateDiagnostic.ber_encode());
                    message = diagnostic.toString();
                }
                catch (Exception e)
                {
                    message = "diagnostic not available - ASN1Exception processing diagnostic ("
                            + e.toString() + ")";
                }
            }
            throw new JaferException(message, diagnostic);
        }

        if (response.s_additionalSearchInfo != null
                && response.s_additionalSearchInfo.value[0] != null)
        {
            OtherInformation1 info = response.s_additionalSearchInfo.value[0];
            ASN1Sequence targetSeq = (ASN1Sequence) info.s_information.c_externallyDefinedInfo.c_singleASN1type;
            ASN1Any[] targets = targetSeq.get();
            DatabaseName dbName;
            SearchResult[] results = new SearchResult[targets.length];

            for (int i = 0; i < targets.length; i++)
            {
                try
                {
                    ASN1Sequence target = (ASN1Sequence) targets[i];
                    ASN1Any[] details = target.get();
                    dbName = new DatabaseName(details[0].ber_encode(), false);
                    if (!dbName.value.value.get().equalsIgnoreCase(databases[i]))
                    {
                        String message = "database name listed in additional search info doesn't match database name in names set.";
                        /** @todo diag condition in exception? */
                        logger.log(Level.WARNING, message);
                        // throw new JaferException(message);
                    }
                    results[i] = new SearchResult();
                    results[i].setDatabaseName(databases[i]);
                    if (details[1] instanceof ASN1Integer)
                    {
                        results[i].setNoOfResults(((ASN1Integer) details[1]).get());
                    }
                    if (details[2] instanceof ASN1Integer)
                    {
                        results[i].setDiagnostic(new JaferException(
                                "Additional Search Information", ((ASN1Integer) details[1]).get(),
                                ""));
                    }
                }
                catch (ASN1Exception ex)
                {
                    String message = "Error in accessing additional search info.";
                    results[i].setNoOfResults(0);
                    results[i].setDiagnostic(new JaferException(message));
                    logger.log(Level.WARNING, message);
                }
            }

            return results;
        }

        SearchResult results = new SearchResult();
        results.setDatabaseName(null);
        results.setNoOfResults(response.s_resultCount.get());
        results.setDiagnostic(null);

        return new SearchResult[] { results };
    }
}
