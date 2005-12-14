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

package org.jafer.registry;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.List;

import org.jafer.registry.model.Category;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.model.Service;
import org.jafer.registry.model.ServiceInfo;
import org.jafer.registry.model.ServiceProvider;
import org.jafer.registry.model.ServiceProviderInfo;
import org.jafer.registry.uddi.model.BusinessEntity;
import org.uddi4j.client.UDDIProxy;

/**
 *
 */
public class ManualTestAndEntityDeleteTool
{

    public static RegistryManager regman = null;

    public static ServiceManager servman = null;

    public static ServiceLocator servloc = null;

    public static ServiceProvider provider = null;

    public static BufferedReader input = null;

    /**
     * Tempory method to remove business entity by key should a failure occur
     */
    public static void deleteBusEntityUtility(String key, String inquiryURL, String publishURL, String username, String credential)
            throws Exception
    {
        System.out.println("Deleting key: " + key);
        // set the system properties for the transport mechanisum that is
        // used
        // to connect to the registry with
        System.setProperty("org.uddi4j.TransportClassName", "org.uddi4j.transport.ApacheAxisTransport");
        // Configure JSSE support against SUN
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        UDDIProxy registryConnection = new UDDIProxy();
        String token = null;
        try
        {
            registryConnection.setInquiryURL(inquiryURL);
            registryConnection.setPublishURL(publishURL);
            token = registryConnection.get_authToken(username, credential).getAuthInfoString();
            registryConnection.delete_business(token, key);
            System.out.println("Deleted key: " + key + " Successfully");

        }
        catch (Exception e)
        {
            System.out.println("Failed Deleting key: " + key);
            throw e;
        }
        finally
        {
            if (token != null)
            {
                registryConnection.discard_authToken(token);
            }
        }

    }

    public static void printSearchProviderResults(List list)
    {
        Iterator iter = list.iterator();
        System.out.println("Found " + list.size() + " service providers");
        while (iter.hasNext())
        {
            ServiceProviderInfo info = (ServiceProviderInfo) iter.next();
            System.out.println("    PROVIDER - " + info.getName() + " ( " + info.getDescription() + " )");
        }
    }

    public static void printSearchServicesResults(List list)
    {
        Iterator iter = list.iterator();
        System.out.println("Found " + list.size() + " services");
        while (iter.hasNext())
        {
            ServiceInfo info = (ServiceInfo) iter.next();
            System.out.println("    Service - " + info.getName());
        }
    }

    public static void printCategories(List categories)
    {
        Iterator iter = categories.iterator();
        System.out.println("Found " + categories.size() + " categories");
        while (iter.hasNext())
        {
            Category cat = (Category) iter.next();
            System.out.println("    CATEGORY - " + cat.getName() + " = " + cat.getValue());
        }
    }

