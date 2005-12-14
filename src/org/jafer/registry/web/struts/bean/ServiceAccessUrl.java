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

package org.jafer.registry.web.struts.bean;

/**
 * This class descriibes an access point for the screen
 */
public class ServiceAccessUrl
{
    /**
     * Stores a reference to the protocol type
     */
    String protocol = "";

    /**
     * Stores a reference to the type WSDL or Acess Point
     */
    String type = "";

    /**
     * Stores a reference to the access point URL
     */
    String url = "";
    
    /**
     * Stores a reference to whether the url is a WSDL file or not
     */
    boolean isWSDL = false;

    /**
     * Contructor
     * @param protocol The protocol type
     * @param type The access point type
     * @param url The access point URL
     * @param isWSDL true if access point is a WSDL file
     */
    public ServiceAccessUrl(String protocol, String type, String url, boolean isWSDL)
    {
        this.protocol = protocol;
        this.type = type;
        this.url = url;
        this.isWSDL = isWSDL;
    }
    
    /**
     * Gets the protocol
     * @return Returns the protocol.
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * Gets the type
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets the URL
     * @return Returns the url.
     */
    public String getUrl()
    {
        return url;
    }
    
    public boolean isWSDL()
    {
        return isWSDL;
    }
}
