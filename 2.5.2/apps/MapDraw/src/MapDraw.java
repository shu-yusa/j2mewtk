import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.midlet.*;

public class MapDraw extends MIDlet {
  MapDrawCanvas c;
  public MapDraw() {
    c = new MapDrawCanvas(this);
    Display.getDisplay(this).setCurrent(c);
  }

  protected void startApp() {}
  protected void pauseApp() {}
  protected void destroyApp(boolean flg) {}
}

class MapDrawCanvas extends Canvas {
  TiledLayer tlay;
  Image img;

  MapDrawCanvas(MapDraw mid) {
    try {
//    img = Image.createImage("/MapChip.PNG");
      img = Image.createImage("/mapchips.png");

      int[][] mdt = {
        {4,4,4,4,4,4,4,4,4,4},
        {4,3,1,1,1,4,1,4,1,4},
        {4,4,1,2,4,4,1,4,1,4},
        {4,4,1,2,3,1,1,4,1,4},
        {4,1,1,2,2,1,3,4,3,4},
        {4,1,3,1,1,1,1,4,1,4},
        {4,1,1,1,2,3,1,4,1,4},
        {4,2,2,1,3,3,1,4,1,4},
        {4,2,2,1,1,1,1,4,1,4},
        {4,4,4,4,4,4,4,4,4,4},
      };

      tlay = new TiledLayer(10,10,img,24,24);
      for(int i=0; i<mdt.length; i++) {
        for (int j=0; j<mdt[i].length; j++) {
          tlay.setCell(j,i,mdt[i][j]);
        }
      }
    } 
    catch(Exception e) {
      e.printStackTrace();
      mid.notifyDestroyed();
    }
  }

  public void paint(Graphics g) {
    tlay.paint(g);
  }
}



