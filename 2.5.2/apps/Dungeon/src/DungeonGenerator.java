import javax.microedition.lcdui.*;
import java.util.Random;

public class DungeonGenerator {
  public static int WIDTH;
  public static int HEIGHT;
  public static int MapWidth = 100;
  public static int MapHeight = 100;
  private static final int OFFSETX = 2;
  private static final int OFFSETY = 2;
  public static final int MinMapWidth = 5;
  public static final int MinMapHeight = 5;
  public static int PATH_WIDTH = 1;
  public static final int NUM_RECT_MAX = 10;
  public static Random rand;
  int numRect;
  int numCreated=0;
  Room rooms[];

  DungeonGenerator() {
    numRect = rand.nextInt(NUM_RECT_MAX) + 2;
  }

  static {
    rand = new Random();
  }

  void regenerate() {
    rooms = new Room[numRect];
    rooms[0] = new Room(0,0,0,100,100);
    numCreated++;
  }

  void split(int rn) {
    int dir;
    if (rooms[rn].isSplitable()) {
      if (hasSplitedH()) {
        dir = 1;
      } else if (hasSplitedV) {
        dir = 0;
      } else {
        dir = rand.nextInt(1);
      }
    }
    if (dir == 0) {
      

    }

  }
}
