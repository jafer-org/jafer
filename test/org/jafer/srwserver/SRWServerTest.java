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

import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.jafer.databeans.DatabeanManagerFactory;
import org.jafer.databeans.DatabeanManagerFactoryConfig;
import org.jafer.databeans.ZurlFactory;
import org.jafer.exception.JaferException;
import org.jafer.interfaces.DatabeanFactory;
import org.jafer.query.JaferQuery;
import org.jafer.query.QueryBuilder;
import org.jafer.record.Field;
import org.jafer.record.HashtableCacheFactory;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.jafer.util.xml.DOMFactory;
import org.w3c.dom.Node;

/**
 * This class tests the SRWSever stubs that represent the axis web service
 */
public class SRWServerTest extends TestCase
{

    /**
     * Stores a reference to the srw server config file location
     */
    private static final String SRW_CONFIG_FILE = "/org/jafer/conf/srwserver/srwserverconfig.xml";

    /**
     * Stores a reference to the databean manager config file location
     */
    private static final String DATA_MANAGER_CONFIG = "/org/jafer/conf/srwserver/databeanmanagerconfig.xml";

    /**
     * Stores a reference to the databeanManagerfactory
     */
    private DatabeanManagerFactory databeanManagerFactory = null;

    /**
     * initialises the databean manager that is supplied to the SRWServer
     */
    private void initialiseDatabeanManagerFactory()
    {
        databeanManagerFactory = new DatabeanManagerFactory();
        databeanManagerFactory.setName("testfactory");

        ZurlFactory zurl1 = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANCE");
        zurl1.setName("db1");

        databeanManagerFactory.setDatabeanFactories(new DatabeanFactory[] { zurl1 });

    }

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

    private void serialiseSearchRetrieveResponse(SearchRetrieveResponseType response)
    {
        try
        {
            // Make sure we can serialise the object correctly
            QName searchQName = new QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse");
            Serializer ser = SearchRetrieveResponseType.getSerializer(null, SearchRetrieveResponseType.class, searchQName);
            StringWriter responseWriter = new StringWriter();
            ser.serialize(searchQName, null, response, new SerializationContext(responseWriter));
            System.out.println(responseWriter.getBuffer().toString());
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
            fail("Failed axis serialise:" + exc);
        }
    }

