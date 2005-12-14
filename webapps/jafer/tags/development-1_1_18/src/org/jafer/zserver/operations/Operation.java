/**
 * JAFER Toolkit Poject.
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

package org.jafer.zserver.operations;

import org.jafer.util.PDUDriver;
import org.jafer.zserver.ZServerThread;
import org.jafer.zserver.Session;
import org.jafer.conf.Config;
import org.jafer.util.ConnectionException;
import org.jafer.zserver.operations.OperationException;

import z3950.v3.PDU;
import z3950.v3.DiagRec;
import z3950.v3.NamePlusRecord_record;
import z3950.v3.DefaultDiagFormat;
import z3950.v3.DefaultDiagFormat_addinfo;
import z3950.v3.InternationalString;
import asn1.ASN1GeneralString;
import asn1.ASN1VisibleString;
import asn1.ASN1ObjectIdentifier;
import asn1.ASN1Integer;

import java.util.logging.Level;

/**
 * <p>Each operation runs in it's own thread and either terminates naturally by sending response PDU (or Diagnostic) or can be stopped by session which forces close of socket.
 * Operations must implement abstract runOp() method. Also includes methods for building diagnostics and handling exceptions.</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public abstract class Operation extends ZServerThread {

  public static final String BIB1_DIAGNOSTIC_OID = "1.2.840.10003.4.1";
  private Session session;
  private PDUDriver pduDriver;

  public Operation(Session session, String name) {

    super(name);
    this.session = session;
    this.pduDriver = session.getPDUDriver();
  }

  public final void start() {
    logger.log(Level.FINE, getName() + " starting...");
    setStopping(false);
    setStopped(false);
    setStartTime(System.currentTimeMillis()/1000);
    run();
  }

  public abstract PDU runOp() throws Exception;

  public final void run() {
/** @todo need to send an error PDU to prevent client hanging? */
    try {
      sendPDU(runOp());
    } catch (OperationException oe) {
      try {
        logger.log(Level.WARNING, getName() + " " + oe.toString());
        if (oe.hasPDUDiagnostic())
          sendPDU(oe.getPDUDiagnostic());
      } catch (ConnectionException ce) {
        logger.log(Level.WARNING, getName() + " " + ce.toString());
      }
    } catch (Exception ex) {
      logger.log(Level.SEVERE, getName() + " " + ex.toString());
      ex.printStackTrace();
    } finally {
      close();
    }
  }

  public final void close() {
    logger.log(Level.FINE, getName() + " stopping...");
    setStopping(true);
    setStopped(true);
  }

  public Session getSession() {
    return session;
  }

  private void sendPDU(PDU pduResponse) throws ConnectionException {
    pduDriver.sendPDU(pduResponse);
  }

  protected DefaultDiagFormat getDiagnostic(DefaultDiagFormat defaultDiagFormat, int condition, String addInfo) {
    //c_nonSurrogateDiagnostic
    defaultDiagFormat.s_diagnosticSetId = new ASN1ObjectIdentifier(Config.convertSyntax(BIB1_DIAGNOSTIC_OID));
    defaultDiagFormat.s_condition = new ASN1Integer(condition);
    defaultDiagFormat.s_addinfo = new DefaultDiagFormat_addinfo();
    if (session.getClientVersion() < 3) {
      defaultDiagFormat.s_addinfo.c_v2Addinfo = new ASN1VisibleString(addInfo == null ? "" : addInfo);
    } else {
      defaultDiagFormat.s_addinfo.c_v3Addinfo = new InternationalString();
      defaultDiagFormat.s_addinfo.c_v3Addinfo.value = new ASN1GeneralString(addInfo == null ? "" : addInfo);
    }
    return defaultDiagFormat;
  }

  protected DiagRec getDiagnostic(DiagRec diagRec, int condition, String addInfo) {
    //c_multipleNonSurDiagnostics
    diagRec.c_defaultFormat = getDiagnostic(new DefaultDiagFormat(), condition, addInfo);
    return diagRec;
  }

  protected NamePlusRecord_record getDiagnostic(NamePlusRecord_record name_record, int condition, String addInfo) {
    //c_surrogateDiagnostic
    name_record.c_surrogateDiagnostic = getDiagnostic(new DiagRec(), condition, addInfo);
    return name_record;
  }
}