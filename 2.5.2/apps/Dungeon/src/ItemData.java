public class ItemData {
  public static final int TOTAL_NUM_ITEMS = 15;
  public static final int ITEM  = 0;
  public static final int WEAPON = 1;
  public static final int ARMER  = 2;
  public static final int SHIELD = 3;
  public static final int HAT = 4;

  public static final int MAXHP = 1;
  public static final int MAXMP = 2;
  public static final int HP = 3;
  public static final int MP = 4;
  public static final int POWER = 5;
  public static final int GUARD = 6;
  public static final int SPEED = 7;
  private static HideCanvas canvas;

//public Vector[] items = new Vector[TOTAL_NUM_ITEMS];
  public static ItemObj[] items = new ItemObj[TOTAL_NUM_ITEMS];

  static {
    canvas = HideSeek.getCanvas();
    items[0] = new ItemObj(0, ITEM, "やくそう", HP, 30, "ﾎﾟｲﾝﾄ回復した.");
    items[1] = new ItemObj(1, WEAPON, "ﾒﾀﾙｷﾝｸﾞの剣", POWER, 130, "");
    items[2] = new ItemObj(2, ARMER, "ﾒﾀﾙｷﾝｸﾞの鎧", GUARD, 70,"");
    items[3] = new ItemObj(3, SHIELD, "ﾒﾀﾙｷﾝｸﾞの盾", GUARD, 40,"");
    items[4] = new ItemObj(4, HAT, "ﾒﾀﾙｷﾝｸﾞの兜", GUARD, 20,"");
  }

  static ItemObj getItem(int ID) {
    return (new ItemObj(items[ID]));
  }

  static String getItemName(int ID) {
    return items[ID].getName();
  }

  static void statusChange(int statusNum) {
  }
}



