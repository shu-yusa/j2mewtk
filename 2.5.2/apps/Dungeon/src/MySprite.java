import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class MySprite {
  private Image image;
  private int width;
  private int height;
  private int frameWidth;
  private int frameHeight;
  private int collX;
  private int collY;
  private int collWidth;
  private int collHeight;
  private int frame;
  private int totFrame;
  private int x, y;
  private int column;
  private int row;

  public MySprite(Image image) {
    this.image = image;
    frameWidth  = width  = image.getWidth();
    frameHeight = height = image.getHeight();
    collX = collY = 0;
    collWidth = frameWidth;
    collHeight = frameHeight;
    totFrame = 1;
    frame = 0;
    x = y = 0;
  }

  public MySprite(Image image, int frameWidth, int frameHeight) {
    this(image);
    column = width / frameWidth;
    row = height / frameHeight;
    this.frameWidth = frameWidth;
    this.frameHeight = frameHeight;
    totFrame = row * column;
    collX = collY = 0;
    collWidth = frameWidth;
    collHeight = frameHeight;
    frame = 0;
    x = y = 0;
  }

  public MySprite(MySprite sp) {
    this(sp.getImage(), sp.getFrameWidth(), sp.getFrameHeight());
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  int getCollX() {
    return collX;
  }

  int getCollY() {
    return collY;
  }

  int getCollWidth() {
    return collWidth;
  }

  int getCollHeight() {
    return collHeight;
  }

  int getFrameWidth() {
    return frameWidth;
  }

  int getFrameHeight() {
    return frameHeight;
  }

  public Image getImage() {
    return image;
//  return Image.createImage(image);
  }

  public void defineCollisionRectangle(int x, int y, int width, int height) {
    collX = x;
    collY = y;
    collWidth = width;
    collHeight = height;
  }

  public boolean collidesWith(Image image, int x, int y, boolean pixelLevel) {
    return (x < (x+collX+collWidth)  && x+collX < x + image.getWidth() &&
            y < (y+collY+collHeight) && y+collY < y + image.getHeight());
  }

  public boolean collidesWith(MySprite s, boolean pixelLevel) {
    return (s.getX()+s.getCollX() < (x+collX+collWidth)  && x+collX < s.getX()+s.getCollX()+s.getCollWidth() &&
            s.getY()+s.getCollY() < (y+collY+collHeight) && y+collY < s.getY()+s.getCollY()+s.getCollHeight());
  }

  public void nextFrame() {
    frame = (frame == totFrame-1 ? 0 : frame+1);
  }

  public void prevFrame() {
    frame = (frame == 0 ? totFrame-1 : frame-1);
  }

  public void setFrame(int sequenceIndex) {
    frame = sequenceIndex;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void move(int dx, int dy) {
    x += dx;
    y += dy;
  }

  public void paint(Graphics g) {
    g.drawRegion(image, frame%column*frameWidth, frame/column*frameHeight,
                 frameWidth, frameHeight, Sprite.TRANS_NONE,
                 x, y, Graphics.LEFT|Graphics.TOP);
  }
}
