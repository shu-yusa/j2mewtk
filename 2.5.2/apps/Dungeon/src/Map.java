import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.io.*;
import java.util.*;

public class Map {
  static int WIDTH;
  static int HEIGHT;
  public static final int CS = 16;
  private CharaProto[] charas;
  private Chara[] subCharas;
  private Myself[] party;
  private CharaProto tmp;
  private Vector tmpcharas;
  private int[] order;
  private int[] ways;
  private int[] imgX, imgY;
  private int[] trgX, trgY;
  private int[] itemIDs;
  private int point;
  private MovePoint[] movePoints;
  private int mapType;
  private int mapNo;
  private int iterX, iterY;
  private int partyNum;
  Image img;
  Image[] imgs;
  TiledLayer tlay;
  int px, py;
  int row, col;
  HideSeek parent;
  HideCanvas canvas;

  Map(int mapNum, String mapfile, String eventfile, int mapNo, int mapType, HideSeek p, HideCanvas hc) {
    parent = p;
    px = py = 0;
    canvas = hc;
    tmpcharas = new Vector();
    this.mapNo = mapNo;
    this.mapType = mapType;
    iterX = 0;
    iterY = 0;

    partyNum = canvas.getPartyNum();
    try {
      img = canvas.getMapImage(mapNum);
      loadEvent(eventfile);
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
    loadMap(mapfile);

    party = new Myself[partyNum];

    subCharas = new Chara[charas.length-party.length];
    for (int i=0; i<subCharas.length; i++) {
      subCharas[i] = (Chara)charas[i+partyNum];
    }
    order = new int[charas.length];
    for (int i=0; i<order.length; i++) order[i] = order.length-i-1;
  }

  void loadMap(String mapfile) {
    try {
      DataInputStream dis = new
        DataInputStream(getClass().getResourceAsStream(mapfile));
      int[][] map;
      row    = dis.readByte();
      col    = dis.readByte();
      WIDTH  = col * CS;
      HEIGHT = row * CS;
      map    = new int[row][col];
      tlay   = new TiledLayer(col, row, img, CS, CS);
      for (int i=0; i<row; i++) {
        for (int j=0; j<col; j++) {
          map[i][j] = dis.read() + 1;
          tlay.setCell(j,i,map[i][j]);
        }
      }
      dis.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      parent.notifyDestroyed();
    }
  }

  HideCanvas getCanvas() {
    return canvas;
  }

  CharaProto[] getCharas() {
    return charas;
  }

  Chara[] getSubCharas() {
    return subCharas;
  }

  void addParty(int destX, int destY) {
    for (int i=0; i<party.length; i++) {
      charas[i] = party[i];
      party[i].changeMap(this);
//    party[i].setPosition(destX,destY);
    }
  }

  void addParty(Myself[] party) {
    this.party = party;
    for (int i=0; i<party.length; i++) {
      charas[i] = this.party[i];
      this.party[i].changeMap(this);
    }
  }

  void addMyself(Myself m) {
    charas[0] = m;
    m.changeMap(this);
  }

  int searchTreasure(int px, int py) {
    for (int i=0; i<itemIDs.length; i++) {
       if (trgX[i]*CS < px && px < (trgX[i]+1)*CS &&
            trgY[i]*CS < py && py < (trgY[i]+1)*CS) return i;
    }
    return -1;
  }

  boolean isHit(CharaProto c, int x, int y) {
    if (x < 0 || x > col-1 || y < 0 || y > row-1) {
      return true;
    }

    boolean coll = false;
    for (int i=0; i<subCharas.length; i++) {
      if (c == subCharas[i]) continue;
      coll |= c.getSprite().collidesWith(subCharas[i].getSprite(), false);
    }
    if (c != charas[0]) {
      coll |= c.getSprite().collidesWith(charas[0].getSprite(), false);
    }
    if (coll) return true;

    int cell = tlay.getCell(x,y);
    for (int i=0; i<ways.length; i++) {
      if (cell == ways[i]) {
        return false;
      }
    }
    return true;

  }

  boolean movePointEquals(int tx, int ty) {
    for (int i=0; i<movePoints.length; i++) {
      if (movePoints[i].getX() == tx && movePoints[i].getY() == ty) {
        point = i;
        return true;
      }
    }
    return false;
  }

  void changeMap() {
    movePoints[point].gotoNextMap();
  }

  void stepCharas() {
    for (int i=0; i<charas.length; i++) {
      if (charas[i].isStepping()) {
        charas[i].addFrame();
      }
    }
  }

  void moveSubCharas() {
    for (int i=0; i<subCharas.length; i++) {
      if (subCharas[i].canWalk()) {
        subCharas[i].randomWalk(CharaProto.SPEED/2);
      }
    }
  }

  void drawMap(Graphics g) {
    int offsetX = canvas.WIDTH/2 - canvas.getMyself().getX();
    int offsetY = canvas.HEIGHT/2 - canvas.getMyself().getY();
    switch (mapType) {
      case 0: 
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, canvas.WIDTH - WIDTH);
        offsetY = Math.min(offsetY, 0);
        offsetY = Math.max(offsetY, canvas.HEIGHT - HEIGHT);
        break;
      case 1:
        if (offsetX > 0) {
          canvas.getMyself().setX2(WIDTH-canvas.WIDTH/2);
          offsetX = canvas.WIDTH - WIDTH;
          iterX--;
        }
        else if (offsetX < canvas.WIDTH - WIDTH) {
          canvas.getMyself().setX2(canvas.WIDTH/2);
          offsetX = 0;
          iterX++;
        }
        if (offsetY > 0) {
          canvas.getMyself().setY2(HEIGHT-canvas.HEIGHT/2);
          offsetY = canvas.HEIGHT - HEIGHT;
          iterY--;
        }
        else if (offsetY < canvas.HEIGHT - HEIGHT) {
          canvas.getMyself().setY2(canvas.HEIGHT/2);
          offsetY = 0;
          iterY++;
        }
        break;
    }

    setMapPosition(offsetX, offsetY);
    tlay.paint(g);
    for (int i=0; i<imgs.length; i++) {
      g.drawImage(imgs[i],imgX[i]*CS-(imgs[i].getWidth()-CS)/2+offsetX,
                          imgY[i]*CS-imgs[i].getHeight()+CS+offsetY,
                          Graphics.LEFT|Graphics.TOP);
    }
    if (mapNo == 3) {
      changeFlower(g, offsetX, offsetY);
    }
    for (int i=0; i<charas.length; i++) {
      charas[order[i]].setOffset(offsetX, offsetY);
      if (charas[order[i]].isInScreen(canvas.WIDTH,canvas.HEIGHT)) {
        charas[order[i]].draw(g);
      }
    }
  }

