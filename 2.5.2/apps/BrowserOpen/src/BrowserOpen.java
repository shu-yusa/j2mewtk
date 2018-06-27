import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class BrowserOpen extends MIDlet {
  public BrowserOpen() {
    BrowserOpenForm f = new BrowserOpenForm(this);
    Display.getDisplay(this).setCurrent(f);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class BrowserOpenForm extends Form implements ItemCommandListener {
  BrowserOpen parent;
  StringItem b1;

  public BrowserOpenForm(BrowserOpen p) {
    super("open browser");
    parent = p;

    append(new Spacer(240,100));
    b1 = new StringItem("移動","",Item.BUTTON);
    b1.setLayout(Item.LAYOUT_CENTER);
    b1.setDefaultCommand(new Command("ページ移動","移動",Command.OK,1));
    b1.setItemCommandListener(this);
    append(b1);
  }

  public void paint(Graphics g) {
  }

  public void commandAction(Command c, Item itm) {
    try {
      parent.platformRequest("http://qwe.jp/appli");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