    public static void main(String[] args)
    {

        try
        {
            String deleteKey = "";

            String inquiryURL = "http://uddi.microsoft.com/inquire";
            String publishURL = "https://uddi.microsoft.com/publish";
            String username = "andy@fostersontheweb.com";
            String credential = null;

            // this method loads the properties file that defines the main
            // information for running the test
            UDDITestConfig config = UDDITestConfig.getInstance();

            inquiryURL = config.getProperty(UDDITestConfig.UDDI_INQUIRE_URL);
            publishURL = config.getProperty(UDDITestConfig.UDDI_PUBLISH_URL);
            username = config.getProperty(UDDITestConfig.UDDI_USERNAME);
            credential = config.getProperty(UDDITestConfig.UDDI_CREDENTIAL);

            input = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            System.out.print("Enter business entity key to delete or return to run test:");
            deleteKey = input.readLine();
            System.out.println("");

            if (credential == null || credential.length() == 0)
            {
                System.out.print("Please Enter Logon Credential for " + username + ":");
                credential = input.readLine();
                System.out.println("");
                System.out.println("Authorising...");
                System.out.println("");
            }

            if (deleteKey.length() > 0)
            {
                System.out.println("Cleaning old business entity keyed " + deleteKey + " ...");
                deleteBusEntityUtility(deleteKey, inquiryURL, publishURL, username, credential);
                return;
            }

            // create the registry
            regman = RegistryFactory.createRegistryManager(inquiryURL, publishURL);

            //manager.initialiseRegistry(username,credential);

            servman = regman.getServiceManager(username, credential);
            servloc = regman.getServiceLocator();

            //testProvider();
            //testServices();

            /* JUST FOR CRETING BASIC VALUES WITHOUT DELETE AT END
            ServiceProvider p = servman.registerServiceProvider("JAFER REGISTRY TEST COMPANY");
            p.setDescription("This is the jafer registry test company");
            p.setHomePage("www.jafer.org");
            p.setContact(servman.createNewContact("Ben D Wire", "worker", "0898 345 6782", "ben@dwire.com"));
            p.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));
            p.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "Steve test cat"));
            p = servman.updateServiceProvider(p);
            Service service = servman.registerService(p, "JAFER REGISTRY SERVICE", Protocol.PROTOCOL_SRW, "www.fostersontheweb.com");
            service.setDescription("Jafer Registry Search Service");
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));
            service.addCategory(regman.getCategory(CategoryType.CATEGORY_LCSH, "Steve test cat"));
            service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.srw.access.com");
            service.setAccessUrl(Protocol.PROTOCOL_Z3950, "www.z3950.access.com");
            service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.srw.wsdl.com");
            service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.z3950.wsdl.com");
            servman.updateService(service);
            */


            System.out.println("Deleting data Please Wait... ");
            if (provider != null)
            {
                servman.deleteServiceProvider(provider);
                provider = null;
            }
            System.out.println("");
            System.out.println("COMPLETED WITH OUT FAILURE ");

        }
        catch (RegistryException e)
        {
            System.out.println("");
            System.out.println("FAILED");
            System.out.println(e.getStackTraceString());
        }
        catch (Exception e)
        {
            System.out.println("");
            System.out.println("FAILED");
            e.printStackTrace();
        }
        finally
        {
            if (provider != null)
            {
                try
                {
                    servman.deleteServiceProvider(provider);
                }
                catch (RegistryException e1)
                {
                    BusinessEntity entity = (BusinessEntity) provider;
                    System.out.println("FAILED TO DELETE KEY is " + entity.getId());
                    System.out.println(e1.toString());
                }
            }
        }

    }

