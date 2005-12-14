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

import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.ServiceManager;
import org.jafer.registry.model.CategoryDoesNotExistException;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Contact;
import org.jafer.registry.model.InvalidNameException;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;

/**
 * This class tests the Service Manager it assumes the service locator is
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
public class ServiceManagerLevel1AccessTest extends UDDITest
{

    public ServiceManagerLevel1AccessTest() throws Exception
    {
    }

    /**
     * Tests the create contact call by:
     * <UL>
     * <li>Creating a new contact with just a name and checking the created
     * object matches the supplied name
     * <li>Creating a new contact with all data and checking the created object
     * matches the supplied name
     * <li>Creating a contact with just an invalid name and checking the
     * InvalidNameException is thrown
     * <li>Creating a contact with all data and an invalid name and checking
     * the InvalidNameException is thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testCreateContact()
    {
        try
        {

            Contact contact = servman.createNewContact("name");
            assertEquals("Mismatch on name", "name", contact.getName());

            contact = servman.createNewContact("name", "desc", "phone", "email");
            assertEquals("Mismatch on name", "name", contact.getName());
            assertEquals("Mismatch on desc", "desc", contact.getDescription());
            assertEquals("Mismatch on phone", "phone", contact.getPhone());
            assertEquals("Mismatch on email", "email", contact.getEmail());

            try
            {
                contact = servman.createNewContact("");
                fail("Should have thrown invalid name exception");
            }
            catch (InvalidNameException e)
            {
                assertTrue(true);
            }

            try
            {
                contact = servman.createNewContact("", "desc", "phone", "email");
                fail("Should have thrown invalid name exception");
            }
            catch (InvalidNameException e)
            {
                assertTrue(true);
            }
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
    }

    /**
     * Tests the register service provider functionality by:
     * <UL>
     * <li>Registering a service provider
     * <li>Checking the created object has the correct name
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
    public void testRegisterAndLocateServiceProvider()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());

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
     * Tests the register service provider functionality with bad credentials
     * by:
     * <UL>
     * <li>Creating a service manager with invalid credentials
     * <li>Performing the register
     * <li>Checking InvalidAuthorisationDetailsException is thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterServiceProviderWithBadCredentials()
    {
        ServiceProvider provider = null;
        try
        {
            ServiceManager badservman = regman.getServiceManager("a", "a");
            // fail a register due to authorisation details
            provider = badservman.registerServiceProvider("JAFER COMPANY");
            fail("Should have failed due to bad credentials");
        }
        catch (InvalidAuthorisationDetailsException e1)
        {
            assertTrue(true);
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
     * Tests the register service provider with no name functionality by:
     * <UL>
     * <li>Registering provider with empty name
     * <li>Checking InvalidNameException thrown
     * <li>Registering a successful provider
     * <li>Try to set new providers name to blank
     * <li>Checking InvalidNameException thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterServiceProviderWithNoName()
    {
        ServiceProvider provider = null;
        try
        {
            try
            {
                // fail a register due to empty name
                provider = servman.registerServiceProvider("");
                fail("Should have failed due to empty name");

            }
            catch (InvalidNameException e1)
            {
                assertTrue(true);
            }
            // now test setter
            try
            {
                provider = servman.registerServiceProvider("JAFER COMPANY");
                provider.setName("");
                fail("Should have failed due to empty name");
            }
            catch (InvalidNameException e1)
            {
                assertTrue(true);
            }
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
     * Tests the update service provider functionality by:
     * <UL>
     * <li>Registering a new provider
     * <li>Adding a new fully populated contact
     * <li>Update Description, name, homepage attribues
     * <li>Perform update call to registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check all updated data matches on retrieved provider
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testUpdateServiceProvider()
    {
        ServiceProvider provider = null, locatedProvider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());

            Contact contact = servman.createNewContact("name", "desc", "phone", "email");
            provider.setContact(contact);
            provider.setDescription("description");
            provider.setName("JAFER TEST COMPANY");
            provider.setHomePage("www.homepgae.com");

            servman.updateServiceProvider(provider);

            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            assertEquals("Mismatch on desc ", "description", locatedProvider.getDescription());
            assertEquals("Mismatch on homepage ", "www.homepgae.com", locatedProvider.getHomePage());
            contact = locatedProvider.getContact();
            assertNotNull(contact);
            assertEquals("Mismatch on name", "name", contact.getName());
            assertEquals("Mismatch on desc", "desc", contact.getDescription());
            assertEquals("Mismatch on phone", "phone", contact.getPhone());
            assertEquals("Mismatch on email", "email", contact.getEmail());

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
     * Tests the service provider category functionality by:
     * <UL>
     * <li>Register a new provider
     * <li>Add a fully initialised contact
     * <li>Add three categories
     * <li>Add a forth that is identical to one of the original 3
     * <li>Update the service provider to the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check contact information matches
     * <li>Check provider has 3 categories not 4 as fourth was already added
     * <li>Search the registry by each of the 3 categories and make sure the
     * provider is returned for each by checking name
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceProviderWithCatagories()
    {
        ServiceProvider provider = null, locatedProvider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());

            Contact contact = servman.createNewContact("name", "desc", "phone", "email");
            provider.setContact(contact);
            provider.setDescription("description");
            provider.setName("JAFER TEST COMPANY");
            provider.setHomePage("www.homepgae.com");
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateServiceProvider(provider);

            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            assertEquals("Mismatch on desc ", "description", locatedProvider.getDescription());
            assertEquals("Mismatch on homepage ", "www.homepgae.com", locatedProvider.getHomePage());
            contact = locatedProvider.getContact();
            assertNotNull(contact);
            assertEquals("Mismatch on name", "name", contact.getName());
            assertEquals("Mismatch on desc", "desc", contact.getDescription());
            assertEquals("Mismatch on phone", "phone", contact.getPhone());
            assertEquals("Mismatch on email", "email", contact.getEmail());
            List categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 3);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

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
     * Tests the service provider category removal functionality by:
     * <UL>
     * <li>Register new provider
     * <li>Add 3 different categories
     * <li>Update the service provider to the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check 3 categories attached
     * <li>Search the registry by each of the 3 categories and make sure the
     * provider is returned for each by checking name
     * <li>Remove 1 category and update the service provider in the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check 2 categories attached
     * <li>Search the registry by each of the 3 original categories and make
     * sure the provider is returned for each by checking name other than the
     * removed category
     * <li>Remove all the categories by global removeall call and update the
     * service provider in the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check 0 categories attached
     * <li>Search the registry by each of the 3 original categories and make
     * sure the provider is no longer found
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceProviderRemovingCatagories()
    {
        ServiceProvider provider = null, locatedProvider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());

            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            provider = servman.updateServiceProvider(provider);

            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            List categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 3);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // ***** TEST remove DDC category ****
            provider.removeCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider = servman.updateServiceProvider(provider);

            //now find the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 2);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // DDC Should no longer find provider
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 0);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            // ***** TEST remove all categories ****
            provider.removeAllCategories();
            provider = servman.updateServiceProvider(provider);

            //now find the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 0);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 0);

            // DDC Should no longer find provider
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 0);

            // now find the service provider using category
            results = servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
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
     * Tests the service provider category invalid removal functionality by:
     * <UL>
     * <li>Register new provider
     * <li>Add 3 different categories
     * <li>Update the service provider to the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check 3 categories attached
     * <li>Remove a category that is not attached
     * <li>Check CategoryDoesNotExistException thrown
     * <li>Remove 1 category
     * <li>Update the service provider to the registry
     * <li>Search the registry for the provider to ensure new instance returned
     * <li>Check 2 categories attached
     * <li>Remove the same category that was originally attached
     * <li>Check CategoryDoesNotExistException thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRemovingNonAttachedCatagoryFromServiceProvider()
    {
        ServiceProvider provider = null, locatedProvider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());

            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            provider.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            provider = servman.updateServiceProvider(provider);

            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            List categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 3);

            try
            {
                provider.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "BAD VALUE"));
                fail("CategoryDoesNotExistException should have been thrown");
            }
            catch (CategoryDoesNotExistException e)
            {
                assertTrue(true);
            }

            // **** TEST Removing same category twice ****
            provider.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            provider = servman.updateServiceProvider(provider);

            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", locatedProvider.getName());
            categories = locatedProvider.getCategories();
            assertTrue(categories.size() == 2);

            try
            {
                provider.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
                fail("CategoryDoesNotExistException should have been thrown");
            }
            catch (CategoryDoesNotExistException e)
            {
                assertTrue(true);
            }
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
     * Tests the delete service provider functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Find the provider to ensure fresh instance and check for name match
     * <li>Delete the provider using the locate information
     * <li>Try and find the provider and check that 0 results found
     * <li>Register a new service provider
     * <li>Find the provider and check for name match
     * <li>Delete the provider using the new provider information
     * <li>Try and find the provider and check that 0 results found
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteServiceProvider()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());

            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            servman.deleteServiceProvider((ServiceProviderInfo) results.get(0));
            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 0);

            // deleted re-register

            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            provider.setName("JAFER");
            provider = servman.updateServiceProvider(provider);

            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER", ((ServiceProviderInfo) results.get(0)).getName());

            servman.deleteServiceProvider(provider);
            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER");
            assertTrue(results.size() == 0);

            // stop the clean up process
            provider = null;

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
     * Tests the register service functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check the found service name matches
     * <li>Check the access point for the SRW protocol matches
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service from
     * the serviceInfo
     * <li>Check only one service on provider and that its service name and
     * access point matches
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterAndLocateService()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
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
     * Tests the register service functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Create a service manager with bad credentials
     * <li>Register service using bad service manager
     * <li>Check InvalidAuthorisationDetailsException thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterServiceWithBadCredentials()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            ServiceManager badservman = regman.getServiceManager("a", "a");
            // fail a register due to authorisation details
            badservman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_Z3950, "sd");
            fail("Should have failed due to bad credentials");
        }
        catch (InvalidAuthorisationDetailsException e1)
        {
            assertTrue(true);
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
     * Tests the register invalid service name functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with empty name
     * <li>Check InvalidNameException thrown
     * <li>Register service with valid name
     * <li>try and set name to blank
     * <li>Check InvalidNameException thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterServiceWithNoName()
    {
        ServiceProvider provider = null;
        try
        {
            try
            {
                provider = servman.registerServiceProvider("JAFER COMPANY");
                // fail a register due to empty name
                servman.registerService(provider, "", Protocol.PROTOCOL_Z3950, "sd");
                fail("Should have failed due to empty name");

            }
            catch (InvalidNameException e1)
            {
                assertTrue(true);
            }
            // now test setter
            try
            {
                Service service = servman.registerService(provider, "sdfsdf", Protocol.PROTOCOL_Z3950, "sd");
                service.setName("");
                fail("Should have failed due to empty name");
            }
            catch (InvalidNameException e1)
            {
                assertTrue(true);
            }
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
     * <li>Search the registry for the service 1 to ensure new instance
     * returned
     * <li>Check the found service 1 name matches
     * <li>Check the found service 1 access point for the SRW protocol matches
     * <li>Search the registry for the service 2 to ensure new instance
     * returned
     * <li>Check the found service 2 name matches
     * <li>Check the found service 2 access point for the SRW protocol matches
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service from
     * the serviceInfo
     * <li>Check only two service on provider and that each service name and
     * access point matches either service 1 or service 2
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRegisterAndLocateTwoServices()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.1.com");
            servman.registerService(provider, "JAFER SERVICE 2", Protocol.PROTOCOL_SRW, "www.2.com");

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

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 2);
            Iterator iter = services.iterator();
            while (iter.hasNext())
            {
                service = (Service) servloc.getService((ServiceInfo) iter.next());
                if (service.getName().equals("JAFER SERVICE 2") || service.getName().equals("JAFER SERVICE 1"))
                {
                    assertTrue(true);
                }
                else
                {
                    fail("service name incorrect should be JAFER SERVICE 1 or 2 : " + service.getName());
                }
            }

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
     * Tests the update service functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update name, descvription, SRW access point and add SRW wsdl url
     * location
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated details match
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service from
     * the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated details match on service obtained from
     * provider
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testUpdateService()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE", service.getName());

            service.setDescription("desc");
            service.setName("JAFER SERVICE 1");
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.accesspointupdated.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.wsdl.com");

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

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
     * Tests the update service protocol functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update SRW access point and add SRW wsdl url location
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated access point details match
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated access point details match on service obtained
     * from provider
     * <li>Remove both accesspoint and wsdlurl for SRW by setting them to blank
     * due to level 1 restriction
     * <li>Set both accesspoint and wsdlurl for Z3950 protocol
     * <li>Update the service to the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check that the old access points for SRW no longer exist
     * <li>Check that the new access points for Z3950 now exist
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>Check that the old access points for SRW no longer exist on service
     * provider service
     * <li>Check that the new access points for Z3950 now exist on service
     * provider service
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

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

            // now update protocols to Z3950 by first removing the SRW. Have to
            // do this due to microsofts account limits
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.Z3950.com");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.wsdlZ3950.com");

            servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.Z3950.com", service.getAccessUrl(Protocol.PROTOCOL_Z3950));
            assertEquals("Mismatch on access point", "www.wsdlZ3950.com", service.getWSDLUrl(Protocol.PROTOCOL_Z3950));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on access point", "", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
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
     * Tests the supports protocol service functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update SRW access point and add SRW wsdl url location
     * <li>Update the service to the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>call suportsProtocol on SRW and check its true
     * <li>call suportsProtocol on Z3950 and check its false
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>call suportsProtocol on SRW and check its true for the service
     * provider obtained service
     * <li>call suportsProtocol on Z3950 and check its false for the service
     * provider obtained service
     * <li>Remove both accesspoint and wsdlurl for SRW by setting them to blank
     * due to level 1 restriction
     * <li>Set both accesspoint and wsdlurl for Z3950 protocol
     * <li>Update the service to the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>call suportsProtocol on SRW and check its false
     * <li>call suportsProtocol on Z3950 and check its true
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>call suportsProtocol on SRW and check its false for the service
     * provider obtained service
     * <li>call suportsProtocol on Z3950 and check its true for the service
     * provider obtained service
     * <li>Remove both accesspoint and wsdlurl for SRW and Z3950 by setting
     * them to blank due to level 1 restriction
     * <li>Update the service to the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>call suportsProtocol on SRW and check its false
     * <li>call suportsProtocol on Z3950 and check its false
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>call suportsProtocol on SRW and check its false for the service
     * provider obtained service
     * <li>call suportsProtocol on Z3950 and check its false for the service
     * provider obtained service
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceSupportsProtocols()
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

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_SRW));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_Z3950));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_SRW));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_Z3950));

            // now update protocols to Z3950 by first removing the SRW. Have to
            // do this due to microsofts account limits
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.accesspointupdated.com");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.wsdl.com");

            servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_Z3950));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_SRW));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertTrue(service.supportsProtocol(Protocol.PROTOCOL_Z3950));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_SRW));

            //now update protocols to Z3950 by first removing the SRW. Have to
            // do this due to microsofts account limits
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "");

            servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_Z3950));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_SRW));

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_Z3950));
            assertFalse(service.supportsProtocol(Protocol.PROTOCOL_SRW));

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
     * Tests the service category functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update name, descvription, SRW access point and add SRW wsdl url
     * location
     * <li>Add three categories
     * <li>Add a forth that is identical to one of the original 3
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated details match
     * <li>Check service has 3 categories not 4 as fourth was already added
     * <li>Search the registry by each of the 3 categories and make sure the
     * service is returned for each by checking name
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated details match on service obtained from
     * provider
     * <li>Check service on provider has 3 categories not 4 as fourth was
     * already added
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceWithCatagories()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE", service.getName());

            service.setDescription("desc");
            service.setName("JAFER SERVICE 1");
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.accesspointupdated.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.wsdl.com");
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            List categories = service.getCategories();
            assertTrue(categories.size() == 3);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspointupdated.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

            categories = service.getCategories();
            assertTrue(categories.size() == 3);

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
     * Tests the service category removal functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Update name, descvription, SRW access point and add SRW wsdl url
     * location
     * <li>Add three categories
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated details match
     * <li>Check service has 3 categories
     * <li>Search the registry by each of the 3 categories and make sure the
     * service is returned for each by checking name
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated details match on service obtained from
     * provider
     * <li>Check service on provider has 3 categories
     * <li>Remove 1 category and update the service in the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated details match
     * <li>Check service has 2 categories
     * <li>Search the registry by each of the 3 categories and make sure the
     * service is returned for each by checking name except the removed category
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated details match on service obtained from
     * provider
     * <li>Check service on provider has 2 categories
     * <li>Remove all the categories by global removeall call and update the
     * service in the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check all the updated details match
     * <li>Check service has 0 categories
     * <li>Search the registry by each of the 3 categories and make sure the
     * service is not returned for any of them
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check only one service on provider
     * <li>check all the updated details match on service obtained from
     * provider
     * <li>Check service on provider has 0 categories
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testServiceRemovingCatagories()
    {
        ServiceProvider provider = null, locatedProvider = null;
        Service service;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE", service.getName());

            service.setDescription("desc");
            service.setName("JAFER SERVICE 1");
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.wsdl.com");
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            List categories = service.getCategories();
            assertTrue(categories.size() == 3);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            categories = service.getCategories();
            assertTrue(categories.size() == 3);

            // ***** TEST remove DDC category ****
            service.removeCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service = servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            categories = service.getCategories();
            assertTrue(categories.size() == 2);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 0);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

            categories = service.getCategories();
            assertTrue(categories.size() == 2);

            // ***** TEST remove all categories ****
            service.removeAllCategories();
            service = servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));
            categories = service.getCategories();
            assertTrue(categories.size() == 0);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            assertTrue(results.size() == 0);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            assertTrue(results.size() == 0);

            // now find the service using category
            results = servloc.findService(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            assertTrue(results.size() == 0);

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            assertEquals("Mismatch on desc ", "desc", service.getDescription());
            assertEquals("Mismatch on access point", "www.accesspoint.com", service.getAccessUrl(Protocol.PROTOCOL_SRW));
            assertEquals("Mismatch on access point", "www.wsdl.com", service.getWSDLUrl(Protocol.PROTOCOL_SRW));

            categories = service.getCategories();
            assertTrue(categories.size() == 0);
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
     * Tests the service category invalid removal functionality by:
     * <UL>
     * <li>Register provider and check for name match
     * <li>Register service with SRW protocol and access point
     * <li>Add three categories
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check service has 3 categories
     * <li>Remove a category that is not attached
     * <li>Check CategoryDoesNotExistException thrown
     * <li>Remove 1 category
     * <li>Update the service to the registry
     * <li>Search the registry for the service to ensure new instance returned
     * <li>Check 2 categories attached
     * <li>Remove the same category that was originally attached
     * <li>Check CategoryDoesNotExistException thrown
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testRemovingNonAttachedCatagoryFromService()
    {
        ServiceProvider provider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "keyword"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_DDC, "ddc"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));

            service = servman.updateService(service);

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            List categories = service.getCategories();
            assertTrue(categories.size() == 3);

            try
            {
                service.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "BAD VALUE"));
                fail("CategoryDoesNotExistException should have been thrown");
            }
            catch (CategoryDoesNotExistException e)
            {
                assertTrue(true);
            }

            // **** TEST Removing same category twice ****
            service.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
            service = servman.updateService(service);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            service = servloc.getService((ServiceInfo) results.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
            categories = service.getCategories();
            assertTrue(categories.size() == 2);

            try
            {
                service.removeCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "lcsh"));
                fail("CategoryDoesNotExistException should have been thrown");
            }
            catch (CategoryDoesNotExistException e)
            {
                assertTrue(true);
            }
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
     * Tests the delete service functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Register service with SRW protocol and access point
     * <li>Find the service to ensure fresh instance and check for name match
     * <li>Delete the service using the locate information
     * <li>Try and find the service and check that 0 results found
     * <li>Register a new service on orginal provider
     * <li>Find the service and check for name match
     * <li>Delete the service using the new service information
     * <li>Try and find the service and check that 0 results found
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteService()
    {
        ServiceProvider provider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            servman.deleteService((ServiceInfo) results.get(0));
            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 0);

            // deleted re-register

            service = servman.registerService(provider, "JAFER SERVICE NEW", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE NEW", service.getName());

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE NEW");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE NEW", ((ServiceInfo) results.get(0)).getName());

            servman.deleteService(service);
            // now find the service using locator
            results = servloc.findService("JAFER SERVICE NEW");
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
     * Tests the delete service using provider functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Register service 1 with SRW protocol and access point
     * <li>Register service 2 with SRW protocol and access point
     * <li>Find both services to ensure fresh instance and check for name match
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check that 2 services are returned
     * <li>remove service 1 from the provider by calling removeService
     * <li>Update the service provider to the registry
     * <li>Try and find service 1 and ensure 0 results returned
     * <li>find service 2 and check name matches
     * <li>Find the service provider for the service, obtain new provider
     * instance, get the list of provider services and obtain the service
     * from the serviceInfo
     * <li>Check that 1 services are returned
     * <li>Check returned service matches service 2
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteServicebyRemovingFromProvider()
    {
        ServiceProvider provider = null;
        Service service1 = null, service2 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());
            service1 = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service1.getName());
            service2 = servman.registerService(provider, "JAFER SERVICE 2", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", service2.getName());

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            ServiceProvider locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 2);

            locatedProvider.removeService(service1);
            servman.updateServiceProvider(locatedProvider);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 0);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 1);
            Service service = (Service) servloc.getService((ServiceInfo) services.get(0));
            assertNotNull(service);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", service.getName());

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
     * Tests the delete all services using provider functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Register service 1 with SRW protocol and access point
     * <li>Register service 2 with SRW protocol and access point
     * <li>Find both services to ensure fresh instance and check for name match
     * <li>Find the service provider for the service, obtain new provider
     * instance and get the list of provider services
     * <li>Check that 2 services are returned
     * <li>remove all services from the provider by calling removeAllServices
     * <li>Update the service provider to the registry
     * <li>Try and find service 1 and ensure 0 results returned
     * <li>Try and find service 2 and ensure 0 results returned
     * <li>Find the service provider for the service, obtain new provider
     * instance and get the list of provider services
     * <li>Check that 0 services are returned
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteAllServicesbyRemovingFromProvider()
    {
        ServiceProvider provider = null;
        Service service1 = null, service2 = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());
            service1 = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service1.getName());
            service2 = servman.registerService(provider, "JAFER SERVICE 2", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", service2.getName());

            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 2");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 2", ((ServiceInfo) results.get(0)).getName());

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            ServiceProvider locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            List services = locatedProvider.getServices();
            assertTrue(services.size() == 2);

            locatedProvider.removeAllServices();
            servman.updateServiceProvider(locatedProvider);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 0);

            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 2");
            assertTrue(results.size() == 0);

            // now find the service via the service provider using locator
            results = servloc.findServiceProvider("JAFER TEST COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", ((ServiceProviderInfo) results.get(0)).getName());

            locatedProvider = servloc.getServiceProvider((ServiceProviderInfo) results.get(0));
            assertNotNull(locatedProvider);
            services = locatedProvider.getServices();
            assertTrue(services.size() == 0);
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
     * Tests the delete service provider functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Find the provider to ensure fresh instance and check for name match
     * <li>Delete the provider using the locate informations id
     * <li>Try and find the provider and check that 0 results found
     * <li>Register a new service provider
     * <li>Find the provider and check for name match
     * <li>Delete the provider using the new provider information id
     * <li>Try and find the provider and check that 0 results found
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteServiceProviderByID()
    {
        ServiceProvider provider = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
    
            // now find the service provider using locator
            List results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER COMPANY", ((ServiceProviderInfo) results.get(0)).getName());
    
            servman.deleteServiceProvider(((ServiceProviderInfo) results.get(0)).getId());
            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER COMPANY");
            assertTrue(results.size() == 0);
    
            // deleted re-register
    
            provider = servman.registerServiceProvider("JAFER COMPANY");
            assertEquals("Mismatch on name ", "JAFER COMPANY", provider.getName());
            provider.setName("JAFER");
            provider = servman.updateServiceProvider(provider);
    
            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER", ((ServiceProviderInfo) results.get(0)).getName());
    
            servman.deleteServiceProvider(provider.getId());
            // now find the service provider using locator
            results = servloc.findServiceProvider("JAFER");
            assertTrue(results.size() == 0);
    
            // stop the clean up process
            provider = null;
    
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
     * Tests the delete service functionality by:
     * <UL>
     * <li>Register a new service provider
     * <li>Register service with SRW protocol and access point
     * <li>Find the service to ensure fresh instance and check for name match
     * <li>Delete the service using the locate information id
     * <li>Try and find the service and check that 0 results found
     * <li>Register a new service on orginal provider
     * <li>Find the service and check for name match
     * <li>Delete the service using the new service information id
     * <li>Try and find the service and check that 0 results found
     * </UL>
     * <b>NOTE: </b>This tests expects all <b>Service Provider Names </b>and <b>
     * Service Names </b> to not be registered in the registry. Any <b>attached
     * categories </b> have been given unusual values and <b>are not expected to
     * be attached to any other objects </b>. If any of these pre-conditions are
     * not met then assertions will fail when searchs return more than the
     * expected count of results </b>
     */
    public void testDeleteServiceById()
    {
        ServiceProvider provider = null;
        Service service = null;
        try
        {
            provider = servman.registerServiceProvider("JAFER TEST COMPANY");
            assertEquals("Mismatch on name ", "JAFER TEST COMPANY", provider.getName());
            service = servman.registerService(provider, "JAFER SERVICE 1", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", service.getName());
    
            // now find the service using locator
            List results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE 1", ((ServiceInfo) results.get(0)).getName());
    
            servman.deleteService(((ServiceInfo) results.get(0)).getId());
            // now find the service using locator
            results = servloc.findService("JAFER SERVICE 1");
            assertTrue(results.size() == 0);
    
            // deleted re-register
    
            service = servman.registerService(provider, "JAFER SERVICE NEW", Protocol.PROTOCOL_SRW, "www.accesspoint.com");
            assertEquals("Mismatch on name ", "JAFER SERVICE NEW", service.getName());
    
            // now find the service using locator
            results = servloc.findService("JAFER SERVICE NEW");
            assertTrue(results.size() == 1);
            assertEquals("Mismatch on name ", "JAFER SERVICE NEW", ((ServiceInfo) results.get(0)).getName());
    
            servman.deleteService(service.getId());
            // now find the service using locator
            results = servloc.findService("JAFER SERVICE NEW");
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
}
