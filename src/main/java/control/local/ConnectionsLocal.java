package control.local;

import java.util.Vector;

import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import control.Connections;
import control.Data;

public abstract class ConnectionsLocal extends Connections {
  
  
  
  
  
  private int connectionCount;
  

  private JChannel channel;
  private AddReceiverAdapter adr;
  
  public ConnectionsLocal() {
    scanForConnections();
    
  }
  @Override
  public void scanForConnections() {


    try {
      System.setProperty("java.net.preferIPv4Stack", "true");
      channel = new JChannel("udp.xml");
      channel.connect("clientPool");
      adr = new AddReceiverAdapter();
      channel.setReceiver(adr);
      adr.memberUpdate();
      stopScanning(); 
      
    } catch (ChannelException e) {
      e.printStackTrace();
    }
  }


  @Override
  public void stopScanning() {
    channel.disconnect();
    channel.close();
    try {
      Thread.sleep(50000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public boolean sendMessage(Data pack, String xTarget) {
    // TODO Auto-generated method stub
    return false;
  }

  public static void main(String[] args) {
    new ConnectionsLocal(){

      @Override
      public void receiveMessage(Data pack) {
        System.out.println(pack);
      }
      
    };
  }
  
  
  private class AddReceiverAdapter extends ReceiverAdapter {
    
    public AddReceiverAdapter() { }
    @Override
    public void viewAccepted(View newView) {
      memberUpdate();
      
    }

    @Override
    public void receive(Message msg) { }

    public void memberUpdate() {
      for (Address m: channel.getView().getMembers()) {
        
        appendDataOf(m.toString());
      }
      // rm old members
      
//      for (String s : getDataSet()) {
//        if (!channel.getView().containsMember()) {
//          remo
//        }
//      }
      printData();
    }
  }
}


