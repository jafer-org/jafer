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
 * This class tests the Service Locator, it assumes the service manager is
 * working correctly. This test class only requires <b>level 1 </b> access to
 * the UBR to run correctly. <br>
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
public class ServiceLocatorLevel1AccessTest extends UDDITest
{

    /**
     * Tests the create contact call by:
     * <UL>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * <li>
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public ServiceLocatorLevel1AccessTest() throws Exception
    {
    }

    /**
     * Tests finding a service provider by name functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Do a search for the provider and checking that only one result is
     * returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByName()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");

            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

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
     * Tests finding a service provider by wildcard name functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Do a search with % wildcard for the provider and checking that only
     * one result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByWildCardName()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");

            List results = servloc.findServiceProvider("JAFER%");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

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
     * Tests finding a service provider by catagory functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Add 3 categories
     * <li>Update the service proivder to the registry
     * <li>Do a search for the provider by category checking that only one
     * result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByCategory()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateServiceProvider(provider);

            List results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

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
     * Tests finding a service provider by muliple catagory functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Add 3 categories
     * <li>Update the service proivder to the registry
     * <li>Do a search for the provider by searching two category with OR on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the provider by searching two category with AND on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the provider by searching two category one which is
     * not attached with OR on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the provider by searching two category one which is
     * not attached with AND on
     * <li>check that no result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByMulipleCategory()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateServiceProvider(provider);

            Vector cats = new Vector();
            cats.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats.add(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));

            List results = servloc.findServiceProvider("", cats, null, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            results = servloc.findServiceProvider("", cats, null, false);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            cats.add(regman.getCategory(CategoryType.CATEGORY_DDC, "not attached value"));

            results = servloc.findServiceProvider("", cats, null, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            results = servloc.findServiceProvider("", cats, null, false);
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
     * Tests finding a service provider by protocol functionality by: <br>
     * For this test to work we have to cheat and add a wild card on name as
     * protocols are attached to more than just test objects. We do use call
     * first to check we get at least 1 returned result
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Do a search for the provider by protocol checking that only one
     * result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceProviderByProtocol()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            // as more than one in registry likly to have protocol just check
            // more than 1
            List results = servloc.findServiceProvider(Protocol.PROTOCOL_SRW);
            assertTrue(results.size() >= 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // now use filter call and add an extra search on name as well
            Vector protocols = new Vector();
            protocols.add(Protocol.PROTOCOL_SRW);
            // as more than one in registry likly to have protocol just check
            // more than 1
            results = servloc.findServiceProvider("JAFER%", null, protocols, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

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
     * Tests getting a service provider functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Do a search for the provider and checking that only one result is
     * returned with a matching name
     * <li>Get an instance of the provider using the provider info
     * <li>Check the name matches
     * <li>Get a new instance of the provider using the service through get and
     * make sure the names match
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testGetServiceProvider()
    {
        ServiceProvider provider = null, provider2 = null, provider3 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            provider2 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(provider2);
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider2.getName());

            provider3 = servloc.getServiceProvider(service);
            assertNotNull(provider2);
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider3.getName());

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
     * Tests getting a service provider functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Do a search for the provider and checking that only one result is
     * returned with a matching name
     * <li>Get an instance of the provider using the provider info
     * <li>Check the name matches
     * <li>Get a new instance of the provider using the service through get and
     * make sure the names match
     * <li>Update original provider object to have a description
     * <li>Confirm that the orginal description is different from the one just
     * obtained
     * <li>Updated the original provider to the registry
     * <li>Refresh the retreieved provider
     * <li>Confirm that the orginal description is no longer different from the
     * refreshed provider
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRefreshServiceProvider()
    {
        ServiceProvider provider = null, provider2 = null, provider3 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");

            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            provider2 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(provider2);
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider2.getName());

            provider.setDescription("DESC");
            assertFalse(provider.getDescription().equals(provider2.getDescription()));
            servman.updateServiceProvider(provider);

            provider3 = servloc.refreshServiceProvider(provider2);
            assertTrue(provider.getDescription().equals(provider3.getDescription()));

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
     * Tests finding a service by name functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Do a search for the service and checking that only one result is
     * returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByName()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findService("JAFER SERVICE");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

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
     * Tests finding a service by service provider info functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Search for the service provider to get service provider info
     * <li>Do a search for the service and checking that only one result is
     * returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByServiceProviderInfo()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            results = servloc.findService((ServiceProviderInfo) results.get(0));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

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
     * Tests finding a service by wildcard name functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Do a search with % wildcard for the service and checking that only
     * one result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByWildCardName()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findService("JAFER%");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

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
     * Tests finding a service by category functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Add 3 categories
     * <li>Update the service to the registry
     * <li>Do a search for the provider by category checking that only one
     * result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByCategory()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateService(service);

            List results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

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
     * Tests finding a service by muliple catagory functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Add 3 categories
     * <li>Update the service to the registry
     * <li>Do a search for the service by searching two category with OR on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the service by searching two category with AND on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the service by searching two category one which is
     * not attached with OR on
     * <li>check that only one result is returned with the a matching name
     * <li>Do a search for the service by searching two category one which is
     * not attached with AND on
     * <li>check that no result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByMulipleCategory()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateService(service);

            Vector cats = new Vector();
            cats.add(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            cats.add(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));

            List results = servloc.findService(null, "", cats, null, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

            results = servloc.findService(null, "", cats, null, false);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

            cats.add(regman.getCategory(CategoryType.CATEGORY_DDC, "not attached value"));

            results = servloc.findService(null, "", cats, null, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE", ((ServiceInfo) results.get(0)).getName());

            results = servloc.findService(null, "", cats, null, false);
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
     * Tests finding a service by protocol functionality by: <br>
     * For this test to work we have to cheat and add a wild card on name as
     * protocols are attached to more than just test objects. We do use call
     * first to check we get at least 1 returned result
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Do a search for the service by protocol checking that only one
     * result is returned with the a matching name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testFindServiceByProtocol()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            // as more than one in registry likly to have protocol just check
            // more than 1
            List results = servloc.findService(Protocol.PROTOCOL_SRW);
            assertTrue(results.size() >= 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now use filter call and add an extra search on name as well
            Vector protocols = new Vector();
            protocols.add(Protocol.PROTOCOL_SRW);
            // as more than one in registry likly to have protocol just check
            // more than 1
            results = servloc.findService(null, "JAFER%", null, protocols, true);
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

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
     * Tests getting a service functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Register service with SRW protocol and access point
     * <li>Do a search for the service and checking that only one result is
     * returned with a matching name
     * <li>Get an instance of the service using the service info
     * <li>Check the name matches
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testGetService()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            Service service2 = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service2);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service2.getName());

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
     * Tests getting a service provider functionality by:
     * <UL>
     * <li>Registering a service provider and service
     * <li>Do a search for the provider and checking that only one result is
     * returned with a matching name
     * <li>Get an instance of the service using the service info
     * <li>Check the name matches
     * <li>Get a new instance of the service using the service through get and
     * make sure the names match
     * <li>Update original provider object to have a description
     * <li>Confirm that the orginal description is different from the one just
     * obtained
     * <li>Updated the original provider to the registry
     * <li>Refresh the retreieved provider
     * <li>Confirm that the orginal description is no longer different from the
     * refreshed provider
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRefreshService()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            Service service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            Service service2 = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service2);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service2.getName());

            service.setDescription("DESC");
            assertFalse(service.getDescription().equals(service2.getDescription()));
            servman.updateService(service);

            Service service3 = servloc.refreshService(service2);
            assertTrue(service.getDescription().equals(service3.getDescription()));

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
}