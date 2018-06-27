import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class SpriteEx extends MIDlet {
  public SpriteEx() {
    SpriteCanvas c = new SpriteCanvas();
    Display.getDisplay(this).setCurrent(c);
    Thread thread = new Thread(c);
    thread.start();
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flag) {}
}