    private static void testProvider() throws Exception, RegistryException
    {
        provider = servman.registerServiceProvider("JAFER COMPANY");
        System.out.println("");
        System.out.println("Registered business entity named 'JAFER COMPANY'...");

        System.out.println("Adding description and homepage link to business entity...");

        provider.setDescription("This is the jafer test company");
        provider.setHomePage("www.jafer.org");
        System.out.println("Updating business entity name from 'JAFER COMPANY' to 'JAFER TEST COMPANY'...");
        provider.setName("JAFER TEST COMPANY");

        System.out.println("Creating a Contact...");
        provider.setContact(servman.createNewContact("Ben D Wire", "worker", "0898 345 6782", "ben@dwire.com"));

        System.out.println("Adding 2 categories to business entity 'JAFER TEST COMPANY'......");

        provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));
        provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Steve test cat"));

        System.out.println("Updating all changes to business entity 'JAFER TEST COMPANY'...");
        provider = servman.updateServiceProvider(provider);

        System.out.println("Finding categories on business entity 'JAFER TEST COMPANY'...");
        printCategories(provider.getCategories());

        System.out.println("");
        System.out.print("Check registered 'JAFER TEST COMPANY' on UBR for changes then press enter here to continue ---->");
        input.readLine();
        System.out.println("");

        System.out.println("");
        System.out.println("Removing category 'Andy test cat' from business entity 'JAFER TEST COMPANY'...");
        provider.removeCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));

        System.out.println("Updating all changes to business entity 'JAFER TEST COMPANY'...");
        provider = servman.updateServiceProvider(provider);

        System.out.println("Finding categories on business entity 'JAFER TEST COMPANY'...");
        printCategories(provider.getCategories());

        System.out.println("Performing a search via serviceLocator 'JAFER TEST COMPANY'...");
        printSearchProviderResults(servloc.findServiceProvider(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS,
                "steve test cat")));

        if (provider != null)
        {
            servman.deleteServiceProvider(provider);
            provider = null;
        }
    }

    private static void testServices() throws Exception, RegistryException
    {
        provider = servman.registerServiceProvider("JAFER COMPANY");
        provider.setDescription("This is the jafer test company");
        provider.setHomePage("www.jafer.org");
        provider.setName("JAFER TEST COMPANY");
        provider.setContact(servman.createNewContact("Ben D Wire", "worker", "0898 345 6782", "ben@dwire.com"));
        provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));
        provider.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Steve test cat"));
        provider = servman.updateServiceProvider(provider);

        System.out.println("");
        System.out.println("** SERVICE PROVIDER TESTS **");
        System.out.println("");
        System.out.println("Registered business service 'JAFER SERVICE' to business entity named 'JAFER TEST COMPANY'...");

        Service service = servman.registerService(provider, "JAFER SERVICE", Protocol.PROTOCOL_SRW, "www.fostersontheweb.com");
        System.out.println("Adding description to business service...");
        service.setDescription("Jafer Search Service");

        System.out.println("Adding 2 categories to business service 'JAFER SERVICE'...");
        service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat"));
        service.addCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Steve test cat"));

        System.out.println("Updating all changes to business service 'JAFER SERVICE'...");
        servman.updateService(service);

        System.out.println("Extacing business services from busines entity 'JAFER TEST COMPANY'...");

        Iterator iter = provider.getServices().iterator();
        System.out.println("Found " + provider.getServices().size() + " business services");

        // loop round processing each uddi business service
        while (iter.hasNext())
        {
            Service s = (Service) iter.next();
            System.out.println("    SERVICE - " + s.getName() + " = " + s.getDescription());
        }

        System.out.println("Finding categories on business service 'JAFER SERVICE'...");
        printCategories(service.getCategories());

        System.out.println("");
        System.out.println("Removing category 'Steve test cat' from business service 'JAFER SERVICE'...");
        service.removeCategory(regman.getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Steve test cat"));

        System.out.println("Updating all changes to business service 'JAFER SERVICE'...");
        servman.updateService(service);

        System.out.println("Finding categories on business service 'JAFER SERVICE'...");
        printCategories(service.getCategories());

        System.out.println("Finding access points on business service 'JAFER SERVICE'...");
        System.out.println("    EndPoint  = " + service.getAccessUrl(Protocol.PROTOCOL_SRW));
        System.out.println("    WSDLPoint = " + service.getWSDLUrl(Protocol.PROTOCOL_SRW));

        System.out.println("");
        System.out.print("Check registered 'JAFER TEST COMPANY' on UBR for changes then press enter here to delete ---->");
        input.readLine();
        System.out.println("");

        System.out.println("Adding wsdl  url to business service 'JAFER SERVICE'...");
        service.setWSDLUrl(Protocol.PROTOCOL_SRW, "www.andy.com");
        System.out.println("Updating access url to business service 'JAFER SERVICE'...");
        service.setAccessUrl(Protocol.PROTOCOL_SRW, "www.steve.com");

        service.setWSDLUrl(Protocol.PROTOCOL_SRW, "");
        service.setWSDLUrl(Protocol.PROTOCOL_Z3950, "www.andy.com");

        System.out.println("Updating all changes to business service 'JAFER SERVICE'...");
        servman.updateService(service);

        System.out.println("Finding categories on business service 'JAFER SERVICE'...");
        printCategories(service.getCategories());

        System.out.println("Finding access points on business service 'JAFER SERVICE'...");
        System.out.println("    EndPoint  = " + service.getAccessUrl(Protocol.PROTOCOL_SRW));
        System.out.println("    WSDLPoint = " + service.getWSDLUrl(Protocol.PROTOCOL_SRW));

        System.out.println("Performing a search via serviceLocator 'JAFER TEST COMPANY'...");
        printSearchProviderResults(servloc.findServiceProvider(Protocol.PROTOCOL_Z3950));

        printSearchServicesResults(servloc.findService("JAFER%"));
        printSearchServicesResults(servloc.findService(regman
                .getCategory(CategoryType.CATEGORY_GENERAL_KEYWORDS, "Andy test cat")));
        printSearchServicesResults(servloc.findService(Protocol.PROTOCOL_Z3950));
        printSearchServicesResults(servloc.findService((ServiceProviderInfo) servloc.findServiceProvider("JAFER%").get(0)));

        System.out.println("");
        System.out.print("Check registered 'JAFER TEST COMPANY' on UBR for changes then press enter here to delete ---->");
        input.readLine();
        System.out.println("");

        if (provider != null)
        {
            servman.deleteServiceProvider(provider);
            provider = null;
        }
    }


}
