import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.*;

import java.util.Random;

public class DobonCanvas extends GameCanvas implements Runnable, CommandListener {
  private Dobon parent = null;
  private Thread gameLoop = null;
  private Command c1, c2;
  private Random rand;
  private Font f0, f1;
  private int[] deck;                       // デッキのカード
  private int restDeck;                     // 山札のカード
  private int keyState;
  private int frmcnt;
  private int frmCpuWait;                   // コンピュータプレイヤーのフレーム待ち時間
  private int[] cpuDobonFrm;                // コンピュータがドボンするフレーム
  private int[] paintOrder;                 // カードを描画する順番
  private int numDoboned;                   // 既にトボンした人の数, paintOrderの設定に使う
  private int numDobon;                     // ドボン可能な人数
  private int multiScore;
  private int winnerId, turnId, loserId;
  private int tmpWinner;
  private int tmpLoser ;
  private String info1, info2;              // info2は2に関する情報
  private boolean cpuReDobonAble;
  private boolean vib_on;
  private boolean sound_on;
  boolean flag2;
  boolean flagDobon;
  boolean flagOver;
  boolean flagGameSet;
  boolean flagStopping;
  int WIDTH;
  int HEIGHT;
  int scene;
  int multiDraw;
  int fieldCard;
  int fieldSuit;
  int numTrash;
  int[] trash;
  int volume;
  DobonPlayer[] players;
  PlayerCPU[] CPU;
  PlayerHuman human;
  Image[] img, img2;
  Image   img3;
  Player se, se2, se3;
  Sprite suit;
  private final static int frmHumanWait = 500;
  private final static int maxVolume = 50;
  private final int SCENETITLE = 0;
  private final int SCENEGAME  = 1;
  private final int SCENESCORE = 2;

  DobonCanvas(Dobon p) {
    super(true);
    f0 = Font.getDefaultFont();
    f1 = Font.getFont(Font.FACE_MONOSPACE,Font.STYLE_BOLD,Font.SIZE_LARGE);
    WIDTH          = getWidth();
    HEIGHT         = getHeight();
    parent         = p;
    flag2          = false;
    flagDobon      = false;
    flagOver       = false;
    flagGameSet    = false;
    flagStopping   = false;
    cpuReDobonAble = false;
    vib_on         = true;
    sound_on       = true;
    volume         = 0;
    keyState       = 0;
    frmcnt         = 0;
    scene          = 0; 
    winnerId       = 0;
    loserId        = 0;
    turnId         = 0;
    multiDraw      = 0;
    numTrash       = 0;
    numDobon       = 0;
    numDoboned     = 0;
    tmpWinner      = -1;
    tmpLoser       = -1;
    multiScore     = 1;
    frmCpuWait     = 15;
    restDeck       = 52;
    info1          = "";
    info2          = "";
    cpuDobonFrm    = new int[]{-1,-1,-1};
    rand           = new Random();
    deck           = new int[52];
    trash          = new int[52];
    img            = new Image[53];
    img2           = new Image[53];

    // リソースの取得
    try {
      String[] num = {"A","2","3","4","5","6","7","8","9","10","J","Q","K",};
      for (int i=0; i<num.length; i++) {
        img[i]    = Image.createImage("/spade" + num[i] + ".png");
        img[i+13] = Image.createImage("/heart" + num[i] + ".png");
        img[i+26] = Image.createImage("/dia"   + num[i] + ".png");
        img[i+39] = Image.createImage("/club"  + num[i] + ".png");
      }

      for (int i=0; i<num.length; i++) {
        img2[i]    = Image.createImage("/mini/S" + (i+1) + ".png");
        img2[i+13] = Image.createImage("/mini/H" + (i+1) + ".png");
        img2[i+26] = Image.createImage("/mini/D" + (i+1) + ".png");
        img2[i+39] = Image.createImage("/mini/C" + (i+1) + ".png");
      }

      img2[52] = Image.createImage("/mini/Rev"+ (rand.nextInt(9)+1) + ".png");
//    if (WIDTH == 480) {
//      img2 = null;
//      for (int i=0; i<img.length; i++) {
//        img2[i] = img[i];
//      }
//    }
      img3 = Image.createImage("/Dobon.png");
      suit = new Sprite(img3, 12, 12);

      InputStream is = getClass().getResourceAsStream("se.wav");
      se = Manager.createPlayer(is, "audio/X-wav");
      is = getClass().getResourceAsStream("se2.wav");
      se2 = Manager.createPlayer(is, "audio/X-wav");
      is = getClass().getResourceAsStream("se3.wav");
      se3 = Manager.createPlayer(is, "audio/X-wav");

      c1 = new Command("TITLE", Command.SCREEN,1);
      addCommand(c1);
      setCommandListener(this);

      // c2はオブジェクトの生成だけしておく
      c2 = new Command("", Command.SCREEN,2);
      removeCommand(c2);
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }

    String strName = "You";
    int numCPU     = 3;
    int iniScore   = 1000;

    // メモリデータへのアクセス
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
      String strScore  = str.substring(sp1+1,sp2);
      String strCPU    = str.substring(sp2+1,sp3);
      String strVib    = str.substring(sp3+1,sp4);
      String strSound  = str.substring(sp4+1,str.length());
      if (strVib == "OFF")   vib_on   = false;
      if (strSound == "OFF") sound_on = false;

      numCPU   = Integer.parseInt(strCPU);
      iniScore = Integer.parseInt(strScore);
    }
    catch (RecordStoreNotFoundException e) {
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
    // プレイヤーオブジェクトの生成
    generateComPlayers(strName,iniScore,numCPU);
  }

