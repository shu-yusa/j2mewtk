import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class DataReadCanvas extends Canvas implements CommandListener {
  String strName;
  String strJob;
  String strSex;
  String strDet;
  Command cmd;
  DataAccess parent;

  public DataReadCanvas(DataAccess p) {
    strName = "";
    strJob  = "";
    strSex  = "";
    strDet  = "";

    parent = p;

    cmd = new Command("戻る",Command.BACK,1);
    addCommand(cmd);
    setCommandListener(this);
  }

  public void commandAction(Command c, Displayable d) {
    if (c == cmd) {
      parent.showMenu();
    }
  }

  public void show() {
    try {
      RecordStore rs = RecordStore.openRecordStore("CharacterData",false);
      byte[] b = rs.getRecord(1);
      rs.closeRecordStore();
      String str = new String(b,"UTF-8");
      int sp1 = str.indexOf((int)',');
      int sp2 = str.indexOf((int)',',sp1+1);
      int sp3 = str.indexOf((int)',',sp2+1);
      strName = str.substring(0,sp1);
      strJob = str.substring(sp1+1,sp2);
      strSex = str.substring(sp2+1,sp3);
      strDet = str.substring(sp3+1,str.length());
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    repaint();
  }

  protected void paint(Graphics g) {
    g.setColor(0x00ffffff);
    g.fillRect(0,0,getWidth(),getHeight());

    g.setColor(0x00000000);

    g.drawString("キャラクタープロフィール",30,50,Graphics.LEFT|Graphics.TOP);
    g.drawString("名前: " + strName,40,80,Graphics.LEFT|Graphics.TOP);
    g.drawString("職業: " + strJob, 40,100,Graphics.LEFT|Graphics.TOP);
    g.drawString("性別: " + strSex, 40,120,Graphics.LEFT|Graphics.TOP);
    g.drawString("特長: " + strDet, 40,140,Graphics.LEFT|Graphics.TOP);
  }
}
