package control.local;

import java.io.Serializable;

import control.Data;


/**
 * Local data is sent in instances of <code>PackageLocal.</code>
 * @author juli
 *
 */
public class DataLocal extends Data {
  
  /**
   * 
   */
  private static final long serialVersionUID = -8428382517401009413L;


  private String idSender;

  
  private Object content;
  
  
  public DataLocal(final String xidSender, final Object xcontent,
      final boolean xinternal) {
    super(xinternal);
    this.idSender = xidSender;
    this.content = xcontent;
  }


  /**
   * @return the content
   */
  public Object getContent() {
    return content;
  }


  /**
   * @param content the content to set
   */
  public void setContent(Object content) {
    this.content = content;
  }


  /**
   * @return the idSender
   */
  public String getIdSender() {
    return idSender;
  }


  /**
   * @param idSender the idSender to set
   */
  public void setIdSender(String idSender) {
    this.idSender = idSender;
  }
  
}
