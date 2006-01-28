/**
 * JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
 * University. This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jafer.zclient;

import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.jafer.query.CQLQuery;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.w3c.dom.Node;

/**
 * This class tests the SRWBinding class
 */
public class SRWBindingTest extends TestCase
{

    /**
     * Simple test to ensure we can search using SRW bridge
     */
    public void testSimpleSearch()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://z3950.loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(cql);
            request.setStartRecord(new PositiveInteger(Integer.toString(1)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(0)));
            // excute the request
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            // make sure we got results back
            assertTrue("Call returned 0 results", response.getNumberOfRecords().intValue() > 0);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("Remote Exception : " + exc);
        }
    }

    /**
     * Simple test to ensure we can scan using SRW bridge
     */
    public void testSimpleScan()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://tweed.lib.ed.ac.uk:8080/elf/search/oxford");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            ScanRequestType request = new ScanRequestType();
            request.setVersion("1.1");
            request.setScanClause(cql);
            // excute the request
            ScanResponseType response = binding.scanOperation(request);
            TermType[] terms = response.getTerms().getTerm();
            assertTrue("Should have got some terms", terms.length > 0);
            // loop round each record make sure the data is not null
            for (int index = 0; index < terms.length; index++)
            {
                assertNotNull("term value should not be null", terms[index].getValue());
                assertTrue("term value should not be empty string", terms[index].getValue().length() > 0);
            }
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("Remote Exception : " + exc);
        }
    }

    /**
     * Simple test to ensure we can search and retreive records using SRW bridge
     */
    public void testSimpleRetrieveSearch()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://z3950.loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(cql);
            request.setStartRecord(new PositiveInteger(Integer.toString(1)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(2)));
            // excute the request
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            // make sure we got results back
            assertTrue("Call returned 0 results", response.getNumberOfRecords().intValue() > 0);
            // get records
            RecordType[] records = response.getRecords().getRecord();
            assertTrue("Should have got two records", records.length == 2);

            // loop round each record make sure the data is not null
            for (int index = 0; index < records.length; index++)
            {
                assertNotNull("record data should not be null", records[index].getRecordData());
            }
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("Remote Exception : " + exc);
        }
    }

    /**
     * Simple test to ensure we can search using SRW bridge and diagnostic
     * returned
     */
    public void testSearchThatReturnsDiagnostic()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://z3950.loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("any", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(cql);
            request.setStartRecord(new PositiveInteger(Integer.toString(1)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(10)));
            // excute the request
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            // make sure we got results back
            assertTrue("Call did not return 0 results", response.getNumberOfRecords().intValue() == 0);
            DiagnosticsType diag = response.getDiagnostics();
            assertNotNull(diag);
            DiagnosticType diagnostic = diag.getDiagnostic(0);
            assertEquals("Bad diagnostic message", "Unsupported index", diagnostic.getMessage());
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("Remote Exception : " + exc);
        }
    }

    /**
     * Test an error is returned from a bad url when preforming search
     */
    public void testMalFormedURLOnSearch()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http//  z3950.loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(cql);
            request.setStartRecord(new PositiveInteger(Integer.toString(1)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(0)));
            // excute the request
            binding.searchRetrieveOperation(request);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getMessage().indexOf("MalformedURLException") != -1);
        }
    }

    /**
     * Test an error is returned from a url that does not exist when preforming
     * search
     */
    public void testNonExistantURLOnSearch()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(cql);
            request.setStartRecord(new PositiveInteger(Integer.toString(1)));
            request.setMaximumRecords(new NonNegativeInteger(Integer.toString(0)));
            // excute the request
            binding.searchRetrieveOperation(request);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getMessage().indexOf("ConnectException") != -1);
        }
    }

    /**
     * Test an error is returned from a bad url when preforming scan
     */
    public void testMalFormedURLOnScan()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http//  z3950.loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            ScanRequestType request = new ScanRequestType();
            request.setVersion("1.1");
            request.setScanClause(cql);
            // excute the request
            binding.scanOperation(request);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getMessage().indexOf("MalformedURLException") != -1);
        }
    }

    /**
     * Test an error is returned from a url that does not exist when preforming
     * scan
     */
    public void testNonExistantURLOnScan()
    {
        try
        {
            // create the binding
            SRWBinding binding = new SRWBinding("http://loc.gov:7090/voyager");
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            String cql = new CQLQuery(new JaferQuery((Node) query)).getCQLQuery();
            // create the request
            ScanRequestType request = new ScanRequestType();
            request.setVersion("1.1");
            request.setScanClause(cql);
            // excute the request
            binding.scanOperation(request);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getMessage().indexOf("ConnectException") != -1);
        }
    }

    

}
