import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.Random;

public class CharaProto implements Common {
  protected int px, py;
  protected int tx, ty;
  protected int frmcnt;
  protected int frame;
  protected int numFrame;
  protected int direction;
  protected static final int SPEED=8;
  protected int cWidth;
  protected int cHeight;
  protected static int LT;
  protected Sprite sp;
  protected HideSeek parent;
  protected Map map;
  protected static Random rand;
  protected boolean stepping;
  protected int speed;
  private int inc;

  protected CharaProto(Map map, HideSeek p) {
    this.map = map;
    parent = p;
    frame = 0;
    LT = Graphics.LEFT|Graphics.TOP;
    inc = 1;
  }

  static {
    rand = new Random();
  }

  int getX() {
    return px;
  }

  int getY() {
    return py;
  }

  int getDirection() {
    return direction; 
  }

  int getNumFrame() {
    return numFrame;
  }

  Sprite getSprite() {
    return sp;
  }

  int getWidth() {
    return cWidth;
  }

  int getHeight() {
    return cHeight;
  }

  boolean isStepping() {
    return stepping;
  }

  HideSeek getMIDlet() {
    return parent;
  }

  Map getMap() {
    return map;
  }

  int getOppositeDirection() {
    switch (direction) {
      case LEFT:  return RIGHT;
      case RIGHT: return LEFT;
      case UP:    return DOWN;
      case DOWN:  return UP;
      default:    return direction;
    }
  }

  void setDirection(int dir) {
    direction = dir;
    renewFrame();
  }

  void addFrame() {
    if (numFrame == 3) {
      if (frame == 0) {
        inc = 1;
      }
      else if (frame == 2) {
        inc = -1;
      }
      frame = frame + inc;
    }
    else {
      frame = ++frame%numFrame;
    }
    sp.setFrame(frame+direction*numFrame);
  }

  void renewFrame() {
    sp.setFrame(frame+direction*numFrame);
  }

  void setOffset(int offsetX, int offsetY) {
    sp.setPosition(px-(cWidth-map.CS)/2+offsetX, py-cHeight+map.CS+offsetY);
  }

  void setX(int tx) {
    this.tx = tx;
    px = tx * map.CS;
    sp.setPosition(px-(cWidth-map.CS)/2, py-cHeight+map.CS);
  }

  void setX2(int px) {
    this.px = px;
    tx = px / map.CS;
    sp.setPosition(px-(cWidth-map.CS)/2, py-cHeight+map.CS);
  }

  void setY(int ty) {
    this.ty = ty;
    py = ty * map.CS;
    sp.setPosition(px-(cWidth-map.CS)/2, py-cHeight+map.CS);
  }

  void setY2(int py) {
    this.py = py;
    ty = py / map.CS;
    sp.setPosition(px-(cWidth-map.CS)/2, py-cHeight+map.CS);
  }

  void setPosition(int tx, int ty) {
    this.tx = tx;
    this.ty = ty;
    px = tx * map.CS;
    py = ty * map.CS;
    sp.setPosition(px-(cWidth-map.CS)/2, py-cHeight+map.CS);
  }

  void move(int dir) {
    move(dir , SPEED);
  }

  void move(int dir, int speed) {
  }

  boolean isInScreen(int w, int h) {
    int x = sp.getX();
    int y = sp.getY();
    return !(x+cWidth < 0 || x > w || y + cHeight < 0 || y > h);
  }

  void draw(Graphics g) {
    sp.paint(g);
  }

  void destroy() {
    sp = null;
    map = null;   
  }
}
