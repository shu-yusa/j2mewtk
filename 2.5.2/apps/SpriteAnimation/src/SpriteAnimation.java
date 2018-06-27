import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class SpriteAnimation extends MIDlet {
  SpriteAnimationCanvas c = null;
  public SpriteAnimation() {
    c = new SpriteAnimationCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class SpriteAnimationCanvas extends GameCanvas implements Runnable {
  Image img;
  Sprite sp;
  int px, py, frmcnt, frame;
  boolean flg;
  SpriteAnimation parent;

  Thread gameLoop = null;

  SpriteAnimationCanvas(SpriteAnimation p) {
    super(false);
    px = py = 0;
    frmcnt = 0;
    flg = true;
    parent = p;
    try {
//    img = Image.createImage("/MuscatWolk.png");
      img = Image.createImage("/slimeKnight.png");
      sp = new Sprite(img,24,32);
      sp.setFrame(2);
      gameLoop = new Thread(this);
      gameLoop.start();
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
  }

  public void run() {
    Graphics g = getGraphics();
    long tm = System.currentTimeMillis();

    while (gameLoop != null) {
      paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 20) gameLoop.sleep(20-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }

      renew();
    }

    parent.notifyDestroyed();
  }

  public void paintFrame(Graphics g) {
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());

    sp.paint(g);

    flushGraphics();
  }

  public void renew() {
    frmcnt++;
    if (frmcnt%192 == 0) {
      flg = !flg;
      if (flg) {
        sp.setTransform(Sprite.TRANS_NONE);
      }
      else {
        sp.setTransform(Sprite.TRANS_MIRROR);
      }
      py += 32;
      if (py >= 192) {
        py = 0;
      }
    }
    if (frmcnt%4 == 0) {
//    sp.nextFrame();
      frame = ++frame%2;
//    if (flg) {
        sp.setFrame(frame+2);
//    }
//    else {
//      sp.setFrame(frame+4);
//    }
    }
    if (flg) {
      px++;
    }
    else {
      px--;
    }
    sp.setPosition(px,py);
  }

  public void endProc() {
    gameLoop = null;
    img = null;
    System.gc();
  }
}
