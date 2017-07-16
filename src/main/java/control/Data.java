package control;

import java.io.Serializable;

public abstract class Data implements Serializable {
  
  private final boolean internal;
  public Data(final boolean xinternal) {
    this.internal = xinternal;
  }
  /**
   * Return whether the data is transferred for internal purpose 
   * (for indicating commit messages for instance)
   * 
   * @return the internal
   */
  public boolean isInternal() {
    return internal;
  }
}
