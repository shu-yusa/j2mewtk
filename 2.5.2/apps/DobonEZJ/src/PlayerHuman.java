import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class PlayerHuman extends DobonPlayer {
  private int choosedCard;
  private int selectSuit;
  public boolean flag8;
  private int WIDTH;
  private int HEIGHT;

  PlayerHuman(String name, int id, int iniScore, DobonCanvas c) {
    super(name,id,iniScore,c);
    choosedCard = 0;
    selectSuit  = 0;
    flag8       = false;
    WIDTH       = c.getWidth();
    HEIGHT      = c.getHeight();
  }

  int getChoosedCard() { return choosedCard; }

  int getSelectSuit() { return selectSuit; }

  void setChoosedCard(int index) { choosedCard = index; }

  void setSelectSuit(int suit) { selectSuit = suit; }

  // カードを出す
  void putCard(int[] nextCard) {
    super.putCard(nextCard);
    c.fieldSuit = c.fieldCard/13;
    choosedCard = 0;
    if (nextCard[1]%13 == 7) {
      flag8 = true;
    }
  }

  // indexで指定されたカードが出せるか判断
  boolean puttable(int index) {
    int mySuit = ownCards[index]/13;
    if (c.flag2) {
      if (ownCards[index]%13 == 1) return true;
      else {
        return false;
      }
    }
    else if (mySuit == c.fieldSuit) return true;
    else if (ownCards[index]%13 == c.fieldCard%13) return true;
    else return false;
  }

  void playerReset() {
    super.playerReset();
    choosedCard = 0;
  }

  void paintMyCards(Graphics g) {
    int s;
    int shift = 1;
    int len = numCards;
    int widthDobon = WIDTH*2/3;                     // 160
    int cardWidth  = c.img2[0].getWidth()-shift*2;   // 32 -> 30
    int cardHeight = c.img2[0].getHeight();
    int ptShift = cardWidth/2;                      // 16
    int arw = (HEIGHT*3)/100/2;
    int space1, space2;

    if (len > 8) {
      // カードの描画
      space1 = (WIDTH - len/2*cardWidth) / (len/2 + 1);
      for (int i=0; i<len/2; i++) {
        s = (i+1) * space1 + i * cardWidth - shift;
        g.drawImage(c.img2[ownCards[i]],s,(HEIGHT*69)/100,Graphics.LEFT|Graphics.TOP);  //184
      }
      space2 = (WIDTH - (len-len/2)*cardWidth) / ((len-len/2) + 1);
      for (int i=0; i<len-len/2; i++) {
        s = (i+1) * space2 + i * cardWidth - shift;
        g.drawImage(c.img2[ownCards[i+len/2]],s,(HEIGHT*86)/100,Graphics.LEFT|Graphics.TOP);//230
      }
      // 矢印の描画
      g.setColor(0,0,255);
      if (choosedCard < len/2) {
        s = (choosedCard+1) * space1 + choosedCard * cardWidth
                                     + ptShift - shift;
        g.fillTriangle(s-arw,(HEIGHT*65)/100,s+arw,(HEIGHT*65)/100,s,(HEIGHT*68)/100); //174,182
      }
      else {
        s = ((choosedCard-len/2)+1)*space2 + (choosedCard-len/2)*cardWidth
                                     + ptShift - shift;
        g.fillTriangle(s-arw,(HEIGHT*82)/100,s+arw,(HEIGHT*82)/100,s,(HEIGHT*85)/100);//219,227
      }
    }
    else {
      if (!dobonPaint) {
        space1 = (WIDTH - len * cardWidth) / (len + 1);
        for (int i=0; i<len; i++) {
          s = (i+1) * space1 + i * cardWidth - shift;
          g.drawImage(c.img2[ownCards[i]],s,(HEIGHT*86)/100,Graphics.LEFT|Graphics.TOP); //230
        }
        s = (choosedCard+1) * space1 + choosedCard * cardWidth 
                                     + ptShift - shift;
        g.setColor(0,0,255);
        g.fillTriangle(s-arw,(HEIGHT*82)/100,s+arw,(HEIGHT*82)/100,s,(HEIGHT*85)/100);//219,227
      }
      else {
        space2 = (widthDobon - len * cardWidth) / (len + 1);
        for (int i=0; i<len; i++) {
          s = (i+1) * space2 + i * cardWidth - shift + (WIDTH-widthDobon)/2;
          g.drawImage(c.img2[ownCards[i]],s,(HEIGHT*64)/100,Graphics.LEFT|Graphics.TOP);
        }
        g.setColor(0,0,0);
        g.drawString("ド ボ ン !!",WIDTH/2,(HEIGHT*90)/100,Graphics.HCENTER|Graphics.TOP);
      }
    }

    if (flag8) {
      g.setColor(0,0,255);
      int frameHeight = (selectSuit < 2 ? 16 : 17);
      int offset = (selectSuit == 3 ? 1 : 0); 
      int suitPosX = (WIDTH*771)/1000;
      int suitPosY = (HEIGHT*224)/1000;
      g.drawRect(suitPosX-3,suitPosY-3+offset+16*selectSuit,17,frameHeight);
      for (int i=0; i<4; i++) {
        c.suit.setFrame(i);
        c.suit.setPosition(suitPosX,suitPosY+i/3+16*i);
        c.suit.paint(g);
      }
    }
  }
}

//    g.setColor(0,0,0);
//    if (selectSuit == 0) g.setColor(0,0,255);
//    g.drawString("Spade",185,60,Graphics.LEFT|Graphics.TOP);
//    g.setColor(0,0,0);
//    if (selectSuit == 1) g.setColor(0,0,255);
//    g.drawString("Heart",185,75,Graphics.LEFT|Graphics.TOP);
//    g.setColor(0,0,0);
//    if (selectSuit == 2) g.setColor(0,0,255);
//    g.drawString("Dia",185,90,Graphics.LEFT|Graphics.TOP);
//    g.setColor(0,0,0);
//    if (selectSuit == 3) g.setColor(0,0,255);
//    g.drawString("Club",185,105,Graphics.LEFT|Graphics.TOP);

//    g.setColor(51,255,0);
//    g.setColor(0,255,255);
//    g.fillRect(183,58+15*selectSuit,15,14);
