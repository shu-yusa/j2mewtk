import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import java.io.*;
import java.util.*;

public class RuleCanvas extends GameCanvas 
                        implements Runnable, CommandListener{
  private Dobon parent;
  private DobonCanvas dc;
  private Vector[] vec;
  private Command c1, c2;
  private boolean selecting;
  private int[] te;
  private int ty, ud;
  private int selNo;
  private int keyState;

  private Thread gameLoop = null;

  RuleCanvas(Dobon p, DobonCanvas dc) {
    super(false);
    parent    = p;
    this.dc   = dc;
    vec       = new Vector[6];
    selecting = true;
    te        = new int[6];
    ty        = 0;
    ud        = 0;
    selNo     = 0;
    keyState  = 0;

    try {
      char[] b = new char[80];
      int c;
      String s;
      InputStreamReader isr;
      String[] str = {"/rule1.txt","/ruleput.txt","/rule28.txt",
                      "/ruledobon.txt","/ruleredobon.txt",
                      "/rulescore.txt",};
      int i = 0;

      for (int k=0; k<vec.length; k++) {
        vec[k] = new Vector();
        isr = new InputStreamReader(
              getClass().getResourceAsStream(str[k]), "UTF-8");
        i = 0;
        while((c=isr.read()) != -1) {
          if ((b[i]=(char)c) == '\n') {
            b[i] = '\n';
            for (int j=i+1; j<b.length; j++) b[j] = '\u0000';
            s = new String(b);
            vec[k].addElement(s);
            i = -1;
          }
          i++;
        }
        if (i != 0) {
          b[i] = '\n';
          s = new String(b);
          vec[k].addElement(s);
        }
        isr.close();

        te[k] = (vec[k].size() - 3) * (-18);
      }

      c1 = new Command("戻る",Command.SCREEN,1);
      addCommand(c1);
      setCommandListener(this);
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
  }

  void startTh() {
    gameLoop = new Thread(this);
    gameLoop.start();
    Display.getDisplay(parent).setCurrent(this);
  }

  public void run() {
    Graphics g = getGraphics();
    g.setClip(0,0,getWidth(),getHeight());
    long tm = System.currentTimeMillis();
    int waitTime;

    while (gameLoop != null) {
      paintFrame(g);

      if (selecting) waitTime = 100;
      else waitTime = 20;

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < waitTime) gameLoop.sleep(waitTime-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }

      renew();
    }
    parent.showMenu();
  }

  public void paintFrame(Graphics g) {
    g.setColor(153,255,102);
    g.setClip(0,0,getWidth(),getHeight());
    g.fillRect(0,0,getWidth(),getHeight());

    if (selecting) {
      paintSelect(g);
    }
    else {
      if (selNo == 0 || selNo == 5) {
        int st = -ty/18;
        int ed = (-ty/18+10 > vec[selNo].size() ?
                             vec[selNo].size() : -ty/18+10);
        g.setColor(0x00ffffff);
        g.fillRoundRect(10,44,220,176,10,10);
        g.setClip(10,51,220,162);
        g.setColor(0,0,255);
        for (int i=st; i<ed; i++) {
          g.drawString((String)vec[selNo].elementAt(i),20,51+ty+i*18,
                        Graphics.LEFT|Graphics.TOP);
        }
      }
      else {
        int st = -ty/18;
        int ed = (-ty/18+5 > vec[selNo].size() ?
                             vec[selNo].size() : -ty/18+5);
        g.setColor(0x00ffffff);
        g.fillRoundRect(0,185,240,83,10,10);
        g.setClip(0,190,240,72);
        g.setColor(0,0,255);
        for (int i=st; i<ed; i++) {
          g.drawString((String)vec[selNo].elementAt(i),8,190+ty+i*18,
                        Graphics.LEFT|Graphics.TOP);
        }
        g.setClip(0,0,240,262);
      }
      switch (selNo) {
        case(1) : paint1(g);
                  break;
        case(2) : paint2(g);
                  break;
        case(3) : paint3(g);
                  break;
        case(4) : paint4(g);
                  break;
      }
    }
    flushGraphics();
  }

  void renew() {
    int ks = getKeyStates();

    if (selecting) {
      if (((ks & UP_PRESSED) != 0) &&
        ((keyState & UP_PRESSED) == 0)) {
           if (selNo > 0) selNo--;
      }
      if (((ks & DOWN_PRESSED) != 0) &&
          ((keyState & DOWN_PRESSED) == 0)) {
            if (selNo < 5) selNo++;
      }
      if (((ks & FIRE_PRESSED) != 0) &&
        ((keyState & FIRE_PRESSED) == 0)) {
        selecting = false;
      }
    }
    else {
      if (ty%18 == 0) {
        if ((ks & UP_PRESSED) != 0 && ty<0) {
          ty += 9;
          ud = 1;
        }
        if ((ks & DOWN_PRESSED) != 0 && ty>te[selNo]) {
          ty -= 9;
          ud = -1;
        }
      }
      else {
        ty = ty + (ud == 1 ? 9 : -9);
        if (ty%18 == 0) ud = 0;
      }
    }
  }

  void paintSelect(Graphics g) {
    int anchor = Graphics.HCENTER|Graphics.BASELINE;
    g.setColor(0,0,255);
    g.drawString("概要",120,60,anchor);
    g.drawString("各ターンの行動",120,90,anchor);
    g.drawString("2と8について",120,120,anchor);
    g.drawString("ドボンについて",120,150,anchor);
    g.drawString("ドボン返しについて",120,180,anchor);
    g.drawString("スコア計算について",120,210,anchor);

    g.setColor(255,0,0);
    g.drawRect(41,selNo*30+40,156,28);
    g.setColor(255,255,255);
    g.drawRect(42,selNo*30+41,154,26);
    g.setColor(255,0,0);
    g.drawRect(43,selNo*30+42,152,24);
  }
 
  // カードの出し方について
  void paint1(Graphics g) {
    int len, j, s;

    g.setColor(0,0,0);
    g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//  g.drawRegion(dc.img3,11,0,11,10,Sprite.TRANS_NONE,
//               60,28,Graphics.LEFT|Graphics.TOP);
    dc.suit.setFrame(1);
    dc.suit.setPosition(60,28);
    dc.suit.paint(g);
    if (-ty/18 >= 0 && -ty/18 < 9) {
      g.drawImage(dc.img2[15],105,70,Graphics.LEFT|Graphics.TOP);

      len = 3; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[2],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[19],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }
    else if (-ty/18 >= 9 && -ty/18 < 11) {
      g.drawImage(dc.img2[15],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[19],105,70,Graphics.LEFT|Graphics.TOP);

      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[2],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }
    else if (-ty/18 >= 11 && -ty/18 < 13) {
      g.drawImage(dc.img2[15],105,70,Graphics.LEFT|Graphics.TOP);

      len = 3; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[3],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[48],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }
    else if (-ty/18 >= 13) {
      g.drawImage(dc.img2[15],105,70,Graphics.LEFT|Graphics.TOP);

      len = 4; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[3],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[48],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 3;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[21],s,150,Graphics.LEFT|Graphics.TOP);
    }

    len = 5;
    for (int i=0; i<len; i++) {
      s = (150-32*len) * (i+1) / (len+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT90,
                   -18,s,Graphics.LEFT|Graphics.TOP);
    }
    len = 3;
    for (int i=0; i<len; i++) {
      s = (200-32*len) * (i+1) / (len+1) + 32*i + 20;
      g.drawImage(dc.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
    }
    len = 4;
    for (int i=0; i<len; i++) {
      s = (170-32*len) * (i+1) / (len+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                 226,s,Graphics.LEFT|Graphics.TOP);
    }
  }
 
  // 2と8について
  void paint2(Graphics g) {
    int len, j, s;
    int len1, len2, len3;
    len1 = len2 = len3 = 0;
    g.setColor(0,0,0);


    if (-ty/18 < 4) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 5; len3 = 4;

      g.drawImage(dc.img2[3],105,70,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[7],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[1],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 8を出す
    else if (-ty/18 >= 4 && -ty/18 < 7) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
      if (-ty/18 == 4) {
//      g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                   60,28,Graphics.LEFT|Graphics.TOP);
        dc.suit.setFrame(0);
        dc.suit.setPosition(60,28);
        dc.suit.paint(g);
        g.setColor(0,0,255);
//      g.drawRect(182,57,16,15);
//      for (int i=0; i<4; i++) {
//        g.drawRegion(dc.img3,11*i,0,11,10,Sprite.TRANS_NONE,
//                     185,60+15*i,Graphics.LEFT|Graphics.TOP);
//      }
        g.drawRect(182,57,17,16);
        for (int i=0; i<4; i++) {
          dc.suit.setFrame(i);
          dc.suit.setPosition(185,60+i/3+16*i);
          dc.suit.paint(g);
        }
      }
      else if (-ty/18 >= 5) {
//      g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                   60,28,Graphics.LEFT|Graphics.TOP);
        dc.suit.setFrame(2);
        dc.suit.setPosition(60,28);
        dc.suit.paint(g);
        if (-ty/18 == 5) {
          g.setColor(0,0,255);
//        g.drawRect(182,87,16,15);
//        for (int i=0; i<4; i++) {
//          g.drawRegion(dc.img3,11*i,0,11,10,Sprite.TRANS_NONE,
//                       185,60+15*i,Graphics.LEFT|Graphics.TOP);
//        }
          g.drawRect(182,89,17,17);
          for (int i=0; i<4; i++) {
            dc.suit.setFrame(i);
            dc.suit.setPosition(185,60+i/3+16*i);
            dc.suit.paint(g);
          }
        }
      }

      len1 = 3; len2 = 5; len3 = 4;

      g.drawImage(dc.img2[3],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[7],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[1],s,150,Graphics.LEFT|Graphics.TOP);

    }
    // 次のプレイヤーがカードを出す
    else if (-ty/18 >= 7 && -ty/18 < 10) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 2; len2 = 5; len3 = 4;
      g.drawImage(dc.img2[7],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[36],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[1],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 2を持ってる
    else if (-ty/18 >= 10 && -ty/18 < 12) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 5; len3 = 4;

      g.drawImage(dc.img2[3],105,70,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[7],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[1],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 2を出す
    else if (-ty/18 >= 12 && -ty/18 < 16) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
      g.drawString("2x1 = 2  <   引く枚数", 20, 40,Graphics.LEFT|Graphics.TOP);
      g.drawLine(92,47,111,47);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 5; len3 = 4;

      g.drawImage(dc.img2[3],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[1],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[7],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 2人目が2を出す
    else if (-ty/18 >= 16 && -ty/18 < 19) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
      g.drawString("2x2 = 4  <   引く枚数", 20, 40,Graphics.LEFT|Graphics.TOP);
      g.drawLine(92,47,111,47);
//    g.drawRegion(dc.img3,11,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(1);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 2; len2 = 5; len3 = 4;

      g.drawImage(dc.img2[1],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[14],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[7],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 3人目が2を出す
    else if (-ty/18 >= 19) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
      g.drawString("2x3 = 6  <   引く枚数", 20, 40,Graphics.LEFT|Graphics.TOP);
      g.drawLine(92,47,111,47);
//    g.drawRegion(dc.img3,33,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(3);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 2; len2 = 4; len3 = 4;

      g.drawImage(dc.img2[14],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[40],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[7],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[37],s,150,Graphics.LEFT|Graphics.TOP);
    }


    // comプレイヤーのカード
    for (int i=0; i<len1; i++) {
      s = (150-32*len1) * (i+1) / (len1+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT90,
                   -18,s,Graphics.LEFT|Graphics.TOP);
    }
    for (int i=0; i<len2; i++) {
      s = (200-32*len2) * (i+1) / (len2+1) + 32*i + 20;
      g.drawImage(dc.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
    }
    for (int i=0; i<len3; i++) {
      s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                 226,s,Graphics.LEFT|Graphics.TOP);
    }
  }

  // ドボンについて
  void paint3(Graphics g) {
    int len, j, s;
    int len1, len2, len3;
    len1 = len2 = len3 = 0;
    g.setColor(0,0,0);

    if ((-ty/18 < 9 || -ty/18>=20) && c2 != null) {
      removeCommand(c2);
      c2 = null;
    }

    if (-ty/18 < 9) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 5; len3 = 2;

      g.drawImage(dc.img2[5],105,70,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[39],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[27],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[28],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 3;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[3],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // 他プレイヤーが10を出す
    else if (-ty/18 >= 9 &&  -ty/18 < 20) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 4; len3 = 2;

      if (c2 == null) {
        c2 = new Command("ドボン", Command.OK,2);
        addCommand(c2);
      }

      g.drawImage(dc.img2[5],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[9],105,70,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[39],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[27],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[28],s,150,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 3;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[3],s,150,Graphics.LEFT|Graphics.TOP);
    }
    // ドボン
    else if (-ty/18 >= 20 && -ty/18 < 30) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);
      g.drawString("(負け)",100,20,Graphics.LEFT|Graphics.TOP);

      len1 = 3; len2 = 4; len3 = 2;

      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!(勝ち)",50,160,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[5],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[9],105,70,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 0;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[39],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 1;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[27],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 2;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[28],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 3;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[3],s,120,Graphics.LEFT|Graphics.TOP);
    }
    // ドボン2人目
    else if (-ty/18 >= 30) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,0,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(0);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 3; len2 = 4; len3 = 2;

      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!(負け)",50,160,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[5],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[9],105,70,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 0;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[39],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 1;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[27],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 2;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[28],s,120,Graphics.LEFT|Graphics.TOP);
      len = 4; j = 3;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[3],s,120,Graphics.LEFT|Graphics.TOP);
    }


    // comプレイヤーのカード
    for (int i=0; i<len1; i++) {
      s = (150-32*len1) * (i+1) / (len1+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT90,
                   -18,s,Graphics.LEFT|Graphics.TOP);
    }
    for (int i=0; i<len2; i++) {
      s = (200-32*len2) * (i+1) / (len2+1) + 32*i + 20;
      g.drawImage(dc.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
    }
    if (-ty/18 < 30 || -ty/18 >= 40) {
      for (int i=0; i<len3; i++) {
        s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
        g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                   226,s,Graphics.LEFT|Graphics.TOP);
      }
    }
    else {
      g.setColor(0,0,0);
      g.drawString("ド",220,30,Graphics.LEFT|Graphics.TOP);
      g.drawString("ボ",220,50,Graphics.LEFT|Graphics.TOP);
      g.drawString("ン",220,70,Graphics.LEFT|Graphics.TOP);
      g.drawString("!!",220,90,Graphics.LEFT|Graphics.TOP);
      g.drawString("(勝ち)",190,110,Graphics.LEFT|Graphics.TOP);
      s = (80-32*len3) / (len3+1) + 40;
      g.drawRegion(dc.img2[16],0,0,32,32,
                   Sprite.TRANS_ROT270, 168,s,
                   Graphics.LEFT|Graphics.TOP);
      s = (80-32*len3) * 1 / (len3+1) + 32 + 40;
      g.drawRegion(dc.img2[44],0,0,32,32,
                   Sprite.TRANS_ROT270, 168,s,
                   Graphics.LEFT|Graphics.TOP);
    }
  }

  void paint4(Graphics g) {
    int len, j, s;
    int len1, len2, len3;
    len1 = len2 = len3 = 0;
    g.setColor(0,0,0);

    if ((-ty/18 < 6 || -ty/18 >= 10) && c2 != null) {
      removeCommand(c2);
      c2 = null;
    }

    if (-ty/18 < 5) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 5; len2 = 3; len3 = 2;

      g.drawImage(dc.img2[29],105,70,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[2],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[19],s,150,Graphics.LEFT|Graphics.TOP);
      len = 3; j = 2;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[35],s,150,Graphics.LEFT|Graphics.TOP);

      // comプレイヤーのカード
      for (int i=0; i<len2; i++) {
        s = (200-32*len2) * (i+1) / (len2+1) + 32*i + 20;
        g.drawImage(dc.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
      }
      for (int i=0; i<len3; i++) {
        s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
        g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                   226,s,Graphics.LEFT|Graphics.TOP);
      }
    }
    // 手札を出す
    else if (-ty/18 == 5) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 5; len2 = 3; len3 = 2;

      g.drawImage(dc.img2[29],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[35],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[2],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[19],s,150,Graphics.LEFT|Graphics.TOP);

      // comプレイヤーのカード
      for (int i=0; i<len2; i++) {
        s = (200-32*len2) * (i+1) / (len2+1) + 32*i + 20;
        g.drawImage(dc.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
      }
      for (int i=0; i<len3; i++) {
        s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
        g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                   226,s,Graphics.LEFT|Graphics.TOP);
      }
    }
    // 他プレイヤーのドボン
    else if (-ty/18 >= 6 && -ty/18 < 10) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 5; len2 = 3; len3 = 2;

      if (c2 == null) {
        c2 = new Command("ドボン", Command.OK,2);
        addCommand(c2);
      }

      g.drawImage(dc.img2[29],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[35],105,70,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[2],s,150,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (240-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(dc.img2[19],s,150,Graphics.LEFT|Graphics.TOP);

      // comプレイヤーのカード
      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!",50,5,Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) / (len2+1) + 40;
      g.drawRegion(dc.img2[41],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 2 / (len2+1) + 32 + 40;
      g.drawRegion(dc.img2[43],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 3 / (len2+1) + 64 + 40;
      g.drawRegion(dc.img2[14],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      for (int i=0; i<len3; i++) {
        s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
        g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                   226,s,Graphics.LEFT|Graphics.TOP);
      }
    }
    // ドボン返し
    else if (-ty/18 >= 10 && -ty/18 < 22) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
//    g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
//                 60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 5; len2 = 3; len3 = 2;

      g.drawImage(dc.img2[29],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[35],105,70,Graphics.LEFT|Graphics.TOP);
      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!(勝ち)",50,160,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[2],s,120,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[19],s,120,Graphics.LEFT|Graphics.TOP);

      // comプレイヤーのカード
      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!(負け)",50,5,Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) / (len2+1) + 40;
      g.drawRegion(dc.img2[41],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 2 / (len2+1) + 32 + 40;
      g.drawRegion(dc.img2[43],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 3 / (len2+1) + 64 + 40;
      g.drawRegion(dc.img2[14],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      for (int i=0; i<len3; i++) {
        s = (170-32*len3) * (i+1) / (len3+1) + 32*i;
        g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                   226,s,Graphics.LEFT|Graphics.TOP);
      }
    }
    // ドボン返しにドボン
    else if (-ty/18 >= 22) {
      g.drawString("suit:", 17, 25,Graphics.LEFT|Graphics.TOP);
///   g.drawRegion(dc.img3,22,0,11,10,Sprite.TRANS_NONE,
///                60,28,Graphics.LEFT|Graphics.TOP);
      dc.suit.setFrame(2);
      dc.suit.setPosition(60,28);
      dc.suit.paint(g);

      len1 = 5; len2 = 3; len3 = 2;

      g.drawImage(dc.img2[29],95,80,Graphics.LEFT|Graphics.TOP);
      g.drawImage(dc.img2[35],105,70,Graphics.LEFT|Graphics.TOP);
      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!(負け)",50,160,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 0;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[2],s,120,Graphics.LEFT|Graphics.TOP);
      len = 2; j = 1;
      s = (160-32*len) * (j+1) / (len+1) + 32*j + 40;
      g.drawImage(dc.img2[19],s,120,Graphics.LEFT|Graphics.TOP);

      // comプレイヤーのカード
      // comプレイヤー2
      g.setColor(0,0,0);
      g.drawString("ド ボ ン !!",80,5,Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) / (len2+1) + 40;
      g.drawRegion(dc.img2[41],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 2 / (len2+1) + 32 + 40;
      g.drawRegion(dc.img2[43],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      s = (160-32*len2) * 3 / (len2+1) + 64 + 40;
      g.drawRegion(dc.img2[14],0,0,32,32,
                   Sprite.TRANS_ROT180, s,45,
                   Graphics.LEFT|Graphics.TOP);
      // comプレイヤー3
      g.setColor(0,0,0);
      g.drawString("ド",220,30,Graphics.LEFT|Graphics.TOP);
      g.drawString("ボ",220,50,Graphics.LEFT|Graphics.TOP);
      g.drawString("ン",220,70,Graphics.LEFT|Graphics.TOP);
      g.drawString("!!",220,90,Graphics.LEFT|Graphics.TOP);
      g.drawString("(勝ち)",190,110,Graphics.LEFT|Graphics.TOP);
      s = (80-32*len3) / (len3+1) + 40;
      g.drawRegion(dc.img2[16],0,0,32,32,
                   Sprite.TRANS_ROT270, 168,s,
                   Graphics.LEFT|Graphics.TOP);
      s = (80-32*len3) * 1 / (len3+1) + 32 + 40;
      g.drawRegion(dc.img2[44],0,0,32,32,
                   Sprite.TRANS_ROT270, 168,s,
                   Graphics.LEFT|Graphics.TOP);
    }

    // comプレイヤーのカード
    for (int i=0; i<len1; i++) {
      s = (150-32*len1) * (i+1) / (len1+1) + 32*i;
      g.drawRegion(dc.img2[52],0,0,32,32,Sprite.TRANS_ROT90,
                   -18,s,Graphics.LEFT|Graphics.TOP);
    }
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      if (!selecting) {
        ty = 0;
        selecting = true;
        removeCommand(c2);
      }
      else gameLoop = null;
    }
  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}


