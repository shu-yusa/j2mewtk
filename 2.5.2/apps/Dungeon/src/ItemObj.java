import java.util.Random;

public class ItemObj {
  int ID;
  int type;
  int ability;
  int whichStatus;
  String name;
  String msg1;
  Myself owner;

  ItemObj(int id, int type, String name, int status, int ability, String msg1) {
    ID = id;
    whichStatus = status;
    this.ability = ability;
    this.type = type;
    this.name = name;
    this.msg1 = msg1;
  }

  ItemObj(ItemObj item) {
    ID      = item.getID();
    ability = item.getAbility();
    whichStatus = item.getWhichStatus();
    type    = item.getType();
    name    = item.getName();
    msg1    = item.getMsg1();
  }

  int getID() {
    return ID;
  }

  int getWhichStatus() {
    return whichStatus;
  }

  int getAbility() {
    return ability;
  }

  int getType() {
    return type;
  }
  
  String getName() {
    return name;
  }

  public String toString() {
    return name;
  }

  String getMsg1() {
    return msg1;
  }

  String statusToString(int status) {
    switch (status) {
      case 1: return "最大HP";
      case 2: return "最大MP";
      case 3: return "HP";
      case 4: return "MP";
      case 5: return "力";
      case 6: return "守り";
      case 7: return "素早さ";
    }
    return "";
  }

  void setOwner(Myself owner) {
    this.owner = owner;
  }

  void use(int selectedNo, Myself target) {
    switch (type) {
      case 0:
        target.addStatus(whichStatus, ability);
        HideSeek.getCanvas().getMsgWin()
          .setY(8*2+15*12+2-HideSeek.getCanvas().getMsgWin().getHeight());
        HideSeek.getCanvas().getMsgWin()
          .setMessage(target.getName()+"の"+statusToString(whichStatus)
                                      +"が"+getAbility()+msg1);
        HideSeek.getCanvas().getMsgWin().show();
        owner.discardItem(selectedNo);
        break;
      case 1:
      case 2:
      case 3:
      case 4:
//      target.addStatus(whichStatus, ability);
        if (target.equip(type-1,this, selectedNo)) {
          HideSeek.getCanvas().getMsgWin()
            .setMessage(target.getName()+"は"+name+"を装備した.");
        }
        else {
          HideSeek.getCanvas().getMsgWin()
            .setMessage(target.getName()+"は"+name+"をはずした.");
        }
        HideSeek.getCanvas().getMsgWin()
          .setY(8*2+15*12+2-HideSeek.getCanvas().getMsgWin().getHeight());
        HideSeek.getCanvas().getMsgWin().show();
        break;
      default:
        break;
    }
  }
}
