import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class SerifuText extends MIDlet {
  SerifuTextCanvas c = null;
  public SerifuText() {
    c = new SerifuTextCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (c != null) c.endProc();
  }
}

class SerifuTextCanvas extends GameCanvas implements Runnable {
  SerifuText parent;
  Thread gameLoop = null;
  Image[] img;
  int te, ty, frmcnt;
  Sprite sp;
  TiledLayer tlay;

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

  public SerifuTextCanvas(SerifuText p) {
    super(false);
    parent = p;
    frmcnt = 0;
    ty = 0;
    img = new Image[2];
    te = (str.length - 3) * (-18);

    try {
      img[0] = Image.createImage("/JumpHaikei.png");

      int[][] mdt = {
        {12,12,12,12, 1, 2, 3, 4},
        {12,12,12,12, 5, 6, 7, 8},
        {12,12,12,12,12,12,12,12},
        { 9,10,11,12,12,12,12,12},
        {13,14,15,12,12,12,12,12},
        {16,16,16,16,16,16,16,16},
        {16,16,16,16,16,16,16,16},
        {16,16,16,16,16,16,16,16},
      };

      tlay = new TiledLayer(8,8,img[0],30,30);
      for (int i=0; i<mdt.length; i++) {
        for (int j=0; j<mdt[i].length; j++) {
          tlay.setCell(j,i,mdt[i][j]);
        }
      }

      img[1] = Image.createImage("/Jump.png");
      sp = new Sprite(img[1],32,32);

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
    g.setClip(0,0,240,240);
    tlay.paint(g);

    sp.setFrame((frmcnt/6)%2);
    sp.setPosition(100,120);
    sp.paint(g);

    g.setColor(0x00ffffff);
    g.fillRoundRect(20,165,200,65,10,10);
    g.setClip(50,170,160,50);

    g.setColor(0,0,255);
    for (int i=0; i<str.length; i++) {
      g.drawString(str[i],50,170+ty+i*18,Graphics.LEFT|Graphics.TOP);
    }
    flushGraphics();
    frmcnt++;
  }

  void renew() {
    int ks = getKeyStates();

    if (ty%18 == 0) {
      if ((ks & FIRE_PRESSED) != 0 && ty>te) ty--;
    }
    else ty--;
  }

  void endProc() {
    img[0] = null;
    img[1] = null;
    img = null;
    gameLoop = null;
    System.gc();
  }
}


