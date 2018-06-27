import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class DataAccess extends MIDlet {
  DataAccessForm daf = null;
  DataWriteForm dwf = null;
  DataReadCanvas drc = null;

  public DataAccess() {
    daf = new DataAccessForm(this);
    dwf = new DataWriteForm(this);
    drc = new DataReadCanvas(this);

    showMenu();
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}

  public void showMenu() {
    Display.getDisplay(this).setCurrent(daf);
  }

  public void showWrite() {
    Display.getDisplay(this).setCurrent(dwf);
  }

  public void showRead() {
    drc.show();
    Display.getDisplay(this).setCurrent(drc);
  }
}

class DataAccessForm extends Form implements ItemCommandListener {
  StringItem b1, b2;
  DataAccess parent;

  public DataAccessForm(DataAccess p) {
    super("キャラクター設定 メニュー");
    parent = p;

    append(new Spacer(240,50));

    b1 = new StringItem("キャラクター登録","",Item.BUTTON);
    b1.setLayout(Item.LAYOUT_CENTER);
    b1.setDefaultCommand(new Command("WRITE","登録",Command.OK,1));
    b1.setItemCommandListener(this);
    append(b1);

    append(new Spacer(240,10));
    b2 = new StringItem("登録内容表示","",Item.BUTTON);
    b2.setLayout(Item.LAYOUT_CENTER);
    b2.setDefaultCommand(new Command("READ","表示",Command.OK,1));
    b2.setItemCommandListener(this);
    append(b2);

  }

  public void commandAction(Command cmd, Item itm) {
    if (cmd.getLabel() == "WRITE") {
      parent.showWrite();
    }
    else if (cmd.getLabel() == "READ") {
      parent.showRead();
    } 
  }
}
