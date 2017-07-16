package control;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public abstract class Connections {
  
  
  /**
   * Each time a new connection is found, it is inserted into the hashmap.
   * In case data arrives that is 
   * 
   * The identifier string is fetched from Connection class using 
   * getIdentifier method.
   */
  private HashMap<String, Vector<Data>> hmData;
  
  
  public Connections() {
    this.hmData = new HashMap<String, Vector<Data>>();
  }
  /*
   * Will be implemented by ConnectionsLocal / ConnectionsRemote
   */
  
  /**
   * Start scanning for connections.
   */
  public abstract void scanForConnections();
  
  
  /**
   * Stop scanning for connections.
   */
  public abstract void stopScanning();
  

  /**
   * Try to send message to specified target.
   * In case the target could not be reached, return false.
   * 
   * @param pack
   * @param xTarget
   * @return
   */
  public abstract boolean sendMessage(final Data pack, 
      final String xTargetIdent);
  
  
  /*
   * Will be implemented by the project that uses this project.
   */
  
  
  public abstract void receiveMessage(final Data pack);


  /**
   */
  protected Vector<Data> getDataOf(final String xSrc) {
    return hmData.get(xSrc);
  }

  /**
   */
  protected void putDataOf(final String xSrc, final Data xdata) {
    if (hmData.containsKey(xSrc)) {
      getDataOf(xSrc).add(xdata);
    } else {
      Vector<Data> vec2add = new Vector<Data>();
      vec2add.add(xdata);
      hmData.put(xSrc, vec2add);
    }
  }
  
  protected void appendDataOf(final String xSrc) {
    if (!hmData.containsKey(xSrc)) {
      hmData.put(xSrc, new Vector<Data>());
    }
  }
  
  protected void printData() {
    System.out.println("print data");
    for (String s : hmData.keySet()) {
      System.out.println(s);
    }
  }
  protected Set<String> getDataSet() {
    return hmData.keySet();
  }
  
}
