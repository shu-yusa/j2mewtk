import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class TextScroll extends MIDlet {
  TextScrollCanvas c = null;
  public TextScroll() {
    c = new TextScrollCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class TextScrollCanvas extends GameCanvas implements Runnable {
  int y;
  TextScroll parent;
  Thread gameLoop = null;

  String str[] = {
    "黄昏よりも昏きもの",
    "血の流れよりも紅きもの",
    "時の流れに埋もれし",
    "偉大な汝の名において",
    "我ここに 闇に誓わん",
    "我等が前に立ち塞がりし",
    "すべての愚かなるものに",
    "我と汝が力もて",
    "等しく滅びを与えんことを",
  };

  TextScrollCanvas(TextScroll p) {
    super(false);
    parent = p;
    y = 240;

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
      y--;
      if (y < -180) break;
    }
    parent.notifyDestroyed();
  }

  public void paintFrame(Graphics g) {
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0x00ff0000);
    for (int i=0; i<str.length; i++) {
      g.drawString(str[i],40,y+i*20,Graphics.LEFT|Graphics.TOP);
    }
    flushGraphics();
  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}
