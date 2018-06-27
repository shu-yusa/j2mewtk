import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class MapScroll extends MIDlet {
  MapScrollCanvas c;
  public MapScroll() {
    c = new MapScrollCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class MapScrollCanvas extends GameCanvas implements Runnable,CommandListener {
  Image img;
  TiledLayer tlay;
  int px, py;
  MapScroll parent;
  Command c;

  Thread gameLoop = null;

  MapScrollCanvas(MapScroll mid) {
    super(false);
    parent = mid;
    px = py = 0;
    try {
      img = Image.createImage("/OtimaboBack.png");
      c = new Command("EXIT",Command.EXIT,1);
      addCommand(c);
      setCommandListener(this);

      int[][] mdt = {
        {1,2,1,2,1,2,1,2,1,2},
        {3,4,3,4,3,4,3,4,3,4},
        {1,2,1,2,1,2,1,2,1,2},
        {3,4,3,4,3,4,3,4,3,4},
        {1,2,1,2,1,2,1,2,1,2},
        {3,4,3,4,3,4,3,4,3,4},
        {1,2,1,2,1,2,1,2,1,2},
        {3,4,3,4,3,4,3,4,3,4},
        {1,2,1,2,1,2,1,2,1,2},
        {3,4,3,4,3,4,3,4,3,4},
      };

      tlay = new TiledLayer(10,10,img,30,30);
      for (int i=0; i<mdt.length; i++) {
        for (int j=0; j<mdt[i].length; j++) {
          tlay.setCell(j,i,mdt[i][j]);
        }
      }

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
      
      px -= 2; py -= 2;
      px%=60; py%=60;
    }
    parent.notifyDestroyed();
  }

  void paintFrame(Graphics g) {
    tlay.setPosition(px,py);
    tlay.paint(g);

    flushGraphics();
  }

  public void commandAction(Command c, Displayable d) {
    if (c == this.c) gameLoop = null;
  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}