  void generateComPlayers(String name, int score, int n) {
    CPU        = null;
    players    = null;
    paintOrder = null;
    CPU        = new PlayerCPU[n];
    players    = new DobonPlayer[CPU.length+1];
    players[0] = human  = new PlayerHuman(name,0,score,this);
    for (int i=0; i<CPU.length; i++) {
      players[i+1] = CPU[i] = new PlayerCPU("com"+(i+1),i+1,score,this);
    }
    paintOrder     = new int[players.length];

    System.gc();
  }

  void setVib(boolean state) {
    vib_on = state;
  }

  void setSound(boolean state) {
    sound_on = state;
  }

  int getVolume() {
    return volume;
  }

  void startTh() {
    gameLoop = new Thread(this);
    volume = 0;
    init();
    if (sound_on) volume = maxVolume;
    gameLoop.start();

    Display.getDisplay(parent).setCurrent(this);
  }

  public void run() {
    Graphics g = getGraphics();
    g.setClip(0,0,WIDTH,HEIGHT);
    long tm = System.currentTimeMillis();

    while (gameLoop != null) {
      paintFrame(g);

      try {
        tm = System.currentTimeMillis() - tm;
        if (tm < 40) gameLoop.sleep(40-tm);
        tm = System.currentTimeMillis();
      }
      catch (Exception e) {
        e.printStackTrace();
        break;
      }
      if (!flagOver) renew();
    }

    g.setColor(153,255,102);
    g.fillRect(0,0,getWidth(),getHeight());
    flushGraphics();
    switch (scene) {
      case SCENESCORE : parent.showScore(); break;
      case SCENETITLE : parent.showMenu();  break;
      default         : parent.notifyDestroyed(); break;
    }
  }

  void paintFrame(Graphics g) {
    g.setColor(153,255,102);
    g.fillRect(0,0,WIDTH,HEIGHT);

    // information
    if (flag2) info2 = "2x"+multiDraw/2+ " = " + multiDraw;
    else info2 = "";
    g.setColor(0,0,0);
    g.drawString(info1,WIDTH/2,HEIGHT*11/20,Graphics.HCENTER|Graphics.TOP);
    g.drawString(info2,   WIDTH/11, HEIGHT/6,Graphics.LEFT|Graphics.TOP);
    g.drawString("suit:", WIDTH/11, HEIGHT/11,Graphics.LEFT|Graphics.TOP);
    suit.setFrame(fieldSuit);
    int fw = f0.stringWidth("suit:");
//  suit.setPosition(WIDTH/11+(fw*3)/2,HEIGHT/11+2);  // 21+28*3/2 = 63, 24+2=26
    suit.setPosition(WIDTH/11+fw+10,HEIGHT/11+2);  // 21+28*3/2 = 63, 24+2=26
    suit.paint(g);
    if (turnId == 0 && !flagOver) {
//    g.drawString("your turn",(WIDTH*42)/100,HEIGHT/11,Graphics.LEFT|Graphics.TOP);
      g.drawString("your turn",(WIDTH*58)/100,HEIGHT/11,Graphics.LEFT|Graphics.TOP);
    }
    // 場札の描画
    if (numTrash != 0) {
      g.drawImage(img[trash[numTrash-1]],WIDTH/2-24,HEIGHT/2+9,
                  Graphics.HCENTER|Graphics.BOTTOM);
    }
    g.drawImage(img[fieldCard],WIDTH/2,HEIGHT/2,Graphics.HCENTER|Graphics.BOTTOM);

    // 手札の描画
    for (int i=0; i<players.length; i++) {
      players[paintOrder[i]].paintMyCards(g);
    }

    flushGraphics();
  }

