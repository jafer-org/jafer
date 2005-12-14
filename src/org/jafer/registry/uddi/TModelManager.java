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

package org.jafer.registry.uddi;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jafer.registry.InvalidAuthorisationDetailsException;
import org.jafer.registry.RegistryException;
import org.jafer.registry.RegistryNotInitialisedException;
import org.jafer.registry.model.CategoryType;
import org.jafer.registry.model.Protocol;
import org.jafer.registry.uddi.model.TModel;
import org.jafer.util.xml.ParseFactory;
import org.jafer.util.xml.ParsingException;
import org.jafer.util.xml.ParsingUtils;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.OverviewDoc;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.response.TModelInfo;
import org.uddi4j.response.TModelList;
import org.uddi4j.transport.TransportException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class manages TModel instances in the registry it is connected to. These
 * have to be initialised first for the user to perform any operations on
 * service providers and their services. The TModels implement the registry
 * protocol and category concepts. In order to create missing tmodels a user
 * name and credential is required. Some applications do not want to expose
 * these to the user hence the TModelManager can be created in two modes. <bR>
 * <br>
 * <b>Read only - </b> The manager will only attempt to load the tmodels
 * throwing a RegistryNotInitialisedException if expected models are not found
 * <br>
 * <b>Create - </b>The manager will load and create any tmodels that are
 * missing.
 */
public class TModelManager
{

    /**
     * Stores a reference to the Z3950 TMODEL key. This key must map exactly to
     * a jafertmodel in the tmodels.xml configuration file.
     */
    public final static String PROTOCOL_Z3950 = "Z3950";

    /**
     * Stores a reference to the SRW TMODEL key. This key must map exactly to a
     * jafertmodel in the tmodels.xml configuration file.
     */
    public final static String PROTOCOL_SRW = "SRW";

    /**
     * Stores a reference to the DDC TMODEL key. This key must map exactly to a
     * jafertmodel in the tmodels.xml configuration file.
     */
    public final static String CATEGORY_DDC = "DDC";

    /**
     * Stores a reference to the LCSH TMODEL key. This key must map exactly to a
     * jafertmodel in the tmodels.xml configuration file.
     */
    public final static String CATEGORY_LCSH = "LCSH";

    /**
     * Stores a reference to the general keywords TMODEL key. This key must map
     * exactly to a jafertmodel in the tmodels.xml configuration file.
     */
    public final static String CATEGORY_GENERAL_KEYWORDS = "uddi-org:general_keywords";

    /**
     * Stores a reference to the configuration file location
     */
    public final static String TMODEL_CONFIG_FILE = "org/jafer/conf/registry/tmodels.xml";

    /**
     * Stores a reference to the Logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.registry.uddi4jimpl.TModelManager");

    /**
     * Stores a reference to the cache of available TModels.
     */
    private HashMap tModels = new HashMap();

    /**
     * This constructor assumes that all TModels are set up already in the
     * registry and hence searches to find the required keys only. All keys must
     * be found for construction to complete succesfully. It is provided to
     * allow callers to initialise seperatley to their application so that user
     * names and credentials do not have to be supplied when only providing a
     * search interface.
     *
     * @param registryConnection The instance to use to comunicate with the
     *        registry
     * @throws RegistryNotInitialisedException
     * @throws RegistryException
     */
    public TModelManager(UDDIProxy registryConnection) throws RegistryNotInitialisedException, RegistryException
    {
        try
        {
            // build the cache by searching. No auto create enabled for missing
            // TModels.
            buildTModelCache(registryConnection, "", "", false);
        }
        catch (InvalidAuthorisationDetailsException e)
        {
            // This should never ocurr as flag indicates we never create
            throw new RegistryExceptionImpl(e);
        }
    }

    /**
     * This constructor assumes that all TModels are not set up in the registry
     * and hence attempts to define them if it is unable to locate them first.
     *
     * @param registryConnection The connection to the UDDI registry
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @throws RegistryNotInitialisedException Signifies the registry has not
     *         been pre-initialised
     * @throws RegistryInitialisationException
     * @throws InvalidAuthorisationDetailsException
     */
    public TModelManager(UDDIProxy registryConnection, String username, String credential) throws RegistryException,
            InvalidAuthorisationDetailsException
    {
        buildTModelCache(registryConnection, username, credential, true);
    }

