package org.jafer.zclient;

import java.rmi.RemoteException;

import gov.loc.www.zing.srw.*;
import gov.loc.www.zing.srw.interfaces.SRWPort;
import org.apache.axis.encoding.Deserializer;
import javax.xml.namespace.QName;

public class SRUBinding implements SRWPort {

    private String url;

    public SRUBinding(String url) {
        this.url = url;
    }

    /**
     * scanOperation
     *
     * @param body ScanRequestType
     * @return ScanResponseType
     * @throws RemoteException
     * @todo Implement this gov.loc.www.zing.srw.interfaces.SRWPort method
     */
    public ScanResponseType scanOperation(ScanRequestType body) throws
            RemoteException {
        return null;
    }

    /**
     * searchRetrieveOperation
     *
     * @param body SearchRetrieveRequestType
     * @return SearchRetrieveResponseType
     * @throws RemoteException
     * @todo Implement this gov.loc.www.zing.srw.interfaces.SRWPort method
     */
    public SearchRetrieveResponseType searchRetrieveOperation(
            SearchRetrieveRequestType body) throws RemoteException {
        StringBuffer parameters = new StringBuffer();

        /**
         * Andy's next mission:
         * Build an SRU URL from the request parameters
         * Send to the endpoint URL
         * Deserialise returned XML into SearchRetrieveResponseType
         *
         */

        QName searchQName = new QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse");
        Deserializer ser = SearchRetrieveResponseType.getDeserializer(null, SearchRetrieveResponseType.class, searchQName);


        return null;
    }
}
