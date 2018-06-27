import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.*;

abstract public class DobonPlayer {
  protected String name;
  protected int id;
  protected int numCards;
  protected int[] ownCards;
  protected int iniScore;
  protected int score;
  protected int prevScore;
  protected int scoreGain;
  protected DobonCanvas c;
  private VolumeControl vc;
  boolean winner, dobonAble;
  boolean reDobonAble;
  boolean dobonPaint;

  DobonPlayer(String name, int id, int iniScore, DobonCanvas c) {
    this.name     = name;
    this.id       = id;
    numCards      = 0;
    ownCards      = new int[26];
    this.iniScore = iniScore;
    score         = iniScore;
    prevScore     = iniScore;
    scoreGain     = 0;
    winner        = false;
    dobonAble     = false;
    dobonPaint    = false;
    reDobonAble   = false;
    this.c        = c;

    try {
      c.se.prefetch();
      c.se2.prefetch();
      c.se3.prefetch();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  // ゲッター
  String getName() { return name; }

  int getNumCards() { return numCards; }
  int getOwnCards(int index) { return ownCards[index]; }
  int getScore() { return score; }
  int getPrevScore() { return prevScore; }
  int getScoreGain() { return scoreGain; }

  // セッター
  void setName(String name) { this.name = name; }
  void setNumCards(int n) { numCards = n; }
  void setScore(int score) { this.score = score; }
  void setPrevScore(int score) { prevScore = score; }
  void setScoreGain(int score) { scoreGain = score; }
  void setIniScore(int iniScore) { this.iniScore = iniScore; }

  // カードを描画
  abstract void paintMyCards(Graphics g);

  // カードを引く
  void drawCards(int num) {
    for (int i=0; i<num; i++) {
      ownCards[numCards++] = c.drawCard();
    }
    if (c.flag2) {
      c.multiDraw = 0;
      c.flag2 = false;
    }
    try {
      c.se2.stop();
      vc = (VolumeControl)c.se2.getControl("VolumeControl");
      vc.setLevel(c.getVolume());
      c.se2.start();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // カードを出す
  void putCard(int[] nextCard) {
    c.trash[c.numTrash++] = c.fieldCard;
    c.fieldCard = nextCard[1];
    if (nextCard[1]%13 == 1) {
      c.flag2 = true;
      c.multiDraw += 2;
    }
    ownCards[nextCard[0]] = ownCards[--numCards];
    try {
      c.se.stop();
      vc = (VolumeControl)c.se.getControl("VolumeControl");
      vc.setLevel(c.getVolume());
      c.se.start();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // ドボン可能かチェック
  boolean dobonCheck() {
    int sum = 0;
    for (int i=0; i<numCards; i++) {
      sum += ownCards[i]%13 + 1;
    }

    if (c.fieldCard%13 + 1 == sum && numCards > 1) {
      return true;
    }
    else {
      return false;
    }
  }

  // ドボンをする
  void dobon() {
    dobonPaint = true;
    try {
      c.se3.stop();
      vc = (VolumeControl)c.se3.getControl("VolumeControl");
      vc.setLevel(c.getVolume());
      c.se3.start();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // スコア以外リセット
  void playerReset() {
    numCards    = 0;
    winner      = false;
    dobonAble   = false;
    dobonPaint  = false;
    reDobonAble = false;
    drawCards(5);
  }

  // スコアだけリセット
  void resetScore() {
    score     = iniScore;
    prevScore = iniScore;
    scoreGain = 0;
  }

  // ストップされた時に支払う得点計算
  int points() {
    int r = 0;
    int multi = 1;
    for (int i=0; i<numCards; i++) {
      if (ownCards[i]%13 == 0 || ownCards[i]%13 > 9) r += 10;
      else r += ownCards[i]%13 + 1;
      if (ownCards[i]%13 == 1) multi *= 2;
    }
    return r * multi;
  }
}
