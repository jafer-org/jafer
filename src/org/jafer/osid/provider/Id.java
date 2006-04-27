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


public class Id implements org.osid.shared.Id {

	private String idString = null;
	
	public String getIdString() throws org.osid.shared.SharedException {
		return this.idString;
	}
	
	protected Id() {
		// This id implementation uses Java RMI UID() method concatenated to the IP address
		// of the local host.
		java.rmi.server.UID uid = new java.rmi.server.UID();
		idString = uid.toString();
		try {
			String ip = java.net.InetAddress.getLocalHost().toString();
			idString = idString + ip.substring(ip.indexOf("/") + 1);
		} catch (Exception ex) {}
	}
	
	protected Id(String idString) throws org.osid.id.IdException {
		if (idString == null) {
			throw new org.osid.id.IdException(org.osid.shared.SharedException.NULL_ARGUMENT);    
		}
		this.idString = idString;
	}
	
	public boolean isEqual(org.osid.shared.Id id) throws org.osid.shared.SharedException {
		return idString.equals(id.getIdString());
	}

}

