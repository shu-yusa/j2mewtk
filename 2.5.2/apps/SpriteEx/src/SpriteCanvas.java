import javax.microedition.lcdui.game.*;
import javax.microedition.lcdui.*;

public class SpriteCanvas extends GameCanvas implements Runnable {
  final static int[][] MAP_DATA = {
    {1,1,1,1,1,1,1,1,1,1},
    {1,2,0,0,0,0,1,0,0,1},
    {1,0,0,0,0,0,1,1,0,1},
    {0,0,1,1,1,1,1,1,0,1},
    {1,0,0,1,0,0,0,1,0,1},
    {1,1,0,1,0,1,1,1,0,1},
    {1,0,0,1,0,0,0,0,0,1},
    {1,0,1,1,0,1,0,1,1,1},
    {1,0,0,0,0,1,0,0,3,1},
    {1,1,1,1,1,1,1,1,1,1},
  };
  Image girlImg;
  Image mapImg;

  SpriteCanvas() {
    // キーイベントの抑制
    super(false);

    try {
      girlImg = Image.createImage("/girl.png");
      mapImg = Image.createImage("/map.png");
    } catch (Exception e) {
      System.out.println(e.getClass().getName());
    }
  }

  public void run() {
    Graphics g = getGraphics();
    int keyState;

    Sprite girl = new Sprite(girlImg, 48, 48);
    int girlX = 48;
    int girlY = 96;
    int girlPX = 0;
    int girlPY = 0;
    int girlFrame = 0;

    //タイルレイヤー
    TiledLayer map = new TiledLayer(100,100,mapImg,48,48);
    for (int j=0; j<MAP_DATA.length; j++) {
      for (int i=0; i<MAP_DATA[j].length; i++) {
        map.setCell(i,j,MAP_DATA[j][i]);
      }
    }

    // レイヤーマネージャー
    LayerManager manager = new LayerManager();
    manager.append(girl);
    manager.append(map);

    while (true) {
      //キー状態の取得
      keyState = getKeyStates();

      //前回xy座標の保持
      girlPX = girlX;
      girlPY = girlY;

      //移動
      if (keyState != 0) girlFrame = (girlFrame + 1) % 2;
      if ((UP_PRESSED    & keyState) != 0) girlY -=24;
      if ((DOWN_PRESSED  & keyState) != 0) girlY +=24;
      if ((LEFT_PRESSED  & keyState) != 0) girlX -=24;
      if ((RIGHT_PRESSED & keyState) != 0) girlX +=24;

      //スプライトのフレームと位置を指定
      girl.setFrame(girlFrame);
      girl.setPosition(girlX,girlY);

      //衝突時は前回xy座標に戻る
      if (girl.collidesWith(map, false)) {
        girlX = girlPX;
        girlY = girlPY;
        girl.setPosition(girlX,girlY);
      }

      //視点の指定
      manager.setViewWindow(
          girlX - (getWidth() - 48) / 2,
          girlY - (getHeight()- 48) / 2,
          getWidth(), getHeight());

      g.setColor(150,200,120);
      g.fillRect(0,0,getWidth(),getHeight());
      manager.paint(g,0,0);
      flushGraphics();

      //sleep
      try {
        Thread.sleep(100);
      } catch (Exception e) {
      }
    }
  }
}
