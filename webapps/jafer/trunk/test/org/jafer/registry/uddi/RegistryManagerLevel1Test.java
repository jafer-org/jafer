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

import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.RegistryFactory;
import org.jafer.registry.RegistryNotInitialisedException;
import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryType;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.TModelInfo;
import org.uddi4j.response.TModelList;

/**
 * This class tests the Registry Manager. This test class only requires <b>level 1</b>
 * access to the UBR to run correctly. <br>
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
public class RegistryManagerLevel1Test extends UDDITest
{

    public RegistryManagerLevel1Test() throws Exception
    {
    }

    /**
     * Always leave the registry fully populated for other tests so on tear down
     * make sure the registry is repopulated fully. Test should fail if the tear
     * down does not work
     */
    protected void tearDown()
    {
        try
        {
            regman.initialiseRegistry(username, credential);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            fail("Can not reinitialise registry: Check credentials");
        }
        catch (RegistryException e)
        {
            fail("Can not reinitialise registry: " + e);
        }
    }

    /**
     * As the registry is fully populated, for certain test to run some of the
     * initialisation data must be removed. This method removes the DDC TModel
     * for the tests. Note however, the username must match the user that
     * created the DDCModel in the registry for the tests in this class to work.
     * <br>
     * <br>
     * If you wish to change ownership of this model then the original owner
     * must first delete the tmodel manually. Running any test will then
     * reregister the DDC with the new owner on tear down.
     */
    private void deleteDDCTModel()
    {
        UDDIProxy registryConnection = new UDDIProxy();
        String token = null;
        try
        {
            registryConnection.setInquiryURL(inquiryURL);
            registryConnection.setPublishURL(publishURL);
            TModelList list = registryConnection.find_tModel("DDC", null, null, null, 10);
            // if not found then no need to try and delete
            if (list.getTModelInfos().size() > 0)
            {
                TModelInfo tmodel = (TModelInfo) list.getTModelInfos().get(0);
                token = registryConnection.get_authToken(username, credential).getAuthInfoString();
                registryConnection.delete_tModel(token, tmodel.getTModelKey());
            }
        }
        catch (Exception e)
        {
            fail("Unable to delete TModel, Check username supplied owns DDC model");
        }
        finally
        {
            if (token != null)
            {
                try
                {
                    registryConnection.discard_authToken(token);
                }
                catch (Exception e)
                {
                    // ignore test may now fail
                }
            }
        }
    }

    /**
     * Test that the RegistryFactory returns an error if a bad URL format is
     * passed
     */
    public void testBadInquiryURL()
    {
        try
        {
            RegistryFactory.createRegistryManager("asasdas", "asdasd");
            fail("Should fail with a bad URL exception");
        }
        catch (RegistryException e)
        {
            assertTrue(true);
        }
    }

    /**
     * Test initialising registry with bad credentials so it can not create the
     * deleted TModel and throws an exception. This method expects to be able to
     * delete the DDC TModel, the logged on user must own this tmodel for the
     * test to work else it must be removed manually
     */
    public void testInitialiseRegistryWithBadCredentials()
    {
        try
        {
            // delete TMOdel to force create
            deleteDDCTModel();

            // now retry
            regman.initialiseRegistry("andy", "465");

            fail("Expected authorisation error, Registry is already initialised");
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            assertTrue(true);
        }
        catch (RegistryException e)
        {
            fail("Expected authorisation error " + e);
        }
    }

    /**
     * Test initialising the registry with proper credentials so it can not
     * create the deleted TModel. This method expects to be able to delete the
     * DDC TModel, the logged on user must own this tmodel for the test to work
     * else it must be removed manually
     */
    public void testInitialiseRegistry()
    {
        try
        {
            // delete TMOdel to force create
            deleteDDCTModel();

            regman.initialiseRegistry(username, credential);

            assertTrue(true);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            fail("Check UDDI properties invalid logon");
        }
        catch (RegistryException e)
        {
            fail("Expected authorisation error " + e);
        }
    }

    /**
     * Test retreiving the service locator when the registry is missing a
     * TModel. This method should fail with a RegistryNotInitialisedException.
     * This method expects to be able to delete the DDC TModel, the logged on
     * user must own this tmodel for the test to work else it must be removed
     * manually
     */
    public void testGetServiceLocatorWithUnitialisedRegistry()
    {
        try
        {
            // delete TMOdel to force bad registry
            deleteDDCTModel();

            RegistryFactory.createRegistryManager(inquiryURL, publishURL).getServiceLocator();

            fail("Expected RegistryNotInitialisedException ");
        }
        catch (RegistryNotInitialisedException e)
        {
            assertTrue(true);
        }
        catch (RegistryException e)
        {
            fail("Expected RegistryNotInitialisedException not RegistryException" + e);
        }
    }

    /**
     * Test retreiving the service manager when the registry is missing a
     * TModel. This method should fail with a RegistryNotInitialisedException.
     * This method expects to be able to delete the DDC TModel, the logged on
     * user must own this tmodel for the test to work else it must be removed
     * manually
     */
    public void testGetServiceManagerWithUnitialisedRegistry()
    {
        try
        {
            // delete TMOdel to force bad registry
            deleteDDCTModel();
            RegistryFactory.createRegistryManager(inquiryURL, publishURL).getServiceManager(username, credential);

            fail("Expected RegistryNotInitialisedException ");
        }
        catch (RegistryNotInitialisedException e)
        {
            assertTrue(true);
        }
        catch (RegistryException e)
        {
            fail("Expected RegistryNotInitialisedException not RegistryException" + e);
        }
    }

    /**
     * Test retreiving the service manager when the registry is correctly
     * initialised with all the TModels. This method should not fail and return
     * a service manager instance. This method expects to be able to delete the
     * DDC TModel, the logged on user must own this tmodel for the test to work
     * else it must be removed manually
     */
    public void testGetServiceManagerInitialisedRegistry()
    {
        try
        {
            assertTrue(RegistryFactory.createRegistryManager(inquiryURL, publishURL).getServiceManager(username, credential) != null);

        }
        catch (RegistryNotInitialisedException e)
        {
            fail("Expected Success not RegistryNotInitialisedException ");
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
    }

    /**
     * Test retreiving the service locator when the registry is correctly
     * initialised with all the TModels. This method should not fail and return
     * a service locator instance. This method expects to be able to delete the
     * DDC TModel, the logged on user must own this tmodel for the test to work
     * else it must be removed manually
     */
    public void testGetServiceLocatorInitialisedRegistry()
    {
        try
        {
            assertTrue(RegistryFactory.createRegistryManager(inquiryURL, publishURL).getServiceLocator() != null);

        }
        catch (RegistryNotInitialisedException e)
        {
            fail("Expected Success not RegistryNotInitialisedException ");
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
    }

    /**
     * Test getting an existing category and that the returned object is
     * correctly initialised with the requested data
     */
    public void testGetExistingCategory()
    {
        try
        {
            Category cat = regman.getCategory(CategoryType.CATEGORY_DDC, "hello");
            assertEquals("Name mismatch", "DDC", cat.getName());
            assertEquals("Value mismatch", "hello", cat.getValue());

        }
        catch (RegistryNotInitialisedException e)
        {
            fail("Expected Success not RegistryNotInitialisedException ");
        }
        catch (RegistryException e)
        {
            fail("Expected Success not RegistryException" + e);
        }
    }

    /**
     * Test a failure when trying to retrieve a category that does not exist. As
     * this is controlled by a defined CategoryType this is simulated by passing
     * null as all CatagoryTypes will exist.
     */
    public void testGetBadCategory()
    {
        try
        {
            regman.getCategory(null, "hello");
            fail("category should not exist as null");
        }
        catch (RegistryException e)
        {
            assertTrue(true);
        }
    }

    /**
     * Test retreiving a category when the registry is missing a TModel. This
     * method should fail with a RegistryNotInitialisedException. This method
     * expects to be able to delete the DDC TModel, the logged on user must own
     * this tmodel for the test to work else it must be removed manually
     */
    public void testGetCategoryWithUnitialisedRegistry()
    {
        try
        {
            // delete TMOdel to force bad registry
            deleteDDCTModel();

            RegistryFactory.createRegistryManager(inquiryURL, publishURL).getCategory(CategoryType.CATEGORY_DDC, "dfdf");
            fail("Expected RegistryNotInitialisedException ");
        }
        catch (RegistryNotInitialisedException e)
        {
            assertTrue(true);
        }
        catch (RegistryException e)
        {
            fail("Expected RegistryNotInitialisedException not RegistryException" + e);
        }
    }

}
