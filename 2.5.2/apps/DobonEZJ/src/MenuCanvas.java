import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class MenuCanvas extends GameCanvas
                        implements Runnable, CommandListener {
  private Image img;
  private int selNo;
  private int keyState;
  private Dobon parent;
  private Command c1;
  private Font f1;
  private int WIDTH;
  private int HEIGHT;

  private Thread gameLoop = null;

  MenuCanvas(Dobon p) {
    super(true);
    parent   = p;
    selNo    = 0;
    keyState = 0;
    WIDTH    = getWidth();
    HEIGHT   = getHeight();
    f1 = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_LARGE);
    try {
      img = Image.createImage("/title.png");

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
    int next   = 0;
    long tm    = System.currentTimeMillis();
    g.setFont(f1);
    g.setClip(0,0,getWidth(),getHeight());

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

      next = renew();
    }
  
    switch (next) {
      case  1 : parent.showGame();        break;
      case  2 : parent.showRule();        break;
      case  3 : parent.showOption();      break;
      default : parent.notifyDestroyed(); break;
    }
  }

  public void paintFrame(Graphics g) {
//  g.setColor(51,0,153);
    g.setColor(0,0,0);
    g.fillRect(0,0,WIDTH,HEIGHT);
    int middle = WIDTH/2 - img.getWidth()/2;
    g.drawImage(img,middle,HEIGHT/10,Graphics.LEFT|Graphics.TOP);

    int anchor = Graphics.HCENTER|Graphics.BASELINE;
    g.setColor(255,153,0);
    int y = (HEIGHT*56)/100;            // 150
    int space = (HEIGHT*15)/100;        // 40
    g.drawString("ゲーム",WIDTH/2+1,y+1,anchor);
    g.drawString("遊び方",WIDTH/2+1,y+space+1,anchor);
    g.drawString("オプション",WIDTH/2+1,y+2*space*1,anchor);
    g.setColor(255,255,0);
    g.drawString("ゲーム",WIDTH/2,y,anchor);
    g.drawString("遊び方",WIDTH/2,y+space,anchor);
    g.drawString("オプション",WIDTH/2,y+2*space,anchor);

    int fh = f1.getHeight();
    y = y - fh/2;                   // "ゲーム"の文字の中心の高さ
    int width = (f1.stringWidth("オプション") * 12)/10;  // 119
    int height = (fh*170)/100;        // 枠の高さ
    g.setColor(255,255,255);
    g.drawRect((WIDTH-width)/2-1,selNo*space+y-height/2-1,width+2,height+2);
    g.setColor(0,0,255);
    g.drawRect((WIDTH-width)/2,  selNo*space+y-height/2,width,height);
    g.setColor(255,255,255);
    g.drawRect((WIDTH-width)/2+1,selNo*space+y-height/2+1,width-2,height-2);

    flushGraphics();
  }

  int renew() {
    int rtn = 0;
    int ks = getKeyStates();

    if (((ks & UP_PRESSED) != 0) &&
       ((keyState & UP_PRESSED) == 0)) {
         if (selNo > 0) selNo--;
    }
    if (((ks & DOWN_PRESSED) != 0) &&
       ((keyState & DOWN_PRESSED) == 0)) {
         if (selNo < 2) selNo++;
    }

    if (((ks & FIRE_PRESSED) != 0) &&
       ((keyState & FIRE_PRESSED) == 0)) {
      try {
        gameLoop.sleep(300);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      gameLoop = null;
      rtn = selNo + 1;
    }
    keyState = ks;
    return rtn;
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      parent.notifyDestroyed();
    }
  }

  public void endProc() {
    img = null;
    gameLoop = null;
    System.gc();
  }
}


