

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import model.constants.Constants;
import model.versioncontrol.computer.ComputerLocal;
import view.View;
import xthread.XThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Singleton class serving information to clients that request them depending
 * on the incoming message.
 * 
 * @author Julius Huelsmann
 * @version %I%, %U%
 * @since 1.0
 */
public class FetchServer {


  /**
   * Private, empty utility class constructor.
   *    
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private FetchServer() {
    
  }
  
  
  /**
   * Contains the server's port.
   *    
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static int port = -1;
  
  
  /**
   * Returns the {@link #port}.
   * 
   * @return the {@link #port}
   */
  public static int getPort() {
    return port;
  }
    
  
  /**
   * Generates and returns new socket with port in between
   * {@link Constants.#globalPortStart} and {@link Constants.#globalPortEnd}
   * and returns it.
   * 
   * @return new Socket with port in range.
   *    
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static DatagramSocket generateNewPortSocket() {
    DatagramSocket socket = null;
    try {
      setPort(-1);
      while (!(getPort() > Constants.globalPortStart 
          && getPort() < Constants.globalPortEnd)) {
        
        if (socket != null) {
          socket.close();
          socket = null;
        }
        
        socket = new DatagramSocket(0);
        setPort(socket.getLocalPort());
        
       
        try {
          Thread.sleep(1);
        } catch (final InterruptedException ex_interrupt) {
          
          View.print("Server Thread interrupted while "
              + " searching new port between "
              + Constants.globalPortStart + " and "
              + Constants.globalPortEnd + ":" + ex_interrupt);
        }
      }
      return socket;
    } catch (final SocketException ex_socket) {

      View.print("Server socket could not be opened between "
          + Constants.globalPortStart + " and "
          + Constants.globalPortEnd + ":" + ex_socket);
      ex_socket.printStackTrace();
    }

    if (socket != null) {
      socket.close();
      socket = null;
    }
    return null;
  }

  
  /**
   * Print server information (port and IP) 
   * 
   * @param xsocket the socket.   
   * 
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static void printServerInformation(final DatagramSocket xsocket) {

    try {
      if (xsocket != null) {
        setPort(xsocket.getLocalPort());
        
        View.print("baby server port "  + xsocket.getLocalPort());
        View.print("baby server ip " + InetAddress.getLocalHost()
            .getHostAddress());  
      }
    } catch (UnknownHostException e1) {
      e1.printStackTrace();
    }
  }
  
  
  /**
   * Start scanning and handle incoming messages by calling the method
   * {@link #handleMessageIn(DatagramPacket, ComputerLocal, DatagramSocket)}.
   * 
   * @param xcl     instance of the local computer class that is used for
   *                handling incoming messages.
   *                
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  public static void startSending(final ComputerLocal xcl) {
      
    XThread trd = new XThread("FetchServer", true) {
      public void run() {


        //
        // Open DatagramSocket in port range.
        DatagramSocket socket = generateNewPortSocket();
        
        printServerInformation(socket);
        try {
          socket.setSoTimeout(1000);
        } catch (SocketException e1) {
          e1.printStackTrace();
        }
        while (!isInterrupted()) {
                 
                 
          try {
            byte[] inbuf = new byte[Constants.BUFFERSIZE];
            DatagramPacket in = new DatagramPacket(
                inbuf, inbuf.length);
            socket.receive(in);

            Staticio.handleMessageIn(in, xcl, socket);
            in = null;
            inbuf = null;
                     
          } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
              e.printStackTrace();
            }
          } 
        }
        View.print("close socket");
        socket.close();
      }

      @Override public void terminateThread() {
        interrupt();
      }
    };
    trd.start();
  }


  public static String getAddress() {
    try {
      return InetAddress.getLocalHost()
          .getHostAddress();
    } catch (UnknownHostException e) {

      e.printStackTrace();
      return "";
    }
  }


  /**
   * @param port the port to set
   */
  public static synchronized void setPort(int xport) {
    Staticio.unskipPort(FetchServer.port);
    Staticio.skipPort(xport);
    FetchServer.port = xport;
  }
    
  
    

}