  void renew() {
    int ks = getKeyStates();

    // カード選択
    if (((ks & RIGHT_PRESSED) != 0) && ((keyState & RIGHT_PRESSED) == 0)) {
      if (human.getChoosedCard()+1 < human.getNumCards()) {
        human.setChoosedCard(human.getChoosedCard()+1);
      }
      else if (human.getChoosedCard()+1 == human.getNumCards()) {
        human.setChoosedCard(0);
      }
      info1 = "";
    }
    
    if (((ks & LEFT_PRESSED) != 0) && ((keyState & LEFT_PRESSED) == 0)) {
      if (human.getChoosedCard() > 0) {
        human.setChoosedCard(human.getChoosedCard()-1);
      }
      else if (human.getChoosedCard() == 0) {
        human.setChoosedCard(human.getNumCards()-1);
      }
      info1 = "";
    }

    // 各ターンでの処理
    if (turnId == 0) {
      frmcnt++;
      if (numDobon == 0 || 
         (numDobon == 1 && human.dobonAble && tmpWinner == -1)) {
        if (!flagStopping) humanTurn(ks);
      }
    }
    else {
      if (frmcnt++ > frmCpuWait) {
        removeCommand(c2);
        if (CPU[turnId-1].myTurn()) dobonCheckAfterCpu();
        frmcnt = 0;
        if (flagStopping = (CPU[turnId-1].getNumCards() == 0)) {
          if (numDobon == 0) finalize(turnId);
          else frmCpuWait = frmHumanWait;
        }
        if (turnId == CPU.length) turnId = 0;
        else turnId++;
      }
    }

    cpuDobon();
    // ユーザーがなかなかキーを押さないときは自動的にドボン
    if ((human.dobonAble||human.reDobonAble) && frmcnt == frmHumanWait) {
      humanDobon();
    }

    keyState = ks;
  }

  void humanDobon() {
    multiScore *= 2;
    human.dobon();
    swapPaintOrder(numDoboned++, 0);
    if (tmpLoser == -1) {
      tmpLoser = (turnId == 0 ? CPU.length : turnId-1);
    }
    else {
      tmpLoser = tmpWinner;
    }
    tmpWinner = 0;
    if (--numDobon == 0 && !cpuReDobonAble) {
      winnerId  = 0;
      loserId   = tmpLoser;
      finalizeDobon(0,loserId);
    }
  }

  void cpuDobon() {
    for (int i=0; i<CPU.length; i++) {
      // ドボンのタイミングが来たら処理
      if (cpuDobonFrm[i] == frmcnt && CPU[i].dobonAble) {
        // ドボンするのが1人目だった場合
        if (tmpLoser == -1) {
//        frmCpuWait = 50;
          // ドボンされた人の特定、tmpLoserになる
          if (turnId == 0) {
            // ドボンされたカードが自分のカードがcom3のカードか判断
            tmpLoser = (human.flag8 ? 0 : CPU.length);
          }
          else {
            tmpLoser = turnId - 1;
          }
          // ドボンした人
          tmpWinner = i+1;
          // comがドボン返し可能なとき、ドボンできるようにする
          if (cpuReDobonAble) {
            CPU[tmpLoser-1].dobonAble = true;
            numDobon++;
            cpuDobonFrm[tmpLoser-1] += frmcnt;
            cpuReDobonAble = false;
          }
          // プレイヤーがドボン返し可能なとき、ドボンできるようにする
          if (human.reDobonAble) {
            frmCpuWait = frmHumanWait;
            numDobon++;
            removeCommand(c2);
            c2 = new Command("ドボン", Command.SCREEN,2);
            addCommand(c2);
          }
          // プレイヤーが通常ドボン可能なとき、待ち時間の延長
          if (human.dobonAble) {
            frmCpuWait = frmHumanWait;
          }
        }
        // ドボン二人目以降
        else {
          tmpLoser  = tmpWinner;
          tmpWinner = i+1;
        }
        CPU[i].dobon();
        multiScore *= 2;
        numDobon--;
        // 全員のドボンが終了
        if (numDobon == 0) {
          winnerId  = tmpWinner;
          loserId   = tmpLoser;
          finalizeDobon(winnerId,loserId);
        }
        swapPaintOrder(numDoboned++, i+1);
        if (tmpLoser == 0 && vib_on) Display.getDisplay(parent).vibrate(300);
      }
    }
  }

  void swapPaintOrder(int numD, int id) {
    int tmp;
    for (int k=numD; k<players.length; k++) {
      if (paintOrder[k] == id) {
        tmp = paintOrder[numD];
        paintOrder[numD] = id;
        paintOrder[k] = tmp;
        break;
      }
    }
  }

