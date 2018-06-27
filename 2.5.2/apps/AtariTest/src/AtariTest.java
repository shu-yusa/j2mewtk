import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class AtariTest extends MIDlet {
  AtariTestCanvas c = null;
  public AtariTest() {
    c = new AtariTestCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class AtariTestCanvas extends GameCanvas implements Runnable {
  AtariTest parent;
  Image img;
  Sprite fighter;
  Sprite shoot;
  Sprite enemy;
  int keyState;

  Thread gameLoop = null;

  public AtariTestCanvas(AtariTest p) {
    super(true);
    parent = p;
    keyState = 0;
    try {
      img = Image.createImage("/Shooting.png");

      shoot = new Sprite(img,5,10);
      shoot.setFrame(6);
      shoot.setPosition(0,-40);

      shoot.defineCollisionRectangle(1,2,3,3);

      enemy = new Sprite(img,30,30);
      enemy.setFrame(2);
      enemy.setPosition(240,10);

      enemy.defineCollisionRectangle(0,14,20,14);

      fighter = new Sprite(img,30,30);
      fighter.setFrame(0);
      fighter.setPosition(105,200);

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
    g.setClip(0,0,240,240);
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
    g.setColor(0,0,0);
    g.fillRect(0,0,getWidth(),getHeight());
    fighter.paint(g);
    shoot.paint(g);
    enemy.paint(g);
    flushGraphics();
  }

  public void renew() {
    int x, y;
    int ks = getKeyStates();

    if (((ks & FIRE_PRESSED) != 0) && ((keyState & FIRE_PRESSED) == 0)) {
      if (shoot.getY() <= -40) {
        // 機体の中心に配置
        x = fighter.getX() + 13;
        y = fighter.getY();
        shoot.setPosition(x,y);
      }
    }

    if (shoot.getY() > -40) {
      x = shoot.getX();
      y = shoot.getY() - 8;
      shoot.setPosition(x,y);
    }

    if (enemy.getX() > -40) {
      x = enemy.getX() - 2;
      y = enemy.getY();
      enemy.setPosition(x,y);
    }
    else {
      enemy.setPosition(240,10);
    }

    if (shoot.collidesWith(enemy,false)) {
      enemy.setPosition(240,10);
      shoot.setPosition(0,-40);
    }
    keyState = ks;
  }
  
  void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}
