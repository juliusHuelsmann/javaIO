package control.remote;

import control.Connections;


public abstract class ConnectionsRemote extends Connections {
  
  
  // Connection identifier (for sending e.g. ip)
  // Connection name
  
  
  private final String ip;
  
  
  private final int port;
  
  
  
  /**
   * Constructor: Store the {@link #connectionIdentifier}, {@link #ip} and
   * {@link #port}.
   * @param xconnectionIdentifier
   * @param xip
   * @param xport
   */
  public ConnectionsRemote(
      final String xconnectionIdentifier,
      final int xconnectionCount,
      final String xip,
      final int xport) {
    
    //
    // Store the information
    super(xconnectionIdentifier, xconnectionCount);
    this.ip = xip;
    this.port = xport;
  }
  
  
  
}