  // case II
  void dobonCheckAfterHuman() {
    int[] cpuDobonId = new int[]{-1,-1,-1};
    int[] orderId = new int[]{0,1,2};
    int tmp, r;

    // ドボンをスルーしたとき
    if (human.dobonAble) {
      removeCommand(c2);
      human.dobonAble = false;
    }
    numDobon = 0;
    for (int i=0; i<CPU.length; i++) {
      CPU[i].dobonAble = false;
      if (CPU[i].dobonCheck()) {
        frmCpuWait = 50;
        CPU[i].dobonAble = true;
        cpuDobonId[numDobon++] = i;
      }
    }
    // case II-AB
    switch (numDobon) {
      // case II-AB-1
      case 1: cpuDobonFrm[cpuDobonId[0]] = 5;
              break; 
      // case II-AB-2
      case 2: if (rand.nextInt(2) == 0) {
                cpuDobonFrm[cpuDobonId[0]] = 15 ;
                cpuDobonFrm[cpuDobonId[1]] = 30;
              }
              else {
                cpuDobonFrm[cpuDobonId[1]] = 15;
                cpuDobonFrm[cpuDobonId[0]] = 30;
              }
              break;
      // case II-AB-3
      case 3: for (int i=0; i<orderId.length; i++) {
                r = rand.nextInt(orderId.length-i) + i;
                tmp = orderId[i];
                orderId[i] = orderId[r];
                orderId[r] = tmp;
              }
              cpuDobonFrm[cpuDobonId[orderId[0]]] = 10;
              cpuDobonFrm[cpuDobonId[orderId[1]]] = 20;
              cpuDobonFrm[cpuDobonId[orderId[2]]] = 30;
              break;
    }
    human.reDobonAble = human.dobonCheck();
  }

  // ドボンできる人の特定や、ドボンの順番、タイミングの設定
  // comが出した後
  // case I
  void dobonCheckAfterCpu() {

    int rId = CPU.length;
    int[] cpuDobonId = new int[rId];
    // cpuDobonId[rId-1] はドボン返しする人のID
    // 残りは通常ドボンする人のID
    for (int i=0; i<rId; i++) {
      cpuDobonId[i] = -1;
    }

    human.reDobonAble = false;
    numDobon = 0;
    cpuReDobonAble     = false;
    // comがドボン可能か、ドボン返し可能か判断
    for (int i=0; i<CPU.length; i++) {
      CPU[i].reDobonAble = false;
      CPU[i].dobonAble   = false;
      if (CPU[i].dobonCheck()) {
        if (i == turnId-1) {          // ドボン返し可能
          CPU[i].dobonAble = false;
          CPU[i].reDobonAble = true;
          cpuReDobonAble = true;
          cpuDobonId[rId-1] = i;
        }
        else {                      // ドボン可能
          frmCpuWait = 50;
          CPU[i].dobonAble = true;
          cpuDobonId[numDobon++] = i;
        }
      }
    }
    // case I-A
    if (human.dobonCheck()) {
      human.dobonAble = true;
      frmCpuWait = 50;
      numDobon++; 
      // ドボン可能人数のチェック
      if (!cpuReDobonAble) {
        switch (numDobon) {
          // case I-A-1
          case 1: break;
          // case I-A-2
          case 2: cpuDobonFrm[cpuDobonId[0]] = 15;
                  break;
          // case I-A-3
          case 3: if (rand.nextInt(2) == 0) {
                    cpuDobonFrm[cpuDobonId[0]] = 15;
                    cpuDobonFrm[cpuDobonId[1]] = 30;
                  }
                  else {
                    cpuDobonFrm[cpuDobonId[1]] = 15;
                    cpuDobonFrm[cpuDobonId[0]] = 30;
                  }
                  break;
        }
      }
      else {
        switch (numDobon) {
          // case I-A-4 cpuDobonId[2]はドボン返しするプレイヤー
          case 1: cpuDobonFrm[cpuDobonId[rId-1]] = 5;
                  break;
          // case I-A-5
          case 2: cpuDobonFrm[cpuDobonId[0]] = 15; // 10もあり
                  cpuDobonFrm[cpuDobonId[rId-1]] = 5;
                  break;
          // case I-A-6
          case 3: if (rand.nextInt(2) == 0) {
                    cpuDobonFrm[cpuDobonId[0]] = 10;
                    if (rand.nextInt(2) == 0) {
                      cpuDobonFrm[cpuDobonId[1]] = 20;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 20;
                    }
                    else {
                      cpuDobonFrm[cpuDobonId[1]] = 30;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 10;
                    }
                  }
                  else {
                    cpuDobonFrm[cpuDobonId[1]] = 10;
                    if (rand.nextInt(2) == 0) {
                      cpuDobonFrm[cpuDobonId[0]] = 20;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 20;
                    }
                    else {
                      cpuDobonFrm[cpuDobonId[0]] = 30;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 10;
                    }
                  }
                  break;
        }
      }
      c2 = new Command("ドボン", Command.SCREEN,2);
      addCommand(c2);
    }
    // Case I-B
    else {
      human.dobonAble = false;
      if (!cpuReDobonAble) {
        switch (numDobon) {
          // case I-B-1
          case 1: cpuDobonFrm[cpuDobonId[0]] = 10;
                  break;
          // case I-B-2
          case 2: if (rand.nextInt(2) == 0) {
                    cpuDobonFrm[cpuDobonId[0]] = 10;
                    cpuDobonFrm[cpuDobonId[1]] = 20;
                  }
                  else {
                    cpuDobonFrm[cpuDobonId[1]] = 10;
                    cpuDobonFrm[cpuDobonId[0]] = 20;
                  }
                  break;
        }
      }
      else {
        switch (numDobon) {
          // case I-B-3
          case 1: cpuDobonFrm[cpuDobonId[0]] = 10;
                  cpuDobonFrm[cpuDobonId[rId-1]] = 10;
                  break;
          // case I-B-4
          case 2: if (rand.nextInt(2) == 0) {
                    cpuDobonFrm[cpuDobonId[0]] = 10;
                    if (rand.nextInt(2) == 0) {
                      cpuDobonFrm[cpuDobonId[1]] = 20;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 20;
                    }
                    else {
                      cpuDobonFrm[cpuDobonId[rId-1]] = 10;
                      cpuDobonFrm[cpuDobonId[1]] = 30;
                    }
                  }
                  else {
                    cpuDobonFrm[cpuDobonId[1]] = 10;
                    if (rand.nextInt(2) == 0) {
                      cpuDobonFrm[cpuDobonId[0]] = 20;
                      cpuDobonFrm[cpuDobonId[rId-1]] = 20;
                    }
                    else {
                      cpuDobonFrm[cpuDobonId[rId-1]] = 10;
                      cpuDobonFrm[cpuDobonId[0]] = 30;
                    }
                  }
                  break;
        }
      }
    }
  }

