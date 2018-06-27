import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.lang.Runtime;

public class HideCanvas extends GameCanvas implements Runnable, CommandListener {
  HideSeek parent;
  static int WIDTH;
  static int HEIGHT;
  static int LT;
  private int FontWidth;
  private int FontHeight;
  public static final int UP    = 0;
  public static final int RIGHT = 1;
  public static final int DOWN  = 2;
  public static final int LEFT  = 3;

  private final static long MAX_INTERVAL = 1000L;
  private final static int FPS = 24;
  private final static long PERIOD=(long)(1000.0/FPS);
  private long frmcntFPS;
  private int frmcntMsg;
  private long interval;
  private long prevTime;
  private float actualFPS;
  private int frmcnt;
  private boolean showInfo;
  private int partyNum;
  private Runtime rt;
  private Font fsmall;
  private MessageManager msgWin;
  private MenuManager menuWin;
  private WindowManager[] windows;
  private Command c1, c2;
  private Myself myself;
  private Nakama slime;
  private Map map;
  private Graphics g;
  private Map[] maps;
  private Sprite sp;
  private Myself[] party;
  private Image[] imgs;
  private Image[] cImg;
  private Image[] objImg;
  private Image[] mapImg;
  private String[] cname =
          {"/slimeknight.png","/slime.png","/slimebeth.png","/hoimislime.png",
           "/bubbleslime.png","/haguremetal.png","/kingslime.png","/rola.png",
           "/ryuka.png","/bianca.png", "babypanther.png","/hawkblizzard.png",
           "/odoruhoseki.png","/slimeknight_dq5.png"};
  private String[] objName =
          {"/flower_blue.png","/flower_red.png","jar.png",};
  private String[] mapName =
          {"/town01_a2-0.png","mymapchip1.png",};

  private Thread gameLoop = null;

  private long sleepTime;
  private long sleepTimeAve;
  private String infoSleep;
  private String err;

