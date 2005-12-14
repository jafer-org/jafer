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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jafer.registry.UDDITestConfig;

/**
 * This class runs the entire UDDI test suite. It will look in the
 * uddi.properties file the access level the user has on the registry and only
 * run the rlevent tests. Level 2 will by default run all the level 1 tests.
 * <br>
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
public class UDDITestSuite
{

   

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(UDDITestSuite.suite());
    }

    public static Test suite()
    {
        UDDITestConfig prop = UDDITestConfig.getInstance();

        String level = prop.getProperty(UDDITestConfig.UDDI_LEVEL);
        
        TestSuite suite = new TestSuite("Test for org.jafer.registry.uddi");
        //$JUnit-BEGIN$
        suite.addTestSuite(RegistryManagerLevel1Test.class);
        suite.addTestSuite(ServiceManagerLevel1AccessTest.class);
        suite.addTestSuite(ServiceLocatorLevel1AccessTest.class);
        // only add the level two tests if set in UDDI.properties file
        if (level.equals("2"))
        {
            suite.addTestSuite(ServiceManagerLevel2AccessTest.class);
            suite.addTestSuite(ServiceLocatorLevel2AccessTest.class);            
        }
        //$JUnit-END$
        return suite;
    }
}

/**
 * Tests the register service functionality by:
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
 * categories </b> have been given unusual values and <b>are not expected to be
 * attached to any other objects </b>. If any of these pre-conditions are not
 * met then assertions will fail when searchs return more than the expected
 * count of results </b>
 */