  void humanTurn(int ks) {
    int[] temp = new int[2];

    if (!human.flag8) {
      // カードを出す
      if (((ks & FIRE_PRESSED) != 0) && ((keyState & FIRE_PRESSED) == 0)) {
        if (human.puttable(temp[0] = human.getChoosedCard())) {
          temp[1] = human.getOwnCards(temp[0]);
          human.putCard(temp);

          frmCpuWait = 15;
          multiScore = 1;
          cpuReDobonAble = false;
          dobonCheckAfterHuman();
          info1 = "";

          frmcnt = 0;
          if (human.getNumCards() == 0 && numDobon == 0) {
            finalize(0);
          }
          if (!human.flag8) turnId = 1;
        }
        else {
          info1 = "出せない";
        }
      }

      // カードを引く
      if (((ks & DOWN_PRESSED) != 0) && ((keyState & DOWN_PRESSED) == 0)) {
        if (human.getNumCards() == human.ownCards.length) {
          info1 = "カードを出してください";
        }
        else {
          removeCommand(c2);
          frmCpuWait = 15;
          frmcnt = 0;
          multiScore = 1;
          cpuReDobonAble = false;
          if (flag2) human.drawCards(multiDraw);
          else human.drawCards(1);
          turnId = 1;
          info1 = "";
        }
      }
    }
    // 8を出したときの絵柄選択
    else {
      if (((ks & UP_PRESSED) != 0) && ((keyState & UP_PRESSED) == 0)) {
        if (human.getSelectSuit() > 0) {
          human.setSelectSuit(human.getSelectSuit() - 1);
        }
        else {
          human.setSelectSuit(3);
        }
      }

      if (((ks & DOWN_PRESSED) != 0) && ((keyState & DOWN_PRESSED) == 0)) {
        if (human.getSelectSuit() < 3) {
          human.setSelectSuit(human.getSelectSuit() + 1);
        }
        else {
          human.setSelectSuit(0);
        }
      }

      if (frmcnt > 5) {
        if (((ks & FIRE_PRESSED) != 0) && ((keyState & FIRE_PRESSED) == 0)) {
          fieldSuit = human.getSelectSuit();
          human.setSelectSuit(0);
          human.flag8 = false;
          frmcnt = 0;
          turnId = 1;
        }
      }
    }
  }

  // ストップによる勝敗決定時の処理
  void finalize(int id) {
    if (flag2) {
      players[id+1<players.length ? id+1:0].drawCards(multiDraw);
    }
    players[id].winner = true;
    winnerId    = id;
    info1       = players[id].name + " win";
    flagOver    = true;
    human.flag8 = false;
    setScores();

    removeCommand(c2);
    c2 = new Command("得点",Command.SCREEN,2);
    addCommand(c2);
  }

  // ﾄﾞﾎﾞﾝによる勝敗決定時の処理
  void finalizeDobon(int winnerId, int loserId) {
    players[winnerId].winner = true;
    this.winnerId = winnerId;
    info1         = players[winnerId].name + " win";
    flagDobon     = true;
    flagOver      = true;
    human.flag8   = false;
    setScores(winnerId, loserId);

    removeCommand(c2);
    c2 = new Command("得点",Command.SCREEN,2);
    addCommand(c2);
  }

