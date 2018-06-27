import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class MenuManager extends WindowManager {
  private static final int FRM_W = 1;
  private int LT,RT;
  private int depth;
  private int focusedWinNo;
  private int focusedWinNoPrev;
  private int[] xs;
  private int[] ys;
  private int[] widths;
  private int[] heights;
  private int[] subx;
  private int[] suby;
  private int[] subwidth;
  private int[] subheight;
  private int[] selectedNos;
  private int[] pageNo;
  private int[] seq;
  private int partyNum;
  private Font fs;
  private HideCanvas hidec;
  private Myself member;
  private ItemObj[] weap;
  private static final int NUM_ITEMS_IN_A_PAGE = 8;
  private static final int NUM_SKILLS_IN_A_PAGE = 8;
  private String[][] menus = {
    {"持ち物", "特技", "ｽﾃｰﾀｽ", "終了"},
    {}, {}, {},
    {"Lv", "HP", "MP", "力", "守り", "素早さ", "最大HP", "最大MP","攻撃", "守備"},
    {"使う", "渡す", "捨てる",}, {"はい","いいえ",}, {}, {}, {}, {},
  };

  MenuManager(int x, int y, int width, int height, GameCanvas c) {
    super(x, y, c);
    this.width = width;
    this.height = height;
    depth = 0;
  }

  MenuManager(GameCanvas c) {
    super(c);
    x = (int)(c.getWidth()*0.02);
    y = x;
    width  = (int)(c.getWidth()*0.25);
    height = (int)(c.getHeight()*0.4);
    fs = Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL);

    LT = Graphics.LEFT|Graphics.TOP;
    RT = Graphics.RIGHT|Graphics.TOP;

    xs          = new int[10];
    ys          = new int[10];
    widths      = new int[10];
    heights     = new int[10];
    selectedNos = new int[10];
    pageNo      = new int[10];
    // Menu Window
    xs[0]     = 1;//(int)(c.getWidth()*0.02);
    ys[0]     = xs[0];
    widths[0]  = (int)(c.getWidth()*0.25);
//  heights[0] = (int)(c.getHeight()*0.4);
    heights[0] = 8+15*menus[0].length;
    // Party Window
    xs[1]     = xs[0]+widths[0]+2;
    ys[1]     = ys[0];
    widths[1]  = (int)(c.getWidth()*0.25);
    heights[1] = 8+15*((HideCanvas)canvas).getPartyNum();;
    // Item Window
    xs[2]       = xs[0];
//  ys[2]       = (int)(c.getHeight()*0.4);
    ys[2]       = ys[0] +heights[0]+2;
    widths[2]   = (int)(c.getWidth()*0.4);
    heights[2]  = 8+15*NUM_ITEMS_IN_A_PAGE; 
    // Skill Window
    xs[3]       = xs[0];
//  ys[3]       = (int)(c.getHeight()*0.4);
    ys[3]       = ys[0] +heights[0]+2;
    widths[3]   = (int)(c.getWidth()*0.4);
    heights[3]  = 8+15*NUM_SKILLS_IN_A_PAGE; 
    // Status Window
    xs[4]       = xs[0];
//  ys[4]       = (int)(c.getHeight()*0.4);
    ys[4]       = ys[0] +heights[0]+2;
    widths[4]   = (int)(c.getWidth()*0.4);
    heights[4]  = 8+15*10; 
    // ItemUsage Window
    xs[5]       = xs[2]+widths[2]+2;
    widths[5]   = (int)(c.getWidth()*0.21);
    heights[5]  = 8 + 15*menus[5].length;;
    ys[5]       = ys[2]+heights[2] - heights[5];
    // ask Window
    xs[6]       = xs[5]+widths[5]+2;
    widths[6]   = (int)(c.getWidth()*0.21);
    heights[6]  = 8 + 15*menus[6].length;;
    ys[6]       = ys[5]+heights[5] - heights[6];
    // Party Window 2
    widths[7]  = (int)(c.getWidth()*0.25);
    xs[7]      = c.getWidth() - ((int)(c.getWidth()*0.02) + widths[7]);
    ys[7]      = (int)(c.getHeight()*0.4);
    heights[7] = 8+15*((HideCanvas)canvas).getPartyNum();;

    // sub Windows
    subx         = new int[3];
    suby         = new int[3];
    subwidth     = new int[3]; 
    subheight    = new int[3];
    subx[1]      = xs[2]+widths[2]+2;
    suby[1]      = ys[2];
    subwidth[1]  = (int)(c.getWidth()*0.125);
    subheight[1] = 8 + 15*1;
    
    subwidth[2]  = (int)(c.getWidth()*0.25)*((HideCanvas)canvas).getPartyNum();
    subheight[2] = 8 + 15*4 - 1;
    subx[2]      = c.getWidth()/2 - subwidth[2]/2;
//  subx[2]      = c.getWidth() - ((int)(c.getWidth()*0.00)+subwidth[2]);
    suby[2]      = c.getHeight() - (1+subheight[2]);

    depth = 0;
    for (int i=0; i<selectedNos.length; i++) {
      selectedNos[i] = 0;
    }
    for (int i=0; i<pageNo.length; i++) {
      pageNo[i] = 0;
    }
    focusedWinNo     = 0;
    focusedWinNoPrev = 0;
    seq = new int[10];
  }

  int getDepth() {
    return depth;
  }

  void show() {
    super.show();
    depth = 1;
    focusedWinNo = 0;
    seq[0] = 0;
  }

  void hide() {
    super.hide();
    depth = 0;
    for (int i=0; i<selectedNos.length; i++) {
      selectedNos[i] = 0;
    }
    for (int i=0; i<pageNo.length; i++) {
      pageNo[i] = 0;
    }
    focusedWinNo = 0;
  }

  void back() {
    if (--depth == 0) {
//    hide();
      return;
    }
    focusedWinNo = seq[depth-1];
  }

  void selectNext() {
    int len = 0;
    switch (focusedWinNo) {
      case 2: len = NUM_ITEMS_IN_A_PAGE-1;         break;
      case 3: len = NUM_SKILLS_IN_A_PAGE-1;        break;
      default:
        len = menus[focusedWinNo].length-1;
        break;
    }
    if (selectedNos[focusedWinNo] < len) {
      selectedNos[focusedWinNo]++;
    }
    else {
      selectedNos[focusedWinNo] = 0;
    }
  }

  void selectPrev() {
    int len = 0;
    switch (focusedWinNo) {
      case 2: len = NUM_ITEMS_IN_A_PAGE-1;        break;
      case 3: len = NUM_SKILLS_IN_A_PAGE-1;       break;
      default:
        len = menus[focusedWinNo].length-1;
        break;
    }
    if (selectedNos[focusedWinNo] > 0) {
      selectedNos[focusedWinNo]--;
    }
    else {
      selectedNos[focusedWinNo] = len;
    }
  }

  void nextPage() {
    switch (focusedWinNo) {
      case 2:
        if (pageNo[focusedWinNo] == Myself.NUM_ITEM_MAX/NUM_ITEMS_IN_A_PAGE-1) {
          pageNo[focusedWinNo] = 0;
        }
        else {
          pageNo[focusedWinNo]++;
        }
        break;
      case 3:
        if (pageNo[focusedWinNo] == Myself.NUM_SKILL_MAX/NUM_SKILLS_IN_A_PAGE-1) {
          pageNo[focusedWinNo] = 0;
        }
        else {
          pageNo[focusedWinNo]++;
        }
        break;
      case 4:
        if (pageNo[focusedWinNo] == 1) {
          pageNo[focusedWinNo] = 0;
        }
        else {
          pageNo[focusedWinNo]++;
        }
        break;
      default:
        break;
    }
  }

  void prevPage() {
    switch (focusedWinNo) {
      case 2:
        if (pageNo[focusedWinNo] == 0) {
          pageNo[focusedWinNo] = Myself.NUM_ITEM_MAX/NUM_ITEMS_IN_A_PAGE-1;
        }
        else {
          pageNo[focusedWinNo]--;
        }
        break;
      case 3:
        if (pageNo[focusedWinNo] == 0) {
          pageNo[focusedWinNo] = Myself.NUM_SKILL_MAX/NUM_SKILLS_IN_A_PAGE-1;
        }
        else {
          pageNo[focusedWinNo]--;
        }
        break;
      case 4:
        if (pageNo[focusedWinNo] == 0) {
          pageNo[focusedWinNo] = 1;
        }
        else {
          pageNo[focusedWinNo]--;
        }
        break;
      default:
        break;
    }
  }

  void setNextWindow() {
    depth++;
  }

  void draw(Graphics g) {
    if (visible) {
      for (int i=0; i<depth; i++) {
        drawWindows(g,seq[i]);
      }
    }
  }

  void drawFocused(Graphics g) {
    if (visible) {
      drawWindows(g,seq[Math.max(0,depth-1)]);
    }
  }

  void firePressed() {
    switch (focusedWinNo) {
      case 0:
        switch (selectedNos[0]) {
          case 0: 
          case 1:
          case 2:
            focusedWinNoPrev = focusedWinNo;
            focusedWinNo = 1;
            seq[depth++] = focusedWinNo;
            selectedNos[1] = 0;
            break;
          case 3:
            ((HideCanvas)canvas).notifyDestroyed();
            break;
          default:
            break;
        }
        break;
      case 1:
        switch (selectedNos[0]) {
          case 0: focusedWinNo = 2; break;
          case 1: focusedWinNo = 3; break;
          case 2: focusedWinNo = 4; break;
          default:
        }
        focusedWinNoPrev = 1;
        seq[depth++] = focusedWinNo;
        selectedNos[focusedWinNo] = 0;
        pageNo[focusedWinNo] = 0;
        break;
      case 2:
        int n = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
        if (n < ((HideCanvas)canvas).getPartyMember(selectedNos[1]).getNumItem()) {
          focusedWinNoPrev = focusedWinNo;
          focusedWinNo = 5;
          seq[depth++] = focusedWinNo;
          selectedNos[5] = 0;

          member = ((HideCanvas)canvas).getPartyMember(selectedNos[1]);
          int s = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
          switch (member.getItem(s).getType()) {
            case 0:
              menus[5][0] = "使う";
              break;
            case 1:
            case 2:
            case 3:
            case 4:
              menus[5][0] = "装備";
              break;
          }
        }

        break;
      case 5:
        switch (selectedNos[5]) {
          case 0:
            int l = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
            member = ((HideCanvas)canvas)
                     .getPartyMember(selectedNos[1]);
            int type = member.getItem(l).getType();
            switch (type) {
              case 0:
                focusedWinNo = 7;
                focusedWinNoPrev = focusedWinNo;
                seq[depth++] = focusedWinNo;
                selectedNos[focusedWinNo] = 0;
                break;
              case 1:
              case 2:
              case 3:
              case 4:
                int k = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
                member.getItem(k).use(selectedNos[2],
                ((HideCanvas)canvas).getPartyMember(selectedNos[1]));
                back();
                break;
            }
            break;
          case 1:
            focusedWinNo = 7;
            focusedWinNoPrev = focusedWinNo;
            seq[depth++] = focusedWinNo;
            selectedNos[focusedWinNo] = 0;
            break;
          case 2: 
            focusedWinNo = 6;
            focusedWinNoPrev = focusedWinNo;
            seq[depth++] = focusedWinNo;
            selectedNos[focusedWinNo] = 1;
            break;
        }
        break;
        case 6:
          switch (selectedNos[6]) {
            case 0:
              int m = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
              ((HideCanvas)canvas).getPartyMember(selectedNos[1]).discardItem(m);
              back();
              back();
              break;
            case 1:
              back();
              break;
          }
          break;
        case 7:
          switch (selectedNos[5]) {
            case 0:
              int k = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
              member.getItem(k).use(selectedNos[2],
              ((HideCanvas)canvas).getPartyMember(selectedNos[7]));
              back();
              back();
              break;
            case 1:
              if (selectedNos[7] != selectedNos[1]) {
                int m = selectedNos[2] + pageNo[2] * NUM_ITEMS_IN_A_PAGE;
                if (((HideCanvas)canvas).getPartyMember(selectedNos[7]).receiveItem(
                     ((HideCanvas)canvas).getPartyMember(selectedNos[1])
                                         .getItem(m)))
                  {
                    ((HideCanvas)canvas).getPartyMember(selectedNos[1]).discardItem(m);
                    back();
                    back();
                }
              }
              break;
          }
          break;
    }
  }

  void drawWindows(Graphics g, int k) {
//  g.setColor(255,0,0);
//  g.drawString("(d:"+depth+",f:"+focusedWinNo+","+visible+")",100,100,LT);
    g.setColor(255,255,255);
    g.fillRect(xs[k],ys[k],widths[k],heights[k]);
    g.setColor(0,0,0);
    g.fillRect(xs[k]+FRM_W, ys[k]+FRM_W, widths[k]-FRM_W*2,heights[k]-FRM_W*2);
    g.setColor(255,255,255);
    switch (k) {
      case 7:
      case 1:
        int num = ((HideCanvas)canvas).getPartyNum();
        menus[k]   = new String[num];
        heights[k] = 8+15*num;
        for (int i=0; i<num; i++) {
          menus[k][i] = ((HideCanvas)canvas).getPartyMember(i).getName();
        }
      case 0:
        if (k == 0) {
          g.fillRect(subx[2],suby[2],subwidth[2],subheight[2]);
          g.setColor(0,0,0);
          g.fillRect(subx[2]+FRM_W,suby[2]+FRM_W,subwidth[2]-FRM_W*2,subheight[2]-FRM_W*2);
//((Hid  eCanvas)hidec).getPartyMember(selectedNos[1])
          g.setColor(255,255,255);
          partyNum = ((HideCanvas)canvas).getPartyNum();
          g.drawLine(subx[2],suby[2]+15+2+1,subx[2]+subwidth[2]-FRM_W,suby[2]+15+2+1);
          for (int j=0; j<partyNum; j++) {
            g.drawString(((HideCanvas)canvas).getPartyMember(j).getName(),
                         subx[2]+6+j*subwidth[2]/partyNum, suby[2]+4, LT);
            g.drawString("HP",subx[2]+6+j*subwidth[2]/partyNum, suby[2]+6+15+2, LT);
            g.drawString(""+((HideCanvas)canvas).getPartyMember(j).getHP(),
                         subx[2]+j*subwidth[2]/partyNum+fshw*9, suby[2]+6+15+1, RT);
            g.drawString("MP",subx[2]+6+j*subwidth[2]/partyNum, suby[2]+6+30+1, LT);
            g.drawString(""+((HideCanvas)canvas).getPartyMember(j).getMP(),
                         subx[2]+j*subwidth[2]/partyNum+fshw*9, suby[2]+6+30+1, RT);
            g.drawString("Lv:",subx[2]+6+j*subwidth[2]/partyNum, suby[2]+6+45+1, LT);
            g.drawString(""+((HideCanvas)canvas).getPartyMember(j).getMP(),
                         subx[2]+j*subwidth[2]/partyNum+fshw*9, suby[2]+6+45+1, RT);
          }
        }
      case 5:
      case 6:
        for (int i=0; i<menus[k].length; i++) {
          if (i == selectedNos[k]) {
            g.fillRect(xs[k]+5,ys[k]+4+15*i,widths[k]-10,15);
            g.setColor(0,0,0);
          }
          else {
            g.setColor(255,255,255);
          }
          g.drawString(menus[k][i], xs[k]+6, ys[k]+6+15*i, LT);
        }
        break;
      case 2:
        menus[k] = ((HideCanvas)canvas).getPartyMember(selectedNos[1]).getItemsName();
        for (int i=0; i<NUM_ITEMS_IN_A_PAGE; i++) {
          if (i == selectedNos[k]) {
            g.fillRect(xs[k]+5,ys[k]+4+15*i,widths[k]-10,15);
            g.fillRect(subx[1],suby[1],subwidth[1],subheight[1]);
            g.setColor(0,0,0);
            g.fillRect(subx[1]+FRM_W,suby[1]+FRM_W,subwidth[1]-FRM_W*2,subheight[1]-FRM_W*2);
          }
          else {
            g.setColor(255,255,255);
          }
          if (menus[k][pageNo[k]*NUM_ITEMS_IN_A_PAGE+i] == "") continue;
          g.drawString(menus[k][pageNo[k]*NUM_ITEMS_IN_A_PAGE+i],
                       xs[k]+6, ys[k]+6+15*i, LT);
        }
        g.setColor(255,255,255);
        g.drawString((pageNo[k]+1)+"/2", subx[1]+6, suby[1]+6, LT);
        break;
      case 3:
        menus[k] = ((HideCanvas)canvas).getPartyMember(selectedNos[1]).getSkills();
        for (int i=0; i<NUM_SKILLS_IN_A_PAGE; i++) {
          if (i == selectedNos[k]) {
            g.fillRect(xs[k]+5,ys[k]+4+15*i,widths[k]-10,15);
            g.fillRect(subx[1],suby[1],subwidth[1],subheight[1]);
            g.setColor(0,0,0);
            g.fillRect(subx[1]+FRM_W,suby[1]+FRM_W,subwidth[1]-FRM_W*2,subheight[1]-FRM_W*2);
          }
          else {
            g.setColor(255,255,255);
          }
          if (menus[k][pageNo[k]*NUM_SKILLS_IN_A_PAGE+i] == "") continue;
          g.drawString(menus[k][pageNo[k]*NUM_SKILLS_IN_A_PAGE+i],
                       xs[k]+6, ys[k]+6+15*i, LT);
        }
        g.setColor(255,255,255);
        g.drawString((pageNo[k]+1)+"/2", subx[1]+6, suby[1]+6, LT);
        break;
      case 4:
        member = ((HideCanvas)canvas).getPartyMember(selectedNos[1]);
        int[] status = member.getStatus();
        int strw = (int)(fs.stringWidth("素早さ   0000"));
        g.setColor(255,255,255);
        g.fillRect(subx[1],suby[1],subwidth[1],subheight[1]);
        g.setColor(0,0,0);
        g.fillRect(subx[1]+FRM_W,suby[1]+FRM_W,subwidth[1]-FRM_W*2,subheight[1]-FRM_W*2);
        g.setColor(255,255,255);
        g.drawString((pageNo[k]+1)+"/2", subx[1]+6, suby[1]+6, LT);

        if (pageNo[k] == 0) {
          g.drawString(member.getName(),xs[k]+6,ys[k]+6,LT);
//        g.drawString(((HideCanvas)canvas).getPartyMember(selectedNos[1]).getName(),
//                     xs[k]+6, ys[k]+6,LT);
          for (int i=0; i<3; i++) {
            g.drawString(menus[k][i], xs[k]+6, ys[k]+6+15*(i+1), LT);
            g.drawString(""+status[i], xs[k]+6+strw, ys[k]+6+15*(i+1),
                         Graphics.RIGHT|Graphics.TOP);
          }
          g.drawLine(xs[k]+FRM_W,ys[k]+3+15*5,xs[k]+widths[k]-FRM_W,ys[k]+3+15*5);
          weap = member.getWeap();
          for (int i=0; i<weap.length; i++) {
            if (weap[i] != null) {
              g.drawString("E "+weap[i].getName(), xs[k]+6, ys[k]+6+15*(i+5), LT);
            }
          }
        }
        else if (pageNo[k] == 1) {
          for (int i=3; i<menus[k].length; i++) {
            g.drawString(menus[k][i], xs[k]+6, ys[k]+6+15*(i-3), LT);
            g.drawString(""+status[i], xs[k]+6+strw, ys[k]+6+15*(i-3),
                         Graphics.RIGHT|Graphics.TOP);
          }
        }
    }
  }

}
