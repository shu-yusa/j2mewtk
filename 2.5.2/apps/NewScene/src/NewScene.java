import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class NewScene extends MIDlet {
  private MenuSelectCanvas msc = null;
  private SpriteAnimationCanvas sac = null;

  public NewScene() {
    msc = new MenuSelectCanvas(this);
    sac = new SpriteAnimationCanvas(this);

    showMenu();
  }

  protected void startApp() {}
  protected void pauseApp() {}
  protected void destroyApp(boolean flg) {
    if (msc != null) msc.endProc();
    if (sac != null) sac.endProc();
  }

  void showMenu() {
    msc.startTh();
  }

  void showGame() {
    sac.startTh();
  }
}

