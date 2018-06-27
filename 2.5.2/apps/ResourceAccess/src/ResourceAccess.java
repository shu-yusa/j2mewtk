import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import java.io.*;
import java.util.*;

public class ResourceAccess extends MIDlet {
  ResourceAccessCanvas c = null;
  public ResourceAccess() {
    c = new ResourceAccessCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class ResourceAccessCanvas extends GameCanvas implements Runnable {
  ResourceAccess parent;
  Vector vec;
  Thread gameLoop = null;
  int y;

  public ResourceAccessCanvas(ResourceAccess p) {
    super(false);
    parent = p;
    vec = new Vector();
    y = 240;

    try {
      InputStreamReader isr = new InputStreamReader(
          getClass().getResourceAsStream("/dragslave.txt"));
      char[] b = new char[80];
      int c;
      String s;
      int i = 0;
      while((c=isr.read()) != -1) {
        if ((b[i]=(char)c) == '\n') {
          b[i] = '\n';
          for (int j=i+1; j<b.length; j++) b[j] = '\u0000';
          s = new String(b);
          vec.addElement(s);
          i = -1;
        }
        i++;
      }
      if (i != 0) {
        b[i] = '\n';
        s = new String(b);
        vec.addElement(s);
      }
      isr.close();

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
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());

    g.setColor(0x00ff0000);
    for (int i=0; i<vec.size(); i++) {
      g.drawString((String)vec.elementAt(i),40,y+i*20,
          Graphics.LEFT|Graphics.TOP);
    }
    flushGraphics();
  }
  
  public void renew() {
    y--;
    if (y < -180) gameLoop = null;
  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}
