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

package org.jafer.zserver.authenticate;

import org.jafer.util.Config;
import org.jafer.interfaces.*;

import java.io.*;
import java.util.*;

/**
 * <p>Provides basic authentication via org.jafer.zserver.operations.Init operation within a session.
 * Based on group and/or user (+password) and optionally userIP address with IPMask.
 * User authentication details can be set via UserCredentials class and specified in server.xml - if authentication fails, session is terminated</p>
 * @author Antony Corfield; Matthew Dovey; Colin Tatham
 * @version 1.0
 */
public class BasicAuthenticate extends Authenticate {

  private static final String GROUP_DELIMITER = "//?//";
  private UserCredentials[] users;
  private Hashtable userLookup;
  private boolean anonymous = false;

  public boolean authenticate(String username, String groupname, String password, String clientIp) {

    if (isAnonymous())
      return true;

    try {
      UserCredentials userCreds;
      if (userLookup.containsKey(null + GROUP_DELIMITER + null))
        // ignore group/user/password
        userCreds = (UserCredentials)userLookup.get(null + GROUP_DELIMITER + null);
      else {
        // authenticate on group/user/password
        if (userLookup.containsKey(groupname + GROUP_DELIMITER + null))
          userCreds = (UserCredentials)userLookup.get(groupname + GROUP_DELIMITER + null);
        else if (userLookup.containsKey(null + GROUP_DELIMITER + username))
          userCreds = (UserCredentials)userLookup.get(null + GROUP_DELIMITER + username);
        else if (userLookup.containsKey(groupname + GROUP_DELIMITER + username))
          userCreds = (UserCredentials)userLookup.get(groupname + GROUP_DELIMITER + username);
        else
          return false;

        if (!(userCreds.getPassword() == null) &&
            !(userCreds.getPassword().equals(password)))
          return false;
      }

      if (userCreds.getIpAddress() != null && userCreds.getIpAddressMask() != null) {
        // authenticate on ip - bit-wise logical AND using clientIp and ipAddressMask
        int[] ipAddressMask = Config.convertSyntax(userCreds.getIpAddressMask());
        /** @todo check this */
        int[] ipAddress = Config.convertSyntax(userCreds.getIpAddress());
        int[] ip = Config.convertSyntax(clientIp);
        for (int i = 0; i < 4; i++) {
          if ((ip[i] &= ipAddressMask[i]) != ipAddress[i])
            return false;
        }
      }

    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  public void setUsers(UserCredentials[] users) {

    this.users = users;
    userLookup = new Hashtable();

    for (int n=0; n < this.users.length; n++) {
      userLookup.put(this.users[n].getGroup() + GROUP_DELIMITER + this.users[n].getUsername(), this.users[n]);
    }
  }

  public UserCredentials[] getUsers() {
    return users;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  public boolean isAnonymous() {
    return anonymous;
  }
}
//      int[] ipAddress = Config.convertSyntax(clientIp);// this works as check of clientIP against mask
