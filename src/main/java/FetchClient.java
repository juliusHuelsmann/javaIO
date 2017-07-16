

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

import log.LoggerRegistry;
import model.constants.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

import xthread.XThread;


/**
 * Class for fetching connection to a server. The function
 * {@link #startScanning(ComputerLocal)} may be called once for generating
 * the {@link #amountOfThreads} Threads that are scanning inside specified 
 * between
 * {@value model.constants.Constants.#globalPortStart} and 
 * {@value model.constants.Constants.#globalPortEnd}.
 * 
 * 
 * @author Julius Huelsmann
 * @version %I%; %U%
 * @since 1.0
 */
public class FetchClient {
  
  /**
   * Contains the number of client Threads that is created by the function
   * {@link #startScanning(ComputerLocal)}. Bests divides the distance between
   * {@link model.constants.Constants.#globalPortStart} and 
   * {@link model.constants.Constants.#globalPortEnd}.
   * 
   * <p>‚
   * Prime factors of the current configuration:
   * 2*2*2*2*3*5*5*5*5.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  public static final int amountOfThreads = 125;

  /**
   * Contains the current port that is processed by the thread specified by 
   * the index.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static Vector<Integer> currentProgress;
  
  
  /**
   * The time each Thread that searches Servers sleeps after one port has been
   * searched.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static int sleepTime = 250;
  
  
  /**
   * The time interval inside which the information on the progress of the 
   * scan-processes are updated by the first scan thread.
   * 
   * @see setProgressInformation
   * @see getNewClient
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static int informationUpdateInterval = 15;
  
  
  
  /**
   * Private constructor that does nothing but to be private.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private FetchClient() { }

  
    
  /**
   * Generate new instance of the extension of the Thread class that sends
   * a demand of information to each server that can be found. Afterwards
   * wait for an answer.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static void getNewClient(
      final int xid,
      final int xportStart,
      final int xportEnd,
      final ComputerLocal xcl) {

    new XThread("Fetch Client " + xid + " [" 
        + xportStart + "," + xportEnd 
        + ")", true) {
      public void run() {
          
        while (!isInterrupted()) {
              
          final double diff = xportEnd - xportStart;
          for (int cport = xportStart; 
              !isInterrupted() && cport < xportEnd; cport++) {

            
            if (Staticio.isScipped(cport)) {
              continue;
            }
            
            // Update the vector which contains information on the progress.
            //
            // If the current thread's identifier is equal to 0 and it's time
            // for visual update, the information are printed to the command 
            // line.
            setPercentage((cport - xportStart) / diff);
            currentProgress.set(xid, new Integer(cport));
            if (xid == 0 && cport % informationUpdateInterval == 0) {
              setProgressInformation();
            }
            try {
              Thread.sleep(sleepTime);
            } catch (InterruptedException e1) {
              interrupt();
            }
            /*
             * generate string that contains a valid question and initialize 
             * DatagramPacket that is sent afterwards.
             */
//            final DatagramPacket receivedData = 
            requestServerInformation(
                Utils.getBroadcastAddressLocalhost(), cport, xcl);
//            if (receivedData != null) {
//              Staticio.handleMessageIn(receivedData, xcl, null);
//            }
          }
        }
      }

      @Override
      public void terminateThread() {
        interrupt();
      }
    } .start();
  }
  
  
  
  
  
  /**
   * Sends the String {@value #SERVER_INFO_REQUEST} contained by the static
   * and final String {@link #SERVER_INFO_REQUEST} to server with specified 
   * InetAdress and port. 
   * 
   * @param xadr    the server's InetAddress,
   * @param xport   the server's port.
   * @return        the DatagramPacket that consists of the server's response.
   *                In case the operation failed, the return value is null.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  public static final DatagramPacket requestServerInformation(
      final InetAddress xadr, final int xport, final ComputerLocal xcl) {
    
    // 
    // Threshold: if the instance of InetAddress is null, return null
    if (xadr == null) {
      return null;
    }
    
    
   
    
    DatagramSocket socket = null;
    try {
      
      // Send QUESTION:
      // Open socket, send datagramPackage and close the socket.
      socket = new DatagramSocket(xport);
      socket.setSoTimeout(Constants.socketTimeout);
      // Generate DatagramPacket and DatagramSocket for the transmission.
      // Generate information string and prepare DatagramPacket that will be
      // sent during the next step.
      final String infoString = Staticio.SERVER_INFO_REQUEST 
          + xcl.generateInformationString();
      final String requestStatus = 
      Staticio.sendStringServerinfoRequest(xadr, xport, xcl, socket, infoString);
      socket.close();
      socket = null; //
//      View.print(requestStatus);
      
      socket = new DatagramSocket(xport);
      socket.setSoTimeout(Constants.socketTimeout); 

      byte[] buf1 = new byte[Constants.BUFFERSIZE];
      DatagramPacket receivedData = new DatagramPacket(buf1, buf1.length);
      socket.receive(receivedData);
      
//      final String messageIn = 
      new String(receivedData.getData()).substring(
            0, receivedData.getLength());
      
      //
      // If the message's origin is not the current computer.
      if (!receivedData.getAddress().getHostAddress().equals(
          FetchServer.getAddress())) {

        View.networkout(FetchClient.class.getSimpleName(),
            receivedData.getAddress().getHostAddress(), 
            xport, infoString, true);
        Staticio.handleMessageIn(receivedData, xcl, socket); 
      }

      socket.close();
      socket = null;

      buf1 = null;
      return receivedData;
    } catch (IOException e) {
      try {
                    
        if (socket != null) {

          socket.close();
          socket = null;
        }
      } catch (Exception i) {
        return null;
      }
      return null;
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  /**
   * Prints information on the current process of each thread that is scanning.
   * Is called by the first scanner thread every 
   * {@link #informationUpdateInterval} ports scanned.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  private static void setProgressInformation() {
    
    
    //
    // If the monitoring of the Thread's progress is enabled generate 
    // Information string that is forwarded to the standard output.
    if (ViewTerminal.progessInformation) {
      
      
      final int spacePerThread = 9;
      final int outputLineLength = 90;
      String strgGraphix = "Scanning ports from "
          + Constants.globalPortStart + " to " + Constants.globalPortEnd 
          + " in " + amountOfThreads + " Threads:\n";
      int graphixLineLength = 0;
      int textLineLength = 0;
      String strgText = "";
      final int d = (Constants.globalPortEnd - Constants.globalPortStart) 
          / amountOfThreads;
      int sum = Constants.globalPortStart;
      for (int i = 0; i < currentProgress.size(); i++) {
        int charPos = spacePerThread * (currentProgress.get(i).intValue() 
            - sum) / d;
        String strgGraphixnew = "|";
        for (int j = 0; j < Math.min(charPos, spacePerThread); j++) {
          strgGraphixnew += " ";
        }
        strgGraphixnew += "o";
        for (int j = charPos + 1; j < spacePerThread; j++) {
          strgGraphixnew += " ";
        }
        graphixLineLength += spacePerThread;
        if (graphixLineLength > outputLineLength) {
          graphixLineLength = spacePerThread;
          strgGraphixnew = "\n" + strgGraphixnew;
        }
        sum = sum + d;
        strgGraphix += strgGraphixnew;
          
        String number = ("" + i);
        while (number.length() < 3) {
          number = " " + number;
        }

        String port = ("" + currentProgress.get(i).intValue());
        while (port.length() < 5) {
          port = " " + port;
        }
          
        final String newStuff = number + " " + port + " | ";
        final int newLength = newStuff.length();
        textLineLength += newLength;
        if (textLineLength > outputLineLength) {
          strgText += "\n";
          textLineLength = newLength;
        }
        strgText += newStuff;
          
      } 
      final String ansiCls = "\u001b[2J";
      final String ansiHome = "\u001b[H";
      System.out.print(ansiCls + ansiHome);
      System.out.flush();
      System.out.println(strgGraphix + "\n"
          + ViewTerminal.getLatestInfos()
          + strgText);
    }
  }
    /**
   * This function is called once for starting to scan for other copies of the
   * program inside the local network. It generates {@value #amountOfThreads} 
   * threads by calling the private method 
   * {@link #getNewClient(int, int, int, ComputerLocal)}.
   * 
   * @param xcl   instance of the Local Computer class that handles
   *              the received information - strings.
   *     
   * @author Julius Huelsmann
   * @version %I%, %U%
   * @since 1.0
   */
  public static void startScanning() {
      
    
    // 
    // Initialize the Vector which contains information on the current process
    // of each XTreaead that is created in the following.
    currentProgress = new Vector<Integer>();
      
    
    
    //
    // Print information message and compute the range of one thread. 
    // Afterwards initialize #amountOfThreads threads by calling the method
    // #getNewClient.
    LoggerRegistry.log("Start scanning for clients in " + amountOfThreads 
        + " Threads.");
    final int threadRange = (Constants.globalPortEnd 
        - Constants.globalPortStart) / amountOfThreads;
    int sum = Constants.globalPortStart;
    for (int i = 0; i < amountOfThreads; i++) {
      
      // Add the lower range of scanning of the client to the progress vector
      // and generate upper range by adding the threadRange.
      currentProgress.add(sum);
      int sump = sum + threadRange;
      
      // Create new client and set the sum - integer to the upper range of the
      // current process.
      getNewClient(i, sum, sump, xcl);
      sum = sump;
    }
  }
}