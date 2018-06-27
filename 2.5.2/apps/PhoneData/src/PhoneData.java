import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class PhoneData extends MIDlet {
  public PhoneData() {
    PhoneDataCanvas c = new PhoneDataCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class PhoneDataCanvas extends Canvas implements CommandListener {
  String strPlf, strEnc, strPrf, strConf, strTotal, strFree;
  Command cmd;
  PhoneData parent;
  
  public PhoneDataCanvas(PhoneData p) {
    parent = p;
    strPlf = System.getProperty("microedition.platform");
    strEnc = System.getProperty("microedition.encoding");
    strPrf = System.getProperty("microedition.profiles");
    strConf= System.getProperty("microedition.configuration");
    try {
      cmd = new Command("EXIT",Command.EXIT,1);
      addCommand(cmd);
      setCommandListener(this);
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }

    Runtime rt =Runtime.getRuntime();

    strTotal = "" + rt.totalMemory();
    strFree = "" + rt.freeMemory();
  }

  public void paint(Graphics g) {
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());

    g.setColor(0x00000000);
    g.drawString("端末情報",0,50,Graphics.LEFT|Graphics.TOP);
    g.drawString("Platform : "+strPlf,10,80,Graphics.LEFT|Graphics.TOP);
    g.drawString("Encoding : "+strEnc,10,100,Graphics.LEFT|Graphics.TOP);
    g.drawString("Profile : "+strPrf,10,120,Graphics.LEFT|Graphics.TOP);
    g.drawString("Congifuration : "+strConf,10,140,Graphics.LEFT|Graphics.TOP);

    g.drawString("Total Memory : "+strTotal,10,180,Graphics.LEFT|Graphics.TOP);
    g.drawString("Free Memory : "+strFree,10,200,Graphics.LEFT|Graphics.TOP);
  }

  public void commandAction(Command c, Displayable d){
    parent.notifyDestroyed();
  }
}
   
