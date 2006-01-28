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

import junit.framework.TestCase;

import org.jafer.exception.JaferException;
import org.jafer.query.QueryBuilder;
import org.jafer.query.QueryException;
import org.jafer.registry.web.struts.bean.ModsRecord;
import org.w3c.dom.Node;

/**
 * This class tests the SRW client class that creates the correct session and
 * binding
 */
public class SRWClientTest extends TestCase
{

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
     * Test that the SRWClient creates a session that uses an SRU binding as the
     * server only supports SRU and can search for records fine
     */
    public void testSRUBindingSearchForServerSupportingSRUOnly()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This session should support SRU only
            bean.setHost("http://herbie.bl.uk:9080/cgi-bin/blils.cgi");
            bean.setAutoReconnect(1);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("title", "bible");
            System.out.println("START --> CHECK THAT CONNECTION MESSAGES REFER TO SRU");
            int results = bean.submitQuery(query);
            System.out.println("END --> CHECK THAT CONNECTION MESSAGES REFER TO SRU");
            assertTrue("did not get any results", results > 0);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("Jafer Exception : " + exc);
        }
    }

    /**
     * Test that the SRWClient creates a session that uses an SRW binding as the
     * server only supports SRW and can search for records fine
     */
    public void testSRWBindingForServerSupportingSRWOnly()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This session should support SRW only

            // *********** TODO NEED TO GET SRW ONLY HOST ************
            // ******** UNABLE TO FIND AT PRESENT USING SRW/U ********

            bean.setHost("http://tweed.lib.ed.ac.uk:8080/elf/search/oxford");
            bean.setAutoReconnect(1);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            System.out.println("START --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");
            int results = bean.submitQuery(query);
            System.out.println("END --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");

            // make sure we got results
            assertTrue("did not get any results", results > 0);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("Jafer Exception : " + exc);
        }
    }

    /**
     * Test that the SRWClient creates a session that uses an SRW binding even
     * though the server supports SRU and can search for records fine
     */
    public void testSRWBindingSearchForServerSupportingSRWandSRU()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This session should support SRW/U only
            bean.setHost("http://tweed.lib.ed.ac.uk:8080/elf/search/oxford");
            bean.setAutoReconnect(1);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            System.out.println("START --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");
            int results = bean.submitQuery(query);
            System.out.println("END --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");
            // make sure we got results
            assertTrue("did not get any results", results > 0);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("Jafer Exception : " + exc);
        }
    }

    /**
     * Test that the SRWClient creates a session that uses an SRW binding as the
     * server does not send any protocol back in Explain and can search for
     * records fine
     */
    public void testSRWBindingSearchWhenNoProtocolInExplainCreated()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This session does not sepcify protocol
            bean.setHost("http://z3950.loc.gov:7090/voyager");
            bean.setAutoReconnect(1);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("author", "thomas");
            System.out.println("START --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");
            int results = bean.submitQuery(query);
            System.out.println("END --> CHECK THAT CONNECTION MESSAGES REFER TO SRW");
            // make sure we got results
            assertTrue("did not get any results", results > 0);
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            fail("Jafer Exception : " + exc);
        }
    }

    /**
     * Test an error is returned from a bad url
     */
    public void testMalFormedURL()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This url is mal formed due to spaces and mising colon after http
            bean.setHost("http//  z3950.loc.gov:7090/voyager");
            bean.setAutoReconnect(0);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("any", "thomas");
            bean.submitQuery(query);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getCause().getMessage().indexOf("MalformedURLException") != -1);
        }
    }

    /**
     * Test an error is returned from a url that does not exist
     */
    public void testNonExistantURL()
    {
        try
        {
            SRWClient bean = new SRWClient();
            // This url is mal formed due to spaces and mising colon after http
            bean.setHost("http://www.loc.gov:7090/voyager");
            bean.setAutoReconnect(0);
            bean.setRecordSchema("http://www.imsglobal.org/services/rli/xsd/imsRLIManDataSchema_v1p0");

            // create and execute query
            QueryBuilder builder = new QueryBuilder();
            Node query = builder.getNode("any", "thomas");
            bean.submitQuery(query);

            fail("Should have failed due to bad URL");
        }
        catch (QueryException exc)
        {
            exc.printStackTrace();
            fail("Query Exception : " + exc);
        }
        catch (JaferException exc)
        {
            exc.printStackTrace();
            assertTrue("wrong exception", exc.getMessage().indexOf("Connection failure") != -1);
        }
    }

    
}