  // ﾄﾞﾎﾞﾝ時のスコア計算
  void setScores(int winnerId, int loserId) {
    int winnersGain = 0;
    int multi = 2;
    multi = multiScore;

    for (int i=0; i<players[winnerId].getNumCards(); i++) {
      if (players[winnerId].getOwnCards(i)%13 == 0 ||
          players[winnerId].getOwnCards(i)%13 > 9) {
        winnersGain += 10;
      }
      else winnersGain += players[winnerId].getOwnCards(i)%13 + 1;
      if (players[winnerId].getOwnCards(i)%13 == 1) multi *= 2;
    }

    for (int i=0; i<players[loserId].getNumCards(); i++) {
      if (players[loserId].getOwnCards(i)%13 == 0 ||
          players[loserId].getOwnCards(i)%13 > 9) {
        winnersGain += 10;
      }
      else winnersGain += players[loserId].getOwnCards(i)%13 + 1;
      if (players[loserId].getOwnCards(i)%13 == 1) multi *= 2;
    }

    if (fieldCard%13 == 0 || fieldCard%13 > 9) winnersGain += 10;
    else winnersGain += fieldCard%13 + 1;
    if (fieldCard%13 == 1) multi *= 2;

    winnersGain = winnersGain * multi;

    players[winnerId].setPrevScore(players[winnerId].getScore());
    players[winnerId].setScoreGain(winnersGain);
    players[winnerId].setScore(players[winnerId].getScore() + winnersGain);
    players[loserId].setPrevScore(players[loserId].getScore());
    players[loserId].setScoreGain(-winnersGain);
    players[loserId].setScore(players[loserId].getScore() - winnersGain);
    if (players[loserId].getScore() < 0) flagGameSet = true;
  }

  // ストップ時のスコア計算
  void setScores() {
    int winnersGain = 0;
    for (int i=0; i<players.length; i++) {
      players[i].setPrevScore(players[i].getScore());
      if (!players[i].winner) {
        players[i].setScoreGain(- players[i].points());
        players[i].setScore(players[i].getScore() + players[i].getScoreGain());
        winnersGain += players[i].points();
        if (players[i].getScore() < 0) flagGameSet = true;
      }
    }
    for (int i=0; i<players.length; i++) {
      if (players[i].winner) {
        players[i].setScoreGain(winnersGain);
        players[i].setScore(players[i].getScore() + winnersGain);
      }
    }
  }

