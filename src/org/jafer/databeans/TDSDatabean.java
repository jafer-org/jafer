/**
 * JAFER Toolkit Project.
 * Copyright (C) 2002, JAFER Toolkit Project, Oxford University.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */

package org.jafer.databeans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.jtds.jdbcx.TdsDataSource;

import org.jafer.exception.JaferException;
import org.w3c.dom.Node;

public class TDSDatabean extends JDBC
{

    /** uses settings from superclass: */
    // public final static String CONFIG_FILE =
    // "org/jafer/conf/jdbcConfig/config.xml";
    // public final static String QUERY_XSLT =
    // "org/jafer/conf/jdbcConfig/query.xsl";
    

    public int submitQuery(Node query) throws JaferException
    {
//      reset the last search exception
        setSearchException(null);
        try
        {/**
         * Presumes ResultSet in use is NOT scrollable, and cannot be re-used
         * for Present operation.
         */
        /** @todo more specific error message: when connection isn't made... */
        if (dataSource == null)
            configureDataSource();

        this.query = query;
        setQueryString("select count(*) ");

        
            ResultSet results = getStatement().executeQuery(getQueryString());
            // logger.log(Level.FINE, "submitQuery(): "+getQueryString());
            System.out.println(getQueryString());
            results.next();
            nResults = results.getInt(1);

            return nResults;
        }
        catch (SQLException ex)
        {
            // store search exception for caller to check against
            setSearchException(new JaferException("Error in database connection, or in generation/execution of SQL statement", ex));
            throw getSearchException();
        }
        catch (JaferException exc)
        {
            // store search exception for caller to check against
            setSearchException(exc);
            throw getSearchException();
        }
    }

    protected boolean alignCursor() throws SQLException, JaferException
    {

        while (resultSet.getRow() != getRecordCursor())
        {
            if (resultSet.getRow() < getRecordCursor())
                resultSet.next(); // put in cache here?
            else
                search(); // TdsDataSource ResultSet is FORWARD ONLY
        }
        return true;
    }

    protected void configureDataSource() throws JaferException
    {

        dataSource = new TdsDataSource();
        ((TdsDataSource) dataSource).setServerName(getHost());
        ((TdsDataSource) dataSource).setPortNumber(getPort());
        ((TdsDataSource) dataSource).setDatabaseName(getCurrentDatabase());
        ((TdsDataSource) dataSource).setUser(username);
        ((TdsDataSource) dataSource).setPassword(password);
    }

    protected Statement getStatement() throws SQLException
    {

        return getConnection().createStatement();
    }
}