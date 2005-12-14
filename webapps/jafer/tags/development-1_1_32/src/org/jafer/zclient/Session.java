package org.jafer.zclient;

import org.jafer.util.PDUDriver;
import org.jafer.exception.JaferException;
import java.util.Vector;
import org.jafer.zclient.operations.PresentException;
import org.w3c.dom.Node;
import org.jafer.util.ConnectionException;

public interface Session {
  public void close();

  public String getGroup();

  public int getId();

  public String getName();

  public PDUDriver getPDUDriver();

  public String getPassword();

  public String getUsername();

  public void init(String group, String username, String password) throws
      ConnectionException;

  public Vector present(int nRecord, int nRecords, int[] recordOID,
                        String eSpec, String resultSetName) throws
      PresentException, ConnectionException;

  public Vector scan(String[] databases, int nTerms, int step, int position,
                     Node term) throws JaferException, ConnectionException;

  public Vector scan(String[] databases, int nTerms, int step, int position,
                     Object termObject) throws JaferException,
      ConnectionException;

  public int[] search(Object queryObject, String[] databases,
                      String resultSetName) throws JaferException,
      ConnectionException;

  public void setPDUDriver(PDUDriver pduDriver);
}
