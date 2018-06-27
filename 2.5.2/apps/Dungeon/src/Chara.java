import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.Random;

public class Chara extends CharaProto {
  private boolean walking;
  private int moving;
  private String msg;

  /**
   * constructor
   * @param fname file name of Image file
   * @param sx キャラチップの画像ファイル内でのx座標
   * @param sy キャラチップの画臓ファイル内でのy座標
   * @param w  各キャラチップの幅
   * @param h  各キャラチップの高さ
   * @param nf 各行から読み込むチップの数
   * @param mt 動作の種類 0: 動かない 1:その場で足踏、2:ランダムウォーク
   * @param stepping キャラが足踏するか否か
   */
  Chara(int cnum, int sx, int sy, int w, int h, int nf, int tx, int ty,
        int dir, int mt, String msg, Map map, HideSeek p) {
    super(map, p);
    this.tx = tx;
    this.ty = ty;
    px = tx * map.CS;
    py = ty * map.CS;
    cWidth    = w;
    cHeight   = h;
    direction = dir;
    numFrame  = nf;
    moving = 0;
    this.msg = msg;

    switch (mt) {
      case 0: stepping = false;
              walking  = false;
              break;
      case 1: stepping = true;
              walking  = false;
              break;
      case 2: stepping = true;
              walking  = true;
              break;
      default: stepping = true;
               walking = false;
               break;
    }
    
    sp = new Sprite(map.getCanvas().getCharaImage(cnum), cWidth, cHeight);
    sp.setFrame(frame+direction*numFrame);
    sp.defineCollisionRectangle((cWidth-map.CS)/2,cHeight-map.CS,map.CS,map.CS);

//  try {
//    Image img = Image.createImage(fname);
//    img = Image.createImage(img, sx, sy, nf*w, 4*h, Sprite.TRANS_NONE);
//    sp = new Sprite(img, cWidth, cHeight);
//    sp.setFrame(frame+direction*numFrame);
//    sp.defineCollisionRectangle((cWidth-map.CS)/2,cHeight-map.CS,map.CS,map.CS);
//    img = null;
//    System.gc();
//  }
//  catch (Exception e) {
//    e.printStackTrace();
//    parent.notifyDestroyed();
//  }

  }

  Chara(CharaProto source, int tx, int ty, int dir, int mt, Map map, HideSeek p) {
    super(map, p);
    cWidth   = source.getWidth();
    cHeight  = source.getHeight();
    numFrame = source.getNumFrame();
    stepping = source.isStepping();
    this.sp = new Sprite(source.getSprite());
    this.tx = tx;
    this.ty = ty;
    px = tx * map.CS;
    py = ty * map.CS;
    direction = dir;
    moving = 0;
    switch (mt) {
      case 0: stepping = false;
              walking  = false;
              break;
      case 1: stepping = true;
              walking  = false;
              break;
      case 2: stepping = true;
              walking  = true;
      default: stepping = true;
               walking = false;
    }
  }

  boolean canWalk() {
    return walking;
  }

  String getMessage() {
    return msg;
  }

  void move(int dir, int speed) {
    int nextTx, nextTy;
    switch (dir) {
      case HideCanvas.LEFT:
        direction = HideCanvas.LEFT;
        sp.move(-speed,0);    // Sprite同士の衝突検出用
        nextTx = (px-speed+1)/map.CS;
        if (map.movePointEquals(nextTx,(py+1)/map.CS)) {
          break;
        }
        if (!map.isHit(this, nextTx, (py+1)/map.CS)  &&
            !map.isHit(this, nextTx, (py+map.CS-1)/map.CS)) {
          px -= speed;
        }
        break;
      case HideCanvas.RIGHT:
        direction = HideCanvas.RIGHT;
        sp.move(speed,0);
        nextTx = (px+map.CS+speed-1)/map.CS;
        if (map.movePointEquals(nextTx,(py+1)/map.CS)) {
          break;
        }
        if (!map.isHit(this, nextTx, (py+1)/map.CS)  &&
            !map.isHit(this, nextTx, (py+map.CS-1)/map.CS)) {
          px += speed;
        }
        break;
      case HideCanvas.UP:
        direction = HideCanvas.UP;
        sp.move(0,-speed);
        nextTy =(py-speed+1)/map.CS;
        if (map.movePointEquals((px+1)/map.CS, nextTy)) {
          break;
        }
        if (!map.isHit(this, (px+1)/map.CS, nextTy)         &&
            !map.isHit(this, (px+map.CS-1)/map.CS, nextTy)) {
          py -= speed;
        }
        break;
      case HideCanvas.DOWN:
        direction = HideCanvas.DOWN;
        sp.move(0,speed);
        nextTy = (py+map.CS+speed-1)/map.CS;
        if (map.movePointEquals((px+1)/map.CS, nextTy)) {
          break;
        }
        if (!map.isHit(this, (px+1)/map.CS, nextTy)         &&
            !map.isHit(this, (px+map.CS-1)/map.CS, nextTy)) {
          py += speed;
        }
        break;
    }
    if (!stepping) addFrame();
    sp.setFrame(frame+direction*numFrame);
  }

  void randomWalk(int speed) {
    if (moving == 0) {
      if (rand.nextInt(50) == 0) {
        direction = rand.nextInt(4);
        move(direction, speed);
        moving += speed;
      }
    }
    else {
      move(direction, speed);
      moving = (moving + speed)%map.CS;
    }
  }
}
