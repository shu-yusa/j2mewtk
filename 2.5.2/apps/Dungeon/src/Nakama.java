import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Nakama extends Myself {
  Myself myself;

  Nakama(Image img, String name, int w, int h, int nf, int tx, int ty, int dir, int mt, Map map, HideSeek p, Myself myself) {
    super(map, p);
    this.myself = myself;
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

    items = new ItemObj[NUM_ITEM_MAX];
    skills = new String[NUM_SKILL_MAX];
    for (int i=0; i<skills.length; i++) {
      skills[i] = "";
    }

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

  void move() {
    int fx=map.getCanvas().getPartyMember(id-1).getPrevX();
    int fy=map.getCanvas().getPartyMember(id-1).getPrevY();
    if (px != fx || py != fy) {
      for (int i=0; i<pxPreva.length-1; i++) {
        pxPreva[i]  = pxPreva[i+1];
        pyPreva[i]  = pyPreva[i+1];
        dirPreva[i] = dirPreva[i+1];
      }
      pxPreva[pxPreva.length-1] = px;
      pyPreva[pyPreva.length-1] = py;
      dirPreva[dirPreva.length-1] = direction;
//    pxPrev = pxtmp;
//    pyPrev = pytmp;
//    pxtmp = px;
//    pytmp = py;
//    dirPrev = dirtmp;
//    dirtmp = direction;
      px = map.getCanvas().getPartyMember(id-1).getPrevX();
      py = map.getCanvas().getPartyMember(id-1).getPrevY();
      tx = px/map.CS;
      ty = py/map.CS;
      direction = map.getCanvas().getPartyMember(id-1).getPrevDir();
    }
    if (!stepping) addFrame();
    sp.setFrame(frame+direction*numFrame);
  }
}