    /**
     * Gets the specified TModel instance
     *
     * @param tModelName The name of the TModel required
     * @return The required TModel or NULL if it does not exist in cache
     */
    public TModel getTModel(String tModelName)
    {
        return (TModel) tModels.get(tModelName);
    }

    /**
     * This method returns a TModel representing the category type
     *
     * @param categoryType The category type to check against
     * @return The TModel instance
     * @throws RegistryException
     */
    public TModel getCategoryTModel(CategoryType categoryType) throws RegistryException
    {
        // we need to map the category type on to the appropriate TModels
        if (categoryType == CategoryType.CATEGORY_GENERAL_KEYWORDS)
        {
            return getTModel(TModelManager.CATEGORY_GENERAL_KEYWORDS);
        }
        else if (categoryType == CategoryType.CATEGORY_LCSH)
        {
            return getTModel(TModelManager.CATEGORY_LCSH);
        }
        else if (categoryType == CategoryType.CATEGORY_DDC)
        {
            return getTModel(TModelManager.CATEGORY_DDC);
        }
        else
        {
            throw new RegistryExceptionImpl("Unknown Category Type: " + categoryType);
        }
    }

    /**
     * This method returns a TModel representing the protocol
     *
     * @param protocol The protocol type to check against
     * @return The TModel instance
     * @throws RegistryException
     */
    public TModel getProtocolTModel(Protocol protocol) throws RegistryException
    {
        // we need to map the category type on to the appropriate TModels
        if (protocol == Protocol.PROTOCOL_Z3950)
        {
            return getTModel(TModelManager.PROTOCOL_Z3950);
        }
        else if (protocol == Protocol.PROTOCOL_SRW)
        {
            return getTModel(TModelManager.PROTOCOL_SRW);
        }
        else
        {
            throw new RegistryExceptionImpl("Unknown protocol: " + protocol);
        }
    }

