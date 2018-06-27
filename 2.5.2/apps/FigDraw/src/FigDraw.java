import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class FigDraw extends MIDlet {
  public FigDraw() {
    FigDrawCanvas c = new FigDrawCanvas();
    Display.getDisplay(this).setCurrent(c);
  }

  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class FigDrawCanvas extends Canvas { 
  public void paint(Graphics g) {
    g.setColor(0x000000ff);
    g.fillRect(0,0,getWidth(),getHeight());

    g.setColor(255,0,255);

    g.fillArc(20,20,200,200,0,120);

    g.setColor(0xff00ffff);
    g.fillArc(20,20,200,200,120,120);
    g.setColor(0xffffff00);
    g.fillArc(20,20,200,200,240,120);
  }
}



