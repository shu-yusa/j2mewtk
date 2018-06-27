import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class ThreadTest extends MIDlet {
  ThreadTestCanvas c;
  public ThreadTest() {
    c = new ThreadTestCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
  if (c != null) c.endProc();
  }
}

class ThreadTestCanvas extends GameCanvas implements Runnable {
  int r;
  ThreadTest parent;

  Thread gameLoop = null;

  ThreadTestCanvas(ThreadTest p) {
    super(false);
    r = 0;
    parent = p;
    try {
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

    while (gameLoop != null) {
      paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 10) gameLoop.sleep(10-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }

      r++;
      r %= 360;
    }

    parent.notifyDestroyed();
  }

  void paintFrame(Graphics g) {
    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0xffffff00);
    g.fillArc(20,20,200,200,0,r);

    flushGraphics();
  }

  public void endProc() {
    gameLoop = null;
  }
}
