import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class ScoreCanvas extends GameCanvas 
                         implements Runnable, CommandListener {
  private int scene;
  private Dobon parent;
  private DobonCanvas dc;
  private Command c1, c2;
  public final int SCENETITLE  = 0;
  public final int SCENEGAME   = 1;
  public final int SCENESCORE  = 2;
  public final int SCENERESULT = 3;

  private Thread gameLoop = null;

  ScoreCanvas(Dobon p, DobonCanvas c) {
    super(true);
    scene = 0;
    parent = p;
    dc = c;

    try {
      c2 = new Command("TITLE", Command.SCREEN,1);
      addCommand(c2);

      c1 = new Command("戻る", Command.SCREEN,2);
      addCommand(c1);
      setCommandListener(this);

    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
  }

  public void startTh() {
    gameLoop = new Thread(this);
    gameLoop.start();

    Display.getDisplay(parent).setCurrent(this);
  }

  public void run() {
    Graphics g = getGraphics();
    long tm = System.currentTimeMillis();

    while (gameLoop != null) {
      if (scene != SCENERESULT) paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 50) gameLoop.sleep(50-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }

      if (scene != SCENERESULT) renew();
    }

    switch (scene) {
      case SCENEGAME   : parent.showGame();        break;
      case SCENETITLE  : parent.showMenu();        break;
      case SCENERESULT : dc.paintFinalResults(g);
                         flushGraphics();
                         break;
      default          : parent.notifyDestroyed(); break;
    }
  }

  public void paintFrame(Graphics g) {
    if (dc.flagDobon) dc.paintScoreDobon(g);
    else dc.paintScore(g);
    flushGraphics();
  }

  void renew() {
    if (dc.flagGameSet) {
      removeCommand(c1);
      c1 = new Command("結果", Command.SCREEN,1);
      addCommand(c1);
      removeCommand(c2);
      scene = SCENERESULT;
    }
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      if (c1.getLabel() == "戻る") {
        gameLoop = null;
        scene = SCENEGAME;
      }
      else if (c1.getLabel() == "結果") {
        gameLoop = null;
        c2 = new Command("TITLE",Command.SCREEN,1);
        addCommand(c2);
        removeCommand(c1);
      }
    }
    else if (c == c2) {
      for(int i=0; i<dc.players.length; i++) {
        dc.players[i].resetScore();
      }
      dc.resetTurnId();
      if (gameLoop != null) {
        gameLoop = null;
        scene = SCENETITLE;
      }
      else {
        c1 = new Command("戻る", Command.SCREEN,2);
        addCommand(c1);
        scene = SCENETITLE;
        parent.showMenu();
      }
    }
  }

  public void endProc() {
    gameLoop = null;
    System.gc();
  }
}
