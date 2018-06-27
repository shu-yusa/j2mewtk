import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class Typewriter extends MIDlet {
  TypewriterCanvas c = null;
  public Typewriter() {
    c = new TypewriterCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class TypewriterCanvas extends GameCanvas implements Runnable {
  Typewriter parent;
  Thread gameLoop = null;
  String line;
  int wi, wj, wx, wy;
  int frmcnt;

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

  public TypewriterCanvas(Typewriter p) {
    super(false);
    parent = p;
    wi = wj = 0;
    wx = wy = 20;
    frmcnt = 0;
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

  public void paintFrame(Graphics g) {
    g.setColor(0,0,0);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0x00ff0000);
    for (int i=0; i<wi; i++) {
      g.drawString(str[i],wx,wy+i*20,Graphics.LEFT|Graphics.TOP);
    }
    line = str[wi].substring(0,wj);
    g.drawString(line,wx,wy+wi*20,Graphics.LEFT|Graphics.TOP);
    flushGraphics();
  }

  public void renew() {
    if (((frmcnt++)%5) == 0) {
      if (str[wi].length() == wj) {
        wi++;
        wj = 0;
      }
      wj++;
    }
    if (str.length == wi) gameLoop = null;
  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}
