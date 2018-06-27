import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.Vector;

public class MessageManager extends WindowManager {
  private int pos;
  private static final int UP = 0;
  private static final int DOWN = 1;
  private static final int FRM_W = 2;
  private static String[] msga;
  private int txtX, txtY;
  private boolean scrolling;
  private int st, ed;

  MessageManager(int x, int y, int width, int height, GameCanvas c) {
    super(x, y, c);
    this.width  = width;
    this.height = height;
    txtX = x + 6;
    txtY = y + 6;
    scrolling = false;
  }

  MessageManager(int x, int y, GameCanvas c) {
    super(x, y, c);
    this.width  = (int)(0.6*c.getWidth());
    this.height = (int)(0.2*c.getHeight());
    txtX = x + 6;
    txtY = y + 6;
    scrolling = false;
  }

  MessageManager(GameCanvas c) {
    super(c);
    x = c.getWidth()/5;
    y = (int)(c.getHeight()*0.8);
    this.width  = (int)(0.6*c.getWidth());
    this.height = (int)(0.2*c.getHeight());
    txtX = x + 6;
    txtY = y + 6;
    scrolling = false;
  }

  void draw(Graphics g) {
    if (visible) {
      g.setColor(255,255,255);
      g.fillRect(x,y,width,height);
      g.setColor(0,0,0);
      g.fillRoundRect(x+FRM_W, y+FRM_W, width-FRM_W*2,height-FRM_W*2,8,8);
      g.setColor(255,255,255);
      g.setClip(x,y,width,height);
      if (scrolling) {
        txtY -= 3;
        if ((txtY - (y + 6))%15 == 0) {
          scrolling = false;
          st++;
          ed++;
        }
      }
//    for (int i=0; i<msga.length; i++) {
      for (int i=st; i<Math.min(ed,msga.length); i++) {
        g.drawString(msga[i],x+6, txtY+15*i,Graphics.LEFT|Graphics.TOP);
      }
      g.setClip(0,0,canvas.getWidth(),canvas.getHeight());
//    g.drawString(""+(width - 2*FRM_W)/(fs.stringWidth("A")),0,0,0);
    }
  }

  void setDefault() {
    x = canvas.getWidth()/5;
    y = (int)(canvas.getHeight()*0.8);
    this.width  = (int)(0.6*canvas.getWidth());
    this.height = (int)(0.2*canvas.getHeight());
  }

  void scrollMessage(){
    if (ed == msga.length+1) {
      hide();
    }
    scrolling = true;
  }

  void setMessage(String msg) {
    msga = splitMessage(msg);
    st = 0;
    ed = st + Math.min(4,msga.length+1);
    txtY = y + 6;
    scrolling = false;
  }

  private String[] splitMessage(String inBuff) {
    byte[] bytData;
    String strChar;
    String buf = new String();
    int len = 0;
    Vector msg = new Vector();
    int numCharInLine = (width - 2*FRM_W) /
                        (fs.stringWidth("A"));
    try {
      for (int i=0; i<inBuff.length(); i++) {
        strChar = inBuff.substring(i,i+1);
        bytData = strChar.getBytes("SJIS");
        if (bytData.length == 2) {
          if (len < numCharInLine-3) {
            len += 2;
            buf = buf.concat(strChar);
          }
          else {
            msg.addElement(buf);
            buf = strChar;
            len = 2;
          }
        }
        else {
          if (len < numCharInLine-2) {
            len++;
            buf = buf.concat(strChar);
          }
          else {
            msg.addElement(buf);
            buf = strChar;
            len = 1;
          }
        }
      }
      msg.addElement(buf);
    }
    catch (java.io.UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    String[] outBuff = new String[msg.size()];
    for (int i=0; i<outBuff.length; i++) {
      outBuff[i] = (String)msg.elementAt(i);
    }
    return outBuff;
  }
}
