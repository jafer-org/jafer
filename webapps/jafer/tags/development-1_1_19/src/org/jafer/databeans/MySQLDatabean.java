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

import org.jafer.exception.JaferException;
import org.jafer.query.XMLRPNQuery;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.util.logging.Level;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import z3950.v3.RPNQuery;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


public class MySQLDatabean extends JDBC {

 /** uses settings from superclass: */
//  public final static String CONFIG_FILE = "org/jafer/conf/jdbcConfig/config.xml";
//  public final static String QUERY_XSLT = "org/jafer/conf/jdbcConfig/query.xsl";

  public int submitQuery(Object query) throws JaferException {

    if (query instanceof Node)
      return submitQuery((Node)query);
    else if (query instanceof RPNQuery)
      return submitQuery((RPNQuery)query);
    else
      throw new JaferException("Only queries of type Node or RPNQuery accepted. (See www.jafer.org)");
  }

  public int submitQuery(RPNQuery query) throws JaferException {

    return submitQuery(XMLRPNQuery.getXMLQuery(query));
  }

  public int submitQuery(Node query) throws JaferException {
/** Overrides superclass submitQuery() in order to have the option of piggyback/re-using the same result set for
 Search and Present. (has scrollable resultSet)*/

    if (dataSource == null)
      configureDataSource();


      try {
	this.query = query;
	String selectPhrase = "";
	if (Boolean.valueOf(getXMLConfigValue("piggyback")).booleanValue())
	  selectPhrase = "select * ";/** @todo  */
	setQueryString(selectPhrase);
        resultSet = getStatement().executeQuery(getQueryString());
	logger.log(Level.FINE, "MySQLDatabean.submitQuery(): "+getQueryString());
        resultSet.last();
        nResults = resultSet.getRow();
        return nResults;
      }
      catch (SQLException ex) {
	throw new JaferException("Error in database connection, or in generation/execution of SQL statement", ex);
      }
  }

  protected Statement getStatement() throws SQLException {

    Statement statement = getConnection().createStatement();
//    statement.setFetchSize(getFetchSize());
    return statement;
  }

  protected void configureDataSource() {

    dataSource = new MysqlDataSource();
    ((MysqlDataSource)dataSource).setServerName(getHost());
    ((MysqlDataSource)dataSource).setPortNumber(getPort());
    ((MysqlDataSource)dataSource).setDatabaseName(getCurrentDatabase());
    ((MysqlDataSource)dataSource).setUser(username);
    ((MysqlDataSource)dataSource).setPassword(password);
  }

  protected Connection getConnection() throws SQLException {
  /** @todo test to see if timed out? */
  if (connection != null)
    return connection;

  connection = dataSource.getConnection();
  return connection;
  }
}