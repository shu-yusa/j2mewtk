import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.Random;

public class Myself extends CharaProto {
  protected int id;
  protected ItemObj[] items;
  protected String[] skills;
  public static final int NUM_ITEM_MAX = 16;
  public static final int NUM_SKILL_MAX = 16;
  protected int Lv;
  protected int MaxHP;
  protected int MaxMP;
  protected int HP;
  protected int MP;
  protected int power;
  protected int guard;
  protected int offence;
  protected int defence;
  protected int speed;
  protected int experience;
  protected String name;
  protected int numItem;
  protected int[] pxPreva;
  protected int[] pyPreva;
  protected int[] dirPreva;
  protected ItemObj[] weap;
//protected int pxPrev, pyPrev, dirPrev;
//protected int pxtmp, pytmp, dirtmp;

  Myself(Image img, String name, int sx, int sy, int w, int h, int nf, int tx, int ty, int dir, int mt, Map map, HideSeek p) {
    this(map, p);
    this.name = name;
    this.tx = tx;
    this.ty = ty;
    px = tx * map.CS;
    py = ty * map.CS;
//  pxtmp     = px;
//  pytmp     = py;
//  pxPrev    = px;
//  pyPrev    = py;
    cWidth    = w;
    cHeight   = h;
    direction = dir;
//  dirtmp    = dir;
//  dirPrev   = dir;
    pxPreva   = new int[map.CS/SPEED];
    pyPreva   = new int[map.CS/SPEED];
    dirPreva  = new int[map.CS/SPEED];
    for (int i=0; i<pxPreva.length; i++) {
      pxPreva[i]  = px;
      pyPreva[i]  = py;
      dirPreva[i] = dir;
    }
    numFrame  = nf;
    numItem   = 0;
    id        = 0;

    items  = new ItemObj[NUM_ITEM_MAX];
    skills = new String[NUM_SKILL_MAX];
    for (int i=0; i<skills.length; i++) {
      skills[i] = "";
    }
    skills[0] = "ﾎｲﾐ";

    switch (mt) {
      case 0: stepping = false;
              break;
      case 1: stepping = true;
              break;
      default: stepping = true;
               break;
    }

    sp = new Sprite(img, cWidth, cHeight);
    sp.setFrame(frame+direction*numFrame);
    sp.defineCollisionRectangle((cWidth-map.CS)/2,cHeight-map.CS,map.CS,map.CS);
  }

  Myself(Map map, HideSeek p) {
    super(map, p);
    weap      = new ItemObj[5];
    for (int i=0; i<weap.length; i++) {
      weap[i] = null;
    }
  }

  int getID() {
    return id;
  }

  String getName() {
    return name;
  }

  int getPrevX() {
//  return pxPrev;
    return pxPreva[0];
  }

  int getPrevY() {
//  return pyPrev;
    return pyPreva[0];
  }

  int getPrevDir() {
//  return dirPrev;
//  return dirtmp;
    return dirPreva[1];   
  }

  int getNumItem() {
    return numItem;
  }

  ItemObj getItem(int i) {
    return items[i];
  }

  String getSkill(int i) {
    return skills[i];
  }

  ItemObj[] getItems() {
    return items;
  }

  ItemObj[] getWeap() {
    return weap;
  }

//String getItemName(int i) {
//  if (items[i] == null) {
//    return "";
//  }
//  else if (items[i].getType() > 1 && items[i].getType() < 5) {
//    if (items[i] == weap[items[i].getType()-1]) {
//      return "E"+items[i].getName();
//    }
//  }
//  return items[i].getName();
//}

  String[] getItemsName() {
    String[] itemNames = new String[items.length];
    for (int i=0; i<itemNames.length; i++) {
      if (items[i] == null) {
        itemNames[i] = "";
      }
      else if (items[i].getType() > 0 && items[i].getType() < 5) {
        if (weap[items[i].getType()-1] == null) {
          itemNames[i] = " "+items[i].getName();
        }
        else if (items[i] == weap[items[i].getType()-1]) {
          itemNames[i] = "E"+items[i].getName();
        }
      }
      else {
        itemNames[i] = " "+items[i].getName();
      }
    }
    return itemNames;
  }

  String[] getSkills() {
    return skills;
  }

  int getHP() {
    return HP;
  }
  
  int getMP() {
    return MP;
  }

  int getLv() {
    return Lv;
  }

  int[] getStatus() {
    return new int[]{Lv,HP,MP,power,guard,speed,MaxHP,MaxMP,offence,defence};
  }

  void setID(int id) {
    this.id = id;
  }

