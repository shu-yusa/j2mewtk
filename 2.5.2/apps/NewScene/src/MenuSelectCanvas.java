import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class MenuSelectCanvas extends GameCanvas implements Runnable {
  private Image img;
  private int selNo;
  private int keyState;
  private NewScene parent;

  private Thread gameLoop = null;

  MenuSelectCanvas(NewScene p) {
    super(true);
    parent = p;
    selNo = 0;
    keyState = 0;
    try {
      img = Image.createImage("/Menu.png");
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
  }

  public void startTh() {
    gameLoop = new Thread(this);
    gameLoop.start();

    Display.getDisplay(parent).setCurrent(this);
  }

  public void run() {
    Graphics g = getGraphics();
    int next = 0;
    long tm = System.currentTimeMillis();

    while (gameLoop != null) {
      paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 40) gameLoop.sleep(40-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }

      next = renew();
    }
  
    if (next == 1) {
      parent.showGame();
    }
    else {
      parent.notifyDestroyed();
    }
  }

  public void paintFrame(Graphics g) {
    g.setColor(0,0,0);
    g.fillRect(0,0,getWidth(),getHeight());

    for (int i=0; i<3; i++) {
      if (i == selNo) {
        g.drawRegion(img,
            60,i*20,60,20,Sprite.TRANS_NONE,
            90,i*30+90,Graphics.LEFT|Graphics.TOP);
      }
      else {
        g.drawRegion(img,
            0,i*20,60,20,Sprite.TRANS_NONE,
            90,i*30+90,Graphics.LEFT|Graphics.TOP);
      }
    }
    flushGraphics();
  }

  int renew() {
    int rtn = 0;
    int ks = getKeyStates();

    if (((ks & UP_PRESSED) != 0) &&
      ((keyState & UP_PRESSED) == 0)) {
         if (selNo > 0) selNo--;
    }
    if (((ks & DOWN_PRESSED) != 0) &&
        ((keyState & DOWN_PRESSED) == 0)) {
          if (selNo < 2) selNo++;
    }

    if (((ks & FIRE_PRESSED) != 0) &&
      ((keyState & FIRE_PRESSED) == 0)) {
      gameLoop = null;
      rtn = selNo + 1;
    }
    keyState = ks;
    return rtn;
  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}

