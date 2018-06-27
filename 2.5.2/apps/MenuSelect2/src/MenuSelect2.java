import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class MenuSelect2 extends MIDlet {
  public MenuSelect2() {
    MenuSelect2Form f = new MenuSelect2Form();
    Display.getDisplay(this).setCurrent(f);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class MenuSelect2Form extends Form implements ItemCommandListener {
  StringItem t1, b1, b2, b3;

  MenuSelect2Form() {
    super("main manu");

    append(new Spacer(240,30));

    t1 = new StringItem("Game Title","",Item.PLAIN);
    t1.setLayout(Item.LAYOUT_CENTER);
    append(t1);

    append(new Spacer(240,50));
    b1 = new StringItem("Game","",Item.BUTTON);
    b1.setLayout(Item.LAYOUT_CENTER);
    b1.setDefaultCommand(new Command("PLAY","開始する",Command.OK,1));
    b1.setItemCommandListener(this);
    append(b1);

    append(new Spacer(240,10));
    b2 = new StringItem("遊び方","",Item.BUTTON);
    b2.setLayout(Item.LAYOUT_CENTER);
    b2.setDefaultCommand(new Command("HOWTO","読む",Command.OK,1));
    b2.setItemCommandListener(this);
    append(b2);

    append(new Spacer(240,10));
    b3 = new StringItem("Option","",Item.BUTTON);
    b3.setLayout(Item.LAYOUT_CENTER);
    b3.setDefaultCommand(new Command("OPTION","設定する", Command.OK,1));
    b3.setItemCommandListener(this);
    append(b3);
  }

  public void commandAction(Command cmd, Item itm) {
    System.out.println(cmd.getLabel());
    System.out.println(cmd.getLongLabel());
  }
}
