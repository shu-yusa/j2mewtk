import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.Random;

public class LoadCanvas extends GameCanvas implements Runnable {
  private Dobon parent;
  private Font f1;
  private Image img;
  private Sprite[] suit;
  private String str;
  private int frmcnt;
  private Random rand;
  private int WIDTH;
  private int HEIGHT;

  private Thread gameLoop = null;

  LoadCanvas(Dobon p) {
    super(false);
    parent = p;
    WIDTH  = getWidth();
    HEIGHT = getHeight();
    frmcnt = 0;
    rand   = new Random();
    str    = "";
    f1 = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_LARGE);
    try {
      img = Image.createImage("/Dobon.png");
      suit = new Sprite[rand.nextInt(3)+1];
      for (int i=0; i<suit.length; i++) {
        suit[i] = new Sprite(img, 12, 12);
        suit[i].setPosition(WIDTH/2, HEIGHT/4);
      }

      gameLoop = new Thread(this);
      gameLoop.start();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    Graphics g = getGraphics();
    long tm    = System.currentTimeMillis();
    g.setFont(f1);

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

  public void paintFrame(Graphics g) {
    int fh = f1.getHeight();
    g.setColor(255,255,255);
    g.fillRect(0,0,WIDTH,HEIGHT);

    for (int i=0; i<suit.length; i++) {
      suit[i].paint(g);
    }

    int strlen = f1.stringWidth("ロード中.");
    g.setColor(0,0,153);
    g.drawString("ロード中"+str,WIDTH/2-strlen/2+1,HEIGHT/2-fh/2+1,Graphics.LEFT|Graphics.TOP);
    g.setColor(0,0,255);
    g.drawString("ロード中"+str,WIDTH/2-strlen/2,HEIGHT/2-fh/2,Graphics.LEFT|Graphics.TOP);

    flushGraphics();
  }

  void renew() {
    switch (frmcnt++%5) {
      case 0: str = ""; break;
      case 1: str = "."; break;
      case 2: str = ".."; break;
      case 3: str = "..."; break;
      case 4: str = "...."; break;
    }

    for (int i=0; i<suit.length; i++) {
      suit[i].setFrame(rand.nextInt(4));
      suit[i].setPosition(rand.nextInt(WIDTH-12),rand.nextInt(HEIGHT-12));
    }
  }
 
  public void endProc() {
    gameLoop = null;
    img = null;
    System.gc();
  }
}
