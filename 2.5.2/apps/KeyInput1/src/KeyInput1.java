import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class KeyInput1 extends MIDlet {
  KeyInput1Canvas c = null;
  public KeyInput1() {
    c = new KeyInput1Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class KeyInput1Canvas extends GameCanvas implements Runnable, CommandListener {
  Image img, img2;
  Sprite sp;
  int px, py, frame = 0, frameAdd=4;
  int frmcnt = 0;
  KeyInput1 parent;
  Command c;

  Thread gameLoop = null;

  KeyInput1Canvas(KeyInput1 p) {
    super(true);
    px = py = 88;
    parent = p;

    try {
      img = Image.createImage("/slimeKnight.png");
      img2= Image.createImage("/DQ2part1.png");
      sp = new Sprite(img, 24, 32);
      sp.setFrame(4);
      c = new Command("EXIT",Command.EXIT,1);
      addCommand(c);
      setCommandListener(this);

      gameLoop = new Thread(this);
      gameLoop.start();
    }
    catch (Exception e) {
      e.printStackTrace();
      p.notifyDestroyed();
    }
  }

  public void run() {
    Graphics g = getGraphics();
    long tm = System.currentTimeMillis();

    while(gameLoop != null) {
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
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());

//  g.drawImage(img, px, py, Graphics.LEFT|Graphics.TOP);
    g.drawImage(img2,0,0,Graphics.LEFT|Graphics.TOP);
    g.drawString("("+px+","+py+")",0,0,0);
    sp.paint(g);
    flushGraphics();
  }

  public void renew() {
    frmcnt++;
    int ks = getKeyStates();
    if (frmcnt%8 == 0) frame = ++frame%2;

    if ((ks & UP_PRESSED)    != 0) {
      frameAdd = 0;
      py -= 4;
    }
    if ((ks & DOWN_PRESSED)  != 0) {
      frameAdd = 4;
      py += 4;
    }
    if ((ks & RIGHT_PRESSED) != 0) {
      frameAdd = 2;
      px += 4;
    }
    if ((ks & LEFT_PRESSED)  != 0) {
      frameAdd = 6;
      px -= 4;
    }

    if (px <= 0) {
      px = 0;
    }
    if (px >= getWidth()-24) {
      px = getWidth()-24;
    }
    if (py <= 0) {
      py = 0;
    }
    if (py >= getHeight()-32) {
      py = getHeight() - 32;
    }

    sp.setFrame(frame+frameAdd);
    sp.setPosition(px,py);
  }

  public void commandAction(Command c, Displayable d) {
    if (c == this.c) gameLoop = null;
  }

  void endProc() {
    gameLoop = null;
    img = null;
    System.gc();
  }
}
