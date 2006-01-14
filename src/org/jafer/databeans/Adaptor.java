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

package org.jafer.databeans;

// import java.util.logging.Logger;
// import java.util.logging.Level;
import javax.xml.transform.Templates;

import org.jafer.exception.JaferException;
import org.jafer.interfaces.Databean;
import org.jafer.interfaces.Present;
import org.jafer.interfaces.Search;
import org.jafer.record.Field;

/**
 * <p>
 * Super class for adaptors, includes methods to set transforms via templates
 * object and specify source and target schemas - configured via server.xml
 * </p>
 * 
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class Adaptor extends Databean implements Search, Present
{

    private Databean databean;

    private Templates template;

    private String sourceSchema, targetSchema;

    public Adaptor()
    {
    }

    public void setTransform(Templates template)
    {
        this.template = template;
    }

    public Templates getTransform()
    {
        return template;
    }

    public void setSourceSchema(String sourceSchema)
    {
        this.sourceSchema = sourceSchema;
    }

    public String getSourceSchema()
    {
        return sourceSchema;
    }

    public void setTargetSchema(String targetSchema)
    {
        this.targetSchema = targetSchema;
    }

    public String getTargetSchema()
    {
        return targetSchema;
    }

    public void setDatabean(Databean databean)
    {
        this.databean = databean;
    }

    public Databean getDatabean()
    {
        return databean;
    }

    public int submitQuery(Object query) throws JaferException
    {
        return ((Search) getDatabean()).submitQuery(query);
    }

    public Field getCurrentRecord() throws JaferException
    {
        return ((Present) getDatabean()).getCurrentRecord();
    }

    public void setRecordCursor(int nRecord) throws JaferException
    {
        ((Present) this.getDatabean()).setRecordCursor(nRecord);
    }

    public int getRecordCursor()
    {
        return ((Present) this.getDatabean()).getRecordCursor();
    }

    public void setCheckRecordFormat(boolean checkRecordFormat)
    {
        ((Present) this.getDatabean()).setCheckRecordFormat(checkRecordFormat);
    }

    public boolean isCheckRecordFormat()
    {
        return ((Present) this.getDatabean()).isCheckRecordFormat();
    }

    public void setElementSpec(String elementSpec)
    {
        ((Present) this.getDatabean()).setElementSpec(elementSpec);
    }

    public String getElementSpec()
    {
        return ((Present) this.getDatabean()).getElementSpec();
    }

    public void setRecordSchema(String schema)
    {
        ((Present) this.getDatabean()).setRecordSchema(schema);
    }

    public String getRecordSchema()
    {
        return ((Present) this.getDatabean()).getRecordSchema();
    }

    public String getCurrentDatabase() throws JaferException
    {
        return ((Present) this.getDatabean()).getCurrentDatabase();
    }

    // public void setSearchProfile(String searchProfile) {
    // ((Search)this.getDatabean()).setSearchProfile(searchProfile);
    // }

    // public String getSearchProfile() {
    // return ((Search)this.getDatabean()).getSearchProfile();
    // }

    public void setResultSetName(String resultSetName)
    {
        /** @todo not implemented */
    }

    public String getResultSetName()
    {
        return ((Search) this.getDatabean()).getResultSetName();
    }

    public void setDatabases(String database)
    {
        ((Search) this.getDatabean()).setDatabases(database);
    }

    public void setDatabases(String[] databases)
    {
        /**
         * @todo not implemented - we don't want to setDatabases on lower bean
         *       here
         */
        // ((Search)this.getDatabean()).setDatabases(databases);
    }

    public String[] getDatabases()
    {
        return ((Search) this.getDatabean()).getDatabases();
    }

    public void setParseQuery(boolean parseQuery)
    {
        ((Search) this.getDatabean()).setParseQuery(parseQuery);
    }

    public boolean isParseQuery()
    {
        return ((Search) this.getDatabean()).isParseQuery();
    }

    public void saveQuery(String file) throws JaferException
    {
        ((Search) this.getDatabean()).saveQuery(file);
    }

    public int getNumberOfResults()
    {
        return ((Search) this.getDatabean()).getNumberOfResults();
    }

    public int getNumberOfResults(String databaseName)
    {
        return ((Search) this.getDatabean()).getNumberOfResults(databaseName);
    }

    public Object getQuery()
    {
        return ((Search) this.getDatabean()).getQuery();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.interfaces.Search#getSearchException(java.lang.String)
     */
    public JaferException getSearchException(String database) throws JaferException
    {
        return ((Search) this.getDatabean()).getSearchException(database);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jafer.interfaces.Search#getSearchException(java.lang.String[])
     */
    public JaferException[] getSearchException(String[] databases) throws JaferException
    {
        return ((Search) this.getDatabean()).getSearchException(databases);
    }
}