  void changeFlower(Graphics g, int offsetX, int offsetY) {
    if (iterX == 5 && iterY == 3) {
      g.drawImage(imgs[1],imgX[0]*CS-(imgs[1].getWidth()-CS)/2+offsetX,
                          imgY[0]*CS-imgs[1].getHeight()+CS+offsetY,
                          Graphics.LEFT|Graphics.TOP);
    }
    if (iterX > 10) iterX = 0;
    if (iterX < -10) iterX = 0;
    if (iterY > 5) iterX = 0;
    if (iterY < -5) iterY = 0;
  }

  void setMapPosition(int x, int y) {
    tlay.setPosition(x, y);
  }

  void setPaintOrder() {
//  CharaProto tmp;
    int k;
    int l;
    int N = charas.length;

    for (int j=0; j<N; j++) {
      tmp = charas[order[j]];
      k = j;
      for(int i=j+1; i<N; i++) {
        if (charas[order[i]].getY() < tmp.getY()) {
          tmp = charas[order[i]];
          k = i;
        }
      }
      if (tmp != charas[order[j]]) {
        l = order[k];
        order[k] = order[j];
        order[j] = l;
      }
    }
  }

  private void loadEvent(String filename) {
    Vector lines = new Vector();
    Vector linesMove = new Vector();
    Vector linesImage = new Vector();
    Vector linesTreasure = new Vector();
    String way = "";
    String line = "";
    try {
      InputStreamReader isr = new
        InputStreamReader(getClass().getResourceAsStream(filename),"UTF-8");
      char[] b = new char[500];
      int c;
      int i=0;
      while ((c = isr.read()) != -1) {
        if ((b[i] = (char)c) ==  '\n') {
          b[i] = '\u0000';
          line = new String(b,0,i);
          if (line.equals("") || line.startsWith("#")) {
            for (int j=0; j<=i; j++) b[j] = '\u0000';
            i = 0;
            continue;
          }
          int st = line.indexOf(",");
          if (line.substring(0,st).equals("CHARA")) {
            lines.addElement(line);
          }
          else if (line.substring(0,st).equals("MOVE")) {
            linesMove.addElement(line);
          }
          else if (line.substring(0,st).equals("BARRIER")) {
            way = line;
          }
          else if (line.substring(0,st).equals("IMAGE")) {
            linesImage.addElement(line);
          }
          else if (line.substring(0,st).equals("TREASURE")) {
            linesTreasure.addElement(line);
          }

          for (int j=0; j<=i; j++) b[j] = '\u0000';
          i = -1;
        }
        i++;
      }
      isr.close();

      charas = new CharaProto[lines.size()+partyNum]; // + 1 for myself
      for (int j=0; j<lines.size(); j++) {
        makeCharacter((String)lines.elementAt(j),j);
      }

      movePoints = new MovePoint[linesMove.size()];
      for (int j=0; j<linesMove.size(); j++) {
        movePoints[j] = new
          MovePoint((String)linesMove.elementAt(j),parent,canvas);
      }

      setWalls(way);

      trgX = new int[linesTreasure.size()];
      trgY = new int[linesTreasure.size()];
      itemIDs = new int[linesTreasure.size()];
      for (int j=0; j<linesTreasure.size(); j++) {
        setTreasure((String)linesTreasure.elementAt(j),j);
      }

      imgs = new Image[linesImage.size()];
      imgX = new int[linesImage.size()];
      imgY = new int[linesImage.size()];
      for (int j=0; j<linesImage.size(); j++) {
        setImage((String)linesImage.elementAt(j),j);
      }

    }
    catch (Exception e) {
      System.out.println("error:loadEvent");
      System.out.println(line);
      e.printStackTrace();
    }
  }

