import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class ImageDraw1 extends MIDlet {
  ImageDraw1Canvas c = null;

  public ImageDraw1() {
    c = new ImageDraw1Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class ImageDraw1Canvas extends Canvas {
  Image img;

  public ImageDraw1Canvas(ImageDraw1 p) {
    try {
      img = Image.createImage("/Robyn2.png");
    } catch (Exception e) {
      e.printStackTrace();

      p.notifyDestroyed();
    }
  }

  public void paint(Graphics g) {
    g.drawImage(img,50,50,Graphics.LEFT|Graphics.TOP);
  }

  void endProc() {
    img = null;
    System.gc();
  }
}
