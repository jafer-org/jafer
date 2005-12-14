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

import java.util.Iterator;
import java.util.List;

import org.jafer.registry.RegistryException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;

/**
 * This class tests the Service Manager it assumes the service locator is
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
public class ServiceManagerLevel2AccessTest extends UDDITest
{

    public ServiceManagerLevel2AccessTest() throws Exception
    {
    }

    /**
     * Tests the register mulitple service provider functionality by:
     * <UL>
     * <li>Registering service provider 1, 2 and 3
     * <li>Checking the created providers have the correct name
     * <li>Search for each provider in turn and check the name matches
     * <li>Search for providers by using wildcard
     * <li>Check 3 matches returned
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterAndLocateServiceProvider()
    {
        ServiceProvider provider = null, provider2 = null, provider3 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY 1");
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", provider.getName());
            provider2 = servman.registerServiceProvider("JAFER COMPANY 2");
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", provider2.getName());
            provider3 = servman.registerServiceProvider("JAFER COMPANY 3");
            assertEquals("Mismatch on name ", "JAFER COMPANY 3", provider3.getName());

            List results = servloc.findServiceProvider("JAFER COMPANY 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", ((ServiceProviderInfo) results.get(0)).getName());
            results = servloc.findServiceProvider("JAFER COMPANY 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", ((ServiceProviderInfo) results.get(0)).getName());
            results = servloc.findServiceProvider("JAFER COMPANY 3");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 3", ((ServiceProviderInfo) results.get(0)).getName());
            // Wild card search
            results = servloc.findServiceProvider("JAFER COMPANY%");
            assertTrue(results.size() == 3);
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
        }

    }

    /**
     * Tests the multiple service protocol functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update SRW access point and add SRW wsdl url location
     * <li>Add Z3950 access point and add Z3950 wsdl url location
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated and added access point details match
     * <li>Find the service provider for the service, obtain new provider
     * instance and get the list of provider services
     * <li>Check only one service on provider
     * <li>check all the updated and added access point details match on
     * service obtained from providers service info
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testUpdateServiceProtocols()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.accesspointupdated.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.wsdl.com");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.Z3950.com");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.wsdlZ3950.com");

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.Z3950.com", service.getAccessUrl(Protocol.PROTOCOL_Z3950));
            assertEquals("Mismatch on access point", "www.wsdlZ3950.com", service.getWSDLUrl(Protocol.PROTOCOL_Z3950));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo)services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.Z3950.com", service.getAccessUrl(Protocol.PROTOCOL_Z3950));
            assertEquals("Mismatch on access point", "www.wsdlZ3950.com", service.getWSDLUrl(Protocol.PROTOCOL_Z3950));

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
     * Tests the multiple service protocol functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update SRW access point and add SRW wsdl url location
     * <li>Add Z3950 access point and add Z3950 wsdl url location
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated and added access point details match
     * <li>call suportsProtocol on SRW and check its true for the located
     * service
     * <li>call suportsProtocol on Z3950 and check its true for the located
     * service
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated and added access point details match on
     * service obtained from provider
     * <li>call suportsProtocol on SRW and check its true for the service
     * provider obtained service
     * <li>call suportsProtocol on Z3950 and check its true for the service
     * provider obtained service
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceSuportsMulipleProtocols()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.accesspointupdated.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.wsdl.com");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.Z3950.com");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.wsdlZ3950.com");

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.Z3950.com", service.getAccessUrl(Protocol.PROTOCOL_Z3950));
            assertEquals("Mismatch on access point", "www.wsdlZ3950.com", service.getWSDLUrl(Protocol.PROTOCOL_Z3950));
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_SRW));
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_Z3950));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo)services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.Z3950.com", service.getAccessUrl(Protocol.PROTOCOL_Z3950));
            assertEquals("Mismatch on access point", "www.wsdlZ3950.com", service.getWSDLUrl(Protocol.PROTOCOL_Z3950));
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_SRW));
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_Z3950));
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
     * Tests the register mulitple service functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service 1 with SRW protocol and access point
     * <li>Register service 2 with SRW protocol and access point
     * <li>Register service 3 with SRW protocol and access point
     * <li>Register service 4 with SRW protocol and access point
     * <li>Register service 5 with SRW protocol and access point
     * <li>Register service 6 with SRW protocol and access point
     * <li>Search the registry for each service to ensure new instance returned
     * <li>Check each found service name matches
     * <li>Check each found service access point for the SRW protocol matches
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check six services on provider and that each service name and access
     * point matches either service 1 to service 6
     * <li>Search with wildcard and check 6 services returned
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterAndLocate6Services()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.1.com");
            servman.registerService(provider, "JAFER SERVICE 2", Protocol.PROTOCOL_SRW, "www.2.com");
            servman.registerService(provider, "JAFER SERVICE 3", Protocol.PROTOCOL_SRW, "www.3.com");
            servman.registerService(provider, "JAFER SERVICE 4", Protocol.PROTOCOL_SRW, "www.4.com");
            servman.registerService(provider, "JAFER SERVICE 5", Protocol.PROTOCOL_SRW, "www.5.com");
            servman.registerService(provider, "JAFER SERVICE 6", Protocol.PROTOCOL_SRW, "www.6.com");

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.1.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", service.getName());
            assertEquals("Mismatch on access point", "www.2.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            results = servloc.findService("JAFER SERVICE 3");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 3", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 3", service.getName());
            assertEquals("Mismatch on access point", "www.3.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            results = servloc.findService("JAFER SERVICE 4");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 4", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 4", service.getName());
            assertEquals("Mismatch on access point", "www.4.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            results = servloc.findService("JAFER SERVICE 5");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 5", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 5", service.getName());
            assertEquals("Mismatch on access point", "www.5.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            results = servloc.findService("JAFER SERVICE 6");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 6", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 6", service.getName());
            assertEquals("Mismatch on access point", "www.6.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 6);
            Iterator iter = services.iterator();
            while (iter.hasNext())
            {
                service = (Service) servloc.getService((ServiceInfo)iter.next());
                if (service.getName().equals("JAFER SERVICE 2") || service.getName().equals("JAFER SERVICE 1")
                        || service.getName().equals("JAFER SERVICE 3") || service.getName().equals("JAFER SERVICE 4")
                        || service.getName().equals("JAFER SERVICE 5") || service.getName().equals("JAFER SERVICE 6"))
                {
                    assertTrue(true);
                }
                else
                {
                    fail("service name incorrect should be JAFER SERVICE 1 to 6 : " + service.getName());
                }
            }
            // wildcard search for sixx entries
            results = servloc.findService("JAFER SERVICE%");
            assertTrue(results.size() == 6);

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
     * Tests registering a service against two providers by:
     * <UL>
     * <li>Registering service provider 1, 2 
     * <li>Checking the created providers have the correct name
     * <li>Create a service against provider 2
     * <li>Add the created service to provider 1
     * <li>update provider 1 and refresh provider 2
     * <li>check that the service is now registered on both providers
     * <li>Refresh the service object and check that its parent is still provider 2
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceonTwoProvider()
    {
        ServiceProvider provider1 = null, provider2 = null, owner = null;
        try
        {
            provider1 = servman.registerServiceProvider("JAFER COMPANY 1");
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", provider1.getName());
            provider2 = servman.registerServiceProvider("JAFER COMPANY 2");
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", provider2.getName());
           
            Service service = servman.registerService(provider2, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.1.com");
            provider1.addService(service);
            
            provider1 = servman.updateServiceProvider(provider1);
            provider2 = servloc.refreshServiceProvider(provider2);
            
            List results = servloc.findServiceProvider("JAFER COMPANY 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", ((ServiceProviderInfo) results.get(0)).getName());
            provider1 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            List services = provider1.getServices();
            assertTrue(services.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo)services.get(0)).getName());
                        
            
            results = servloc.findServiceProvider("JAFER COMPANY 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", provider2.getName());
            provider2 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            services = provider2.getServices();
            assertTrue(services.size() == 1);assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo)services.get(0)).getName());
            
            service = servloc.getService((ServiceInfo)services.get(0));
            owner = servloc.getServiceProvider(service);
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", owner.getName());
            
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            if (provider1 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider1);
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
            
        }

    }

    /**
     * Tests registering a service against two providers by:
     * <UL>
     * <li>Registering service provider 1, 2 
     * <li>Checking the created providers have the correct name
     * <li>Create a service against provider 2
     * <li>Add the created service to provider 1
     * <li>update provider 1 and refresh provider 2
     * <li>check that the service is now registered on both providers
     * <li>Refresh the service object and check that its parent is still provider 2
     * <li>Delete the service
     * <li>Refresh both providers
     * <li>ChecK service no longer on provider 2
     * <li>Check service exists but as bad link now on provider 1
     * <li>Update provider 1 
     * <li>Check the bad link has now been removed 
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testUpdateProviderRemovingBadService()
    {
        ServiceProvider provider1 = null, provider2 = null, owner = null;
        try
        {
            provider1 = servman.registerServiceProvider("JAFER COMPANY 1");
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", provider1.getName());
            provider2 = servman.registerServiceProvider("JAFER COMPANY 2");
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", provider2.getName());
           
            Service service = servman.registerService(provider2, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.1.com");
            provider1.addService(service);
            
            provider1 = servman.updateServiceProvider(provider1);
            provider2 = servloc.refreshServiceProvider(provider2);
            
            List results = servloc.findServiceProvider("JAFER COMPANY 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 1", ((ServiceProviderInfo) results.get(0)).getName());
            provider1 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            List services = provider1.getServices();
            assertTrue(services.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo)services.get(0)).getName());
                        
            
            results = servloc.findServiceProvider("JAFER COMPANY 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", provider2.getName());
            provider2 = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            services = provider2.getServices();
            assertTrue(services.size() == 1);assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo)services.get(0)).getName());
            
            service = servloc.getService((ServiceInfo)services.get(0));
            owner = servloc.getServiceProvider(service);
            assertEquals("Mismatch on name ", "JAFER COMPANY 2", owner.getName());
            
            servman.deleteService(service);
            
            provider1 = servloc.refreshServiceProvider(provider1);
            provider2 = servloc.refreshServiceProvider(provider2);
            // provider 2 should no longer have service
            assertTrue(provider2.getServices().size() == 0);
            // even though service is deleted bad link should exist
            assertTrue(provider1.getServices().size() == 1);
            // now clena the bad link
            provider1 = servman.updateServiceProvider(provider1);
            // should be back to 0
            assertTrue(provider1.getServices().size() == 0); 
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
        finally
        {
            if (provider1 != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider1);
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
            
        }
    
    }
}
