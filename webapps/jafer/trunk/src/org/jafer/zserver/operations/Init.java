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

import org.jafer.util.Config;
import org.jafer.zserver.Session;

import java.util.logging.Level;

import z3950.RS_SUTRS.*;
import z3950.v3.*;
import asn1.*;

/**
 * <p>Checks/sets authentication and concurrent operations if requested by client</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class Init extends Operation {

  private PDU pduRequest;
  private PDU pduResponse;
  private String clientInfo;
  private int clientVersion;

  public Init(Session session, PDU pduRequest) {

    super(session, "init");
    this.pduRequest = pduRequest;
    this.pduResponse = new PDU();
  }

  public PDU runOp() throws Exception {

    pduResponse.c_initResponse = new InitializeResponse();
    pduResponse.c_initResponse.s_exceptionalRecordSize = pduRequest.c_initRequest.s_exceptionalRecordSize;
    pduResponse.c_initResponse.s_preferredMessageSize = pduRequest.c_initRequest.s_preferredMessageSize;
    pduResponse.c_initResponse.s_implementationId = new InternationalString();
    pduResponse.c_initResponse.s_implementationId.value = new ASN1GeneralString(getSession().getName());
    pduResponse.c_initResponse.s_implementationName = new InternationalString();
    pduResponse.c_initResponse.s_implementationName.value = new ASN1GeneralString("JAFER ZServer");
    pduResponse.c_initResponse.s_implementationVersion = new InternationalString();
    pduResponse.c_initResponse.s_implementationVersion.value = new ASN1GeneralString("prototype");
    pduResponse.c_initResponse.s_referenceId = pduRequest.c_initRequest.s_referenceId;

    String userId = null, groupId = null, password = null;
    if(pduRequest.c_initRequest.s_idAuthentication != null) {
      if(pduRequest.c_initRequest.s_idAuthentication.c_anonymous == null &&
         pduRequest.c_initRequest.s_idAuthentication.c_idPass != null) {
        if (pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_userId != null)
          userId = pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_userId.value.get();
        if (pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_groupId != null)
          groupId = pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_groupId.value.get();
        if (pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_password != null)
          password = pduRequest.c_initRequest.s_idAuthentication.c_idPass.s_password.value.get();
      }
    }
    getSession().setAuthenticated(userId, groupId, password);
    pduResponse.c_initResponse.s_result = new ASN1Boolean(getSession().getAuthenticated());

    clientVersion = getClientVersion();
    clientInfo = getClientInfo();
    getSession().setClientVersion(clientVersion);
    getSession().setClientInfo(clientInfo);
    getSession().setPreferredMessageSize(pduRequest.c_initRequest.s_preferredMessageSize.get());
    getSession().setExceptionalRecordSize(pduRequest.c_initRequest.s_exceptionalRecordSize.get());

    SutrsRecord sutrs_record =  new SutrsRecord();
    sutrs_record.value =  new InternationalString();
    if (getSession().getAuthenticated())
      sutrs_record.value.value = new ASN1GeneralString("Java ZServer copyright 2002 - JAFER Project. Components copyright Crossnet/DTSC");
    else
      sutrs_record.value.value = new ASN1GeneralString("Authentication failed, session terminated by target");
    pduResponse.c_initResponse.s_userInformationField = new ASN1External();
    pduResponse.c_initResponse.s_userInformationField.s_direct_reference = new ASN1ObjectIdentifier(
        Config.convertSyntax(Config.getRecordSyntaxFromName("SUTRS")));
    pduResponse.c_initResponse.s_userInformationField.c_singleASN1type = (ASN1Any)sutrs_record;

    boolean version[] = new boolean[3];
    version[0] = true;
    version[1] = true;
    version[2] = true;
    pduResponse.c_initResponse.s_protocolVersion = new ProtocolVersion();
    pduResponse.c_initResponse.s_protocolVersion.value = new ASN1BitString(version);

    boolean canSearch = false;
    boolean canPresent = false;
    boolean canScan = false;
    boolean canSort = false;

    Object bean = getSession().getDatabean();
    Class beanClass = bean.getClass();
    Class[] interfaces = beanClass.getInterfaces();

    for (int n = 0; n < interfaces.length; n++) {
      if (interfaces[n].getName().equals("org.jafer.interfaces.Search"))
        canSearch = true;
      else if (interfaces[n].getName().equals("org.jafer.interfaces.Present"))
        canPresent = true;
      else if (interfaces[n].getName().equals("org.jafer.interfaces.Scan"))
        canScan = true;
      else if (interfaces[n].getName().equals("org.jafer.interfaces.Sort"))
        canSort = true;
    }

    boolean options[] = new boolean[15];

    if (pduRequest.c_initRequest.s_options.value.get().length > 0)
      options[0] = canSearch & pduRequest.c_initRequest.s_options.value.get()[0];
    else
      options[0] = false;

    if (pduRequest.c_initRequest.s_options.value.get().length > 1)
      options[1] = canPresent & pduRequest.c_initRequest.s_options.value.get()[1];
    else
      options[1] = false;

    if (pduRequest.c_initRequest.s_options.value.get().length > 2)
      options[2] = pduRequest.c_initRequest.s_options.value.get()[2]; //delSet
    else
      options[2] = false;

    options[3] = false; //resourceReport
    options[4] = false; //triggerResourceControl
    options[5] = false; //resourceCtrl
    options[6] = false; //accessCtrl

    if (pduRequest.c_initRequest.s_options.value.get().length > 7)
      options[7] = canScan & pduRequest.c_initRequest.s_options.value.get()[7];
    else
      options[7] = false;

    if (pduRequest.c_initRequest.s_options.value.get().length > 8)
      options[8] = canSort  & pduRequest.c_initRequest.s_options.value.get()[8];
    else
      options[8] = false;

    options[9] = false; //reserved
    options[10] = false; //extendedServices
    options[11] = false; //level-1Segmentation
    options[12] = false; //level-2Segmentation

    if (pduRequest.c_initRequest.s_options.value.get().length > 13) {
      options[13] = pduRequest.c_initRequest.s_options.value.get()[13]; //concurrentOperations
      getSession().setConcurrent(pduRequest.c_initRequest.s_options.value.get()[13]);
    } else
      options[13] = false;

    if (pduRequest.c_initRequest.s_options.value.get().length > 14)
      options[13] = pduRequest.c_initRequest.s_options.value.get()[14]; //namedResultSets
    else
      options[14] = false;

    pduResponse.c_initResponse.s_options = new Options();
    pduResponse.c_initResponse.s_options.value = new ASN1BitString(options);

    return pduResponse;
  }

  private int getClientVersion() {

    int clientVersion = 0;
    if (pduRequest.c_initRequest.s_protocolVersion != null) {
      for (int n = 0; n < pduRequest.c_initRequest.s_protocolVersion.value.get().length; n++) {
        if (pduRequest.c_initRequest.s_protocolVersion.value.get()[n])
          clientVersion = n + 1;
      }
    }
    return clientVersion;
  }

  private String getClientInfo() {

    String clientInfo = "Z39.50 client";
    if (pduRequest.c_initRequest.s_implementationName != null) {
      clientInfo = pduRequest.c_initRequest.s_implementationName.toString();
      if (pduRequest.c_initRequest.s_implementationVersion != null)
        clientInfo += " - " + pduRequest.c_initRequest.s_implementationVersion.toString();
    }

    if (clientVersion > 0)
      clientInfo += " (Version " + clientVersion + ")";
    else
      clientInfo += " (Version unknown)";

    if (pduRequest.c_initRequest.s_userInformationField != null) {
      if (pduRequest.c_initRequest.s_userInformationField.c_singleASN1type != null)
        clientInfo += "\n" + pduRequest.c_initRequest.s_userInformationField.c_singleASN1type.toString();
    }

    if (pduRequest.c_initRequest.s_otherInfo != null)
      clientInfo += "\n" + pduRequest.c_initRequest.s_otherInfo.toString();
    clientInfo = clientInfo.replaceAll("\"", "");

    return clientInfo;
  }
}