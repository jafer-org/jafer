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

package org.jafer.registry.uddi;

import java.util.List;
import java.util.Vector;

import org.jafer.registry.RegistryException;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;

/**
 * This class tests the Service Locator it assumes the service manager is
 * working correctly. This test class requires <b>level 2 </b> access to the UBR
 * to run correctly. <br>
 * <br>
 * Microsoft UBR Level definitions are used by all the tests and are defined as:
 * <br>
 * <bR>
 * Level 1 is defined as the ability to only create: <br>
 * <ul>
 * <li>1 businessEntity
 * <li>2 services per business entity
 * <li>2 binding templates per service
 * </ul>
 * Level 2 is unrestricted access to create objects but must be specially
 * requested and jusified with the UBR manager
 */
public class ServiceLocatorLevel2AccessTest extends UDDITest
{

    public ServiceLocatorLevel2AccessTest() throws Exception
    {
    }

    /**
     * Tests finding a service provider by name with a maximum results set
     * functionality by:
     * <UL>
     * <li>Registering 4 service providers
     * <li>Set max returned to 2
     * <li>Search on name with wildcard check that 2 results not 4 returned
     * <li>Set max returned to 4
     * <li>Search on name with wildcard check that 4 results returned
     * <li>Set max returned to 5
     * <li>Search on name with wildcard check that 4 results returned
     * <li>Set max returned to 0
     * <li>Search on name with wildcard check that 4 results returned as no
     * limit applied
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByNameWithMaxResults()
    {
        ServiceProvider provider = null, provider2 = null, provider3 = null, provider4 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY 1");
            provider2 = servman.registerServiceProvider("JAFER COMPANY 2");
            provider3 = servman.registerServiceProvider("JAFER COMPANY 3");
            provider4 = servman.registerServiceProvider("JAFER COMPANY 4");

            servloc.setMaxReturned(2);

            List results = servloc.findServiceProvider("JAFER%");
            assertTrue(results.size() == 2);

            servloc.setMaxReturned(4);
            results = servloc.findServiceProvider("JAFER%");
            assertTrue(results.size() == 4);

            servloc.setMaxReturned(5);
            results = servloc.findServiceProvider("JAFER%");
            assertTrue(results.size() == 4);

            servloc.setMaxReturned(0);
            results = servloc.findServiceProvider("JAFER%");
            assertTrue(results.size() == 4);
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            // set max back to 20
            servloc.setMaxReturned(20);
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider2 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider2);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider3 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider3);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider4 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider4);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
        }
    }

    /**
     * Tests finding a service provider by muliple protocol functionality by:
     * <br>
     * For this test to work we have to cheat and add a wild card on name as
     * protocols are attached to more than just test objects. We do use call
     * first to check we get at least 1 returned result
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Add a second access point of type Z3950
     * <li>Update the service to the registry
     * <li>Do a search for the provider by searching two protocols with OR on
     * <li>Check the service is found and name matches
     * <li>Do a search for the provider by searching two protocols with AND on
     * <li>Check the service is found and name matches
     * <li>Remove the Z3950 protocol access point
     * <li>Update the service to the registry
     * <li>Do a search for the provider by searching two protocols with AND on
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByMulitpleProtocol()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.Z3950.com");
            servman.updateService(service);

            Vector protocols = new Vector();
            protocols.add(Protocol.PROTOCOL_SRW);
            protocols.add(Protocol.PROTOCOL_Z3950);

            List results = servloc.findServiceProvider(null, null, protocols, true);
            assertTrue(results.size() >= 1);
            results = servloc.findServiceProvider("JAFER%", null, protocols, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            results = servloc.findServiceProvider(null, null, protocols, false);
            assertTrue(results.size() >= 1);
            results = servloc.findServiceProvider("JAFER%", null, protocols, false);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "");
            servman.updateService(service);

            results = servloc.findServiceProvider("JAFER%", null, protocols, false);
            assertTrue(results.size() == 0);

        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
        }
    }

    /**
     * Tests finding a service by muliple protocol functionality by: <br>
     * For this test to work we have to cheat and add a wild card on name as
     * protocols are attached to more than just test objects. We do use call
     * first to check we get at least 1 returned result
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Add a second access point of type Z3950
     * <li>Update the service to the registry
     * <li>Do a search for the service by searching two protocols with OR on
     * <li>Check the service is found and name matches
     * <li>Do a search for the service by searching two protocols with AND on
     * <li>Check the service is found and name matches
     * <li>Remove the Z3950 protocol access point
     * <li>Update the service to the registry
     * <li>Do a search for the service by searching two protocols with AND on
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByMulitpleProtocol()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.Z3950.com");
            servman.updateService(service);

            Vector protocols = new Vector();
            protocols.add(Protocol.PROTOCOL_SRW);
            protocols.add(Protocol.PROTOCOL_Z3950);

            List results = servloc.findService(null, null, null, protocols, true);
            assertTrue(results.size() >= 1);
            results = servloc.findService(null, "JAFER%", null, protocols, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            results = servloc.findService(null, null, null, protocols, false);
            assertTrue(results.size() >= 1);
            results = servloc.findService(null, "JAFER%", null, protocols, false);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "");
            servman.updateService(service);

            results = servloc.findService(null, "JAFER%", null, protocols, false);
            assertTrue(results.size() == 0);

        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
        }
    }

    /**
     * Tests finding a service provider by name with a maximum results set
     * functionality by:
     * <UL>
     * <li>Registering 4 service providers
     * <li>Add mixture of categories to each
     * <li>Create 2 category arrays containing selection of categories
     * <li>Search with both arrays with OR and AND seperatly and check correct
     * number are returned
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindMulipleServiceProvidersByCategory()
    {
        ServiceProvider provider = null, provider2 = null, provider3 = null, provider4 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY 1");
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            provider2 = servman.registerServiceProvider("JAFER COMPANY 2");
            provider2.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));

            provider3 = servman.registerServiceProvider("JAFER COMPANY 3");

            provider3.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider3.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            provider4 = servman.registerServiceProvider("JAFER COMPANY 4");
            provider4.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider4.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateServiceProvider(provider);
            servman.updateServiceProvider(provider2);
            servman.updateServiceProvider(provider3);
            servman.updateServiceProvider(provider4);
            
            Vector cats1 = new Vector();
            cats1.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats1.add(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));

            Vector cats2 = new Vector();
            cats2.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats2.add(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));

            List results = servloc.findServiceProvider("", cats1, null, true);
            assertTrue(results.size() == 3);

            results = servloc.findServiceProvider("", cats2, null, true);
            assertTrue(results.size() == 4);

            results = servloc.findServiceProvider("", cats1, null, false);
            assertTrue(results.size() == 2);

            results = servloc.findServiceProvider("", cats2, null, false);
            assertTrue(results.size() == 2);

        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            // set max back to 20
            servloc.setMaxReturned(20);
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider2 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider2);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider3 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider3);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
            if (provider4 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider4);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
        }
    }
    
    /**
     * Tests finding a service  by name with a maximum results set
     * functionality by:
     * <UL>
     * <li>Registering 4 services on a  provider
     * <li>Add mixture of categories to each service
     * <li>Create 2 category arrays containing selection of categories
     * <li>Search with both arrays with OR and AND seperatly and check correct
     * number are returned
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindMulipleServiceByCategory()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY 1");
            Service service1 = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            Service service2 = servman.registerService(provider, "JAFER SERVICE 2", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            Service service3 = servman.registerService(provider, "JAFER SERVICE 3", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            Service service4 = servman.registerService(provider, "JAFER SERVICE 4", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
   
            service1.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service1.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service1.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            service2.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            
            service3.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service3.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            
            service4.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service4.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateService(service1);
            servman.updateService(service2);
            servman.updateService(service3);
            servman.updateService(service4);
            
            Vector cats1 = new Vector();
            cats1.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats1.add(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));

            Vector cats2 = new Vector();
            cats2.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats2.add(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));

            List results = servloc.findService(null,"", cats1, null, true);
            assertTrue(results.size() == 3);

            results = servloc.findService(null,"", cats2, null, true);
            assertTrue(results.size() == 4);

            results = servloc.findService(null,"", cats1, null, false);
            assertTrue(results.size() == 2);

            results = servloc.findService(null,"", cats2, null, false);
            assertTrue(results.size() == 2);

        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            // set max back to 20
            servloc.setMaxReturned(20);
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e)
                {
                    fail("FAILED TO DELETE NEED MANUAL CLEANUP FOR TESTS TO RUN " + e);
                }
            }
           
        }
    }
}