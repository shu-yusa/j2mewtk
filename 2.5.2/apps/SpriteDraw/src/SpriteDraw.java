import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class SpriteDraw extends MIDlet {
  SpriteDrawCanvas c = null;
  public SpriteDraw() {
    c = new SpriteDrawCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  
  protected void startApp() {}
  protected void pauseApp() {}
  protected void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class SpriteDrawCanvas extends Canvas {
  Image[] img;
  Sprite sp;

  SpriteDrawCanvas(SpriteDraw mid) {
    String[] resName = {
      "/DQ2.jpg",
      "/Robyn2.png",
    };
    img = new Image[resName.length];
    try {
      for(int i=0; i<resName.length; i++) {
        img[i] = Image.createImage(resName[i]);
      }
      sp = new Sprite(img[1]);
      sp.setPosition(90,100);
    } catch(Exception e) {
      e.printStackTrace();
      mid.notifyDestroyed();
    }
  }

  public void paint(Graphics g) {
    g.drawImage(img[0],0,0,Graphics.LEFT|Graphics.TOP);
    sp.paint(g);
  }

  void endProc() {
    for(int i=0; i<img.length; i++) {
      img[i] = null;
      System.gc();
    }
  }
}
