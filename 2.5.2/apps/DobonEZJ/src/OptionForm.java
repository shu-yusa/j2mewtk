import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

class OptionForm extends Form implements CommandListener, 
                                         ItemCommandListener {
  String[] strScore = {"100", "500", "1000", "2000", "5000", "10000",};
  String[] strCPU = {"1", "2", "3",};
  String[] strVib = {"ON", "OFF"};
  String[] strSE = strVib;
  ChoiceGroup c1, c2, c3, c4;
  TextField t1, t2, t3;
  StringItem b1;
  Dobon parent;
  DobonCanvas dc;
  Command cmd1, cmd2;

  public OptionForm(Dobon p, DobonCanvas c) {
    super("オプション設定");
    dc = c;
    parent = p;

    String strName      = "You";
    int iniScore        = 1000;
    int numCPU          = 3;
    String currentVib   = "ON";
    String currentSound = "ON";
    // 現在の設定値を取得
    try {
      RecordStore rs = RecordStore.openRecordStore("DobonOption",false);
      byte[] b = rs.getRecord(1);
      rs.closeRecordStore();
      String str = new String(b);
      int sp1   = str.indexOf((int)',');
      int sp2   = str.indexOf((int)',',sp1+1);
      int sp3   = str.indexOf((int)',',sp2+1);
      int sp4   = str.indexOf((int)',',sp3+1);
      strName   = str.substring(0,sp1);
      String currentScore  = str.substring(sp1+1,sp2);
      String currentCPU    = str.substring(sp2+1,sp3);
      currentVib           = str.substring(sp3+1,sp4);
      currentSound         = str.substring(sp4+1,str.length());

      numCPU   = Integer.parseInt(currentCPU);
      iniScore = Integer.parseInt(currentScore);
    }
    catch (RecordStoreNotFoundException e) {
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }

    append(new Spacer(240,5));
    t1 = new TextField("名前", strName, 10, TextField.ANY);
    append(t1);

    append(new Spacer(240,5));
    c1 = new ChoiceGroup("初期スコア", Choice.POPUP, strScore, null);
    for (int i=0; i<strScore.length; i++) {
      if (iniScore == Integer.parseInt(strScore[i])) {
        c1.setSelectedIndex(i, true);
      }
    }
    append(c1);

    append(new Spacer(240,5));
    c2 = new ChoiceGroup("コンピュータ人数", Choice.POPUP, strCPU, null);
    c2.setSelectedIndex(numCPU-1, true);
    append(c2);

    append(new Spacer(240,5));
    c3 = new ChoiceGroup("バイブレーション機能", Choice.POPUP, strVib, null);
    if (currentVib.equals("ON")) {
      c3.setSelectedIndex(0, true);
    }
    else if (currentVib.equals("OFF")) {
      c3.setSelectedIndex(1, true);
    }
    append(c3);

    append(new Spacer(240,5));
    c4 = new ChoiceGroup("効果音", Choice.POPUP, strSE, null);
    if (currentSound.equals("ON")) {
      c4.setSelectedIndex(0, true);
    }
    else if (currentSound.equals("OFF")) {
      c4.setSelectedIndex(1, true);
    }
    append(c4);

    cmd1 = new Command("戻る",Command.BACK,1);
    addCommand(cmd1);

    cmd2 = new Command("OK",Command.OK,1);
    addCommand(cmd2);
    setCommandListener(this);
  }

  void recording() {
    String str = t1.getString();
    if (str.equals("")) {
      str = " , , , , ";
    }
    else {
      String strNewScore  = strScore[c1.getSelectedIndex()];
      String strNewCPUNum = strCPU[c2.getSelectedIndex()];
      String strNewVib    = strVib[c3.getSelectedIndex()];
      String strNewSound  = strSE[c4.getSelectedIndex()];
      int newScore  = Integer.parseInt(strNewScore);
      int newCPUNum = Integer.parseInt(strNewCPUNum);

      dc.generateComPlayers(str, newScore, newCPUNum);
      dc.setVib(strNewVib.equals("ON"));
      dc.setSound(strNewSound.equals("ON"));
      str += "," + strNewScore + "," + strNewCPUNum
           + "," + strNewVib   + "," + strNewSound;
    }

    try {
      RecordStore rs = RecordStore.openRecordStore("DobonOption",true);
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

  public void commandAction(Command cmd, Displayable d) {
    if (cmd == cmd1) {
      parent.showMenu();
    }
    else if (cmd == cmd2) {
      recording();
    }
  }
  
  public void commandAction(Command cmd, Item itm) {
  }
}
//  append(new Spacer(240,5));
//  b1 = new StringItem("","OK",Item.BUTTON);
//  b1.setLayout(Item.LAYOUT_RIGHT);
//  b1.setDefaultCommand(new Command("OK",Command.OK,1));
//  b1.setItemCommandListener(this);
//  append(b1);

