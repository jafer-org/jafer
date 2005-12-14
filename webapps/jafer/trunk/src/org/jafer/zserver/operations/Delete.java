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
 *
 *
 */

/**
 *  Title: JAFER Toolkit
 *  Description:
 *  Copyright: Copyright (c) 2002
 *
 *@author     Antony Corfield; Matthew Dovey; Colin Tatham
 *@version    1.0
 */



package org.jafer.zserver.operations;

import org.jafer.transport.ConnectionException;

import org.jafer.transport.PDUDriver;
import org.jafer.zserver.*;

import java.net.*;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import z3950.v3.*;
import asn1.*;
import z3950.RS_SUTRS.*;
import org.jafer.exception.*;

public class Delete extends Operation {

  private PDU pduRequest;
  private PDU pduResponse;
  private boolean status = true;

  public Delete(Session session, PDU pduRequest) {

    super(session, "delete");
    this.pduRequest = pduRequest;
    this.pduResponse = new PDU();
  }

  public PDU runOp() throws Exception {

    pduResponse.c_deleteResultSetResponse = new DeleteResultSetResponse();
    pduResponse.c_deleteResultSetResponse.s_referenceId = pduRequest.c_deleteResultSetRequest.s_referenceId;


    if (pduRequest.c_deleteResultSetRequest.s_deleteFunction == new ASN1Integer(pduRequest.c_deleteResultSetRequest.E_all)) {
      try {
        getSession().removeAllDatabeans();
      } catch (JaferException ex) {
        logger.log(Level.WARNING, getName() + ex.toString());
        status = false;
      }
    } else {
      pduResponse.c_deleteResultSetResponse.s_deleteListStatuses = new ListStatuses();
      pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value = new ListStatuses1[pduRequest.c_deleteResultSetRequest.s_resultSetList.length];
      for (int n = 0; n < pduRequest.c_deleteResultSetRequest.s_resultSetList.length; n++) {
        pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n] = new ListStatuses1();
        pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_id = new ResultSetId();
        pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_id.value = pduRequest.c_deleteResultSetRequest.s_resultSetList[n].value;

        String resultSetName = pduRequest.c_deleteResultSetRequest.s_resultSetList[n].value.value.get();
        try {
          if (getSession().containsDatabean(resultSetName)) {
            getSession().removeDatabean(resultSetName);
            pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status = new DeleteSetStatus();
            pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.value = new ASN1Integer(pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.E_success);
          } else {
            status = false;
            pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.value = new ASN1Integer(pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.E_resultSetDidNotExist);
          }
        } catch (JaferException ex) {
          logger.log(Level.WARNING, getName() + ex.toString());
          status = false;
          pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.value = new ASN1Integer(pduResponse.c_deleteResultSetResponse.s_deleteListStatuses.value[n].s_status.E_resultSetInUse);
        }
      }
    }

    pduResponse.c_deleteResultSetResponse.s_deleteOperationStatus = new DeleteSetStatus();
    if (status)
      pduResponse.c_deleteResultSetResponse.s_deleteOperationStatus.value = new ASN1Integer(pduResponse.c_deleteResultSetResponse.s_deleteOperationStatus.E_notAllRequestedResultSetsDeleted);
    else
      pduResponse.c_deleteResultSetResponse.s_deleteOperationStatus.value = new ASN1Integer(pduResponse.c_deleteResultSetResponse.s_deleteOperationStatus.E_success);

    return pduResponse;
  }
}