  HideCanvas(HideSeek p) {
    super(false);
    WIDTH     = getWidth();
    HEIGHT    = getHeight();
    LT        = Graphics.LEFT|Graphics.TOP;
    showInfo  = false;
    parent    = p;
    err       = "";

    cImg    = new Image[cname.length];
    mapImg  = new Image[mapName.length];
    objImg = new Image[objName.length];
    try {
      for (int i=0; i<cImg.length; i++) {
        cImg[i] = Image.createImage(cname[i]);
      }
      for (int i=0; i<mapImg.length; i++) {
        mapImg[i] = Image.createImage(mapName[i]);
      }
      for (int i=0; i<objName.length; i++) {
        objImg[i] = Image.createImage(objName[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }

    partyNum = 4;
    map = new Map(0, "/test.map", "/event.txt",0,0,parent, this);

    myself    = new Myself(cImg[8],"ﾘｭｶ",0,0,16,24,2,2,2,
                           DOWN,1, map, parent);
    myself.setStatus();
    myself.obtainItem(1);
    myself.obtainItem(2);
    myself.obtainItem(3);
    myself.obtainItem(4);
    myself.obtainItem(0);

    party = new Myself[partyNum];
    party[0] = myself;
    party[1] = new Nakama(cImg[13],"ﾋﾟｴｰﾙ",16,24,2,2,2,DOWN,1,map, parent, myself);
    party[2] = new Nakama(cImg[11],"ﾌﾞﾘｰﾄﾞ",16,24,2,2,2,DOWN,1,map, parent, myself);
    party[3] = new Nakama(cImg[12],"ｼﾞｭｴﾙ",16,24,2,2,2,DOWN,1,map, parent, myself);
//  party[1] = new Nakama(cImg[3],"ﾎｲﾐﾝ",24,32,2,2,2,DOWN,1,map, parent, myself);
//  party[2] = new Nakama(cImg[1],"ｽﾗｷﾁ",24,32,2,2,2,DOWN,1,map, parent, myself);
//  party[3] = new Nakama(cImg[5],"はぐりん",24,32,2,2,2,DOWN,1,map, parent, myself);
    map.addParty(party);
    for (int i=0; i<partyNum; i++) {
      party[i].setID(i);
    }

    frmcnt    = 0;
    frmcntMsg = 0;
    interval  = 0L;
    prevTime  = 0L;
    actualFPS = 0.0f;
    sleepTime = 0L;
    sleepTimeAve = 0L;
    infoSleep  = "";
    fsmall = Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL);

    windows = new WindowManager[2];
    windows[0] = menuWin = new MenuManager(this);
    windows[1] = msgWin  = new MessageManager(this);

    c1 = new Command("EXIT", Command.EXIT, 1);
    c2 = new Command("ﾒﾆｭｰ", Command.SCREEN, 1);
    addCommand(c1);
    addCommand(c2);
    setCommandListener(this);
    rt = Runtime.getRuntime();
  }

  void startTh() {
    gameLoop = new Thread(this);
    gameLoop.start();
    Display.getDisplay(parent).setCurrent(this);
  }

  Myself getMyself() {
    return myself;
  }

  Myself[] getParty() {
    return party;
  }

  Myself getPartyMember(int id) {
    return party[id];
  }

  Nakama getNakama() {
    return slime;
  }

  int getPartyNum() {
    return partyNum;
  }

  Image getCharaImage(int n) {
    return cImg[n];
  }

  Image getMapImage(int n) {
    return mapImg[n];
  }

  Image getObjImage(int n) {
    return objImg[n];
  }

  MessageManager getMsgWin() {
    return msgWin;
  }

  void changeMap(Map newMap) {
    map = newMap;
  }

  public void run() {
    g = getGraphics();
    g.setClip(0,0,WIDTH,HEIGHT);
    g.setFont(fsmall);
    long befTime   = System.currentTimeMillis();
    long aftTime   = 0L;
    long timeDiff  = 0L;
//  long sleepTime = 0L;
    long overSleep = 0L;
    prevTime = befTime;

    while (gameLoop != null) {
      paintFrame(g);

      try {
        aftTime = System.currentTimeMillis();
        timeDiff = aftTime - befTime;
        sleepTime = (PERIOD - timeDiff) - overSleep;
        if (sleepTime > 0) {
          gameLoop.sleep(sleepTime);
          overSleep = System.currentTimeMillis()  - (aftTime + sleepTime);
        }
        else {
          overSleep = 0L;
        }
        befTime = System.currentTimeMillis();
      }
      catch (Exception e) { 
        e.printStackTrace();
        break;
      }

      renew();
    }

    parent.notifyDestroyed();
  }

  void paintFrame(Graphics g) {
    map.setPaintOrder();
    map.drawMap(g);
    for (int i=0; i<windows.length; i++) {
      windows[i].draw(g);
    }
    if (showInfo) drawInfo(g);

    flushGraphics();
  }

  void renew() {
    int ks = getKeyStates();
    boolean open=false;

    frmcnt++; 
    if (frmcnt%8 == 0 && !menuWin.isVisible()) {
      map.stepCharas();
    }

    for (int i=0; i<windows.length; i++) {
      open |= windows[i].isVisible();
    }

    if (!open) {
      map.moveSubCharas();
      if (naname(ks)) {
        for (int i=1; i<party.length; i++) {
          ((Nakama)party[i]).move();
        }
        moveMyself(ks);
      }
      else {
        moveMyself(ks);
        if (ks != 0) {
          for (int i=1; i<party.length; i++) {
            ((Nakama)party[i]).move();
          }
        }
      }
    }
    calcFPS();
  }

  boolean naname(int ks) {
    return ((((ks & RIGHT_PRESSED) != 0) &&
              (ks & UP_PRESSED)    != 0) ||
            (((ks & RIGHT_PRESSED) != 0) &&
              (ks & DOWN_PRESSED)  != 0) ||
            (((ks & LEFT_PRESSED)  != 0) &&
              (ks & UP_PRESSED)    != 0) ||
            (((ks & LEFT_PRESSED)  != 0) &&
              (ks & DOWN_PRESSED)  != 0));
  }

  void drawInfo(Graphics g) {
    g.setColor(255,255,255);
    String info = "FPS:"+(int)actualFPS+"."+((int)(actualFPS*10)%10);
    g.drawString(info, 0, 0, LT);
    info = "Mem:"+((rt.totalMemory()-rt.freeMemory())/1000)
                 +"k/"+(rt.totalMemory()/1000)+"k";
    g.drawString(info,0,15,LT);
    g.drawString("("+myself.getX()/map.CS+","
                    +myself.getY()/map.CS+")",0,30,LT);
    sleepTimeAve += sleepTime;
    if (frmcnt%10 == 0) {
      sleepTimeAve /= 10;
      infoSleep = sleepTimeAve+"ms";
    }
    g.drawString(infoSleep,0,45,LT);
//  g.drawString("w:"+WIDTH+",h:"+HEIGHT,0,60,LT);
//  g.drawString(""+fsmall.stringWidth("A")+","+fsmall.stringWidth("あ"),0,75,LT);
//  g.drawString(""+fsmall.getHeight(),0,90,LT);
  }

  private void moveMyself(int ks) {
    if ((ks & UP_PRESSED) != 0) {
      myself.move(UP);
//    if (myself.getY() < 0) {
//      myself.move(DOWN);
//      myself.setDirection(UP);
//    }
    }
    if ((ks & RIGHT_PRESSED) != 0) {
      myself.move(RIGHT);
    }
    if ((ks & DOWN_PRESSED) != 0) {
      myself.move(DOWN);
    }
    if ((ks & LEFT_PRESSED) != 0) {
      myself.move(LEFT);
//    if (myself.getX() < 0) {
//      myself.move(RIGHT);
//      myself.setDirection(LEFT);
//    }
    }
  }

  private void calcFPS() {
    frmcntFPS++;
    interval += PERIOD;

    if (interval >= MAX_INTERVAL) {
      actualFPS = ((float) frmcntFPS/(System.currentTimeMillis()-prevTime))*1000L;

      frmcntFPS = 0L;
      interval  = 0L;
      prevTime  = System.currentTimeMillis();

    }
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      if (c.getLabel() == "EXIT") {
        gameLoop = null;
      }
    }
    else if (c == c2) {
      if (c.getLabel() == "gc") {
        System.gc();
      }
      else if (c.getLabel() == "ﾒﾆｭｰ" && !msgWin.isVisible()) {
        removeCommand(c2);
        c2 = new Command("戻る", Command.BACK, 1);
        addCommand(c2);
        menuWin.show();
      }
      else if (c.getLabel() == "戻る") {
        removeCommand(c2);
        c2 = new Command("ﾒﾆｭｰ",Command.SCREEN,1);
        addCommand(c2);
        menuWin.hide();
        msgWin.hide();
      }
    }
  }

  protected void keyPressed(int keyCode) {
    int gameAction = getGameAction(keyCode);
    switch (keyCode) {
      case KEY_STAR:
        showInfo = !showInfo;
        return;
      case KEY_POUND:
        System.gc();
        return;
      default:
        break;
    }

    if (msgWin.isVisible()) {
      keyActionWithMsg(keyCode, gameAction);
    }
    else if (menuWin.isVisible()) {
      keyActionWithMenu(keyCode, gameAction);
    }
    else {
      keyAction(keyCode, gameAction);
    }
  }

  private void keyActionWithMenu(int keyCode, int gameAction) {
    switch (keyCode) {
      case KEY_NUM2:
        menuWin.back();
        if (menuWin.getDepth() == 0) {
          removeCommand(c2);
          c2 = new Command("ﾒﾆｭｰ",Command.SCREEN,1);
          addCommand(c2);
        }
        break;
      default:
        switch (gameAction) {
          case Canvas.DOWN:
            menuWin.selectNext();
            break;
          case Canvas.UP:
            menuWin.selectPrev();
            break;
          case Canvas.FIRE:
            menuWin.firePressed();
            break;
          case Canvas.LEFT:
            menuWin.nextPage();
            break;
          case Canvas.RIGHT:
            menuWin.prevPage();
            break;
          default:
            break;
        }
        break;
    }
  }

  private void keyActionWithMsg(int keyCode, int gameAction) {
    switch (keyCode) {
      case KEY_NUM2:
        msgWin.hide();
        break;
      case KEY_NUM4:
        break;
      case KEY_NUM6:
        break;
      case KEY_NUM8:
        break;
      default:
        switch (gameAction) {
          case FIRE:
        //  msgWin.hide();
            msgWin.scrollMessage();
            break;
          case UP:
            break;
          default:
        }
        break;
    }
  }

  private void keyAction(int keyCode, int gameAction) {
    switch (keyCode) {
      case KEY_NUM2:
        break;
      case KEY_NUM4:
        break;
      case KEY_NUM6:
        break;
      case KEY_NUM8:
        break;
      default:
        switch (gameAction) {
          case FIRE:
            Chara talker;
            if ((talker=myself.talkWith(map.getSubCharas())) != null) {
              talker.setDirection(myself.getOppositeDirection());
//            talker.renewFrame();
//            if (sp.getY() + sp.getHeight() > (int)(HEIGHT*0.8)) {
              if (map.HEIGHT-myself.getY() < (int)(HEIGHT*0.2)) {
                msgWin.setY(HEIGHT/2-msgWin.getHeight());
              }
              else {
                msgWin.setY((int)(HEIGHT*0.8));
              }
              msgWin.setMessage(talker.getMessage());
              msgWin.show();
            }
            else {
              myself.searchItem();
            }
            break;
          default: 
            break;
        }
    }
  }

  public void notifyDestroyed() {
    parent.notifyDestroyed();
  }

  protected void keyReleased(int keyCode) {
    if (menuWin.isVisible() && menuWin.getDepth() == 0) {
      menuWin.hide();
    }
  }

  protected void keyRepeated(int keyCode) {
  }

  void endProc() {
    gameLoop = null;
    System.gc();
  }
}

