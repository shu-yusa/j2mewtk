import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class MenuSelect1 extends MIDlet {
  MenuSelect1Canvas c;
  public MenuSelect1() {
    c = new MenuSelect1Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class MenuSelect1Canvas extends GameCanvas implements Runnable {
  Image img;
  int selNo;
  int keyState;
  MenuSelect1 parent;

  Thread gameLoop = null;

  MenuSelect1Canvas(MenuSelect1 p) {
    super(true);
    selNo = 0;
    keyState = 0;
    parent = p;


    try {
      img = Image.createImage("/Menu.png");
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

    while(gameLoop != null) {
      paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 40) gameLoop.sleep(40 - tm);
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

  void renew() {
    int ks = getKeyStates();

    if (((ks & UP_PRESSED) != 0) &&
      ((keyState & UP_PRESSED) == 0)) {
         if (selNo > 0) selNo--;
    }
    if (((ks & DOWN_PRESSED) != 0) &&
        ((keyState & DOWN_PRESSED) == 0)) {
          if (selNo < 2) selNo++;
    }

    keyState = ks;
  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}









