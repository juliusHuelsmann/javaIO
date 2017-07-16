package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.awt.*;
import java.awt.datatransfer.*;

import javax.swing.*;

import org.jgroups.*;


public class Test {
  
  private static JChannel c;
  public static void main(String[] args) throws Exception {
    
    
    System.setProperty("java.net.preferIPv4Stack", "true");
    c = new JChannel("udp.xml");
    c.connect("newClients");
    c.setReceiver(new ReceiverAdapter() {
      @Override
      public void viewAccepted(View newView) {
        System.out.println("Network Clipboard - " + c.getLocalAddress());
      }

      @Override
      public void receive(Message msg) {
        System.out.println("IN: \t" + msg.getObject());
      }
    });

    userInputHandler();
  }

  
  public static void send(final String xmsg) {

    try {
      Message msg = new Message();
      msg.setObject(xmsg);
      c.send(msg);
    } catch (ChannelNotConnectedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ChannelClosedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * User input handler (via Terminal).
   */
  public static void userInputHandler() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          System.in));
      
      boolean running = true;
      while (running) {
        final String line = reader.readLine();
//        String[] task = reader.readLine().split(" ");
        send(line);
        System.out.println("OUT\t " + line);
      }
      reader.close();
      
    } catch (IOException ex) {
    }
  }
  
}

