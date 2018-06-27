import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class JumpAnimation extends MIDlet {
  JumpAnimationCanvas c;
  public JumpAnimation() {
    c = new JumpAnimationCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class JumpAnimationCanvas extends GameCanvas implements Runnable {
  JumpAnimation parent;
  Image[] img;
  Thread gameLoop = null;
  int grd, frmcnt;
  double px, py, vx, vy, ay;
  Sprite sp;
  TiledLayer tlay;

  public JumpAnimationCanvas(JumpAnimation p) {
    super(false);
    parent = p;
    grd = 180;
    px = 100;
    py = grd;
    vx = vy = ay = 0;
    img = new Image[2];

    try {
      img[0] = Image.createImage("/JumpHaikei.png");

      int[][] mdt = {
        {12,12,12,12, 1, 2, 3, 4},
        {12,12,12,12, 5, 6, 7, 8},
        {12,12,12,12,12,12,12,12},
        {12,12,12,12,12,12,12,12},
        {12,12,12,12,12,12,12,12},
        { 9,10,11,12,12,12,12,12},
        {13,14,15,12,12,12,12,12},
        {16,16,16,16,16,16,16,16},
      };

      tlay = new TiledLayer(8,8,img[0],30,30);
      for (int i=0; i<mdt.length; i++) {
        for (int j=0; j<mdt[i].length; j++) {
          tlay.setCell(j,i,mdt[i][j]);
        }
      }

      img[1] = Image.createImage("/Jump.png");
      sp = new Sprite(img[1],32,32);

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
    tlay.paint(g);
    if (vy == 0) {
      sp.setFrame((frmcnt/6)%2);
    }
    else if (vy < 2.0 && vy > -1.5) {
      sp.setFrame(4);
    }
    else if (vy < 0.0) {
      sp.setFrame(3);
    }
    else {
      sp.setFrame(5);
    }
    sp.setPosition((int)px,(int)py);
    sp.paint(g);

    flushGraphics();
    frmcnt++;
  }

  public void renew() {
    int ks = getKeyStates();

    if (vy == 0 && (ks & FIRE_PRESSED) != 0) {
      vy = -20.0;
      ay = 0.2;
    }

    vy += ay;
    py += vy;

    if (py > grd) {
      py = grd;
      vy = 0;
      ay = 0;
    }
  }

  public void endProc() {
    img[0] = null;
    img[1] = null;
    img = null;
    gameLoop = null;
    System.gc();
  }
}