  // ストップ時のスコアを表示
  void paintScore(Graphics g) {
    int x, y, len;
    String str;
//  g.setColor(255,255,255);
    g.setColor(255,204,255);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0,0,0);
    for (int i=0; i<players.length; i++) {
      if (players[i].winner) {
        str = players[i].name + ":"
            + players[i].getPrevScore() + "+"
            + players[i].getScoreGain() + " = " 
            + players[i].getScore() + "点";
        g.drawString(str,5,67*i+5,Graphics.LEFT|Graphics.TOP);
      }
      else {
        str = players[i].name + ":"
            + players[i].getPrevScore() + "-"
            + (-players[i].getScoreGain()) + " = " 
            + players[i].getScore() + "点";
        g.drawString(str,5,67*i+5,Graphics.LEFT|Graphics.TOP);
        g.drawString(" ="+Math.abs(players[i].getScoreGain()),
                     186,67*i+40, Graphics.LEFT|Graphics.TOP);
      }
      len = players[i].getNumCards();
      y = 67*i + 32;
      for (int j=0; j<len; j++) {
        x = (180-32*len) * (j+1) / (len+1) + 32*j + 4;
        g.drawImage(img2[players[i].ownCards[j]],x,y,
                    Graphics.LEFT|Graphics.TOP);
      }
      g.setColor(0,0,255);
      g.drawLine(0,y+35,getWidth(),y+35);
      g.setColor(0,0,0);
    }
  }

  // ドボン時のスコアを表示, ScoreCanvasから呼ばれる
  void paintScoreDobon(Graphics g) {
    int x, len, k=0;
    String str;
//  g.setColor(255,255,255);
    g.setColor(255,204,255);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0,0,0);

    // winner score and cards
    str = players[winnerId].name + ":"
        + players[winnerId].getPrevScore() + "+"
        + players[winnerId].getScoreGain() + " = " 
        + players[winnerId].getScore() + "点";
    g.drawString(str,5,5,Graphics.LEFT|Graphics.TOP);
    len = players[winnerId].getNumCards();
    for (int j=0; j<len; j++) {
      x = (180-32*len) * (j+1) / (len+1) + 32*j;
      g.drawImage(img2[players[winnerId].ownCards[j]],x,45,
                  Graphics.LEFT|Graphics.TOP);
    }

    // losers score and cards
    str = players[loserId].name + ":"
        + players[loserId].getPrevScore() +
        + players[loserId].getScoreGain() + " = " 
        + players[loserId].getScore() + "点";
    g.drawString(str,5,25,Graphics.LEFT|Graphics.TOP);
    len = players[loserId].getNumCards();
    for (int j=0; j<len; j++) {
      x = (180-32*(len+1)) * (j+1) / (len+2) + 32*j;
      g.drawImage(img2[players[loserId].ownCards[j]],x,82,
                  Graphics.LEFT|Graphics.TOP);
    }
    x = (180-32*(len+1)) * (len+1) / (len+2) + 32*len;

    // field card
    g.drawImage(img2[fieldCard],x,82,
                Graphics.LEFT|Graphics.TOP);

    g.drawString(" ="+Math.abs(players[winnerId].getScoreGain()),
                 184,80,Graphics.LEFT|Graphics.TOP);

    g.setColor(0,0,255);
    g.drawLine(0,117,getWidth(),117);
    g.setColor(0,0,0);

    for (int i=0; i<players.length; i++) {
      if ((i != winnerId) && (i != loserId)) {
        str = players[i].name + ":" + players[i].getScore() + "点";
        g.drawString(str,5,125+k*20,Graphics.LEFT|Graphics.TOP);
        k++;
      }
    }
  }

  void paintFinalResults(Graphics g) {
    int[] score = new int[players.length];
    int[] order = new int[players.length];
    int id, nextScore;
    for (int i=0; i<players.length; i++) {
      score[i] = players[i].getScore();
    }

    // order[i] i位のid
    for (int i=0; i<score.length; i++) {
      id = 0;
      nextScore = score[0];
      for (int j=0; j<score.length; j++) {
        if (score[j] > nextScore) {
          nextScore = score[j];
          id = j;
        }
      }
      order[i]  = id;
      score[id] = -1000000;
    }

    g.setColor(255,204,255);
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(0,0,0);

    String str[] = new String[players.length];
    int k    = 1;
    int prev = 0;
    int pos  = getWidth()/2;
    for (int i=0; i<players.length; i++) {
      if (i > 0) {
        if (players[order[i]].getScore() == 
            players[order[i-1]].getScore()) {
          k = prev;
        }
        else {
          k = i + 1;
        }
      }
      
      str[i] = k + "位: " + players[order[i]].getName() 
                 + "("    + players[order[i]].getScore() + "点)";
//    pos = Math.max(pos, getWidth()/2-f1.stringWidth(str[i])/2);
      pos = Math.min(pos, getWidth()/2-f1.stringWidth(str[i])/2);
      prev = k;
    }
    g.setFont(f1);
    for (int i=0; i<players.length; i++) {
      g.drawString(str[i],pos,50+25*i,Graphics.LEFT|Graphics.TOP);
    }
    g.setFont(f0);

  }

  // カードをシャッフル
  void shuffle(int[] ary, int len) {
    int tmp, r;
    for (int i=0; i<len; i++) {
      r = rand.nextInt(len-i) + i;
      tmp = ary[i];
      ary[i] = ary[r];
      ary[r] = tmp;
    }
  }

  // デッキの一番上からカードを引く。デッキにカードがないときは
  // 捨て札をシャッフルしてデッキにする
  int drawCard() {
    int r;
    if (restDeck > 0) {
      r = deck[0];
      restDeck--;
      for (int i=0; i<restDeck; i++) {
        deck[i] = deck[i+1];
      }
      return r;
    }
    else {
      for (int i=0; i<numTrash; i++) {
        deck[i] = trash[i];
      }
      restDeck = numTrash;
      numTrash = 0;
      shuffle(deck,restDeck);

      r = deck[0];
      restDeck--;
      for (int i=0; i<restDeck; i++) {
        deck[i] = deck[i+1];
      }
      return r;
    }
  }

  // initialize Game
  void init() {
    // カードを集めてシャッフル
    restDeck = deck.length;
    numTrash = 0;
    for (int i=0; i<restDeck; i++) {
      deck[i] = i;
    }
    shuffle(deck,restDeck);

//  testDeck();
//  testDeck2(); turnId = players.length-1;
//  testDeck3();
//  testDeck4(); turnId = 1;
    // プレイヤーに配り、場に1枚
    for (int i=0; i<players.length; i++) {
      players[i].playerReset();
    }
    fieldCard = drawCard();
    fieldSuit = fieldCard/13;

    // 前回の勝者が親
    turnId = winnerId;
    tmpWinner   = -1;
    tmpLoser    = -1;

    // 最初が2のとき
    if (fieldCard%13 == 1) {
      flag2 = true;
      multiDraw += 2;
    }

    // flagやinfoの初期化
    flagDobon      = false;
    flagOver       = false;
    flagGameSet    = false;
    flagStopping   = false;
    cpuReDobonAble = false;
    info1          = "";
    numDobon       = 0;
    multiScore     = 1;
    frmCpuWait     = 15;
    frmcnt         = 0;
    for (int i=0; i<cpuDobonFrm.length; i++) cpuDobonFrm[i] = -1;
    numDoboned = 0;
    for (int i=0; i<paintOrder.length; i++) {
      paintOrder[i] = i;
    }

    removeCommand(c2);
  }

  void resetTurnId() {
    turnId   = 0;
    winnerId = 0;
    loserId  = 0;
  }

  public void commandAction(Command c, Displayable d) {
    if (c == c1) {
      if (c1.getLabel() == "TITLE") {
        for(int i=0; i<players.length; i++) {
          players[i].resetScore();
        }
        resetTurnId();
        gameLoop = null;
        scene    = SCENETITLE;
      }
    }
    if (c == c2) {
      if (c2.getLabel() == "得点") {
        gameLoop = null;
        scene    = SCENESCORE;
      }
      else if (c2.getLabel() == "ドボン") {
        humanDobon();
      }
    }
  }

  void endProc() {
    for (int i=0; i<img.length; i++) {
      img[i]  = null;
      img2[i] = null;
    }
    img  = null;
    img2 = null;
    img3 = null;
    gameLoop = null;
    System.gc();
  }
