/** JAFER Toolkit Project. Copyright (C) 2002, JAFER Toolkit Project, Oxford
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
package org.jafer.osid.provider;


public class IdManager implements org.osid.id.IdManager {

	public org.osid.shared.Id createId() throws org.osid.id.IdException {
		return new Id();
	}

	public org.osid.shared.Id getId(String idString) throws org.osid.id.IdException {
		return new Id(idString);
	}

	/**
	 * Verify to OsidLoader that it is loading
	 * 
	 * <p>
	 * OSID Version: 2.0
	 * </p>
	 * .
	 */
	 public void osidVersion_2_0() throws org.osid.OsidException {
	 }
	
	org.osid.OsidContext context;
	java.util.Properties configuration;

	public void assignOsidContext(org.osid.OsidContext context) throws org.osid.OsidException {
		this.context = context;
	}
	public void assignConfiguration(java.util.Properties configuration) throws org.osid.OsidException {
		this.configuration = configuration;
		//java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		//configuration.list(new java.io.PrintStream(bout));
		//System.out.println("config properties:\n"+bout.toString());
	}
	public org.osid.OsidContext getOsidContext() throws org.osid.OsidException {
		return context;
	}

}