    /**
     * This method is responsible for building the cache of available TMOdels.
     * It uses the tmodels.xml configuration file to specify the TModels it
     * should search for as the construction information required if it is not
     * found. This method will only create TModels if they are not found and the
     * create if not found flag has been set.
     *
     * @param registryConnection The connection to the UDDI registry
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @param createIfNotFound Flag to indicate if the the TModel should be
     *        created if not found
     * @throws RegistryNotInitialisedException
     * @throws RegistryExceptionImpl
     * @throws InvalidAuthorisationDetailsException
     */
    private void buildTModelCache(UDDIProxy registryConnection, String username, String credential, boolean createIfNotFound)
            throws RegistryNotInitialisedException, RegistryException, InvalidAuthorisationDetailsException
    {
        logger.entering("TModelManager", "buildTModelCache");
        try
        {
            // parse the configuration document
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(TMODEL_CONFIG_FILE);
            Document document = ParseFactory.parse(stream);

            // get the list of jafer tmodels
            NodeList jaferTModels = document.getElementsByTagName("jafertmodel");
            // make sure we found atleast one tmodel
            if (jaferTModels.getLength() == 0)
            {
                throw new RegistryExceptionImpl("No Jafer TModels found in configuration file");
            }

            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Processing " + jaferTModels.getLength() + " jafer TModels");
            }

            // now iterate and process each tmodel
            for (int i = 0; i < jaferTModels.getLength(); i++)
            {
                Node jaferTModelNode = jaferTModels.item(i);
                // build the TModel
                TModel tmodel = createJaferTModel(registryConnection, username, credential, createIfNotFound, jaferTModelNode);
                // add the tmodel to the TModels map
                tModels.put(tmodel.getName(), tmodel);
            }
        }
        catch (ParsingException e)
        {
            throw new RegistryExceptionImpl("Error parsing TModel configuration file ", e);
        }
        finally
        {
            logger.exiting("TModelManager", "buildTModelCache");
        }
    }

    /**
     * Creates a complete Jafer TModel that encapsulates the set of defined
     * UDDI TModels.
     *
     * @param registryConnection The connection to the UDDI registry
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @param createIfNotFound Flag to indicate if the the TModel should be
     *        created if not found
     * @param jaferTModelNode The XML node representing a jafer tmodel in the
     *        configuration file
     * @return The jafer TModel that encapsulates the UDDI TModels
     * @throws RegistryNotInitialisedException
     * @throws RegistryExceptionImpl
     * @throws JaferException
     * @throws InvalidAuthorisationDetailsException
     */
    private TModel createJaferTModel(UDDIProxy registryConnection, String username, String credential, boolean createIfNotFound,
            Node jaferTModelNode) throws RegistryNotInitialisedException, RegistryException, ParsingException,
            InvalidAuthorisationDetailsException
    {
        logger.entering("TModelManager", "buildTModel");
        // create a vector to hold all the loaded TModel instances.
        Vector models;
        // get the name of the TModel
        String name;
        try
        {
            models = new Vector();
            name = ParsingUtils.getValue(ParsingUtils.selectSingleNode(jaferTModelNode, "@name"));
            // make sure we got a name
            if (name == null || name.length() == 0)
            {
                throw new RegistryExceptionImpl("No name attrb specified for JAFER tmodel");
            }

            // get each defined tmodel tag
            NodeList tmodels = ParsingUtils.selectNodeList(jaferTModelNode, "tmodel");
            // make sure we found atleast one tmodel
            if (tmodels.getLength() == 0)
            {
                throw new RegistryExceptionImpl("No UDDI TModels found in configuration file for TMODEL " + name);
            }

            // now iterate and process each tmodel
            for (int i = 0; i < tmodels.getLength(); i++)
            {
                Node tModelNode = tmodels.item(i);

                // get the TModel attributes
                String tModelName = ParsingUtils.getValue(ParsingUtils.selectSingleNode(tModelNode, "@name"));
                String tModelDesc = ParsingUtils.getValue(ParsingUtils.selectSingleNode(tModelNode, "@desc"));
                String tModeldocURL = ParsingUtils.getValue(ParsingUtils.selectSingleNode(tModelNode, "@docurl"));

                // make sure we have no empty strings returned.
                if ((tModelName == null || tModelName.length() == 0) || (tModelDesc == null || tModelDesc.length() == 0)
                        || (tModeldocURL == null || tModeldocURL.length() == 0))
                {
                    throw new RegistryExceptionImpl("Invalid UDDI TModel MISSING ATTRIBUTES for TMODEL " + name);
                }

                // now we need to find and intialise the TModels from the UDDI
                // registry

                TModelList foundModels = registryConnection.find_tModel(tModelName, null, null, null, 10);
                int numModelsFound = foundModels.getTModelInfos().size();

                // model must be found if not auto creating
                if (numModelsFound == 0 && createIfNotFound == false)
                {
                    // model was expected to be already registered
                    throw new RegistryNotInitialisedException(tModelName);
                }
                if (numModelsFound == 0 && createIfNotFound == true)
                {
                    // create the TModel instance in the registry
                    models.add(publishTModel(registryConnection, username, credential, tModelName, tModelDesc, tModeldocURL));
                }
                else
                {
                    // instantiate all the found TModels and add to models array
                    Iterator iter = foundModels.getTModelInfos().getTModelInfoVector().iterator();
                    while (iter.hasNext())
                    {
                        TModelInfo info = (TModelInfo) iter.next();
                        models.add(retrieveTModel(registryConnection, info.getTModelKey()));
                    }
                }
            }
        }
        catch (org.uddi4j.UDDIException e)
        {
            throw new RegistryExceptionImpl("Error finding tmodels: ", e);
        }
        catch (TransportException e)
        {
            throw new RegistryExceptionImpl(e);
        }
        finally
        {
            logger.exiting("TModelManager", "buildTModel");
        }
        // return the new TModel that encapsulates all the registered TMOdels
        return new org.jafer.registry.uddi.model.TModel(models, name);
    }

    /**
     * This method creates the TModel in the registry and then returns it.
     *
     * @param registryConnection The connection to the UDDI registry
     * @param username The username of the user using the registry.
     * @param credential The credential required to authenticate user
     * @param name The name of the model to create
     * @param description the description of the model to create
     * @param docURL The url to the tmodel overview document
     * @return The fully created TModel from the UDDI registry
     * @throws RegistryExceptionImpl
     * @throws InvalidAuthorisationDetailsException
     */
    private org.uddi4j.datatype.tmodel.TModel publishTModel(UDDIProxy registryConnection, String username, String credential,
            String name, String description, String docURL) throws org.jafer.registry.RegistryException,
            InvalidAuthorisationDetailsException
    {
        logger.entering("TModelManager", "publishTModel");
        try
        {
            // get authorisation token
            AuthToken token = registryConnection.get_authToken(username, credential);

            // create TModel providing its name and blank key so that it is
            // generated
            org.uddi4j.datatype.tmodel.TModel tmodel = new org.uddi4j.datatype.tmodel.TModel("", name);
            // set description
            tmodel.setDefaultDescriptionString(description);
            // create overview information
            OverviewDoc overviewDoc = new OverviewDoc();
            overviewDoc.setOverviewURL(docURL);
            tmodel.setOverviewDoc(overviewDoc);
            // publish the tmodel
            Vector modelsToCreate = new Vector();
            modelsToCreate.add(tmodel);
            //publish the TModel
            TModelDetail tModelDetail = registryConnection.save_tModel(token.getAuthInfoString(), modelsToCreate);
            Vector foundModels = tModelDetail.getTModelVector();
            // make sure we have a created tmodel in vector
            if (foundModels.isEmpty())
            {
                throw new RegistryExceptionImpl("Error finding created Tmodel " + name);
            }
            // discard authorisation token
            registryConnection.discard_authToken(token.getAuthInfoString());

            // return the created tmodel
            return (org.uddi4j.datatype.tmodel.TModel) tModelDetail.getTModelVector().firstElement();
        }
        catch (org.uddi4j.UDDIException e)
        {
            // did we get invalid token error
            if (RegistryExceptionImpl.isErrorOfType(e.getDispositionReport(), DispositionReport.E_unknownUser))
            {
                // user did not authenticate correctly
                throw new InvalidAuthorisationDetailsException(e);
            }
            throw new RegistryExceptionImpl("Error creating tmodel: ", e);
        }
        catch (TransportException e)
        {
            throw new RegistryExceptionImpl(e);
        }
        finally
        {
            logger.exiting("TModelManager", "publishTModel");
        }
    }

    /**
     * This method creates the TModel by retrieving it from the UDDI registry
     *
     * @param registryConnection The connection to the UDDI registry
     * @param key The key for the TModel
     * @return The fully created TModel from the UDDI registry
     * @throws RegistryExceptionImpl
     */
    private org.uddi4j.datatype.tmodel.TModel retrieveTModel(UDDIProxy registryConnection, String key)
            throws org.jafer.registry.RegistryException
    {
        logger.entering("TModelManager", "retrieveTModel");
        try
        {
            Vector foundModels = registryConnection.get_tModelDetail(key).getTModelVector();
            // make sure we found a TModel although we should find it as it came
            // back in a search
            if (foundModels.isEmpty())
            {
                throw new RegistryExceptionImpl("Error finding tmodel detail for key " + key);
            }
            // return the found tmodel. As we are searching by key their can
            // only ever be one
            return (org.uddi4j.datatype.tmodel.TModel) foundModels.firstElement();
        }
        catch (org.uddi4j.UDDIException e)
        {
            throw new RegistryExceptionImpl("Error finding tmodel detail: ", e);
        }
        catch (TransportException e)
        {
            throw new RegistryExceptionImpl(e);
        }
        finally
        {
            logger.exiting("TModelManager", "retrieveTModel");
        }
    }
}
