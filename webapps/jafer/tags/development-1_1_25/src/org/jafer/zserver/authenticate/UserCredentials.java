package org.jafer.zserver.authenticate;

import java.io.*;

public class UserCredentials implements Serializable {

  private String username;
  private String password;
  private String group;
  private String ipAddress;
  private String ipAddressMask = "255.255.255.255";

  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }
  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getUsername() {
    return username;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPassword() {
    return password;
  }
  public void setGroup(String group) {
    this.group = group;
  }
  public String getGroup() {
    return group;
  }
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
  public String getIpAddress() {
    return ipAddress;
  }
  public void setIpAddressMask(String ipAddressMask) {
    this.ipAddressMask = ipAddressMask;
  }
  public String getIpAddressMask() {
    return ipAddressMask;
  }
}