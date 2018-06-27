import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

abstract public class WindowManager {
  protected int x;
  protected int y;
  protected int width;
  protected int height;
  protected boolean visible;
  protected GameCanvas canvas;
  protected static Font fs=Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL);
  protected static int fshw;

  WindowManager(GameCanvas c) {
    visible = false;
    canvas = c;
    fshw = fs.stringWidth("A");
  }

  WindowManager(int x, int y, GameCanvas c) {
    this(c);
    this.x = x;
    this.y = y;
  }

  boolean isVisible() {
    return visible;
  }

  void setX(int x) {
    this.x = x;
  }

  void setY(int y) {
    this.y = y;
  }

  void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  void show() {
    visible = true;
  }

  void hide() {
    visible = false;
  }

  abstract void draw(Graphics g);

}
