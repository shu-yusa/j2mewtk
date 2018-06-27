import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.game.*;

public class PlayerCPU extends DobonPlayer {

  PlayerCPU(String name, int id, int iniScore, DobonCanvas c) {
    super(name, id, iniScore, c);
  }

  // 各ターンでの行動
  boolean myTurn() {
    int[] nextCard = new int[2];

    nextCard = chooseNextCard();
    if (nextCard[0] == -1) {
      if (c.flag2) drawCards(c.multiDraw);
      else drawCards(1);
      return false;
    }
    else {
      putCard(nextCard);
      return true;
    }
  }

  // 次に出すカードを決める。
  int[] chooseNextCard() {
    int same;
    int[] val = {-1, -1};

    if (c.flag2) {
      for (int i=0; i<numCards; i++) {
        if (ownCards[i]%13 == 1) {
          val[0] = i;
          val[1] = ownCards[i];
        }
      }
    }
    else {
      for (int i=0; i<numCards; i++) {
        if (ownCards[i]/13 == c.fieldSuit) {
          if (ownCards[i]%13 > val[1]%13) {
            val[0] = i;
            val[1] = ownCards[i];
          }
        }
        if (ownCards[i]%13 == c.fieldCard%13) {
          if (ownCards[i]%13 > val[1]%13) {
            val[0] = i;
            val[1] = ownCards[i];
          }
        }
      }
    }
    return val;
  }

  // カードを出す(オーバーライド)
 void putCard(int[] nextCard) {
    super.putCard(nextCard);
    // decide next Suit
    if (nextCard[1]%13 == 7){
      int maxVal = ownCards[0];
      for (int i=1; i<numCards; i++) {
        if (ownCards[i]%13 > maxVal%13) {
          maxVal = ownCards[i];
        }
      }
      c.fieldSuit = maxVal/13;
    }
    else if (nextCard[0] != -1) {
      c.fieldSuit = nextCard[1]/13;
    }
  }

  void vibrate(MIDlet m, int duration) {
    Display.getDisplay(m).vibrate(duration);
  }

  // オーバーライド
  void playerReset() {
    super.playerReset();
    dobonPaint = false;
  }

  // 実装
  void paintMyCards(Graphics g) {
    int s;
    int len = numCards;
    int width = c.getWidth();

    if (c.CPU.length == 1) {
      if (!dobonPaint) {
        for (int i=0; i<len; i++) {
          s = (200-32*len) * (i+1) / (len+1) + 32*i + 20;
          g.drawImage(c.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
//        g.drawImage(c.img2[ownCards[i]],s,-15,Graphics.LEFT|Graphics.TOP);
        }
      }
      else {
        for (int i=0; i<len; i++) {
          s = (160-32*len) * (i+1) / (len+1) + 32*i + 40;
          g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
                       Sprite.TRANS_ROT180, s,45,
                       Graphics.LEFT|Graphics.TOP);
        }
        g.setColor(0,0,0);
        g.drawString("ド ボ ン !!",width/2,5,Graphics.HCENTER|Graphics.TOP);
      }
    }
    else {
      switch (id) {
        case 1: if (!dobonPaint) {
                  for (int i=0; i<len; i++) {
                    s = (170-32*len) * (i+1) / (len+1) + 32*i;
             //     g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
             //                  Sprite.TRANS_ROT90,
             //                  -18,s,Graphics.LEFT|Graphics.TOP);
                    g.drawRegion(c.img2[52],0,0,32,32,Sprite.TRANS_ROT90,
                                 -18,s,Graphics.LEFT|Graphics.TOP);
                  }
                }
                else {
                  for (int i=0; i<len; i++) {
                    s = (140-32*len) * (i+1) / (len+1) + 32*i + 40;
                    g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
                                 Sprite.TRANS_ROT90, 40,s,
                                 Graphics.LEFT|Graphics.TOP);
                  }
                  g.setColor(0,0,0);
                  g.drawString("ド",10,50,Graphics.LEFT|Graphics.TOP);
                  g.drawString("ボ",10,70,Graphics.LEFT|Graphics.TOP);
                  g.drawString("ン",10,90,Graphics.LEFT|Graphics.TOP);
                  g.drawString("!!",10,110,Graphics.LEFT|Graphics.TOP);
                }
                break;
        case 2: if (!dobonPaint) {
                  for (int i=0; i<len; i++) {
                    s = (200-32*len) * (i+1) / (len+1) + 32*i + 20;
                    g.drawImage(c.img2[52],s,-18,Graphics.LEFT|Graphics.TOP);
            //      g.drawImage(c.img2[ownCards[i]],s,-18,
            //                  Graphics.LEFT|Graphics.TOP);
                  }
                }
                else {
                  for (int i=0; i<len; i++) {
                    s = (160-32*len) * (i+1) / (len+1) + 32*i + 40;
                    g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
                                 Sprite.TRANS_ROT180, s,45,
                                 Graphics.LEFT|Graphics.TOP);
                  }
                  g.setColor(0,0,0);
                  g.drawString("ド ボ ン !!",width/2,5,Graphics.HCENTER|Graphics.TOP);
                }
                break;
        case 3: if (!dobonPaint) {
                  for (int i=0; i<len; i++) {
                    s = (170-32*len) * (i+1) / (len+1) + 32*i;
            //      g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
            //                   Sprite.TRANS_ROT270,
            //                 226,s,Graphics.LEFT|Graphics.TOP);
                    g.drawRegion(c.img2[52],0,0,32,32,Sprite.TRANS_ROT270,
                               226,s,Graphics.LEFT|Graphics.TOP);
                  }
                }
                else {
                  for (int i=0; i<len; i++) {
                    s = (140-32*len) * (i+1) / (len+1) + 32*i + 40;
                    g.drawRegion(c.img2[ownCards[i]],0,0,32,32,
                                 Sprite.TRANS_ROT270, 168,s,
                                 Graphics.LEFT|Graphics.TOP);
                  }
                  g.setColor(0,0,0);
                  g.drawString("ド",220,50,Graphics.LEFT|Graphics.TOP);
                  g.drawString("ボ",220,70,Graphics.LEFT|Graphics.TOP);
                  g.drawString("ン",220,90,Graphics.LEFT|Graphics.TOP);
                  g.drawString("!!",220,110,Graphics.LEFT|Graphics.TOP);
                }
                break;
      }
    }
  }
}