    /**
     * Test the SRWServer initialises correctly from config
     */
    public void testSRWServerInitialisationFromConfig()
    {
        try
        {
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, DATA_MANAGER_CONFIG);
            assertNotNull("Failed to create server", server);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer initialises correctly when supplied with a databean
     * manager
     */
    public void testSRWServerInitialisation()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when no factory past
     */
    public void testSRWServerInitialisationWithNoFactory()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            new SRWServer("", "");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getMessage().indexOf("Unable to configure") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer does a simple search retrieving no records
     */
    public void testSRWServerSearchFindsResultsbutDoesNotRetrieveRecords()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);

            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() != null && response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);

            serialiseSearchRetrieveResponse(response);

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

    }

    /**
     * Test the SRWServer does a simple search retrieving records
     */
    public void testSRWServerSearchFindsResultsRetrieveRecords()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

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

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() != null && response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);
            assertTrue("Should have got records", response.getRecords() != null && response.getRecords().length > 0);

            // get the mods data
            RecordType rec = response.getRecords()[0];
            MessageElement[] elements = rec.getRecordData().get_any();
            Node root = DOMFactory.parse(elements[0].getNodeValue()).getDocumentElement();
            Field field = new Field(root, root);
            ModsRecord record = new ModsRecord(field);

            outputModsData(record, 1);

            serialiseSearchRetrieveResponse(response);

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
    }

    /**
     * Test the SRWServer does a simple search retrieving records in XML format
     */
    public void testSRWServerSearchFindsResultsRetrieveXMLRecords()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

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

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should not have had any diagnostics", (response.getDiagnostics() != null && response.getDiagnostics().length == 0));
            assertTrue("Should have got results", response.getNumberOfRecords().intValue() > 0);
            assertTrue("Should have got records", response.getRecords() != null && response.getRecords().length > 0);

            // get the mods data
            RecordType rec = response.getRecords()[0];
            MessageElement[] elements = rec.getRecordData().get_any();
            Node root = elements[0].getFirstChild();
            Field field = new Field(root, root);
            ModsRecord record = new ModsRecord(field);

            outputModsData(record, 1);

            serialiseSearchRetrieveResponse(response);

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
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad version
     */
    public void testBadRequestVersion()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

            // create the query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "golf");

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("9.1");
            request.setQuery(new JaferQuery(query).toCQLQuery().getCQLQuery());

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());

            serialiseSearchRetrieveResponse(response);
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
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad query
     */
    public void testBadRequestQuery()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("6.1");

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());
            diag = response.getDiagnostics()[1];
            assertEquals("Incorrect diag message", "Mandatory parameter not supplied", diag.getMessage());
            assertEquals("Incorrect diag details", "Query", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/7", diag.getUri().getPath());

            serialiseSearchRetrieveResponse(response);
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
    }

    /**
     * Test the SRWServer returns a diagnostic for a bad version and query
     */
    public void testBadRequestVersionAndQuery()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

            // create the request
            SearchRetrieveRequestType request = new SearchRetrieveRequestType();
            request.setVersion("9.1");

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "Unsupported version", diag.getMessage());
            assertEquals("Incorrect diag details", "1.1", diag.getDetails());
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/5", diag.getUri().getPath());

            serialiseSearchRetrieveResponse(response);
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
    }

    /**
     * Test the SRWServer returns a replecated request in response
     */
    public void testRequestReplecatedIntoResponse()
    {
        try
        {
            initialiseDatabeanManagerFactory();
            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

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

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
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

            serialiseSearchRetrieveResponse(response);
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
    }

    /**
     * Test the SRWServer returns the correct diagnostics when the database
     * defined does not exist
     */
    public void testSRWServerSearchFailsDueToBadDatabeanManagerURLs()
    {
        try
        {
            databeanManagerFactory = new DatabeanManagerFactory();
            databeanManagerFactory.setName("testfactory");

            ZurlFactory zurl1 = new ZurlFactory("z3950s://library.ox.ac.uk:210/ADVANC");
            zurl1.setName("db1");

            databeanManagerFactory.setDatabeanFactories(new DatabeanFactory[] { zurl1 });

            SRWServer server = new SRWServer(SRW_CONFIG_FILE, databeanManagerFactory);
            assertNotNull("Failed to create server", server);

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

            // execute and evaluate response
            SearchRetrieveResponseType response = server.searchRetrieveOperation(request);
            assertTrue("Should have got no result", response.getNumberOfRecords().intValue() == 0);

            DiagnosticType diag = response.getDiagnostics()[0];
            assertEquals("Incorrect diag message", "General system error", diag.getMessage());
            assertTrue("Incorrect diag details", diag.getDetails().indexOf("Database unavailable") != -1);
            assertEquals("Incorrect diag URI", "srw/diagnostic/1/1", diag.getUri().getPath());

            serialiseSearchRetrieveResponse(response);

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
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the SRWServerConfig
     */
    public void testBadSRWConfigFileMissingDefaultSchema()
    {
        try
        {
            new SRWServer("/org/jafer/srwserver/testmissingschema.xml", DATA_MANAGER_CONFIG);
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getMessage().indexOf("default schema") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the SRWServerConfig
     */
    public void testBadSRWConfigFileMissingMaxRecords()
    {
        try
        {
            new SRWServer("/org/jafer/srwserver/testmissingmaxrecords.xml", DATA_MANAGER_CONFIG);
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getMessage().indexOf("default max records") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the SRWServerConfig
     */
    public void testBadSRWConfigFileMissingHighVerison()
    {
        try
        {
            new SRWServer("/org/jafer/srwserver/testmissinghighestversion.xml", DATA_MANAGER_CONFIG);
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getMessage().indexOf("highest supported search version") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingName()
    {
        try
        {
            new SRWServer(SRW_CONFIG_FILE, "/org/jafer/srwserver/testmissingdatabeanname.xml");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getMessage().indexOf("databean manager name") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingFactoryName()
    {
        try
        {
            new SRWServer(SRW_CONFIG_FILE, "/org/jafer/srwserver/testNoFactoryName.xml");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getCause().getMessage().indexOf("does not have a name attribute defined") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingFactoryURL()
    {
        try
        {
            new SRWServer(SRW_CONFIG_FILE, "/org/jafer/srwserver/testNoFactoryURL.xml");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getCause().getMessage().indexOf("does not have a url attribute defined") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingFactoryClassName()
    {
        try
        {
            new SRWServer(SRW_CONFIG_FILE, "/org/jafer/srwserver/testNoFactoryClass.xml");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getCause().getMessage().indexOf("does not have a factory class attribute defined") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingFactoryBadFactoryName()
    {
        try
        {
            new SRWServer(SRW_CONFIG_FILE, "/org/jafer/srwserver/testBadFactoryClass.xml");
            fail("should have had an exception");
        }
        catch (JaferException exc)
        {
            if (exc.getCause().getMessage().indexOf("Unable to construct url factory class") == -1)
            {
                fail("JaferException:" + exc);
            }
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigNoCacheFactory()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testNoCacheFactory.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertNull("should not have a cache factory set", databeanManagerFactory.getCacheFactory());
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigCacheFactoryDefault()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testCacheFactoryDefaultSize.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertTrue(
                    "Bad Data Cache Size not default",
                    databeanManagerFactory.getCacheFactory().getCache().getDataCacheSize() == HashtableCacheFactory.DEFAULT_DATACACHE_SIZE);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigCacheFactoryTen()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testCacheFactorySize10.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            ;
            assertTrue("Data Cache Size not 10", databeanManagerFactory.getCacheFactory().getCache().getDataCacheSize() == 10);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig
     */
    public void testBadDBManFacConfigFileMissingMode()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testmissingmode.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertEquals("Missing mode not defaulted correctly", databeanManagerFactory.getMode(),
                    DatabeanManagerFactory.MODE_PARALLEL);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig. Check that mode parrallel is set
     * correctly when an invalid mode value is supplied.
     */
    public void testBadDBManFacConfigFileInvalidModeValue()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testinvalidmode.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertEquals("Missing mode not defaulted correctly", databeanManagerFactory.getMode(),
                    DatabeanManagerFactory.MODE_PARALLEL);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig. Check that mode serial is set
     * correctly.
     */
    public void testBadDBManFacConfigFileModeSerial()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testmodeserial.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertEquals("Mode not set correctly", databeanManagerFactory.getMode(), DatabeanManagerFactory.MODE_SERIAL);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }

    /**
     * Test the SRWServer throws an error when the config file is invalid for
     * the DatabeanManagerFactoryConfig. Check that mode parrallel is set
     * correctly.
     */
    public void testBadDBManFacConfigFileModeParallel()
    {
        try
        {
            DatabeanManagerFactoryConfig config = new DatabeanManagerFactoryConfig();
            config.initialiseFromResourceStream("/org/jafer/srwserver/testmodeparallel.xml");
            databeanManagerFactory = config.getDatabeanManagerFactory();
            assertEquals("Mode not set correctly", databeanManagerFactory.getMode(), DatabeanManagerFactory.MODE_PARALLEL);
        }
        catch (JaferException exc)
        {
            fail("JaferException:" + exc);
        }
    }
}
