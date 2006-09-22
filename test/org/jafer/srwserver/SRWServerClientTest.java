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
package org.jafer.srwserver;

import gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.jafer.exception.JaferException;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryBuilder;
import org.jafer.record.Field;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class tests the SRWSever by calling it through axis
 */
public class SRWServerClientTest extends TestCase
{

    /**
     * Stores a reference to the service URL of the SRW server
     */
    public static String SERVICE_URL = "http://localhost:8080/SRWServer/services/JaferSRW";

    /**
     * Utility method to print out the mods record
     * 
     * @param mods themods record
     * @param index the index position
     */
    public void outputModsData(ModsRecord mods, int index)
    {
        System.out.println("MODS RECORD:" + index);
        System.out.println("\tTitle    :" + mods.getTitle());
        System.out.println("\tAuthor   :" + mods.getAuthor());
        System.out.println("\tPublisher:" + mods.getField("publisher"));
        System.out.println("\tType     :" + mods.getType());
    }

    /**
     * Test the SRWServer does a simple search retrieving no records
     */
    public void testSRWServerSearchFindsResultsbutDoesNotRetrieveRecords()
    {
        try
        {
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            
            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() == null || response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);

        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }

    }

    /**
     * Test the SRWServer does a simple search retrieving records
     */
    public void testSRWServerSearchFindsResultsRetrieveRecords()
    {
        try
        {
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());
            request.setRecordSchema("http://www.loc.gov/mods/v3");
            request.setMaximumRecords(new NonNegativeInteger("4"));
            request.setRecordPacking("string");
            request.setStartRecord(new PositiveInteger("1"));

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() == null || response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);
            assertTrue("Should have got records", response.getRecords() != null && response.getRecords().length > 0);

            // get the mods data
            RecordType rec = response.getRecords()[0];
            MessageElement[] elements = rec.getRecordData().get_any();
            Node root = DOMFactory.parse(elements[0].getNodeValue()).getDocumentElement();
            Field field = new Field(root, root);
            ModsRecord record = new ModsRecord(field);

            outputModsData(record, 1);

        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

    /**
     * Test the SRWServer does a simple search retrieving records in XML format
     */
    public void testSRWServerSearchFindsResultsRetrieveXMLRecords()
    {
        try
        {
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());
            request.setRecordSchema("http://www.loc.gov/mods/v3");
            request.setMaximumRecords(new NonNegativeInteger("4"));
            request.setRecordPacking("xml");
            request.setStartRecord(new PositiveInteger("1"));

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() == null || response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);
            assertTrue("Should have got records", response.getRecords() != null && response.getRecords().length > 0);

            // get the mods data
            RecordType rec = response.getRecords()[0];
            MessageElement[] elements = rec.getRecordData().get_any();
            Node root = elements[0].getFirstChild();
            Field field = new Field(root, root);
            ModsRecord record = new ModsRecord(field);

            outputModsData(record, 1);

        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad version
     */
    public void testBadRequestVersion()
    {
        try
        {
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("9.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad query
     */
    public void testBadRequestQuery()
    {
        try
        {
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("6.1");
            request.setQuery("");

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);
            assertEquals("invalid number of diagnostics",2, response.getDiagnostics().length);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());
            diag = response.getDiagnostics()[1];
            assertEquals("Incorrect diag message", "Mandatory parameter not supplied", diag.getMessage());
            assertEquals("Incorrect diag details", "Query", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/7", diag.getUri().getPath());
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad version and query
     */
    public void testBadRequestVersionAndQuery()
    {
        try
        {
            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("9.1");
            request.setQuery("");

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

    /**
     * Test the SRWServer returns a replecated request in response
     */
    public void testRequestReplecatedIntoResponse()
    {
        try
        {
            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");
            String cql = new JaferQuery(query).toCQLQuery().getCQLQuery();

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("9.1");
            request.setQuery(cql);
            request.setMaximumRecords(new NonNegativeInteger("1"));
            request.setRecordSchema("abc.schema");
            request.setRecordPacking("xml");
            request.setStartRecord(new PositiveInteger("5"));
            request.setStylesheet(new URI("info:a/b/c"));
            request.setRecordXPath("/f/t/h");
            request.setSortKeys("abc");
            request.setResultSetTTL(new NonNegativeInteger("5"));

            // create the binding stubb and execute the request
            SRWSoapBindingStub binding = new SRWSoapBindingStub(new URL(SERVICE_URL), null);
            SearchRetrieveResponseType response = binding.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());

            EchoedSearchRetrieveRequestType echo = response.getEchoedSearchRetrieveRequest();
            assertEquals("Incorrect echoed request version ", "9.1", echo.getVersion());
            assertEquals("Incorrect echoed request query ", cql, echo.getQuery());
            assertEquals("Incorrect echoed request max records ", 1, echo.getMaximumRecords().intValue());
            assertEquals("Incorrect echoed request record schema ", "abc.schema", echo.getRecordSchema());
            assertEquals("Incorrect echoed request record packing ", "xml", echo.getRecordPacking());
            assertEquals("Incorrect echoed request start record ", 5, echo.getStartRecord().intValue());
            assertEquals("Incorrect echoed request stylesheet ", "a/b/c", echo.getStylesheet().getPath());
            assertEquals("Incorrect echoed request xpath ", "/f/t/h", echo.getRecordXPath());
            assertEquals("Incorrect echoed request sortkeys ", "abc", echo.getSortKeys());
            assertEquals("Incorrect echoed request result set ttl ", 5, echo.getResultSetTTL().intValue());

        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
        catch (RemoteException exc)
        {
            exc.printStackTrace();
            fail("RemoteException:" + exc);
        }
        catch (MalformedURIException exc)
        {
            exc.printStackTrace();
            fail("MalformedURIException:" + exc);
        }
        catch (MalformedURLException exc)
        {
            exc.printStackTrace();
            fail("MalformedURLException:" + exc);
        }
    }

}
