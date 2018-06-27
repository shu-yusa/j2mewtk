import javax.microedition.lcdui.*;
import java.util.Random;

public class StatusManager {
  Myself[] party;

  void addParty(Myself[] party) {
    this.party = party;
  }

  void heal(int n, int plusHP) {
    party[n].addHP(plusHP);
  }

}