//void drawAll(Graphics g) {
//  int s, len;
//  for (int j=0; j<restDeck/8+1; j++) {
//    len = (j != restDeck/8) ? 8 : restDeck%8;
//    for (int i=0; i<len; i++) {
//      s = (240-32*8) * (i+1) / (8+1) + 32*i;
//      g.drawImage(img2[deck[j*8+i]],s,32*j, 
//                  Graphics.LEFT|Graphics.TOP);
//    }
//  }
//}
}


//// 自分が出して他の3人がドボン
//void testDeck() {
//  deck[0] = 12;
//  deck[1] = 11;
//  deck[2] = 10;
//  deck[3] = 9;
//  deck[4] = 8;

//  deck[5] = 0;
//  deck[6] = 1;
//  deck[7] = 2;
//  deck[8] = 13;
//  deck[9] = 5;

//  deck[10] = 26;
//  deck[11] = 14;
//  deck[12] = 15;
//  deck[13] = 27;
//  deck[14] = 4;

//  deck[15] = 39;
//  deck[16] = 40;
//  deck[17] = 28;
//  deck[18] = 41;
//  deck[19] = 42;

//  deck[20] = 25;
//  for (int i=21; i<restDeck; i++) {
//    deck[i] = i;
//  }
//}
//// player[3]が出して他の3人がドボン
//void testDeck2() {
//  deck[0] =  0;
//  deck[1] =  1;
//  deck[2] =  2;
//  deck[3] = 13;
//  deck[4] =  5;

//  deck[5] = 39;
//  deck[6] = 40;
//  deck[7] = 28;
//  deck[8] = 41;
//  deck[9] = 42;

//  deck[10] = 26;
//  deck[11] = 14;
//  deck[12] = 15;
//  deck[13] = 27;
//  deck[14] = 4;

//  deck[15] = 12;
//  deck[16] = 11;
//  deck[17] = 10;
//  deck[18] =  9;
//  deck[19] =  8;

//  deck[20] = 25;
//  for (int i=21; i<restDeck; i++) {
//    deck[i] = i;
//  }
//}

//// 自分がドボン返し
//void testDeck3() {
//  deck[0] = 38;
//  deck[1] =  1;
//  deck[2] =  2;
//  deck[3] = 13;
//  deck[4] =  6;

//  deck[5] = 12;
//  deck[6] = 11;
//  deck[7] = 10;
//  deck[8] =  9;
//  deck[9] =  8;

//  deck[10] = 26;
//  deck[11] = 14;
//  deck[12] = 15;
//  deck[13] = 27;
//  deck[14] = 4;

//  deck[15] = 39;
//  deck[16] = 40;
//  deck[17] = 28;
//  deck[18] = 41;
//  deck[19] = 42;

//  deck[20] = 25;
//  for (int i=21; i<restDeck; i++) {
//    deck[i] = i;
//  }
//}

//// player[1]がドボン返し
//void testDeck4() {
//  deck[0] = 12;
//  deck[1] = 11;
//  deck[2] = 10;
//  deck[3] =  9;
//  deck[4] =  8;

//  deck[5] = 38;
//  deck[6] =  1;
//  deck[7] =  2;
//  deck[8] = 13;
//  deck[9] =  6;

//  deck[10] = 26;
//  deck[11] = 14;
//  deck[12] = 15;
//  deck[13] = 27;
//  deck[14] =  4;

//  deck[15] = 39;
//  deck[16] = 40;
//  deck[17] = 28;
//  deck[18] = 41;
//  deck[19] = 42;

//  deck[20] = 25;
//  for (int i=21; i<restDeck; i++) {
//    deck[i] = i;
//  }
//}

