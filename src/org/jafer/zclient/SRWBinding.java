package org.jafer.zclient;

import gov.loc.www.zing.srw.interfaces.SRWPort;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.rmi.RemoteException;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub;
import org.apache.axis.AxisFault;
import java.net.MalformedURLException;

public class SRWBinding implements SRWPort {

    String url;

    public SRWBinding(String url) {
        this.url = url;
    }

    public SearchRetrieveResponseType searchRetrieveOperation(
            SearchRetrieveRequestType body) throws RemoteException {
        SRWPort srwBinding;
        try {
            srwBinding = new SRWSoapBindingStub(new java.net.URL(url), null);
        } catch (MalformedURLException ex) {
            throw new RemoteException("Malformed URL", ex);
        } catch (AxisFault ex) {
            throw new RemoteException("Axis Fault", ex);
        }
        return srwBinding.searchRetrieveOperation(body);
    }

    public ScanResponseType scanOperation(ScanRequestType body) throws
            RemoteException {
        SRWPort srwBinding;
        try {
            srwBinding = new SRWSoapBindingStub(new java.net.URL(url), null);
        } catch (MalformedURLException ex) {
            throw new RemoteException("Malformed URL", ex);
        } catch (AxisFault ex) {
            throw new RemoteException("Axis Fault", ex);
        }
        return srwBinding.scanOperation(body);
    }
}
