import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class DataInput extends MIDlet {
  public DataInput() {
    DataInputForm f = new DataInputForm();
    Display.getDisplay(this).setCurrent(f);
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {}
}

class DataInputForm extends Form implements ItemCommandListener {
  String[] strJob={"勇者","魔法使い","神官","武闘家","盗賊"};
  String[] strSex={"男","女"};
  String[] strDet={"基本情報","パラメータ","装備、魔法"};
  ChoiceGroup c1, c2, c3;
  TextField t1;
  StringItem b1;

  DataInputForm() {
    super("キャラクター設定");

    append(new Spacer(240,5));
    t1 = new TextField("名前","",10,TextField.ANY);
    t1.setInitialInputMode("UCB_HIRAGANA");
    append(t1);

    append(new Spacer(240,5));
    c1 = new ChoiceGroup("職業",Choice.POPUP);
    for (int i=0; i<strJob.length; i++) {
      c1.append(strJob[i],null);
    }
    append(c1);

    append(new Spacer(240,5));
    c2 = new ChoiceGroup("性別",Choice.EXCLUSIVE);
    for (int i=0; i<strSex.length; i++) {
      c2.append(strSex[i],null);
    }
    append(c2);

    append(new Spacer(240,5));
    c3 = new ChoiceGroup("詳細設定",Choice.MULTIPLE);
    for (int i=0; i<strDet.length; i++) {
      c3.append(strDet[i],null);
    }
    append(c3);

    append(new Spacer(240,5));
    b1 = new StringItem("","OK",Item.BUTTON);
    b1.setLayout(Item.LAYOUT_RIGHT);
    b1.setDefaultCommand(new Command("OK",Command.OK,1));
    b1.setItemCommandListener(this);
    append(b1);
  }
  
  public void commandAction(Command cmd, Item itm) {
    if (!t1.getString().equals("")) {
      System.out.println("***入力された値の確認***");
      System.out.println("名前 : " + t1.getString());
      System.out.println("職業 : " + strJob[c1.getSelectedIndex()]);
      System.out.println("性別 : " + strSex[c2.getSelectedIndex()]);
      System.out.print("詳細 : ");
      for (int i=0; i<strDet.length; i++) {
        if (c3.isSelected(i)) {
          System.out.println(strDet[i] + " ");
        }
      }
      System.out.println("");
    }
  }
}
