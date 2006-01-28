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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This class runs the entire JAFER test suite. 
 */
public class ZClientTestSuite
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ZClientTestSuite.suite());
    }

    public static Test suite()
    {
        
        TestSuite suite = new TestSuite("Test for org.jafer.zclient");
        //$JUnit-BEGIN$
        suite.addTestSuite(SRUBindingTest.class);
        suite.addTestSuite(SRWBindingTest.class);
        suite.addTestSuite(SRWClientTest.class);
        //$JUnit-END$
        return suite;
    }
}

/**
 * Tests the FUNC_TESTED functionality by:
 * <UL>
 * <li>
 * <li>
 * <li>
 * <li>
 * <li>
 * <li>
 * </UL>
 */