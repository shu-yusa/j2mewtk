import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class ScoreDraw2 extends MIDlet {
  ScoreDraw2Canvas c;
  public ScoreDraw2() {
    c = new ScoreDraw2Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class ScoreDraw2Canvas extends GameCanvas implements Runnable {
  Image img;
  ScoreDraw2 parent;
  int score;
  Thread gameLoop = null;

  public ScoreDraw2Canvas(ScoreDraw2 p) {
    super(false);
    score = 0;
    parent = p;

    try {
      img = Image.createImage("/Number.png");

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
    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());

    int[] s = new int[4];
    s[0] = score/1000;
    s[1] = (score/100)%10;
    s[2] = (score/10)%10;
    s[3] = score%10;

    for (int i=0; i<s.length; i++) {
      g.drawRegion(img,s[i]*16,0,16,32,Sprite.TRANS_NONE,
                   100+i*16,100,Graphics.LEFT|Graphics.TOP);
    }
    flushGraphics();
  }

  public void renew() {
    int ks = getKeyStates();
    
    if (((ks & UP_PRESSED) != 0) && score<10000) score++;
    if (((ks & DOWN_PRESSED) != 0) && score>0) score--;

  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}
