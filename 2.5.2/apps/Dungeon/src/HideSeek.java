import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class HideSeek extends MIDlet {
  private static HideCanvas hc;

  public HideSeek() {
    hc = new HideCanvas(this);

    showGame();
  }

  static HideCanvas getCanvas() {
    return hc;
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (hc != null) hc.endProc();
  }

  void showGame() {
    hc.startTh();
  }
}
