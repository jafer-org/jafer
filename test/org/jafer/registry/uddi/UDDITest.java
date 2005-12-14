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

import java.io.BufferedReader;

import junit.framework.TestCase;

import org.jafer.registry.RegistryFactory;
import org.jafer.registry.RegistryManager;
import org.jafer.registry.ServiceLocator;
import org.jafer.registry.ServiceManager;
import org.jafer.registry.UDDITestConfig;

/**
 * Suoer class for all UDDI tests providing basic set up and tear down
 */
public abstract class UDDITest extends TestCase
{

        // the connection strings
    protected static String inquiryURL = null;

    protected static String publishURL = null;

    // the username and credentials
    protected static String username = null;

    protected static String credential = null;

    // provides a connection to the registry manager for the tests
    protected static RegistryManager regman = null;

    protected static ServiceLocator servloc = null;

    protected static ServiceManager servman = null;

    public UDDITest() throws Exception
    {
        initialise();
    }

    public UDDITest(String name) throws Exception
    {
        super(name);
        initialise();
    }

    /**
     * This method loads all the basic information form the properties file if
     * it has not been loaded by another test. It also creates fully initialised
     * RegistryManager, ServiceManager and ServiceLocator for the tests to make
     * use of
     * 
     * @throws Exception
     */
    private void initialise() throws Exception
    {
        // only load if the values are still null
        if (inquiryURL == null || publishURL == null || username == null || credential == null)
        {
            UDDITestConfig prop = UDDITestConfig.getInstance();

            inquiryURL = prop.getProperty(UDDITestConfig.UDDI_INQUIRE_URL);
            publishURL = prop.getProperty(UDDITestConfig.UDDI_PUBLISH_URL);
            username = prop.getProperty(UDDITestConfig.UDDI_USERNAME);
            credential = prop.getProperty(UDDITestConfig.UDDI_CREDENTIAL);

            // make sure we loaded everything
            if (inquiryURL == null || publishURL == null || username == null || credential == null)
            {
                throw new Exception("Missing properties can not configure test check: " + UDDITestConfig.UDDI_CONFIG);
            }

            // check to see if the credential is entered
            if (credential.length() == 0)
            {
                // try and get of the command line
                BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                System.out.print("Please Enter Logon Credential for " + username + ":");
                credential = input.readLine();
            }

            // initialise reg manager
            regman = RegistryFactory.createRegistryManager(inquiryURL, publishURL);
            servman = regman.getServiceManager(username, credential);
            servloc = regman.getServiceLocator();
        }
    }
}
