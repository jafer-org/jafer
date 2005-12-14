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

package org.jafer.registry.model;

import java.io.Serializable;

/**
 * This interface represents the business entities contact infomration for
 * JAFER.
 */
public interface Contact extends Serializable
{

    /**
     * Gets the name of the contact
     * 
     * @return The contacts name
     */
    public String getName();

    /**
     * Sets the name of the contact
     * 
     * @param name The name to set
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public void setName(String name) throws InvalidNameException, InvalidLengthException;

    /**
     * Gets the description of the contact in English
     * 
     * @return The contacts name
     */
    public String getDescription();

    /**
     * Sets the description of the contact in English
     * 
     * @param description The description to set
     * @throws InvalidLengthException
     */
    public void setDescrition(String description) throws InvalidLengthException;;

    /**
     * Gets the phone of the contact
     * 
     * @return The contacts name
     */
    public String getPhone();

    /**
     * Sets the phone of the contact
     * 
     * @param phone The phone to set
     * @throws InvalidLengthException
     */
    public void setPhone(String phone) throws InvalidLengthException;

    /**
     * Gets the email of the contact
     * 
     * @return The contacts name
     */
    public String getEmail();

    /**
     * Sets the email of the contact
     * 
     * @param email The email to set
     * @throws InvalidLengthException
     */
    public void setEmail(String email) throws InvalidLengthException;

}
