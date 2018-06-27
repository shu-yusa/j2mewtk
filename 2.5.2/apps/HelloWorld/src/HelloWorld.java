import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class HelloWorld extends MIDlet {
  HelloWorldCanvas c;
  public HelloWorld() {
    c = new HelloWorldCanvas();
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flag) {
  }
}

class HelloWorldCanvas extends Canvas {
  public void paint(Graphics g) {
    g.setColor(255,255,255);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0,0,0);
    g.drawString("Hello World",100,100,Graphics.LEFT|Graphics.TOP);
  }
}

