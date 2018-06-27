import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class SinCurve extends MIDlet {
  SinCurveCanvas c;
  public SinCurve() {
    c = new SinCurveCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class SinCurveCanvas extends GameCanvas implements Runnable {
  SinCurve parent;
  int ox, oy;
  int rx, ry;
  int px, py;
  int ang;
  boolean push1, push2, push3, push4, push5, push0;
  Thread gameLoop = null;

  public SinCurveCanvas(SinCurve p) {
    super(false);
    parent = p;
    rx = 20;
    ry = 20;
    ang = 0;
    ox = 120;
    oy = 120;
    px = ox + rx;
    py = oy + ry;
    push1 = false;
    push2 = false;
    push3 = false;
    push4 = false;
    push5 = false;

    try {
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

  void paintFrame(Graphics g) {
    if (!(push1 || push2 || push3)) {
      g.setColor(0x000000ff);
      g.fillRect(0,0,getWidth(),getHeight());
    }

    g.setColor(255,0,0);
    g.fillRect(px-2,py-2,4,4);

    flushGraphics();
  }

  void renew() {
    px = ox + (int)(Math.cos(Math.toRadians(ang))*(double)rx);
    py = oy - (int)(Math.sin(Math.toRadians(ang))*(double)ry);
    if (push2) {rx++;ry++;}
    if (push3) {if(rx>0)rx--;if(ry>0)ry--;}
    if (push4) {ox++;if (ox>240) ox=-4;}
    if (push5) {oy++;if (oy>240) oy=-4;}
    ang += 6;
    ang %= 360;
  }

  protected void keyPressed(int keyCode) {
    if (keyCode == KEY_NUM1) {push1 = true;}
    if (keyCode == KEY_NUM2) {push2 = true;}
    if (keyCode == KEY_NUM3) {push3 = true;}
    if (keyCode == KEY_NUM4) {
      if (push4) {
        push4 = false;
      }
      else {
        ox = -4;
        oy = 120;
        rx = 0;
        ry = 20;
        push4 = true;
        push5 = false;
      }
    }
    if (keyCode == KEY_NUM5) {
      if (push5) {
        push5 = false;
      }
      else {
        oy = -4;
        ox = 120;
        rx = 20;
        ry = 0;
        push4 = false;
        push5 = true;
      }
    }
    if (keyCode == KEY_NUM0) {
      ox = 120;
      oy = 120;
      rx = 20;
      ry = 20;
      push4 = false;
      push5 = false;
    }
  }

  protected void keyReleased(int keyCode) {
    if (keyCode == KEY_NUM1) {push1 = false;}
    if (keyCode == KEY_NUM2) {push2 = false;}
    if (keyCode == KEY_NUM3) {push3 = false;}
  }

  void endProc() {
    gameLoop = null;
    System.gc();
  }
}
