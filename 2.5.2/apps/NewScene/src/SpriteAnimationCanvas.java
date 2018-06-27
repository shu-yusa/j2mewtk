import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class SpriteAnimationCanvas extends GameCanvas implements Runnable, CommandListener {
  Image img;
  Sprite sp;
  int px, py, frmcnt, frame;
  boolean flg;
  Command c1;
  NewScene parent;

  Thread gameLoop = null;

  SpriteAnimationCanvas(NewScene p) {
    super(false);
    px = py = 0;
    frmcnt = 0;
    flg = true;
    parent = p;

    try {
      img = Image.createImage("/slimeKnight.png");
      sp = new Sprite(img,24,32);
      sp.setFrame(2);
      c1 = new Command("EXIT", Command.EXIT,1);
      addCommand(c1);
      setCommandListener(this);
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
      frame = ++frame%2;
        sp.setFrame(frame+2);
    }
    if (flg) {
      px++;
    }
    else {
      px--;
    }
    sp.setPosition(px,py);
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      gameLoop = null;
    }
  }

  public void endProc() {
    gameLoop = null;
    img = null;
    System.gc();
  }
}

