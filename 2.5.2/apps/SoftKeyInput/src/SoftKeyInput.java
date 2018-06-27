import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class SoftKeyInput extends MIDlet {
  SoftKeyInputCanvas c;
  public SoftKeyInput() {
    c = new SoftKeyInputCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class SoftKeyInputCanvas extends GameCanvas 
  implements Runnable,CommandListener{
  private Image img;
  private Command c1, c2;
  private SoftKeyInput parent;
  private int px, py, vx;

  private Thread gameLoop = null;

  SoftKeyInputCanvas(SoftKeyInput p) {
    super(false);
    px = 4;
    py = 32;
    vx = 0;

    try {
      parent = p;
      img = Image.createImage("/BalloonBaby.png");
      
      c1 = new Command("進む", Command.SCREEN, 1);
      c2 = new Command("EXIT", Command.EXIT, 1);
      addCommand(c1);
      addCommand(c2);
      setCommandListener(this);

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
    g.setColor(0x0000ff00);
    g.fillRect(0,0,getWidth(),getHeight());
    g.drawImage(img,px,py,Graphics.LEFT|Graphics.TOP);

    flushGraphics();
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      if (vx == 0) {
        removeCommand(c1);
        c1 = new Command("停止",Command.SCREEN,1);
        addCommand(c1);
        
        vx = 1;
      }
      else {
        removeCommand(c1);
        c1 = new Command("進む",Command.SCREEN,1);
        addCommand(c1);
        
        vx = 0;
      }
    }
    else if (c == c2) gameLoop = null;
  }

  public void renew() {
    px = px<240 ? px + vx : -64;
  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}
