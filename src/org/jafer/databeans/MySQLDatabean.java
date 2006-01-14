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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.jafer.exception.JaferException;
import org.w3c.dom.Node;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MySQLDatabean extends JDBC
{

    /** uses settings from superclass: */
    // public final static String CONFIG_FILE =
    // "org/jafer/conf/jdbcConfig/config.xml";
    // public final static String QUERY_XSLT =
    // "org/jafer/conf/jdbcConfig/query.xsl";
    public int submitQuery(Node query) throws JaferException
    {
        // reset the last search exception
        setSearchException(null);
        try
        {
            /**
             * Overrides superclass submitQuery() in order to have the option of
             * piggyback/re-using the same result set for Search and Present.
             * (has scrollable resultSet)
             */

            if (dataSource == null)
                configureDataSource();

            this.query = query;
            String selectPhrase = "";
            if (Boolean.valueOf(getXMLConfigValue("piggyback")).booleanValue())
                selectPhrase = "select * ";
            /** @todo */
            setQueryString(selectPhrase);
            resultSet = getStatement().executeQuery(getQueryString());
            logger.log(Level.FINE, "MySQLDatabean.submitQuery(): " + getQueryString());
            resultSet.last();
            nResults = resultSet.getRow();
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

    protected Statement getStatement() throws SQLException
    {

        Statement statement = getConnection().createStatement();
        // statement.setFetchSize(getFetchSize());
        return statement;
    }

    protected void configureDataSource()
    {

        dataSource = new MysqlDataSource();
        ((MysqlDataSource) dataSource).setServerName(getHost());
        ((MysqlDataSource) dataSource).setPortNumber(getPort());
        ((MysqlDataSource) dataSource).setDatabaseName(getCurrentDatabase());
        ((MysqlDataSource) dataSource).setUser(username);
        ((MysqlDataSource) dataSource).setPassword(password);
    }

    protected Connection getConnection() throws SQLException
    {
        /** @todo test to see if timed out? */
        if (connection != null)
            return connection;

        connection = dataSource.getConnection();
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.interfaces.Search#getSearchException(java.lang.String)
     */
    public JaferException getSearchException(String database) throws JaferException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.interfaces.Search#getSearchException(java.lang.String[])
     */
    public JaferException[] getSearchException(String[] databases) throws JaferException
    {
        // TODO Auto-generated method stub
        return null;
    }

}