import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class ImageDraw2 extends MIDlet {
  ImageDraw2Canvas c = null;
  public ImageDraw2() {
    c = new ImageDraw2Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }

  protected void startApp() {}
  protected void pauseApp() {}
  protected void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class ImageDraw2Canvas extends Canvas {
  Image img;

  ImageDraw2Canvas(ImageDraw2 p) {
    try {
      img = Image.createImage("/DQ2.jpg");
    } catch (Exception e) {
      e.printStackTrace();
      p.notifyDestroyed();
    }
  }

  public void paint(Graphics g) {
    g.drawImage(img,0,0,Graphics.LEFT|Graphics.TOP);
    g.drawRegion(img,128,96,16,16,Sprite.TRANS_ROT90,224,276,
                 Graphics.LEFT|Graphics.TOP);

    g.drawString(""+getWidth(),0,250,Graphics.LEFT|Graphics.TOP);
    g.drawString(""+getHeight(),0,270,Graphics.LEFT|Graphics.TOP);
  }

  void endProc() {
    img = null;
    System.gc();
  }
}

