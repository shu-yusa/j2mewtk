import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class FontTextDraw extends MIDlet {
  public FontTextDrawCanvas c;
  public FontTextDraw() {
    c = new FontTextDrawCanvas();
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class FontTextDrawCanvas extends Canvas {
  Font f1, f2, f3;

  FontTextDrawCanvas() {
    f1 = Font.getFont(
        Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_MEDIUM);
    f2 = Font.getFont(
        Font.FACE_MONOSPACE,Font.STYLE_BOLD|Font.STYLE_UNDERLINED,Font.SIZE_LARGE);
    f3 = Font.getFont(
        Font.FACE_PROPORTIONAL,Font.STYLE_ITALIC,Font.SIZE_LARGE);
  }

  public void paint(Graphics g) {
    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0x00ffffff);
    g.setFont(f1);
    int anchor;
    anchor = Graphics.LEFT|Graphics.TOP;
    g.drawString("Hello World"+anchor,0,0,20);
    g.setFont(f2);
    g.drawString("Hello World",80,120,0);
    g.setFont(f3);
    g.drawString("Hello World",20,200,0);
  }
}
