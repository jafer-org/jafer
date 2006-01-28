package org.jafer.zclient;

import gov.loc.www.zing.srw.interfaces.SRWPort;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub;
import org.apache.axis.AxisFault;
import java.net.MalformedURLException;

/**
 * This class binds an SRWSession to a host that supports SRW/SRU
 */
public class SRWBinding implements SRWPort
{

    /**
     * Stores a reference to the logger
     */
    protected static Logger logger = Logger.getLogger("org.jafer.zclient");

    /**
     * Stores a reference to url of the binding
     */
    private String url;

    /**
     * Constructor
     * 
     * @param url The url of the binding
     */
    public SRWBinding(String url)
    {
        this.url = url;
    }

    /**
     * This method performs the scanOperation connecting to the host using SRW
     * 
     * @param request The ScanRequestType message
     * @return The ScanResponseType message
     * @throws RemoteException
     */
    public SearchRetrieveResponseType searchRetrieveOperation(SearchRetrieveRequestType request) throws RemoteException
    {
        try
        {
            logger.fine("Executing search and retrieve operation using SRW");
            SRWPort srwBinding = new SRWSoapBindingStub(new java.net.URL(url), null);
            return srwBinding.searchRetrieveOperation(request);
        }
        catch (MalformedURLException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("IO ERROR performing SRW search: " + exc);
        }
        catch (AxisFault exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("Axis Fault performing SRW search", exc);
        }
        finally
        {
            logger.fine("Completed search and retrieve operation using SRW");
        }
    }

    /**
     * This method performs the searchRetrieveOperation connecting to the host
     * using SRW
     * 
     * @param body The SearchRetrieveRequestType message
     * @return The SearchRetrieveResponseType message
     * @throws RemoteException
     */
    public ScanResponseType scanOperation(ScanRequestType request) throws RemoteException
    {
        try
        {
            logger.fine("Executing scan operation using SRW");
            SRWPort srwBinding = new SRWSoapBindingStub(new java.net.URL(url), null);
            return srwBinding.scanOperation(request);
        }
        catch (MalformedURLException exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("IO ERROR performing SRW scan: " + exc);
        }
        catch (AxisFault exc)
        {
            logger.severe(exc.getMessage());
            throw new RemoteException("Axis Fault performing SRW scan", exc);
        }
        finally
        {
            logger.fine("Completed scan operation using SRW");
        }
    }
}
