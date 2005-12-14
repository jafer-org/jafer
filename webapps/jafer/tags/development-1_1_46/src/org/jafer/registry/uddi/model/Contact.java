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

package org.jafer.registry.uddi.model;

import org.jafer.registry.model.InvalidLengthException;
import org.jafer.registry.model.InvalidNameException;
import org.uddi4j.datatype.business.Email;
import org.uddi4j.datatype.business.Phone;

/**
 * This class represent a contact for a business entity.This Class should never
 * be directly instantiated.
 */
public class Contact implements org.jafer.registry.model.Contact
{

    /**
     * Stores a reference to the maximumn number of characters for the phone
     * field
     */
    private static final int MAX_PHONE_LENGTH = 50;

    /**
     * Stores a reference to the maximumn number of characters for the name
     * field
     */
    private static final int MAX_NAME_LENGTH = 255;

    /**
     * Stores a reference to the maximumn number of characters for the description
     * field
     */
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    /**
     * Stores a reference to the maximumn number of characters for the email
     * field
     */
    private static final int MAX_EMAIL_LENGTH = 255;

    /**
     * Stores a reference to the use type for phone numbers when not already set
     */
    private static final String PHONE_USETYPE = "Main Phone";

    /**
     * Stores a reference to the use type for email address when not already set
     */
    private static final String EMAIL_USETYPE = "Main Email";

    /**
     * Stores a reference to the contact type. Set to Main Contact if not
     * currently set.
     */
    private String type = "Main Contact";

    /**
     * Stores a reference to the contacts name
     */
    private String name = "";

    /**
     * Stores a reference to the contacts description
     */
    private String description = "";

    /**
     * Stores a reference to the contacts email
     */
    private String email = "";

    /**
     * Stores a reference to the contacts phone
     */
    private String phone = "";

    /**
     * Constructor for the UDDI contact object
     * 
     * @param name The contacts name
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public Contact(String name) throws InvalidNameException, InvalidLengthException
    {
        if (name == null || name.length() == 0)
        {
            throw new InvalidNameException();
        }
        if (name.length() > MAX_NAME_LENGTH)
        {
            throw new InvalidLengthException("name", MAX_NAME_LENGTH);
        }
        this.name = name;
    }

    /**
     * Constructor for the UDDI contact object
     * 
     * @param name The contacts name
     * @param desc The contacts description
     * @param phone The contacts phone number
     * @param email The contacts email
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public Contact(String name, String desc, String phone, String email) throws InvalidNameException, InvalidLengthException
    {
        if (name == null || name.length() == 0)
        {
            throw new InvalidNameException();
        }
        if (name.length() > MAX_NAME_LENGTH)
        {
            throw new InvalidLengthException("name", MAX_NAME_LENGTH);
        }
        this.name = name;
        this.description = desc;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Creates a Contact object by extracting the details from the UDDI contact
     * object
     * 
     * @param uddiContact The uddi contact object to extract from
     */
    public Contact(org.uddi4j.datatype.business.Contact uddiContact)
    {

        // Set name and description
        name = uddiContact.getPersonNameString();
        description = uddiContact.getDefaultDescriptionString();

        // only store the type if currently set
        if (uddiContact.getUseType() != null && uddiContact.getUseType().length() > 0)
        {
            type = uddiContact.getUseType();
        }

        // Do we have a phone to set
        if (uddiContact.getPhoneVector().size() > 0)
        {
            Phone phone = (Phone) uddiContact.getPhoneVector().firstElement();
            this.phone = phone.getText();
        }
        // Do we have a email to set
        if (uddiContact.getEmailVector().size() > 0)
        {
            Email email = (Email) uddiContact.getEmailVector().firstElement();
            this.email = email.getText();
        }
    }

    /**
     * Updates the supplied uddi contact object
     * 
     * @param uddiContact The object to update
     * @return The updated uddicontact object
     */
    public void updateUDDIContact(org.uddi4j.datatype.business.Contact uddiContact)
    {
        uddiContact.setUseType(type);
        uddiContact.setPersonName(getName());

        //must have details to set
        if (getDescription().length() > 0)
        {
            uddiContact.setDefaultDescriptionString(getDescription());
        }
        // must have details to set
        if (getPhone().length() > 0)
        {
            // Do we have a phone to set
            if (uddiContact.getPhoneVector().size() > 0)
            {
                // update it
                Phone phone = (Phone) uddiContact.getPhoneVector().firstElement();
                phone.setText(getPhone());
            }
            else
            {
                // create it
                Phone phone = new Phone(getPhone());
                phone.setUseType(PHONE_USETYPE);
                uddiContact.getPhoneVector().add(phone);
            }
        }
        //must have details to set
        if (getEmail().length() > 0)
        {
            // Do we have a email to set
            if (uddiContact.getEmailVector().size() > 0)
            {
                // update it
                Email email = (Email) uddiContact.getEmailVector().firstElement();
                email.setText(getEmail());
            }
            else
            {
                // create it
                Email email = new Email(getEmail());
                email.setUseType(EMAIL_USETYPE);
                uddiContact.getEmailVector().add(email);
            }
        }
    }

    /**
     * Gets the name of the contact
     * 
     * @return The contacts name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the contact
     * 
     * @param name The name to set
     * @throws InvalidNameException
     * @throws InvalidLengthException
     */
    public void setName(String name) throws InvalidNameException, InvalidLengthException
    {
        if (name == null || name.length() == 0)
        {
            throw new InvalidNameException();
        }
        if (name.length() > MAX_NAME_LENGTH)
        {
            throw new InvalidLengthException("name", MAX_NAME_LENGTH);
        }
        this.name = name;
    }

    /**
     * Gets the description of the contact in English
     * 
     * @return The contacts name
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of the contact in English
     * 
     * @param description The description to set
     * @throws InvalidLengthException
     */
    public void setDescrition(String description) throws InvalidLengthException
    {
        if (description.length() > MAX_DESCRIPTION_LENGTH)
        {
            throw new InvalidLengthException("description", MAX_DESCRIPTION_LENGTH);
        }
        this.description = description;
    }

    /**
     * Gets the first phone number of the contact only
     * 
     * @return The contacts name
     */
    public String getPhone()
    {
        return phone;
    }

    /**
     * Sets the phone of the contact
     * 
     * @param phone The phone to set
     * @throws InvalidLengthException
     */
    public void setPhone(String phone) throws InvalidLengthException
    {
        if (phone.length() > MAX_PHONE_LENGTH)
        {
            throw new InvalidLengthException("phone", MAX_PHONE_LENGTH);
        }
        this.phone = phone;
    }

    /**
     * Gets the email of the contact
     * 
     * @return The contacts name
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets the email of the contact
     * 
     * @param email The email to set
     * @throws InvalidLengthException
     */
    public void setEmail(String email) throws InvalidLengthException
    {
        if (email.length() > MAX_EMAIL_LENGTH)
        {
            throw new InvalidLengthException("email", MAX_EMAIL_LENGTH);
        }
        this.email = email;
    }

}
