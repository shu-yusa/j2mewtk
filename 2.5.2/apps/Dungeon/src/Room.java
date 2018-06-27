public class Room {
  private static final int TILE2PIXEL = 16;
  private int roomNumber;
  private int rect_tx, rect_ty;
  private int rect_px, rect_py;
  private int rectWidth;
  private int rectHeight;
  private int room_tx, room_ty;
  private int room_px, room_py;
  private int roomWidth;
  private int roomHeight;
  private boolean splitedH = false;
  private boolean splitedV = false;
  private boolean splitable = true;

  Room(int roomNumber) {
    this.roomNumber = roomNumber;
  }

  Room(int roomNumber, int tx, int ty, int width, int height) {
    this(roomNumber);
    rect_tx = tx;
    rect_ty = ty;
    rectWidth = width;
    rectHeight = height;
    rect_px = tx * TILE2PIXEL;
    rect_py = ty * TILE2PIXEL;
    room_tx = 0;
    room_ty = 0;
    roomWidth  = 0;
    roomHeight = 0;
  }

  int getRoomNumber() {
    return roomNumber;
  }

  int getRectTx() {
    return rect_tx;
  }

  int getRectTy() {
    return rect_ty;
  }

  int getRectPx() {
    return rect_px;
  }

  int getRectPy() {
    return rect_py;
  }

  int getRoomTx() {
    return room_tx;
  }

  int getRoomTy() {
    return room_ty;
  }

  int getRoomPx() {
    return room_px;
  }

  int getRoomPy() {
    return room_py;
  }

  int getRectWidth() {
    return rectWidth;
  }

  int getRectHeight() {
    return rectHeight;
  }

  int getRoomWidth() {
    return roomWidth;
  }

  int getRoomHeight() {
    return roomHeight;
  }

  void setRectPosition(int tx, int ty) {
    rect_tx = tx;
    rect_ty = ty;
    rect_px = tx * TILE2PIXEL;
    rect_py = ty * TILE2PIXEL;
  }

  void setRectWidth(int width) {
    rectWidth = width;
  }

  void setRectHeight(int height) {
    rectHeight = height;
  }

  void setRoomPosition(int tx, int ty) {
    room_tx = tx;
    room_ty = ty;
    room_px = tx * TILE2PIXEL;
    room_py = ty * TILE2PIXEL;
  }

  void setRoomWidth(int width) {
    roomWidth = width;
  }

  void setRoomHeight(int height) {
    roomHeight = height;
  }

  boolean isSplitable() {
    return splitable;
  }

  boolean hasSplitedH() {
    return splitedH;
  }

  boolean hasSplitedV() {
    return splitedV;
  }

  void notifySplitedH() {
    splitedH = true;
    if (splitedV) splitable = false;
  }

  void notifySplitedV() {
    splitedV = true;
    if (splitedH) splitable = false;
  }

}
