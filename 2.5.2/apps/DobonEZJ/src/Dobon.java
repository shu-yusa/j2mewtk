import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class Dobon extends MIDlet {
  DobonCanvas dc = null;
  ScoreCanvas sc = null;
  LoadCanvas  lc = null;
  MenuCanvas msc = null;
  OptionForm  of = null;
  RuleCanvas  rc = null;

  public Dobon() {
    lc  = new LoadCanvas(this);
    Display.getDisplay(this).setCurrent(lc);
    dc  = new DobonCanvas(this);
    msc = new MenuCanvas(this);
    sc  = new ScoreCanvas(this, dc);
    of  = new OptionForm(this, dc);
    rc  = new RuleCanvas(this, dc);

    lc.endProc();

    showMenu();
  }
  public void startApp() {}
  public void pauseApp() {}
  public void destroyApp(boolean flg) {
    if (dc  != null) dc.endProc();
    if (msc != null) msc.endProc();
    if (sc  != null) sc.endProc();
    if (lc  != null) lc.endProc();
    if (rc  != null) rc.endProc();
  }

  void showMenu()   { msc.startTh();}
  void showGame()   { dc.startTh(); }
  void showScore()  { sc.startTh(); }
  void showRule()   { rc.startTh(); }
  void showOption() { Display.getDisplay(this).setCurrent(of); }
}

