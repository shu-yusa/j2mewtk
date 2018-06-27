import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;
import java.util.Random;

public class RandomProc extends MIDlet {
  RandomProcCanvas c;
  public RandomProc() {
    c = new RandomProcCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class RandomProcCanvas extends GameCanvas implements Runnable {
  RandomProc parent;
  Thread gameLoop = null;
  Image img;
  Sprite[] sp;
  int[] vy;
  Random rand;

  public RandomProcCanvas(RandomProc p) {
    super(false);
    parent = p;
    rand = new Random();
    sp = new Sprite[4];
    vy = new int[sp.length];

    try {
      img = Image.createImage("/Random.png");
      for (int i=0; i<sp.length; i++) {
        sp[i] = new Sprite(img,30,30);
        sp[i].setFrame(i);
        sp[i].setPosition(0,300);
        vy[i] = 0;
      }
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
        if (tm < 40) gameLoop.sleep(40-tm);
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
    g.setColor(255,255,255);
    g.fillRect(0,0,getWidth(),getHeight());
    for (int i=0; i<sp.length; i++) {
      sp[i].paint(g);
    }
    flushGraphics();
  }

  public void renew() {
    int x, y;

    for (int i=0; i<sp.length; i++) {
      if (sp[i].getY() < 240) {
        x = sp[i].getX();
        y = sp[i].getY() + vy[i];
        sp[i].setPosition(x,y);
      }
    }

    for (int i=0; i<sp.length; i++) {
      if (sp[i].getY() >= 240) {
//      if (rand.nextInt(100) == 0) {
          x = rand.nextInt(210);
          y = -40;
          sp[i].setPosition(x,y);
          vy[i] = rand.nextInt(3)+3;
//      }
      }
    }
  }

  void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}