  private void setTreasure(String line, int j) {
    int sp1  = line.indexOf(",");
    int sp2  = line.indexOf(",",sp1+1);
    int sp3  = line.indexOf(",",sp2+1);
    int sp4  = line.indexOf(",",sp3+1);
    itemIDs[j] = Integer.parseInt(line.substring(sp1+1,sp2));
    trgX[j]    = Integer.parseInt(line.substring(sp2+1,sp3));
    trgY[j]    = Integer.parseInt(line.substring(sp3+1,line.length()));
  }

  private void setImage(String line, int j) {
    int sp1  = line.indexOf(",");
    int sp2  = line.indexOf(",",sp1+1);
    int sp3  = line.indexOf(",",sp2+1);
    int sp4  = line.indexOf(",",sp3+1);
    int sp5  = line.indexOf(",",sp4+1);
    int sp6  = line.indexOf(",",sp5+1);
    int sp7  = line.indexOf(",",sp6+1);
    String fname = line.substring(sp1+1,sp2);
    int sx  = Integer.parseInt(line.substring(sp2+1,sp3));
    int sy  = Integer.parseInt(line.substring(sp3+1,sp4));
    int w   = Integer.parseInt(line.substring(sp4+1,sp5));
    int h   = Integer.parseInt(line.substring(sp5+1,sp6));
    int tx  = Integer.parseInt(line.substring(sp6+1,sp7));
    int ty  = Integer.parseInt(line.substring(sp7+1,line.length()));

    try {
      Image img = Image.createImage(fname);
      imgs[j] = Image.createImage(img, sx, sy, w, h, Sprite.TRANS_NONE);
      imgX[j] = tx;
      imgY[j] = ty;
      img = null;
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void setWalls(String way) {
    int sp0 = way.indexOf(",");
    int sp  = -1;
    Vector v = new Vector();
    while (true) {
      sp = way.indexOf(",", sp0+1);
      if (sp == -1) {
        v.addElement(way.substring(sp0+1,way.length()).trim());
        break;
      }
      v.addElement(way.substring(sp0+1,sp));
      sp0 = sp;
    }
    ways = new int[v.size()];
    for (int i=0; i<ways.length; i++) {
      ways[i] = Integer.parseInt((String)v.elementAt(i));
    }
  }

  void destroyMap() {
    order = null;
    ways = null;
    imgX = null;
    imgY = null;
    img = null;
    if (imgs != null) {
      for (int i=0; i<imgs.length; i++) {
        imgs[i] = null;
      }
      imgs = null;
    }
//  tmpcharas = null;
    for (int i=0; i<subCharas.length; i++) {
//    subCharas[i].destroy();
      subCharas[i] = null;
    }
    subCharas = null;
//  for (int i=0; i<charas.length; i++) {
//    charas[i] = null;
//  }
//  charas = null;
  }

  /**
    * キャラクターイベントを作成
    *
    */
  private void makeCharacter(String line, int j) {
    int sp1  = line.indexOf(",");
    int sp2  = line.indexOf(",",sp1+1);
    int sp3  = line.indexOf(",",sp2+1);
    int sp4  = line.indexOf(",",sp3+1);
    int sp5  = line.indexOf(",",sp4+1);
    int sp6  = line.indexOf(",",sp5+1);
    int sp7  = line.indexOf(",",sp6+1);
    int sp8  = line.indexOf(",",sp7+1);
    int sp9  = line.indexOf(",",sp8+1);
    int sp10 = line.indexOf(",",sp9+1);
    int sp11 = line.indexOf(",",sp10+1);
    int sp12 = line.indexOf(",",sp11+1);
//  String fname = line.substring(sp1+1,sp2);
    int cnum= Integer.parseInt(line.substring(sp1+1,sp2));
    int sx  = Integer.parseInt(line.substring(sp2+1,sp3));
    int sy  = Integer.parseInt(line.substring(sp3+1,sp4));
    int w   = Integer.parseInt(line.substring(sp4+1,sp5));
    int h   = Integer.parseInt(line.substring(sp5+1,sp6));
    int nf  = Integer.parseInt(line.substring(sp6+1,sp7));
    int tx  = Integer.parseInt(line.substring(sp7+1,sp8));
    int ty  = Integer.parseInt(line.substring(sp8+1,sp9));
    int id  = Integer.parseInt(line.substring(sp9+1,sp10));
    int dir = Integer.parseInt(line.substring(sp10+1,sp11));
    int mt  = Integer.parseInt(line.substring(sp11+1,sp12));
    String msg = line.substring(sp12+1,line.length());

    charas[j+partyNum] = new Chara(cnum,sx,sy,w,h,nf,tx,ty,dir,mt,msg,this,parent);
  }

  class MovePoint {
    private String imgFile;
    private String mapFile;
    private String eventFile;
    private int mapNum;
    private int x, y;
    private int destX, destY;
    private int mapType, mapNo;
    private HideSeek midlet;
    private HideCanvas canvas;

    MovePoint(String line, HideSeek midlet, HideCanvas canvas) {
      int sp1   = line.indexOf(",");
      int sp2   = line.indexOf(",",sp1+1);
      int sp3   = line.indexOf(",",sp2+1);
      int sp4   = line.indexOf(",",sp3+1);
      int sp5   = line.indexOf(",",sp4+1);
      int sp6   = line.indexOf(",",sp5+1);
      int sp7   = line.indexOf(",",sp6+1);
      int sp8   = line.indexOf(",",sp7+1);
      int sp9   = line.indexOf(",",sp8+1);
//    imgFile   = line.substring(sp1+1,sp2);
      mapNum    = Integer.parseInt(line.substring(sp1+1,sp2));
      mapFile   = line.substring(sp2+1,sp3);
      eventFile = line.substring(sp3+1,sp4);
      x         = Integer.parseInt(line.substring(sp4+1,sp5));
      y         = Integer.parseInt(line.substring(sp5+1,sp6));
      mapNo     = Integer.parseInt(line.substring(sp6+1,sp7));
      mapType   = Integer.parseInt(line.substring(sp7+1,sp8));
      destX     = Integer.parseInt(line.substring(sp8+1,sp9));
      destY     = Integer.parseInt(line.substring(sp9+1,line.length()).trim());

      this.midlet = midlet;
      this.canvas = canvas;
    }

    int getX() {
      return x;
    }

    int getY() {
      return y;
    }

    boolean isEntered(int tx, int ty) {
      return (x == tx && y == ty); 
    }

    void gotoNextMap() {
      Map newMap = new Map(mapNum,mapFile,eventFile,mapNo,mapType,midlet,canvas);
      canvas.changeMap(newMap);
      newMap.addParty(party);
      for (int i=0; i<party.length; i++) {
        party[i].setPosition(destX,destY);
      }
    }
  }
}
