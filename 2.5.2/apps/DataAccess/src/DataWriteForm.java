import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

class DataWriteForm extends Form implements CommandListener, 
                                        ItemCommandListener {
  String[] strJob={"勇者","魔法使い","神官","武闘家","盗賊"};
  String[] strSex={"男","女"};
  ChoiceGroup c1, c2;
  TextField t1, t2;
  StringItem b1;
  DataAccess parent;
  Command cmd1;

  public DataWriteForm(DataAccess p) {
    super("キャラクター設定");
    parent = p;

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
    t2 = new TextField("特長","",10,TextField.ANY);
    t2.setInitialInputMode("UCB_HIRAGANA");
    append(t2);

    append(new Spacer(240,5));
    b1 = new StringItem("","OK",Item.BUTTON);
    b1.setLayout(Item.LAYOUT_RIGHT);
    b1.setDefaultCommand(new Command("OK",Command.OK,1));
    b1.setItemCommandListener(this);
    append(b1);

    cmd1 = new Command("戻る",Command.BACK,1);
    addCommand(cmd1);
    setCommandListener(this);
  }

  public void commandAction(Command cmd, Displayable d) {
    if (cmd == cmd1) {
      parent.showMenu();
    }
  }
  
  public void commandAction(Command cmd, Item itm) {
    String str = t1.getString();
    if (str.equals("")) {
      str = " , , , ";
    }
    else {
      str += ","
        + strJob[c1.getSelectedIndex()]
        + ","
        + strSex[c2.getSelectedIndex()]
        + ","
        + t2.getString();
    }

    try {
      RecordStore rs = RecordStore.openRecordStore("CharacterData",true);
      byte[] b = str.getBytes();
      if (rs.getNumRecords() == 0) {
        rs.addRecord(b,0,b.length);
      }
      else {
        rs.setRecord(1,b,0,b.length);
      }
      rs.closeRecordStore();

      parent.showMenu();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