  boolean equip(int part, ItemObj item, int selectedNo) {
    if (weap[part] == null) {
      weap[part] = item;
      if (part == 0) {
        offence = power + item.getAbility();
      }
      else {
        defence += item.getAbility();
      }
      int count = 0;
      for (int i=0; i<part; i++) {
        if (weap[i] != null) count++;
      }
      swap(count, selectedNo);
      return true;
    }
    else {
      weap[part] = null;
      if (part == 0) {
        offence -= item.getAbility();
      }
      else {
        defence -= item.getAbility();
      }
      int count = 0;
      for (int i=0; i<part; i++) {
        if (weap[i] != null) count++;
      }
//    for (int i=part+1; i<weap.length; i++) {
//      if (weap[i] != null) {
//        items[count] = items[count+1];
//        count++;
//      }
//    }
//    items[count] = item;

      return false;
    }
  }

  void swap(int n, int m) {
    ItemObj tmp = items[n];
    items[n] = items[m];
    items[m] = tmp;
  }

  int equipped(ItemObj item) {
    for (int i=0; i<weap.length; i++) {
      if (weap[i] == null) {
        continue;
      }
      else if (weap[i] == item) {
        return i;
      }
    }
    return -1;
  }
 
  boolean receiveItem(ItemObj item) {
    if (numItem == NUM_ITEM_MAX) {
      map.getCanvas().getMsgWin().setMessage("持ち物がいっぱいです.");
      map.getCanvas().getMsgWin().show();
      return false;
    }
    items[numItem++] = item;
    return true;
  }

  boolean obtainItem(ItemObj item) {
    if (numItem == NUM_ITEM_MAX) {
      if (map.getCanvas().getPartyNum() == id + 1) {
        map.getCanvas().getMsgWin().setMessage("持ち物がいっぱいです.");
        map.getCanvas().getMsgWin().show();
        return false;
      }
      else {
        return map.getCanvas().getPartyMember(id+1).obtainItem(item);
      }
    }
    item.setOwner(this);
    items[numItem++] = item;
    return true;
  }

  String obtainItem(int ID) {
    if (numItem == NUM_ITEM_MAX) {
      if (map.getCanvas().getPartyNum() == id + 1) {
        map.getCanvas().getMsgWin().setMessage("持ち物がいっぱいです.");
        map.getCanvas().getMsgWin().show();
        return "";
      }
      else {
        return map.getCanvas().getPartyMember(id+1).obtainItem(ID);
      }
    }
    items[numItem] = ItemData.getItem(ID);
    items[numItem++].setOwner(this);
    return name;
  }

  void discardItem(int selectedNo) {
//  items[selectedNo] = null;
    int n;
    if (equipped(items[selectedNo]) != -1) {
      weap[selectedNo] = null;
    }
    for (int i=selectedNo; i<numItem-1; i++) {
      items[i] = items[i+1];
      if ((n = equipped(items[i+1])) != -1) {
        weap[n] = items[i];
      }
    }
    items[numItem-1] = null;
    numItem--;
  }

  void setStatus() {
    Lv = 20;
    HP = 150;
    MP = 10;
    MaxHP = 250;
    MaxMP = 50;
    power = 180;
    guard = 150;
    offence = 180;
    defence = 150;
    speed = 120;
  }

  void addStatus(int n, int plus) {
    switch (n) {
      case 1:
        addMaxHP(plus);
        break;
      case 2:
        addMaxMP(plus);
        break;
      case 3:
        addHP(plus);
        break;
      case 4:
        addMP(plus);
        break;
      case 5:
        addPower(plus);
        break;
      case 6:
        addGuard(plus);
        break;
      case 7:
        addSpeed(plus);
        break;
    }
  }

  void levelUp() {
    Lv++;
  }

  void addHP(int plus) {
    HP += plus;
    if (HP > MaxHP) {
      HP = MaxHP;
    }
  }

  void addMP(int plus) {
    MP += plus;
    if (MP > MaxMP) {
      MP = MaxMP;
    }
  }

  void addMaxHP(int plus) {
    MaxHP += plus;
  }

  void addMaxMP(int plus) {
    MaxMP += plus;
  }

  void addPower(int plus) {
    power += plus;
  }

  void addGuard(int plus) {
    guard += plus;
  }

  void addSpeed(int plus) {
    speed += plus;
  }

  void addExperience(int plus) {
    experience += plus;
  }

