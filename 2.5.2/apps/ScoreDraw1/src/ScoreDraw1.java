import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class ScoreDraw1 extends MIDlet {
  ScoreDraw1Canvas c = null;
  public ScoreDraw1() {
    c = new ScoreDraw1Canvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class ScoreDraw1Canvas extends GameCanvas implements Runnable {
  ScoreDraw1 parent;
  int score;
  Font f1;
  Thread gameLoop = null;

  public ScoreDraw1Canvas(ScoreDraw1 p) {
    super(false);
    parent = p;
    score = 0;
    f1 = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_ITALIC,Font.SIZE_LARGE);

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
    int[] s = new int[4];
    s[0] = score/1000;
    s[1] = (score/100)%10;
    s[2] = (score/10)%10;
    s[3] = score % 10;
    String str = "" + s[0] + s[1] + s[2] + s[3];
    g.setFont(f1);


    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0x00000000);
    g.drawString(str,60,101,Graphics.LEFT|Graphics.TOP);
    g.drawString(str,61,100,Graphics.LEFT|Graphics.TOP);
    g.drawString(str,61,101,Graphics.LEFT|Graphics.TOP);

    g.setColor(0x00ffffff);
    g.drawString(str,60,100,Graphics.LEFT|Graphics.TOP);

    g.drawString(str,140,100,Graphics.LEFT|Graphics.TOP);

    flushGraphics();
  }

  public void renew() {
    int ks = getKeyStates();

    if (((ks & UP_PRESSED) != 0) && score < 9999) score++;
    if (((ks & DOWN_PRESSED) != 0) && score>0) score--;

  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}



