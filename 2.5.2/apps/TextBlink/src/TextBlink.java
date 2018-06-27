import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class TextBlink extends MIDlet {
  TextBlinkCanvas c;
  public TextBlink() {
    c = new TextBlinkCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class TextBlinkCanvas extends GameCanvas implements Runnable {
  int frmcnt;
  Thread gameLoop = null;
  TextBlink parent;
  Font f1;

  public TextBlinkCanvas(TextBlink p) {
    super(true);
    parent = p;
    frmcnt = 0;
    f1 = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_LARGE);

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
    long tm = System.currentTimeMillis();
    Graphics g = getGraphics();

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
  }

  void paintFrame(Graphics g) {
    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setFont(f1);

    if ((frmcnt++)/6%3 == 0) g.setColor(0x00ff8080);
    else g.setColor(0x00ff0000);
    g.drawString("Game Over!",80,120,Graphics.LEFT|Graphics.TOP);

    flushGraphics();
  }

  void renew() {}

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}
       