  void move(int dir, int speed) {
    int nextTx, nextTy;
    boolean canMove = false;
    int dx = 0;
    int dy = 0;

    switch (dir) {
      case HideCanvas.LEFT:
        direction = HideCanvas.LEFT;
        sp.move(-speed,0);    // Sprite同士の衝突検出用
        nextTx = (px-speed)/map.CS;
        if (map.movePointEquals(nextTx,(py+1)/map.CS)) {
          map.destroyMap();
          map.changeMap();
          canMove = true;
          System.gc();
          break;
        }
        else if (!map.isHit(this, nextTx, (py+1)/map.CS)  &&
            !map.isHit(this, nextTx, (py+map.CS-1)/map.CS)) {
          canMove = true;
          dx = -speed;
        }
        break;
      case HideCanvas.RIGHT:
        direction = HideCanvas.RIGHT;
        sp.move(speed,0);
        nextTx = (px+map.CS+speed-1)/map.CS;
        if (map.movePointEquals(nextTx,(py+1)/map.CS)) {
          map.destroyMap();
          map.changeMap();
          canMove = true;
          System.gc();
          break;
        }
        else if (!map.isHit(this, nextTx, (py+1)/map.CS)  &&
            !map.isHit(this, nextTx, (py+map.CS-1)/map.CS)) {
          canMove = true;
          dx = speed;
        }
        break;
      case HideCanvas.UP:
        direction = HideCanvas.UP;
        sp.move(0,-speed);
        nextTy =(py-speed)/map.CS;
        if (map.movePointEquals((px+1)/map.CS, nextTy)) {
          map.destroyMap();
          map.changeMap();
          canMove = true;
          System.gc();
          break;
        }
        else if (!map.isHit(this, (px+1)/map.CS, nextTy)         &&
            !map.isHit(this, (px+map.CS-1)/map.CS, nextTy)) {
          canMove = true;
          dy = -speed;
        }
        break;
      case HideCanvas.DOWN:
        direction = HideCanvas.DOWN;
        sp.move(0,speed);
        nextTy = (py+map.CS+speed-1)/map.CS;
        if (map.movePointEquals((px+1)/map.CS, nextTy)) {
          map.destroyMap();
          map.changeMap();
          canMove = true;
          System.gc();
          break;
        }
        else if (!map.isHit(this, (px+1)/map.CS, nextTy)         &&
            !map.isHit(this, (px+map.CS-1)/map.CS, nextTy)) {
          canMove = true;
          dy = speed;
        }
        break;

    }
    if (canMove) {
//    pxPrev = pxtmp;
//    pyPrev = pytmp;
//    pxtmp = px;
//    pytmp = py;
//    dirPrev = dirtmp;
//    dirtmp = dir;
      for (int i=0; i<pxPreva.length-1; i++) {
        pxPreva[i]  = pxPreva[i+1];
        pyPreva[i]  = pyPreva[i+1];
        dirPreva[i] = dirPreva[i+1];
      }
      pxPreva[pxPreva.length-1] = px;
      pyPreva[pyPreva.length-1] = py;
      dirPreva[dirPreva.length-1] = dir;

      px += dx;
      py += dy;
    }    
    if (!stepping) addFrame();
    sp.setFrame(frame+direction*numFrame);
  }

  void changeMap(Map newMap) {
     map = newMap;
  }

  void searchItem() {
    int id=-1;
    switch (direction) {
      case LEFT:
        id = map.searchTreasure(px-map.CS/2,py+map.CS/2);
        break;
      case RIGHT:
        id = map.searchTreasure(px+map.CS+map.CS/2,py+map.CS/2);
        break;
      case UP:
        id = map.searchTreasure(px+map.CS/2,py-map.CS/2);
        break;
      case DOWN:
        id = map.searchTreasure(px+map.CS/2,py+map.CS+map.CS/2);
        break;
    }
    if (id != -1) {
      String name;
      if ((name = obtainItem(id)) != "") {
        map.getCanvas().getMsgWin().setMessage(
            name+"は"+ ItemData.getItemName(id)+"を手に入れた!");
        map.getCanvas().getMsgWin().setY((int)(map.getCanvas().HEIGHT*0.8));
        map.getCanvas().getMsgWin().show();
      }
    }
  }

  Chara talkWith(Chara[] charas) {
    switch (direction) {
      case LEFT:
        sp.move(-map.CS/2,0);
        break;
      case RIGHT:
        sp.move(map.CS/2,0);
        break;
      case UP:
        sp.move(0,-map.CS);
        break;
      case DOWN:
        sp.move(0,map.CS);
        break;
    }
    for (int i=0; i<charas.length; i++) {
      if (sp.collidesWith(charas[i].getSprite(), false)) {
        return charas[i];
      }
    }
    return null;
  }
//  int frontTx;
//  int frontTy;
//  switch (direction) {
//    case LEFT:
//      frontTx = (px-map.CS/2)/map.CS;
//      frontTy = (py+map.CS/2)/map.CS;
//      break;
//    case RIGHT:
//      frontTx = (px+map.CS/2)/map.CS + 1;
//      frontTy = (py+map.CS/2)/map.CS;
//      break;
//    case UP:   
//      frontTx = (px+map.CS/2)/map.CS;
//      frontTy = (py-map.CS/2)/map.CS;
//      break;
//    case DOWN:
//      frontTx = (px+map.CS/2)/map.CS;
//      frontTy = (py+map.CS/2)/map.CS + 1;
//      break;
//  }


}
