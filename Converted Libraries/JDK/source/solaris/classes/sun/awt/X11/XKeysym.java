package sun.awt.X11;
import java.util.Hashtable;
import sun.misc.Unsafe;
import sun.util.logging.PlatformLogger;
public class XKeysym {
  public static void main(  String args[]){
    System.out.println("Cyrillc zhe:" + convertKeysym(0x06d6,0));
    System.out.println("Arabic sheen:" + convertKeysym(0x05d4,0));
    System.out.println("Latin a breve:" + convertKeysym(0x01e3,0));
    System.out.println("Latin f:" + convertKeysym(0x066,0));
    System.out.println("Backspace:" + Integer.toHexString(convertKeysym(0xff08,0)));
    System.out.println("Ctrl+f:" + Integer.toHexString(convertKeysym(0x066,XConstants.ControlMask)));
  }
  private XKeysym(){
  }
static class Keysym2JavaKeycode {
    int jkeycode;
    int keyLocation;
    int getJavaKeycode(){
      return jkeycode;
    }
    int getKeyLocation(){
      return keyLocation;
    }
    Keysym2JavaKeycode(    int jk,    int loc){
      jkeycode=jk;
      keyLocation=loc;
    }
  }
  private static Unsafe unsafe=XlibWrapper.unsafe;
  static Hashtable<Long,Keysym2JavaKeycode> keysym2JavaKeycodeHash=new Hashtable<Long,Keysym2JavaKeycode>();
  static Hashtable<Long,Character> keysym2UCSHash=new Hashtable<Long,Character>();
  static Hashtable<Long,Long> uppercaseHash=new Hashtable<Long,Long>();
  static Hashtable<Integer,Long> javaKeycode2KeysymHash=new Hashtable<Integer,Long>();
  static long keysym_lowercase=unsafe.allocateMemory(Native.getLongSize());
  static long keysym_uppercase=unsafe.allocateMemory(Native.getLongSize());
  static Keysym2JavaKeycode kanaLock=new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KANA_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD);
  private static PlatformLogger keyEventLog=PlatformLogger.getLogger("sun.awt.X11.kye.XKeysym");
  public static char convertKeysym(  long ks,  int state){
    if ((ks >= 0x0020 && ks <= 0x007e) || (ks >= 0x00a0 && ks <= 0x00ff)) {
      if ((state & XConstants.ControlMask) != 0) {
        if ((ks >= 'A' && ks <= ']') || (ks == '_') || (ks >= 'a' && ks <= 'z')) {
          ks&=0x1F;
        }
      }
      return (char)ks;
    }
    if ((ks & 0xff000000) == 0x01000000)     return (char)(ks & 0x00ffffff);
    Character ch=keysym2UCSHash.get(ks);
    return ch == null ? (char)0 : ch.charValue();
  }
  static long xkeycode2keysym_noxkb(  XKeyEvent ev,  int ndx){
    XToolkit.awtLock();
    try {
      return XlibWrapper.XKeycodeToKeysym(ev.get_display(),ev.get_keycode(),ndx);
    }
  finally {
      XToolkit.awtUnlock();
    }
  }
  static long xkeycode2keysym_xkb(  XKeyEvent ev,  int ndx){
    XToolkit.awtLock();
    try {
      int mods=ev.get_state();
      if ((ndx == 0) && ((mods & XConstants.ShiftMask) != 0)) {
        mods^=XConstants.ShiftMask;
      }
      long kbdDesc=XToolkit.getXKBKbdDesc();
      if (kbdDesc != 0) {
        XlibWrapper.XkbTranslateKeyCode(kbdDesc,ev.get_keycode(),mods,XlibWrapper.iarg1,XlibWrapper.larg3);
      }
 else {
        keyEventLog.fine("Thread race: Toolkit shutdown before the end of a key event processing.");
        return 0;
      }
      return Native.getLong(XlibWrapper.larg3);
    }
  finally {
      XToolkit.awtUnlock();
    }
  }
  static long xkeycode2keysym(  XKeyEvent ev,  int ndx){
    XToolkit.awtLock();
    try {
      if (XToolkit.canUseXKBCalls()) {
        return xkeycode2keysym_xkb(ev,ndx);
      }
 else {
        return xkeycode2keysym_noxkb(ev,ndx);
      }
    }
  finally {
      XToolkit.awtUnlock();
    }
  }
  static long xkeycode2primary_keysym(  XKeyEvent ev){
    return xkeycode2keysym(ev,0);
  }
  public static boolean isKPEvent(  XKeyEvent ev){
    int ndx=XToolkit.isXsunKPBehavior() && !XToolkit.isXKBenabled() ? 2 : 1;
    XToolkit.awtLock();
    try {
      return (XlibWrapper.IsKeypadKey(XlibWrapper.XKeycodeToKeysym(ev.get_display(),ev.get_keycode(),ndx)) || XlibWrapper.IsKeypadKey(XlibWrapper.XKeycodeToKeysym(ev.get_display(),ev.get_keycode(),0)));
    }
  finally {
      XToolkit.awtUnlock();
    }
  }
  /** 
 * Return uppercase keysym correspondent to a given keysym.
 * If input keysym does not belong to any lower/uppercase pair, return -1.
 */
  public static long getUppercaseAlphabetic(  long keysym){
    long lc=-1;
    long uc=-1;
    Long stored=uppercaseHash.get(keysym);
    if (stored != null) {
      return stored.longValue();
    }
    XToolkit.awtLock();
    try {
      XlibWrapper.XConvertCase(keysym,keysym_lowercase,keysym_uppercase);
      lc=Native.getLong(keysym_lowercase);
      uc=Native.getLong(keysym_uppercase);
      if (lc == uc) {
        uc=-1;
      }
      uppercaseHash.put(keysym,uc);
    }
  finally {
      XToolkit.awtUnlock();
    }
    return uc;
  }
  /** 
 * Get a keypad keysym derived from a keycode.
 * I do not check if this is a keypad event, I just presume it.
 */
  private static long getKeypadKeysym(  XKeyEvent ev){
    int ndx=0;
    long keysym=XConstants.NoSymbol;
    if (XToolkit.isXsunKPBehavior() && !XToolkit.isXKBenabled()) {
      if ((ev.get_state() & XConstants.ShiftMask) != 0) {
        ndx=3;
        keysym=xkeycode2keysym(ev,ndx);
      }
 else {
        ndx=2;
        keysym=xkeycode2keysym(ev,ndx);
      }
    }
 else {
      if ((ev.get_state() & XConstants.ShiftMask) != 0 || ((ev.get_state() & XConstants.LockMask) != 0 && (XToolkit.modLockIsShiftLock != 0))) {
        ndx=0;
        keysym=xkeycode2keysym(ev,ndx);
      }
 else {
        ndx=1;
        keysym=xkeycode2keysym(ev,ndx);
      }
    }
    return keysym;
  }
  /** 
 * Return java.awt.KeyEvent constant meaning (Java) keycode, derived from X keysym.
 * Some keysyms maps to more than one keycode, these would require extra processing.
 */
  static Keysym2JavaKeycode getJavaKeycode(  long keysym){
    if (keysym == XKeySymConstants.XK_Mode_switch) {
      if (XToolkit.isKanaKeyboard()) {
        return kanaLock;
      }
    }
 else     if (keysym == XKeySymConstants.XK_L1) {
      if (XToolkit.isSunKeyboard()) {
        keysym=XKeySymConstants.SunXK_Stop;
      }
    }
 else     if (keysym == XKeySymConstants.XK_L2) {
      if (XToolkit.isSunKeyboard()) {
        keysym=XKeySymConstants.SunXK_Again;
      }
    }
    return keysym2JavaKeycodeHash.get(keysym);
  }
  /** 
 * Return java.awt.KeyEvent constant meaning (Java) keycode, derived from X Window KeyEvent.
 * Algorithm is, extract via XKeycodeToKeysym  a proper keysym according to Xlib spec rules and
 * err exceptions, then search a java keycode in a table.
 */
  static Keysym2JavaKeycode getJavaKeycode(  XKeyEvent ev){
    long keysym=XConstants.NoSymbol;
    int ndx=0;
    if ((ev.get_state() & XToolkit.numLockMask) != 0 && isKPEvent(ev)) {
      keysym=getKeypadKeysym(ev);
    }
 else {
      ndx=0;
      keysym=xkeycode2keysym(ev,ndx);
    }
    Keysym2JavaKeycode jkc=getJavaKeycode(keysym);
    return jkc;
  }
  static int getJavaKeycodeOnly(  XKeyEvent ev){
    Keysym2JavaKeycode jkc=getJavaKeycode(ev);
    return jkc == null ? java.awt.event.KeyEvent.VK_UNDEFINED : jkc.getJavaKeycode();
  }
  /** 
 * Return an integer java keycode apprx as it was before extending keycodes range.
 * This call would ignore for instance XKB and process whatever is on the bottom
 * of keysym stack. Result will not depend on actual locale, will differ between
 * dual/multiple keyboard setup systems (e.g. English+Russian vs French+Russian)
 * but will be someway compatible with old releases.
 */
  static int getLegacyJavaKeycodeOnly(  XKeyEvent ev){
    long keysym=XConstants.NoSymbol;
    int ndx=0;
    if ((ev.get_state() & XToolkit.numLockMask) != 0 && isKPEvent(ev)) {
      keysym=getKeypadKeysym(ev);
    }
 else {
      ndx=0;
      keysym=xkeycode2keysym_noxkb(ev,ndx);
    }
    Keysym2JavaKeycode jkc=getJavaKeycode(keysym);
    return jkc == null ? java.awt.event.KeyEvent.VK_UNDEFINED : jkc.getJavaKeycode();
  }
  static long javaKeycode2Keysym(  int jkey){
    Long ks=javaKeycode2KeysymHash.get(jkey);
    return (ks == null ? 0 : ks.longValue());
  }
  /** 
 * Return keysym derived from a keycode and modifiers.
 * Usually an input method does this. However non-system input methods (e.g. Java IMs) do not.
 * For rules, see "Xlib - C Language X Interface",
 * MIT X Consortium Standard
 * X Version 11, Release 6
 * Ch. 12.7
 * XXX TODO: or maybe not to do: process Mode Lock and therefore
 * not only 0-th and 1-st but 2-nd and 3-rd keysyms for a keystroke.
 */
  static long getKeysym(  XKeyEvent ev){
    long keysym=XConstants.NoSymbol;
    long uppercaseKeysym=XConstants.NoSymbol;
    int ndx=0;
    boolean getUppercase=false;
    if ((ev.get_state() & XToolkit.numLockMask) != 0 && isKPEvent(ev)) {
      keysym=getKeypadKeysym(ev);
    }
 else {
      if ((ev.get_state() & XConstants.ShiftMask) == 0) {
        if ((ev.get_state() & XConstants.LockMask) == 0) {
          ndx=0;
          getUppercase=false;
        }
 else         if ((ev.get_state() & XConstants.LockMask) != 0 && (XToolkit.modLockIsShiftLock == 0)) {
          ndx=0;
          getUppercase=true;
        }
 else         if ((ev.get_state() & XConstants.LockMask) != 0 && (XToolkit.modLockIsShiftLock != 0)) {
          ndx=1;
          getUppercase=false;
        }
      }
 else {
        if ((ev.get_state() & XConstants.LockMask) != 0 && (XToolkit.modLockIsShiftLock == 0)) {
          ndx=1;
          getUppercase=true;
        }
 else {
          ndx=1;
          getUppercase=false;
        }
      }
      keysym=xkeycode2keysym(ev,ndx);
      if (getUppercase && (uppercaseKeysym=getUppercaseAlphabetic(keysym)) != -1) {
        keysym=uppercaseKeysym;
      }
    }
    return keysym;
  }
static {
    keysym2UCSHash.put((long)0xFF08,(char)0x0008);
    keysym2UCSHash.put((long)0xFF09,(char)0x0009);
    keysym2UCSHash.put((long)0xFF0A,(char)0x000a);
    keysym2UCSHash.put((long)0xFF0B,(char)0x000b);
    keysym2UCSHash.put((long)0xFF0D,(char)0x000a);
    keysym2UCSHash.put((long)0xFF1B,(char)0x001B);
    keysym2UCSHash.put((long)0xFFFF,(char)0x007F);
    keysym2UCSHash.put((long)0xFF80,(char)0x0020);
    keysym2UCSHash.put((long)0xFF89,(char)0x0009);
    keysym2UCSHash.put((long)0xFF8D,(char)0x000A);
    keysym2UCSHash.put((long)0xFF9F,(char)0x007F);
    keysym2UCSHash.put((long)0xFFBD,(char)0x003d);
    keysym2UCSHash.put((long)0xFFAA,(char)0x002a);
    keysym2UCSHash.put((long)0xFFAB,(char)0x002b);
    keysym2UCSHash.put((long)0xFFAC,(char)0x002c);
    keysym2UCSHash.put((long)0xFFAD,(char)0x002d);
    keysym2UCSHash.put((long)0xFFAE,(char)0x002e);
    keysym2UCSHash.put((long)0xFFAF,(char)0x002f);
    keysym2UCSHash.put((long)0xFFB0,(char)0x0030);
    keysym2UCSHash.put((long)0xFFB1,(char)0x0031);
    keysym2UCSHash.put((long)0xFFB2,(char)0x0032);
    keysym2UCSHash.put((long)0xFFB3,(char)0x0033);
    keysym2UCSHash.put((long)0xFFB4,(char)0x0034);
    keysym2UCSHash.put((long)0xFFB5,(char)0x0035);
    keysym2UCSHash.put((long)0xFFB6,(char)0x0036);
    keysym2UCSHash.put((long)0xFFB7,(char)0x0037);
    keysym2UCSHash.put((long)0xFFB8,(char)0x0038);
    keysym2UCSHash.put((long)0xFFB9,(char)0x0039);
    keysym2UCSHash.put((long)0xFE20,(char)0x0009);
    keysym2UCSHash.put((long)0x1a1,(char)0x0104);
    keysym2UCSHash.put((long)0x1a2,(char)0x02d8);
    keysym2UCSHash.put((long)0x1a3,(char)0x0141);
    keysym2UCSHash.put((long)0x1a5,(char)0x013d);
    keysym2UCSHash.put((long)0x1a6,(char)0x015a);
    keysym2UCSHash.put((long)0x1a9,(char)0x0160);
    keysym2UCSHash.put((long)0x1aa,(char)0x015e);
    keysym2UCSHash.put((long)0x1ab,(char)0x0164);
    keysym2UCSHash.put((long)0x1ac,(char)0x0179);
    keysym2UCSHash.put((long)0x1ae,(char)0x017d);
    keysym2UCSHash.put((long)0x1af,(char)0x017b);
    keysym2UCSHash.put((long)0x1b1,(char)0x0105);
    keysym2UCSHash.put((long)0x1b2,(char)0x02db);
    keysym2UCSHash.put((long)0x1b3,(char)0x0142);
    keysym2UCSHash.put((long)0x1b5,(char)0x013e);
    keysym2UCSHash.put((long)0x1b6,(char)0x015b);
    keysym2UCSHash.put((long)0x1b7,(char)0x02c7);
    keysym2UCSHash.put((long)0x1b9,(char)0x0161);
    keysym2UCSHash.put((long)0x1ba,(char)0x015f);
    keysym2UCSHash.put((long)0x1bb,(char)0x0165);
    keysym2UCSHash.put((long)0x1bc,(char)0x017a);
    keysym2UCSHash.put((long)0x1bd,(char)0x02dd);
    keysym2UCSHash.put((long)0x1be,(char)0x017e);
    keysym2UCSHash.put((long)0x1bf,(char)0x017c);
    keysym2UCSHash.put((long)0x1c0,(char)0x0154);
    keysym2UCSHash.put((long)0x1c3,(char)0x0102);
    keysym2UCSHash.put((long)0x1c5,(char)0x0139);
    keysym2UCSHash.put((long)0x1c6,(char)0x0106);
    keysym2UCSHash.put((long)0x1c8,(char)0x010c);
    keysym2UCSHash.put((long)0x1ca,(char)0x0118);
    keysym2UCSHash.put((long)0x1cc,(char)0x011a);
    keysym2UCSHash.put((long)0x1cf,(char)0x010e);
    keysym2UCSHash.put((long)0x1d0,(char)0x0110);
    keysym2UCSHash.put((long)0x1d1,(char)0x0143);
    keysym2UCSHash.put((long)0x1d2,(char)0x0147);
    keysym2UCSHash.put((long)0x1d5,(char)0x0150);
    keysym2UCSHash.put((long)0x1d8,(char)0x0158);
    keysym2UCSHash.put((long)0x1d9,(char)0x016e);
    keysym2UCSHash.put((long)0x1db,(char)0x0170);
    keysym2UCSHash.put((long)0x1de,(char)0x0162);
    keysym2UCSHash.put((long)0x1e0,(char)0x0155);
    keysym2UCSHash.put((long)0x1e3,(char)0x0103);
    keysym2UCSHash.put((long)0x1e5,(char)0x013a);
    keysym2UCSHash.put((long)0x1e6,(char)0x0107);
    keysym2UCSHash.put((long)0x1e8,(char)0x010d);
    keysym2UCSHash.put((long)0x1ea,(char)0x0119);
    keysym2UCSHash.put((long)0x1ec,(char)0x011b);
    keysym2UCSHash.put((long)0x1ef,(char)0x010f);
    keysym2UCSHash.put((long)0x1f0,(char)0x0111);
    keysym2UCSHash.put((long)0x1f1,(char)0x0144);
    keysym2UCSHash.put((long)0x1f2,(char)0x0148);
    keysym2UCSHash.put((long)0x1f5,(char)0x0151);
    keysym2UCSHash.put((long)0x1fb,(char)0x0171);
    keysym2UCSHash.put((long)0x1f8,(char)0x0159);
    keysym2UCSHash.put((long)0x1f9,(char)0x016f);
    keysym2UCSHash.put((long)0x1fe,(char)0x0163);
    keysym2UCSHash.put((long)0x1ff,(char)0x02d9);
    keysym2UCSHash.put((long)0x2a1,(char)0x0126);
    keysym2UCSHash.put((long)0x2a6,(char)0x0124);
    keysym2UCSHash.put((long)0x2a9,(char)0x0130);
    keysym2UCSHash.put((long)0x2ab,(char)0x011e);
    keysym2UCSHash.put((long)0x2ac,(char)0x0134);
    keysym2UCSHash.put((long)0x2b1,(char)0x0127);
    keysym2UCSHash.put((long)0x2b6,(char)0x0125);
    keysym2UCSHash.put((long)0x2b9,(char)0x0131);
    keysym2UCSHash.put((long)0x2bb,(char)0x011f);
    keysym2UCSHash.put((long)0x2bc,(char)0x0135);
    keysym2UCSHash.put((long)0x2c5,(char)0x010a);
    keysym2UCSHash.put((long)0x2c6,(char)0x0108);
    keysym2UCSHash.put((long)0x2d5,(char)0x0120);
    keysym2UCSHash.put((long)0x2d8,(char)0x011c);
    keysym2UCSHash.put((long)0x2dd,(char)0x016c);
    keysym2UCSHash.put((long)0x2de,(char)0x015c);
    keysym2UCSHash.put((long)0x2e5,(char)0x010b);
    keysym2UCSHash.put((long)0x2e6,(char)0x0109);
    keysym2UCSHash.put((long)0x2f5,(char)0x0121);
    keysym2UCSHash.put((long)0x2f8,(char)0x011d);
    keysym2UCSHash.put((long)0x2fd,(char)0x016d);
    keysym2UCSHash.put((long)0x2fe,(char)0x015d);
    keysym2UCSHash.put((long)0x3a2,(char)0x0138);
    keysym2UCSHash.put((long)0x3a3,(char)0x0156);
    keysym2UCSHash.put((long)0x3a5,(char)0x0128);
    keysym2UCSHash.put((long)0x3a6,(char)0x013b);
    keysym2UCSHash.put((long)0x3aa,(char)0x0112);
    keysym2UCSHash.put((long)0x3ab,(char)0x0122);
    keysym2UCSHash.put((long)0x3ac,(char)0x0166);
    keysym2UCSHash.put((long)0x3b3,(char)0x0157);
    keysym2UCSHash.put((long)0x3b5,(char)0x0129);
    keysym2UCSHash.put((long)0x3b6,(char)0x013c);
    keysym2UCSHash.put((long)0x3ba,(char)0x0113);
    keysym2UCSHash.put((long)0x3bb,(char)0x0123);
    keysym2UCSHash.put((long)0x3bc,(char)0x0167);
    keysym2UCSHash.put((long)0x3bd,(char)0x014a);
    keysym2UCSHash.put((long)0x3bf,(char)0x014b);
    keysym2UCSHash.put((long)0x3c0,(char)0x0100);
    keysym2UCSHash.put((long)0x3c7,(char)0x012e);
    keysym2UCSHash.put((long)0x3cc,(char)0x0116);
    keysym2UCSHash.put((long)0x3cf,(char)0x012a);
    keysym2UCSHash.put((long)0x3d1,(char)0x0145);
    keysym2UCSHash.put((long)0x3d2,(char)0x014c);
    keysym2UCSHash.put((long)0x3d3,(char)0x0136);
    keysym2UCSHash.put((long)0x3d9,(char)0x0172);
    keysym2UCSHash.put((long)0x3dd,(char)0x0168);
    keysym2UCSHash.put((long)0x3de,(char)0x016a);
    keysym2UCSHash.put((long)0x3e0,(char)0x0101);
    keysym2UCSHash.put((long)0x3e7,(char)0x012f);
    keysym2UCSHash.put((long)0x3ec,(char)0x0117);
    keysym2UCSHash.put((long)0x3ef,(char)0x012b);
    keysym2UCSHash.put((long)0x3f1,(char)0x0146);
    keysym2UCSHash.put((long)0x3f2,(char)0x014d);
    keysym2UCSHash.put((long)0x3f3,(char)0x0137);
    keysym2UCSHash.put((long)0x3f9,(char)0x0173);
    keysym2UCSHash.put((long)0x3fd,(char)0x0169);
    keysym2UCSHash.put((long)0x3fe,(char)0x016b);
    keysym2UCSHash.put((long)0x12a1,(char)0x1e02);
    keysym2UCSHash.put((long)0x12a2,(char)0x1e03);
    keysym2UCSHash.put((long)0x12a6,(char)0x1e0a);
    keysym2UCSHash.put((long)0x12a8,(char)0x1e80);
    keysym2UCSHash.put((long)0x12aa,(char)0x1e82);
    keysym2UCSHash.put((long)0x12ab,(char)0x1e0b);
    keysym2UCSHash.put((long)0x12ac,(char)0x1ef2);
    keysym2UCSHash.put((long)0x12b0,(char)0x1e1e);
    keysym2UCSHash.put((long)0x12b1,(char)0x1e1f);
    keysym2UCSHash.put((long)0x12b4,(char)0x1e40);
    keysym2UCSHash.put((long)0x12b5,(char)0x1e41);
    keysym2UCSHash.put((long)0x12b7,(char)0x1e56);
    keysym2UCSHash.put((long)0x12b8,(char)0x1e81);
    keysym2UCSHash.put((long)0x12b9,(char)0x1e57);
    keysym2UCSHash.put((long)0x12ba,(char)0x1e83);
    keysym2UCSHash.put((long)0x12bb,(char)0x1e60);
    keysym2UCSHash.put((long)0x12bc,(char)0x1ef3);
    keysym2UCSHash.put((long)0x12bd,(char)0x1e84);
    keysym2UCSHash.put((long)0x12be,(char)0x1e85);
    keysym2UCSHash.put((long)0x12bf,(char)0x1e61);
    keysym2UCSHash.put((long)0x12d0,(char)0x017);
    keysym2UCSHash.put((long)0x12d7,(char)0x1e6a);
    keysym2UCSHash.put((long)0x12de,(char)0x0176);
    keysym2UCSHash.put((long)0x12f0,(char)0x0175);
    keysym2UCSHash.put((long)0x12f7,(char)0x1e6b);
    keysym2UCSHash.put((long)0x12fe,(char)0x0177);
    keysym2UCSHash.put((long)0x13bc,(char)0x0152);
    keysym2UCSHash.put((long)0x13bd,(char)0x0153);
    keysym2UCSHash.put((long)0x13be,(char)0x0178);
    keysym2UCSHash.put((long)0x47e,(char)0x203e);
    keysym2UCSHash.put((long)0x4a1,(char)0x3002);
    keysym2UCSHash.put((long)0x4a2,(char)0x300c);
    keysym2UCSHash.put((long)0x4a3,(char)0x300d);
    keysym2UCSHash.put((long)0x4a4,(char)0x3001);
    keysym2UCSHash.put((long)0x4a5,(char)0x30fb);
    keysym2UCSHash.put((long)0x4a6,(char)0x30f2);
    keysym2UCSHash.put((long)0x4a7,(char)0x30a1);
    keysym2UCSHash.put((long)0x4a8,(char)0x30a3);
    keysym2UCSHash.put((long)0x4a9,(char)0x30a5);
    keysym2UCSHash.put((long)0x4aa,(char)0x30a7);
    keysym2UCSHash.put((long)0x4ab,(char)0x30a9);
    keysym2UCSHash.put((long)0x4ac,(char)0x30e3);
    keysym2UCSHash.put((long)0x4ad,(char)0x30e5);
    keysym2UCSHash.put((long)0x4ae,(char)0x30e7);
    keysym2UCSHash.put((long)0x4af,(char)0x30c3);
    keysym2UCSHash.put((long)0x4b0,(char)0x30fc);
    keysym2UCSHash.put((long)0x4b1,(char)0x30a2);
    keysym2UCSHash.put((long)0x4b2,(char)0x30a4);
    keysym2UCSHash.put((long)0x4b3,(char)0x30a6);
    keysym2UCSHash.put((long)0x4b4,(char)0x30a8);
    keysym2UCSHash.put((long)0x4b5,(char)0x30aa);
    keysym2UCSHash.put((long)0x4b6,(char)0x30ab);
    keysym2UCSHash.put((long)0x4b7,(char)0x30ad);
    keysym2UCSHash.put((long)0x4b8,(char)0x30af);
    keysym2UCSHash.put((long)0x4b9,(char)0x30b1);
    keysym2UCSHash.put((long)0x4ba,(char)0x30b3);
    keysym2UCSHash.put((long)0x4bb,(char)0x30b5);
    keysym2UCSHash.put((long)0x4bc,(char)0x30b7);
    keysym2UCSHash.put((long)0x4bd,(char)0x30b9);
    keysym2UCSHash.put((long)0x4be,(char)0x30bb);
    keysym2UCSHash.put((long)0x4bf,(char)0x30bd);
    keysym2UCSHash.put((long)0x4c0,(char)0x30bf);
    keysym2UCSHash.put((long)0x4c1,(char)0x30c1);
    keysym2UCSHash.put((long)0x4c2,(char)0x30c4);
    keysym2UCSHash.put((long)0x4c3,(char)0x30c6);
    keysym2UCSHash.put((long)0x4c4,(char)0x30c8);
    keysym2UCSHash.put((long)0x4c5,(char)0x30ca);
    keysym2UCSHash.put((long)0x4c6,(char)0x30cb);
    keysym2UCSHash.put((long)0x4c7,(char)0x30cc);
    keysym2UCSHash.put((long)0x4c8,(char)0x30cd);
    keysym2UCSHash.put((long)0x4c9,(char)0x30ce);
    keysym2UCSHash.put((long)0x4ca,(char)0x30cf);
    keysym2UCSHash.put((long)0x4cb,(char)0x30d2);
    keysym2UCSHash.put((long)0x4cc,(char)0x30d5);
    keysym2UCSHash.put((long)0x4cd,(char)0x30d8);
    keysym2UCSHash.put((long)0x4ce,(char)0x30db);
    keysym2UCSHash.put((long)0x4cf,(char)0x30de);
    keysym2UCSHash.put((long)0x4d0,(char)0x30df);
    keysym2UCSHash.put((long)0x4d1,(char)0x30e0);
    keysym2UCSHash.put((long)0x4d2,(char)0x30e1);
    keysym2UCSHash.put((long)0x4d3,(char)0x30e2);
    keysym2UCSHash.put((long)0x4d4,(char)0x30e4);
    keysym2UCSHash.put((long)0x4d5,(char)0x30e6);
    keysym2UCSHash.put((long)0x4d6,(char)0x30e8);
    keysym2UCSHash.put((long)0x4d7,(char)0x30e9);
    keysym2UCSHash.put((long)0x4d8,(char)0x30ea);
    keysym2UCSHash.put((long)0x4d9,(char)0x30eb);
    keysym2UCSHash.put((long)0x4da,(char)0x30ec);
    keysym2UCSHash.put((long)0x4db,(char)0x30ed);
    keysym2UCSHash.put((long)0x4dc,(char)0x30ef);
    keysym2UCSHash.put((long)0x4dd,(char)0x30f3);
    keysym2UCSHash.put((long)0x4de,(char)0x309b);
    keysym2UCSHash.put((long)0x4df,(char)0x309c);
    keysym2UCSHash.put((long)0x590,(char)0x0670);
    keysym2UCSHash.put((long)0x591,(char)0x06f1);
    keysym2UCSHash.put((long)0x592,(char)0x06f2);
    keysym2UCSHash.put((long)0x593,(char)0x06f3);
    keysym2UCSHash.put((long)0x594,(char)0x06f4);
    keysym2UCSHash.put((long)0x595,(char)0x06f5);
    keysym2UCSHash.put((long)0x596,(char)0x06f6);
    keysym2UCSHash.put((long)0x597,(char)0x06f7);
    keysym2UCSHash.put((long)0x598,(char)0x06f8);
    keysym2UCSHash.put((long)0x599,(char)0x06f9);
    keysym2UCSHash.put((long)0x5a5,(char)0x066a);
    keysym2UCSHash.put((long)0x5a6,(char)0x0670);
    keysym2UCSHash.put((long)0x5a7,(char)0x0679);
    keysym2UCSHash.put((long)0x5a8,(char)0x067e);
    keysym2UCSHash.put((long)0x5a9,(char)0x0686);
    keysym2UCSHash.put((long)0x5aa,(char)0x0688);
    keysym2UCSHash.put((long)0x5ab,(char)0x0691);
    keysym2UCSHash.put((long)0x5ac,(char)0x060c);
    keysym2UCSHash.put((long)0x5ae,(char)0x06d4);
    keysym2UCSHash.put((long)0x5b0,(char)0x0660);
    keysym2UCSHash.put((long)0x5b1,(char)0x0661);
    keysym2UCSHash.put((long)0x5b2,(char)0x0662);
    keysym2UCSHash.put((long)0x5b3,(char)0x0663);
    keysym2UCSHash.put((long)0x5b4,(char)0x0664);
    keysym2UCSHash.put((long)0x5b5,(char)0x0665);
    keysym2UCSHash.put((long)0x5b6,(char)0x0666);
    keysym2UCSHash.put((long)0x5b7,(char)0x0667);
    keysym2UCSHash.put((long)0x5b8,(char)0x0668);
    keysym2UCSHash.put((long)0x5b9,(char)0x0669);
    keysym2UCSHash.put((long)0x5bb,(char)0x061b);
    keysym2UCSHash.put((long)0x5bf,(char)0x061f);
    keysym2UCSHash.put((long)0x5c1,(char)0x0621);
    keysym2UCSHash.put((long)0x5c2,(char)0x0622);
    keysym2UCSHash.put((long)0x5c3,(char)0x0623);
    keysym2UCSHash.put((long)0x5c4,(char)0x0624);
    keysym2UCSHash.put((long)0x5c5,(char)0x0625);
    keysym2UCSHash.put((long)0x5c6,(char)0x0626);
    keysym2UCSHash.put((long)0x5c7,(char)0x0627);
    keysym2UCSHash.put((long)0x5c8,(char)0x0628);
    keysym2UCSHash.put((long)0x5c9,(char)0x0629);
    keysym2UCSHash.put((long)0x5ca,(char)0x062a);
    keysym2UCSHash.put((long)0x5cb,(char)0x062b);
    keysym2UCSHash.put((long)0x5cc,(char)0x062c);
    keysym2UCSHash.put((long)0x5cd,(char)0x062d);
    keysym2UCSHash.put((long)0x5ce,(char)0x062e);
    keysym2UCSHash.put((long)0x5cf,(char)0x062f);
    keysym2UCSHash.put((long)0x5d0,(char)0x0630);
    keysym2UCSHash.put((long)0x5d1,(char)0x0631);
    keysym2UCSHash.put((long)0x5d2,(char)0x0632);
    keysym2UCSHash.put((long)0x5d3,(char)0x0633);
    keysym2UCSHash.put((long)0x5d4,(char)0x0634);
    keysym2UCSHash.put((long)0x5d5,(char)0x0635);
    keysym2UCSHash.put((long)0x5d6,(char)0x0636);
    keysym2UCSHash.put((long)0x5d7,(char)0x0637);
    keysym2UCSHash.put((long)0x5d8,(char)0x0638);
    keysym2UCSHash.put((long)0x5d9,(char)0x0639);
    keysym2UCSHash.put((long)0x5da,(char)0x063a);
    keysym2UCSHash.put((long)0x5e0,(char)0x0640);
    keysym2UCSHash.put((long)0x5e1,(char)0x0641);
    keysym2UCSHash.put((long)0x5e2,(char)0x0642);
    keysym2UCSHash.put((long)0x5e3,(char)0x0643);
    keysym2UCSHash.put((long)0x5e4,(char)0x0644);
    keysym2UCSHash.put((long)0x5e5,(char)0x0645);
    keysym2UCSHash.put((long)0x5e6,(char)0x0646);
    keysym2UCSHash.put((long)0x5e7,(char)0x0647);
    keysym2UCSHash.put((long)0x5e8,(char)0x0648);
    keysym2UCSHash.put((long)0x5e9,(char)0x0649);
    keysym2UCSHash.put((long)0x5ea,(char)0x064a);
    keysym2UCSHash.put((long)0x5eb,(char)0x064b);
    keysym2UCSHash.put((long)0x5ec,(char)0x064c);
    keysym2UCSHash.put((long)0x5ed,(char)0x064d);
    keysym2UCSHash.put((long)0x5ee,(char)0x064e);
    keysym2UCSHash.put((long)0x5ef,(char)0x064f);
    keysym2UCSHash.put((long)0x5f0,(char)0x0650);
    keysym2UCSHash.put((long)0x5f1,(char)0x0651);
    keysym2UCSHash.put((long)0x5f2,(char)0x0652);
    keysym2UCSHash.put((long)0x5f3,(char)0x0653);
    keysym2UCSHash.put((long)0x5f4,(char)0x0654);
    keysym2UCSHash.put((long)0x5f5,(char)0x0655);
    keysym2UCSHash.put((long)0x5f6,(char)0x0698);
    keysym2UCSHash.put((long)0x5f7,(char)0x06a4);
    keysym2UCSHash.put((long)0x5f8,(char)0x06a9);
    keysym2UCSHash.put((long)0x5f9,(char)0x06af);
    keysym2UCSHash.put((long)0x5fa,(char)0x06ba);
    keysym2UCSHash.put((long)0x5fb,(char)0x06be);
    keysym2UCSHash.put((long)0x5fc,(char)0x06cc);
    keysym2UCSHash.put((long)0x5fd,(char)0x06d2);
    keysym2UCSHash.put((long)0x5fe,(char)0x06c1);
    keysym2UCSHash.put((long)0x680,(char)0x0492);
    keysym2UCSHash.put((long)0x690,(char)0x0493);
    keysym2UCSHash.put((long)0x681,(char)0x0496);
    keysym2UCSHash.put((long)0x691,(char)0x0497);
    keysym2UCSHash.put((long)0x682,(char)0x049a);
    keysym2UCSHash.put((long)0x692,(char)0x049b);
    keysym2UCSHash.put((long)0x683,(char)0x049c);
    keysym2UCSHash.put((long)0x693,(char)0x049d);
    keysym2UCSHash.put((long)0x684,(char)0x04a2);
    keysym2UCSHash.put((long)0x694,(char)0x04a3);
    keysym2UCSHash.put((long)0x685,(char)0x04ae);
    keysym2UCSHash.put((long)0x695,(char)0x04af);
    keysym2UCSHash.put((long)0x686,(char)0x04b0);
    keysym2UCSHash.put((long)0x696,(char)0x04b1);
    keysym2UCSHash.put((long)0x687,(char)0x04b2);
    keysym2UCSHash.put((long)0x697,(char)0x04b3);
    keysym2UCSHash.put((long)0x688,(char)0x04b6);
    keysym2UCSHash.put((long)0x698,(char)0x04b7);
    keysym2UCSHash.put((long)0x689,(char)0x04b8);
    keysym2UCSHash.put((long)0x699,(char)0x04b9);
    keysym2UCSHash.put((long)0x68a,(char)0x04ba);
    keysym2UCSHash.put((long)0x69a,(char)0x04bb);
    keysym2UCSHash.put((long)0x68c,(char)0x04d8);
    keysym2UCSHash.put((long)0x69c,(char)0x04d9);
    keysym2UCSHash.put((long)0x68d,(char)0x04e2);
    keysym2UCSHash.put((long)0x69d,(char)0x04e3);
    keysym2UCSHash.put((long)0x68e,(char)0x04e8);
    keysym2UCSHash.put((long)0x69e,(char)0x04e9);
    keysym2UCSHash.put((long)0x68f,(char)0x04ee);
    keysym2UCSHash.put((long)0x69f,(char)0x04ef);
    keysym2UCSHash.put((long)0x6a1,(char)0x0452);
    keysym2UCSHash.put((long)0x6a2,(char)0x0453);
    keysym2UCSHash.put((long)0x6a3,(char)0x0451);
    keysym2UCSHash.put((long)0x6a4,(char)0x0454);
    keysym2UCSHash.put((long)0x6a5,(char)0x0455);
    keysym2UCSHash.put((long)0x6a6,(char)0x0456);
    keysym2UCSHash.put((long)0x6a7,(char)0x0457);
    keysym2UCSHash.put((long)0x6a8,(char)0x0458);
    keysym2UCSHash.put((long)0x6a9,(char)0x0459);
    keysym2UCSHash.put((long)0x6aa,(char)0x045a);
    keysym2UCSHash.put((long)0x6ab,(char)0x045b);
    keysym2UCSHash.put((long)0x6ac,(char)0x045c);
    keysym2UCSHash.put((long)0x6ad,(char)0x0491);
    keysym2UCSHash.put((long)0x6ae,(char)0x045e);
    keysym2UCSHash.put((long)0x6af,(char)0x045f);
    keysym2UCSHash.put((long)0x6b0,(char)0x2116);
    keysym2UCSHash.put((long)0x6b1,(char)0x0402);
    keysym2UCSHash.put((long)0x6b2,(char)0x0403);
    keysym2UCSHash.put((long)0x6b3,(char)0x0401);
    keysym2UCSHash.put((long)0x6b4,(char)0x0404);
    keysym2UCSHash.put((long)0x6b5,(char)0x0405);
    keysym2UCSHash.put((long)0x6b6,(char)0x0406);
    keysym2UCSHash.put((long)0x6b7,(char)0x0407);
    keysym2UCSHash.put((long)0x6b8,(char)0x0408);
    keysym2UCSHash.put((long)0x6b9,(char)0x0409);
    keysym2UCSHash.put((long)0x6ba,(char)0x040a);
    keysym2UCSHash.put((long)0x6bb,(char)0x040b);
    keysym2UCSHash.put((long)0x6bc,(char)0x040c);
    keysym2UCSHash.put((long)0x6bd,(char)0x0490);
    keysym2UCSHash.put((long)0x6be,(char)0x040e);
    keysym2UCSHash.put((long)0x6bf,(char)0x040f);
    keysym2UCSHash.put((long)0x6c0,(char)0x044e);
    keysym2UCSHash.put((long)0x6c1,(char)0x0430);
    keysym2UCSHash.put((long)0x6c2,(char)0x0431);
    keysym2UCSHash.put((long)0x6c3,(char)0x0446);
    keysym2UCSHash.put((long)0x6c4,(char)0x0434);
    keysym2UCSHash.put((long)0x6c5,(char)0x0435);
    keysym2UCSHash.put((long)0x6c6,(char)0x0444);
    keysym2UCSHash.put((long)0x6c7,(char)0x0433);
    keysym2UCSHash.put((long)0x6c8,(char)0x0445);
    keysym2UCSHash.put((long)0x6c9,(char)0x0438);
    keysym2UCSHash.put((long)0x6ca,(char)0x0439);
    keysym2UCSHash.put((long)0x6cb,(char)0x043a);
    keysym2UCSHash.put((long)0x6cc,(char)0x043b);
    keysym2UCSHash.put((long)0x6cd,(char)0x043c);
    keysym2UCSHash.put((long)0x6ce,(char)0x043d);
    keysym2UCSHash.put((long)0x6cf,(char)0x043e);
    keysym2UCSHash.put((long)0x6d0,(char)0x043f);
    keysym2UCSHash.put((long)0x6d1,(char)0x044f);
    keysym2UCSHash.put((long)0x6d2,(char)0x0440);
    keysym2UCSHash.put((long)0x6d3,(char)0x0441);
    keysym2UCSHash.put((long)0x6d4,(char)0x0442);
    keysym2UCSHash.put((long)0x6d5,(char)0x0443);
    keysym2UCSHash.put((long)0x6d6,(char)0x0436);
    keysym2UCSHash.put((long)0x6d7,(char)0x0432);
    keysym2UCSHash.put((long)0x6d8,(char)0x044c);
    keysym2UCSHash.put((long)0x6d9,(char)0x044b);
    keysym2UCSHash.put((long)0x6da,(char)0x0437);
    keysym2UCSHash.put((long)0x6db,(char)0x0448);
    keysym2UCSHash.put((long)0x6dc,(char)0x044d);
    keysym2UCSHash.put((long)0x6dd,(char)0x0449);
    keysym2UCSHash.put((long)0x6de,(char)0x0447);
    keysym2UCSHash.put((long)0x6df,(char)0x044a);
    keysym2UCSHash.put((long)0x6e0,(char)0x042e);
    keysym2UCSHash.put((long)0x6e1,(char)0x0410);
    keysym2UCSHash.put((long)0x6e2,(char)0x0411);
    keysym2UCSHash.put((long)0x6e3,(char)0x0426);
    keysym2UCSHash.put((long)0x6e4,(char)0x0414);
    keysym2UCSHash.put((long)0x6e5,(char)0x0415);
    keysym2UCSHash.put((long)0x6e6,(char)0x0424);
    keysym2UCSHash.put((long)0x6e7,(char)0x0413);
    keysym2UCSHash.put((long)0x6e8,(char)0x0425);
    keysym2UCSHash.put((long)0x6e9,(char)0x0418);
    keysym2UCSHash.put((long)0x6ea,(char)0x0419);
    keysym2UCSHash.put((long)0x6eb,(char)0x041a);
    keysym2UCSHash.put((long)0x6ec,(char)0x041b);
    keysym2UCSHash.put((long)0x6ed,(char)0x041c);
    keysym2UCSHash.put((long)0x6ee,(char)0x041d);
    keysym2UCSHash.put((long)0x6ef,(char)0x041e);
    keysym2UCSHash.put((long)0x6f0,(char)0x041f);
    keysym2UCSHash.put((long)0x6f1,(char)0x042f);
    keysym2UCSHash.put((long)0x6f2,(char)0x0420);
    keysym2UCSHash.put((long)0x6f3,(char)0x0421);
    keysym2UCSHash.put((long)0x6f4,(char)0x0422);
    keysym2UCSHash.put((long)0x6f5,(char)0x0423);
    keysym2UCSHash.put((long)0x6f6,(char)0x0416);
    keysym2UCSHash.put((long)0x6f7,(char)0x0412);
    keysym2UCSHash.put((long)0x6f8,(char)0x042c);
    keysym2UCSHash.put((long)0x6f9,(char)0x042b);
    keysym2UCSHash.put((long)0x6fa,(char)0x0417);
    keysym2UCSHash.put((long)0x6fb,(char)0x0428);
    keysym2UCSHash.put((long)0x6fc,(char)0x042d);
    keysym2UCSHash.put((long)0x6fd,(char)0x0429);
    keysym2UCSHash.put((long)0x6fe,(char)0x0427);
    keysym2UCSHash.put((long)0x6ff,(char)0x042a);
    keysym2UCSHash.put((long)0x7a1,(char)0x0386);
    keysym2UCSHash.put((long)0x7a2,(char)0x0388);
    keysym2UCSHash.put((long)0x7a3,(char)0x0389);
    keysym2UCSHash.put((long)0x7a4,(char)0x038a);
    keysym2UCSHash.put((long)0x7a5,(char)0x03aa);
    keysym2UCSHash.put((long)0x7a7,(char)0x038c);
    keysym2UCSHash.put((long)0x7a8,(char)0x038e);
    keysym2UCSHash.put((long)0x7a9,(char)0x03ab);
    keysym2UCSHash.put((long)0x7ab,(char)0x038f);
    keysym2UCSHash.put((long)0x7ae,(char)0x0385);
    keysym2UCSHash.put((long)0x7af,(char)0x2015);
    keysym2UCSHash.put((long)0x7b1,(char)0x03ac);
    keysym2UCSHash.put((long)0x7b2,(char)0x03ad);
    keysym2UCSHash.put((long)0x7b3,(char)0x03ae);
    keysym2UCSHash.put((long)0x7b4,(char)0x03af);
    keysym2UCSHash.put((long)0x7b5,(char)0x03ca);
    keysym2UCSHash.put((long)0x7b6,(char)0x0390);
    keysym2UCSHash.put((long)0x7b7,(char)0x03cc);
    keysym2UCSHash.put((long)0x7b8,(char)0x03cd);
    keysym2UCSHash.put((long)0x7b9,(char)0x03cb);
    keysym2UCSHash.put((long)0x7ba,(char)0x03b0);
    keysym2UCSHash.put((long)0x7bb,(char)0x03ce);
    keysym2UCSHash.put((long)0x7c1,(char)0x0391);
    keysym2UCSHash.put((long)0x7c2,(char)0x0392);
    keysym2UCSHash.put((long)0x7c3,(char)0x0393);
    keysym2UCSHash.put((long)0x7c4,(char)0x0394);
    keysym2UCSHash.put((long)0x7c5,(char)0x0395);
    keysym2UCSHash.put((long)0x7c6,(char)0x0396);
    keysym2UCSHash.put((long)0x7c7,(char)0x0397);
    keysym2UCSHash.put((long)0x7c8,(char)0x0398);
    keysym2UCSHash.put((long)0x7c9,(char)0x0399);
    keysym2UCSHash.put((long)0x7ca,(char)0x039a);
    keysym2UCSHash.put((long)0x7cb,(char)0x039b);
    keysym2UCSHash.put((long)0x7cc,(char)0x039c);
    keysym2UCSHash.put((long)0x7cd,(char)0x039d);
    keysym2UCSHash.put((long)0x7ce,(char)0x039e);
    keysym2UCSHash.put((long)0x7cf,(char)0x039f);
    keysym2UCSHash.put((long)0x7d0,(char)0x03a0);
    keysym2UCSHash.put((long)0x7d1,(char)0x03a1);
    keysym2UCSHash.put((long)0x7d2,(char)0x03a3);
    keysym2UCSHash.put((long)0x7d4,(char)0x03a4);
    keysym2UCSHash.put((long)0x7d5,(char)0x03a5);
    keysym2UCSHash.put((long)0x7d6,(char)0x03a6);
    keysym2UCSHash.put((long)0x7d7,(char)0x03a7);
    keysym2UCSHash.put((long)0x7d8,(char)0x03a8);
    keysym2UCSHash.put((long)0x7d9,(char)0x03a9);
    keysym2UCSHash.put((long)0x7e1,(char)0x03b1);
    keysym2UCSHash.put((long)0x7e2,(char)0x03b2);
    keysym2UCSHash.put((long)0x7e3,(char)0x03b3);
    keysym2UCSHash.put((long)0x7e4,(char)0x03b4);
    keysym2UCSHash.put((long)0x7e5,(char)0x03b5);
    keysym2UCSHash.put((long)0x7e6,(char)0x03b6);
    keysym2UCSHash.put((long)0x7e7,(char)0x03b7);
    keysym2UCSHash.put((long)0x7e8,(char)0x03b8);
    keysym2UCSHash.put((long)0x7e9,(char)0x03b9);
    keysym2UCSHash.put((long)0x7ea,(char)0x03ba);
    keysym2UCSHash.put((long)0x7eb,(char)0x03bb);
    keysym2UCSHash.put((long)0x7ec,(char)0x03bc);
    keysym2UCSHash.put((long)0x7ed,(char)0x03bd);
    keysym2UCSHash.put((long)0x7ee,(char)0x03be);
    keysym2UCSHash.put((long)0x7ef,(char)0x03bf);
    keysym2UCSHash.put((long)0x7f0,(char)0x03c0);
    keysym2UCSHash.put((long)0x7f1,(char)0x03c1);
    keysym2UCSHash.put((long)0x7f2,(char)0x03c3);
    keysym2UCSHash.put((long)0x7f3,(char)0x03c2);
    keysym2UCSHash.put((long)0x7f4,(char)0x03c4);
    keysym2UCSHash.put((long)0x7f5,(char)0x03c5);
    keysym2UCSHash.put((long)0x7f6,(char)0x03c6);
    keysym2UCSHash.put((long)0x7f7,(char)0x03c7);
    keysym2UCSHash.put((long)0x7f8,(char)0x03c8);
    keysym2UCSHash.put((long)0x7f9,(char)0x03c9);
    keysym2UCSHash.put((long)0x8a1,(char)0x23b7);
    keysym2UCSHash.put((long)0x8a2,(char)0x250c);
    keysym2UCSHash.put((long)0x8a3,(char)0x2500);
    keysym2UCSHash.put((long)0x8a4,(char)0x2320);
    keysym2UCSHash.put((long)0x8a5,(char)0x2321);
    keysym2UCSHash.put((long)0x8a6,(char)0x2502);
    keysym2UCSHash.put((long)0x8a7,(char)0x23a1);
    keysym2UCSHash.put((long)0x8a8,(char)0x23a3);
    keysym2UCSHash.put((long)0x8a9,(char)0x23a4);
    keysym2UCSHash.put((long)0x8aa,(char)0x23a6);
    keysym2UCSHash.put((long)0x8ab,(char)0x239b);
    keysym2UCSHash.put((long)0x8ac,(char)0x239d);
    keysym2UCSHash.put((long)0x8ad,(char)0x239e);
    keysym2UCSHash.put((long)0x8ae,(char)0x23a0);
    keysym2UCSHash.put((long)0x8af,(char)0x23a8);
    keysym2UCSHash.put((long)0x8b0,(char)0x23ac);
    keysym2UCSHash.put((long)0x8bc,(char)0x2264);
    keysym2UCSHash.put((long)0x8bd,(char)0x2260);
    keysym2UCSHash.put((long)0x8be,(char)0x2265);
    keysym2UCSHash.put((long)0x8bf,(char)0x222b);
    keysym2UCSHash.put((long)0x8c0,(char)0x2234);
    keysym2UCSHash.put((long)0x8c1,(char)0x221d);
    keysym2UCSHash.put((long)0x8c2,(char)0x221e);
    keysym2UCSHash.put((long)0x8c5,(char)0x2207);
    keysym2UCSHash.put((long)0x8c8,(char)0x223c);
    keysym2UCSHash.put((long)0x8c9,(char)0x2243);
    keysym2UCSHash.put((long)0x8cd,(char)0x2104);
    keysym2UCSHash.put((long)0x8ce,(char)0x21d2);
    keysym2UCSHash.put((long)0x8cf,(char)0x2261);
    keysym2UCSHash.put((long)0x8d6,(char)0x221a);
    keysym2UCSHash.put((long)0x8da,(char)0x2282);
    keysym2UCSHash.put((long)0x8db,(char)0x2283);
    keysym2UCSHash.put((long)0x8dc,(char)0x2229);
    keysym2UCSHash.put((long)0x8dd,(char)0x222a);
    keysym2UCSHash.put((long)0x8de,(char)0x2227);
    keysym2UCSHash.put((long)0x8df,(char)0x2228);
    keysym2UCSHash.put((long)0x8ef,(char)0x2202);
    keysym2UCSHash.put((long)0x8f6,(char)0x0192);
    keysym2UCSHash.put((long)0x8fb,(char)0x2190);
    keysym2UCSHash.put((long)0x8fc,(char)0x2191);
    keysym2UCSHash.put((long)0x8fd,(char)0x2192);
    keysym2UCSHash.put((long)0x8fe,(char)0x2193);
    keysym2UCSHash.put((long)0x9e0,(char)0x25c6);
    keysym2UCSHash.put((long)0x9e1,(char)0x2592);
    keysym2UCSHash.put((long)0x9e2,(char)0x2409);
    keysym2UCSHash.put((long)0x9e3,(char)0x240c);
    keysym2UCSHash.put((long)0x9e4,(char)0x240d);
    keysym2UCSHash.put((long)0x9e5,(char)0x240a);
    keysym2UCSHash.put((long)0x9e8,(char)0x2424);
    keysym2UCSHash.put((long)0x9e9,(char)0x240b);
    keysym2UCSHash.put((long)0x9ea,(char)0x2518);
    keysym2UCSHash.put((long)0x9eb,(char)0x2510);
    keysym2UCSHash.put((long)0x9ec,(char)0x250c);
    keysym2UCSHash.put((long)0x9ed,(char)0x2514);
    keysym2UCSHash.put((long)0x9ee,(char)0x253c);
    keysym2UCSHash.put((long)0x9ef,(char)0x23ba);
    keysym2UCSHash.put((long)0x9f0,(char)0x23bb);
    keysym2UCSHash.put((long)0x9f1,(char)0x2500);
    keysym2UCSHash.put((long)0x9f2,(char)0x23bc);
    keysym2UCSHash.put((long)0x9f3,(char)0x23bd);
    keysym2UCSHash.put((long)0x9f4,(char)0x251c);
    keysym2UCSHash.put((long)0x9f5,(char)0x2524);
    keysym2UCSHash.put((long)0x9f6,(char)0x2534);
    keysym2UCSHash.put((long)0x9f7,(char)0x242c);
    keysym2UCSHash.put((long)0x9f8,(char)0x2502);
    keysym2UCSHash.put((long)0xaa1,(char)0x2003);
    keysym2UCSHash.put((long)0xaa2,(char)0x2002);
    keysym2UCSHash.put((long)0xaa3,(char)0x2004);
    keysym2UCSHash.put((long)0xaa4,(char)0x2005);
    keysym2UCSHash.put((long)0xaa5,(char)0x2007);
    keysym2UCSHash.put((long)0xaa6,(char)0x2008);
    keysym2UCSHash.put((long)0xaa7,(char)0x2009);
    keysym2UCSHash.put((long)0xaa8,(char)0x200a);
    keysym2UCSHash.put((long)0xaa9,(char)0x2014);
    keysym2UCSHash.put((long)0xaaa,(char)0x2013);
    keysym2UCSHash.put((long)0xaac,(char)0x2423);
    keysym2UCSHash.put((long)0xaae,(char)0x2026);
    keysym2UCSHash.put((long)0xaaf,(char)0x2025);
    keysym2UCSHash.put((long)0xab0,(char)0x2153);
    keysym2UCSHash.put((long)0xab1,(char)0x2154);
    keysym2UCSHash.put((long)0xab2,(char)0x2155);
    keysym2UCSHash.put((long)0xab3,(char)0x2156);
    keysym2UCSHash.put((long)0xab4,(char)0x2157);
    keysym2UCSHash.put((long)0xab5,(char)0x2158);
    keysym2UCSHash.put((long)0xab6,(char)0x2159);
    keysym2UCSHash.put((long)0xab7,(char)0x215a);
    keysym2UCSHash.put((long)0xab8,(char)0x2105);
    keysym2UCSHash.put((long)0xabb,(char)0x2012);
    keysym2UCSHash.put((long)0xabc,(char)0x27e8);
    keysym2UCSHash.put((long)0xabd,(char)0x002e);
    keysym2UCSHash.put((long)0xabe,(char)0x27e9);
    keysym2UCSHash.put((long)0xac3,(char)0x215b);
    keysym2UCSHash.put((long)0xac4,(char)0x215c);
    keysym2UCSHash.put((long)0xac5,(char)0x215d);
    keysym2UCSHash.put((long)0xac6,(char)0x215e);
    keysym2UCSHash.put((long)0xac9,(char)0x2122);
    keysym2UCSHash.put((long)0xaca,(char)0x2613);
    keysym2UCSHash.put((long)0xacc,(char)0x25c1);
    keysym2UCSHash.put((long)0xacd,(char)0x25b7);
    keysym2UCSHash.put((long)0xace,(char)0x25cb);
    keysym2UCSHash.put((long)0xacf,(char)0x25af);
    keysym2UCSHash.put((long)0xad0,(char)0x2018);
    keysym2UCSHash.put((long)0xad1,(char)0x2019);
    keysym2UCSHash.put((long)0xad2,(char)0x201c);
    keysym2UCSHash.put((long)0xad3,(char)0x201d);
    keysym2UCSHash.put((long)0xad4,(char)0x211e);
    keysym2UCSHash.put((long)0xad6,(char)0x2032);
    keysym2UCSHash.put((long)0xad7,(char)0x2033);
    keysym2UCSHash.put((long)0xad9,(char)0x271d);
    keysym2UCSHash.put((long)0xadb,(char)0x25ac);
    keysym2UCSHash.put((long)0xadc,(char)0x25c0);
    keysym2UCSHash.put((long)0xadd,(char)0x25b6);
    keysym2UCSHash.put((long)0xade,(char)0x25cf);
    keysym2UCSHash.put((long)0xadf,(char)0x25ae);
    keysym2UCSHash.put((long)0xae0,(char)0x25e6);
    keysym2UCSHash.put((long)0xae1,(char)0x25ab);
    keysym2UCSHash.put((long)0xae2,(char)0x25ad);
    keysym2UCSHash.put((long)0xae3,(char)0x25b3);
    keysym2UCSHash.put((long)0xae4,(char)0x25bd);
    keysym2UCSHash.put((long)0xae5,(char)0x2606);
    keysym2UCSHash.put((long)0xae6,(char)0x2022);
    keysym2UCSHash.put((long)0xae7,(char)0x25aa);
    keysym2UCSHash.put((long)0xae8,(char)0x25b2);
    keysym2UCSHash.put((long)0xae9,(char)0x25bc);
    keysym2UCSHash.put((long)0xaea,(char)0x261c);
    keysym2UCSHash.put((long)0xaeb,(char)0x261e);
    keysym2UCSHash.put((long)0xaec,(char)0x2663);
    keysym2UCSHash.put((long)0xaed,(char)0x2666);
    keysym2UCSHash.put((long)0xaee,(char)0x2665);
    keysym2UCSHash.put((long)0xaf0,(char)0x2720);
    keysym2UCSHash.put((long)0xaf1,(char)0x2020);
    keysym2UCSHash.put((long)0xaf2,(char)0x2021);
    keysym2UCSHash.put((long)0xaf3,(char)0x2713);
    keysym2UCSHash.put((long)0xaf4,(char)0x2717);
    keysym2UCSHash.put((long)0xaf5,(char)0x266f);
    keysym2UCSHash.put((long)0xaf6,(char)0x266d);
    keysym2UCSHash.put((long)0xaf7,(char)0x2642);
    keysym2UCSHash.put((long)0xaf8,(char)0x2640);
    keysym2UCSHash.put((long)0xaf9,(char)0x260e);
    keysym2UCSHash.put((long)0xafa,(char)0x2315);
    keysym2UCSHash.put((long)0xafb,(char)0x2117);
    keysym2UCSHash.put((long)0xafc,(char)0x2038);
    keysym2UCSHash.put((long)0xafd,(char)0x201a);
    keysym2UCSHash.put((long)0xafe,(char)0x201e);
    keysym2UCSHash.put((long)0xba3,(char)0x003c);
    keysym2UCSHash.put((long)0xba6,(char)0x003e);
    keysym2UCSHash.put((long)0xba8,(char)0x2228);
    keysym2UCSHash.put((long)0xba9,(char)0x2227);
    keysym2UCSHash.put((long)0xbc0,(char)0x00af);
    keysym2UCSHash.put((long)0xbc2,(char)0x22a5);
    keysym2UCSHash.put((long)0xbc3,(char)0x2229);
    keysym2UCSHash.put((long)0xbc4,(char)0x230a);
    keysym2UCSHash.put((long)0xbc6,(char)0x005f);
    keysym2UCSHash.put((long)0xbca,(char)0x2218);
    keysym2UCSHash.put((long)0xbcc,(char)0x2395);
    keysym2UCSHash.put((long)0xbce,(char)0x22a4);
    keysym2UCSHash.put((long)0xbcf,(char)0x25cb);
    keysym2UCSHash.put((long)0xbd3,(char)0x2308);
    keysym2UCSHash.put((long)0xbd6,(char)0x222a);
    keysym2UCSHash.put((long)0xbd8,(char)0x2283);
    keysym2UCSHash.put((long)0xbda,(char)0x2282);
    keysym2UCSHash.put((long)0xbdc,(char)0x22a2);
    keysym2UCSHash.put((long)0xbfc,(char)0x22a3);
    keysym2UCSHash.put((long)0xcdf,(char)0x2017);
    keysym2UCSHash.put((long)0xce0,(char)0x05d0);
    keysym2UCSHash.put((long)0xce1,(char)0x05d1);
    keysym2UCSHash.put((long)0xce2,(char)0x05d2);
    keysym2UCSHash.put((long)0xce3,(char)0x05d3);
    keysym2UCSHash.put((long)0xce4,(char)0x05d4);
    keysym2UCSHash.put((long)0xce5,(char)0x05d5);
    keysym2UCSHash.put((long)0xce6,(char)0x05d6);
    keysym2UCSHash.put((long)0xce7,(char)0x05d7);
    keysym2UCSHash.put((long)0xce8,(char)0x05d8);
    keysym2UCSHash.put((long)0xce9,(char)0x05d9);
    keysym2UCSHash.put((long)0xcea,(char)0x05da);
    keysym2UCSHash.put((long)0xceb,(char)0x05db);
    keysym2UCSHash.put((long)0xcec,(char)0x05dc);
    keysym2UCSHash.put((long)0xced,(char)0x05dd);
    keysym2UCSHash.put((long)0xcee,(char)0x05de);
    keysym2UCSHash.put((long)0xcef,(char)0x05df);
    keysym2UCSHash.put((long)0xcf0,(char)0x05e0);
    keysym2UCSHash.put((long)0xcf1,(char)0x05e1);
    keysym2UCSHash.put((long)0xcf2,(char)0x05e2);
    keysym2UCSHash.put((long)0xcf3,(char)0x05e3);
    keysym2UCSHash.put((long)0xcf4,(char)0x05e4);
    keysym2UCSHash.put((long)0xcf5,(char)0x05e5);
    keysym2UCSHash.put((long)0xcf6,(char)0x05e6);
    keysym2UCSHash.put((long)0xcf7,(char)0x05e7);
    keysym2UCSHash.put((long)0xcf8,(char)0x05e8);
    keysym2UCSHash.put((long)0xcf9,(char)0x05e9);
    keysym2UCSHash.put((long)0xcfa,(char)0x05ea);
    keysym2UCSHash.put((long)0xda1,(char)0x0e01);
    keysym2UCSHash.put((long)0xda2,(char)0x0e02);
    keysym2UCSHash.put((long)0xda3,(char)0x0e03);
    keysym2UCSHash.put((long)0xda4,(char)0x0e04);
    keysym2UCSHash.put((long)0xda5,(char)0x0e05);
    keysym2UCSHash.put((long)0xda6,(char)0x0e06);
    keysym2UCSHash.put((long)0xda7,(char)0x0e07);
    keysym2UCSHash.put((long)0xda8,(char)0x0e08);
    keysym2UCSHash.put((long)0xda9,(char)0x0e09);
    keysym2UCSHash.put((long)0xdaa,(char)0x0e0a);
    keysym2UCSHash.put((long)0xdab,(char)0x0e0b);
    keysym2UCSHash.put((long)0xdac,(char)0x0e0c);
    keysym2UCSHash.put((long)0xdad,(char)0x0e0d);
    keysym2UCSHash.put((long)0xdae,(char)0x0e0e);
    keysym2UCSHash.put((long)0xdaf,(char)0x0e0f);
    keysym2UCSHash.put((long)0xdb0,(char)0x0e10);
    keysym2UCSHash.put((long)0xdb1,(char)0x0e11);
    keysym2UCSHash.put((long)0xdb2,(char)0x0e12);
    keysym2UCSHash.put((long)0xdb3,(char)0x0e13);
    keysym2UCSHash.put((long)0xdb4,(char)0x0e14);
    keysym2UCSHash.put((long)0xdb5,(char)0x0e15);
    keysym2UCSHash.put((long)0xdb6,(char)0x0e16);
    keysym2UCSHash.put((long)0xdb7,(char)0x0e17);
    keysym2UCSHash.put((long)0xdb8,(char)0x0e18);
    keysym2UCSHash.put((long)0xdb9,(char)0x0e19);
    keysym2UCSHash.put((long)0xdba,(char)0x0e1a);
    keysym2UCSHash.put((long)0xdbb,(char)0x0e1b);
    keysym2UCSHash.put((long)0xdbc,(char)0x0e1c);
    keysym2UCSHash.put((long)0xdbd,(char)0x0e1d);
    keysym2UCSHash.put((long)0xdbe,(char)0x0e1e);
    keysym2UCSHash.put((long)0xdbf,(char)0x0e1f);
    keysym2UCSHash.put((long)0xdc0,(char)0x0e20);
    keysym2UCSHash.put((long)0xdc1,(char)0x0e21);
    keysym2UCSHash.put((long)0xdc2,(char)0x0e22);
    keysym2UCSHash.put((long)0xdc3,(char)0x0e23);
    keysym2UCSHash.put((long)0xdc4,(char)0x0e24);
    keysym2UCSHash.put((long)0xdc5,(char)0x0e25);
    keysym2UCSHash.put((long)0xdc6,(char)0x0e26);
    keysym2UCSHash.put((long)0xdc7,(char)0x0e27);
    keysym2UCSHash.put((long)0xdc8,(char)0x0e28);
    keysym2UCSHash.put((long)0xdc9,(char)0x0e29);
    keysym2UCSHash.put((long)0xdca,(char)0x0e2a);
    keysym2UCSHash.put((long)0xdcb,(char)0x0e2b);
    keysym2UCSHash.put((long)0xdcc,(char)0x0e2c);
    keysym2UCSHash.put((long)0xdcd,(char)0x0e2d);
    keysym2UCSHash.put((long)0xdce,(char)0x0e2e);
    keysym2UCSHash.put((long)0xdcf,(char)0x0e2f);
    keysym2UCSHash.put((long)0xdd0,(char)0x0e30);
    keysym2UCSHash.put((long)0xdd1,(char)0x0e31);
    keysym2UCSHash.put((long)0xdd2,(char)0x0e32);
    keysym2UCSHash.put((long)0xdd3,(char)0x0e33);
    keysym2UCSHash.put((long)0xdd4,(char)0x0e34);
    keysym2UCSHash.put((long)0xdd5,(char)0x0e35);
    keysym2UCSHash.put((long)0xdd6,(char)0x0e36);
    keysym2UCSHash.put((long)0xdd7,(char)0x0e37);
    keysym2UCSHash.put((long)0xdd8,(char)0x0e38);
    keysym2UCSHash.put((long)0xdd9,(char)0x0e39);
    keysym2UCSHash.put((long)0xdda,(char)0x0e3a);
    keysym2UCSHash.put((long)0xddf,(char)0x0e3f);
    keysym2UCSHash.put((long)0xde0,(char)0x0e40);
    keysym2UCSHash.put((long)0xde1,(char)0x0e41);
    keysym2UCSHash.put((long)0xde2,(char)0x0e42);
    keysym2UCSHash.put((long)0xde3,(char)0x0e43);
    keysym2UCSHash.put((long)0xde4,(char)0x0e44);
    keysym2UCSHash.put((long)0xde5,(char)0x0e45);
    keysym2UCSHash.put((long)0xde6,(char)0x0e46);
    keysym2UCSHash.put((long)0xde7,(char)0x0e47);
    keysym2UCSHash.put((long)0xde8,(char)0x0e48);
    keysym2UCSHash.put((long)0xde9,(char)0x0e49);
    keysym2UCSHash.put((long)0xdea,(char)0x0e4a);
    keysym2UCSHash.put((long)0xdeb,(char)0x0e4b);
    keysym2UCSHash.put((long)0xdec,(char)0x0e4c);
    keysym2UCSHash.put((long)0xded,(char)0x0e4d);
    keysym2UCSHash.put((long)0xdf0,(char)0x0e50);
    keysym2UCSHash.put((long)0xdf1,(char)0x0e51);
    keysym2UCSHash.put((long)0xdf2,(char)0x0e52);
    keysym2UCSHash.put((long)0xdf3,(char)0x0e53);
    keysym2UCSHash.put((long)0xdf4,(char)0x0e54);
    keysym2UCSHash.put((long)0xdf5,(char)0x0e55);
    keysym2UCSHash.put((long)0xdf6,(char)0x0e56);
    keysym2UCSHash.put((long)0xdf7,(char)0x0e57);
    keysym2UCSHash.put((long)0xdf8,(char)0x0e58);
    keysym2UCSHash.put((long)0xdf9,(char)0x0e59);
    keysym2UCSHash.put((long)0xea1,(char)0x3131);
    keysym2UCSHash.put((long)0xea2,(char)0x3132);
    keysym2UCSHash.put((long)0xea3,(char)0x3133);
    keysym2UCSHash.put((long)0xea4,(char)0x3134);
    keysym2UCSHash.put((long)0xea5,(char)0x3135);
    keysym2UCSHash.put((long)0xea6,(char)0x3136);
    keysym2UCSHash.put((long)0xea7,(char)0x3137);
    keysym2UCSHash.put((long)0xea8,(char)0x3138);
    keysym2UCSHash.put((long)0xea9,(char)0x3139);
    keysym2UCSHash.put((long)0xeaa,(char)0x313a);
    keysym2UCSHash.put((long)0xeab,(char)0x313b);
    keysym2UCSHash.put((long)0xeac,(char)0x313c);
    keysym2UCSHash.put((long)0xead,(char)0x313d);
    keysym2UCSHash.put((long)0xeae,(char)0x313e);
    keysym2UCSHash.put((long)0xeaf,(char)0x313f);
    keysym2UCSHash.put((long)0xeb0,(char)0x3140);
    keysym2UCSHash.put((long)0xeb1,(char)0x3141);
    keysym2UCSHash.put((long)0xeb2,(char)0x3142);
    keysym2UCSHash.put((long)0xeb3,(char)0x3143);
    keysym2UCSHash.put((long)0xeb4,(char)0x3144);
    keysym2UCSHash.put((long)0xeb5,(char)0x3145);
    keysym2UCSHash.put((long)0xeb6,(char)0x3146);
    keysym2UCSHash.put((long)0xeb7,(char)0x3147);
    keysym2UCSHash.put((long)0xeb8,(char)0x3148);
    keysym2UCSHash.put((long)0xeb9,(char)0x3149);
    keysym2UCSHash.put((long)0xeba,(char)0x314a);
    keysym2UCSHash.put((long)0xebb,(char)0x314b);
    keysym2UCSHash.put((long)0xebc,(char)0x314c);
    keysym2UCSHash.put((long)0xebd,(char)0x314d);
    keysym2UCSHash.put((long)0xebe,(char)0x314e);
    keysym2UCSHash.put((long)0xebf,(char)0x314f);
    keysym2UCSHash.put((long)0xec0,(char)0x3150);
    keysym2UCSHash.put((long)0xec1,(char)0x3151);
    keysym2UCSHash.put((long)0xec2,(char)0x3152);
    keysym2UCSHash.put((long)0xec3,(char)0x3153);
    keysym2UCSHash.put((long)0xec4,(char)0x3154);
    keysym2UCSHash.put((long)0xec5,(char)0x3155);
    keysym2UCSHash.put((long)0xec6,(char)0x3156);
    keysym2UCSHash.put((long)0xec7,(char)0x3157);
    keysym2UCSHash.put((long)0xec8,(char)0x3158);
    keysym2UCSHash.put((long)0xec9,(char)0x3159);
    keysym2UCSHash.put((long)0xeca,(char)0x315a);
    keysym2UCSHash.put((long)0xecb,(char)0x315b);
    keysym2UCSHash.put((long)0xecc,(char)0x315c);
    keysym2UCSHash.put((long)0xecd,(char)0x315d);
    keysym2UCSHash.put((long)0xece,(char)0x315e);
    keysym2UCSHash.put((long)0xecf,(char)0x315f);
    keysym2UCSHash.put((long)0xed0,(char)0x3160);
    keysym2UCSHash.put((long)0xed1,(char)0x3161);
    keysym2UCSHash.put((long)0xed2,(char)0x3162);
    keysym2UCSHash.put((long)0xed3,(char)0x3163);
    keysym2UCSHash.put((long)0xed4,(char)0x11a8);
    keysym2UCSHash.put((long)0xed5,(char)0x11a9);
    keysym2UCSHash.put((long)0xed6,(char)0x11aa);
    keysym2UCSHash.put((long)0xed7,(char)0x11ab);
    keysym2UCSHash.put((long)0xed8,(char)0x11ac);
    keysym2UCSHash.put((long)0xed9,(char)0x11ad);
    keysym2UCSHash.put((long)0xeda,(char)0x11ae);
    keysym2UCSHash.put((long)0xedb,(char)0x11af);
    keysym2UCSHash.put((long)0xedc,(char)0x11b0);
    keysym2UCSHash.put((long)0xedd,(char)0x11b1);
    keysym2UCSHash.put((long)0xede,(char)0x11b2);
    keysym2UCSHash.put((long)0xedf,(char)0x11b3);
    keysym2UCSHash.put((long)0xee0,(char)0x11b4);
    keysym2UCSHash.put((long)0xee1,(char)0x11b5);
    keysym2UCSHash.put((long)0xee2,(char)0x11b6);
    keysym2UCSHash.put((long)0xee3,(char)0x11b7);
    keysym2UCSHash.put((long)0xee4,(char)0x11b8);
    keysym2UCSHash.put((long)0xee5,(char)0x11b9);
    keysym2UCSHash.put((long)0xee6,(char)0x11ba);
    keysym2UCSHash.put((long)0xee7,(char)0x11bb);
    keysym2UCSHash.put((long)0xee8,(char)0x11bc);
    keysym2UCSHash.put((long)0xee9,(char)0x11bd);
    keysym2UCSHash.put((long)0xeea,(char)0x11be);
    keysym2UCSHash.put((long)0xeeb,(char)0x11bf);
    keysym2UCSHash.put((long)0xeec,(char)0x11c0);
    keysym2UCSHash.put((long)0xeed,(char)0x11c1);
    keysym2UCSHash.put((long)0xeee,(char)0x11c2);
    keysym2UCSHash.put((long)0xeef,(char)0x316d);
    keysym2UCSHash.put((long)0xef0,(char)0x3171);
    keysym2UCSHash.put((long)0xef1,(char)0x3178);
    keysym2UCSHash.put((long)0xef2,(char)0x317f);
    keysym2UCSHash.put((long)0xef3,(char)0x3181);
    keysym2UCSHash.put((long)0xef4,(char)0x3184);
    keysym2UCSHash.put((long)0xef5,(char)0x3186);
    keysym2UCSHash.put((long)0xef6,(char)0x318d);
    keysym2UCSHash.put((long)0xef7,(char)0x318e);
    keysym2UCSHash.put((long)0xef8,(char)0x11eb);
    keysym2UCSHash.put((long)0xef9,(char)0x11f0);
    keysym2UCSHash.put((long)0xefa,(char)0x11f9);
    keysym2UCSHash.put((long)0xeff,(char)0x20a9);
    keysym2UCSHash.put((long)0x16a3,(char)0x1e8a);
    keysym2UCSHash.put((long)0x16a6,(char)0x012c);
    keysym2UCSHash.put((long)0x16a9,(char)0x01b5);
    keysym2UCSHash.put((long)0x16aa,(char)0x01e6);
    keysym2UCSHash.put((long)0x16af,(char)0x019f);
    keysym2UCSHash.put((long)0x16b3,(char)0x1e8b);
    keysym2UCSHash.put((long)0x16b6,(char)0x012d);
    keysym2UCSHash.put((long)0x16b9,(char)0x01b6);
    keysym2UCSHash.put((long)0x16ba,(char)0x01e7);
    keysym2UCSHash.put((long)0x16bd,(char)0x01d2);
    keysym2UCSHash.put((long)0x16bf,(char)0x0275);
    keysym2UCSHash.put((long)0x16c6,(char)0x018f);
    keysym2UCSHash.put((long)0x16f6,(char)0x0259);
    keysym2UCSHash.put((long)0x1ea0,(char)0x1ea0);
    keysym2UCSHash.put((long)0x1ea1,(char)0x1ea1);
    keysym2UCSHash.put((long)0x1ea2,(char)0x1ea2);
    keysym2UCSHash.put((long)0x1ea3,(char)0x1ea3);
    keysym2UCSHash.put((long)0x1ea4,(char)0x1ea4);
    keysym2UCSHash.put((long)0x1ea5,(char)0x1ea5);
    keysym2UCSHash.put((long)0x1ea6,(char)0x1ea6);
    keysym2UCSHash.put((long)0x1ea7,(char)0x1ea7);
    keysym2UCSHash.put((long)0x1ea8,(char)0x1ea8);
    keysym2UCSHash.put((long)0x1ea9,(char)0x1ea9);
    keysym2UCSHash.put((long)0x1eaa,(char)0x1eaa);
    keysym2UCSHash.put((long)0x1eab,(char)0x1eab);
    keysym2UCSHash.put((long)0x1eac,(char)0x1eac);
    keysym2UCSHash.put((long)0x1ead,(char)0x1ead);
    keysym2UCSHash.put((long)0x1eae,(char)0x1eae);
    keysym2UCSHash.put((long)0x1eaf,(char)0x1eaf);
    keysym2UCSHash.put((long)0x1eb0,(char)0x1eb0);
    keysym2UCSHash.put((long)0x1eb1,(char)0x1eb1);
    keysym2UCSHash.put((long)0x1eb2,(char)0x1eb2);
    keysym2UCSHash.put((long)0x1eb3,(char)0x1eb3);
    keysym2UCSHash.put((long)0x1eb4,(char)0x1eb4);
    keysym2UCSHash.put((long)0x1eb5,(char)0x1eb5);
    keysym2UCSHash.put((long)0x1eb6,(char)0x1eb6);
    keysym2UCSHash.put((long)0x1eb7,(char)0x1eb7);
    keysym2UCSHash.put((long)0x1eb8,(char)0x1eb8);
    keysym2UCSHash.put((long)0x1eb9,(char)0x1eb9);
    keysym2UCSHash.put((long)0x1eba,(char)0x1eba);
    keysym2UCSHash.put((long)0x1ebb,(char)0x1ebb);
    keysym2UCSHash.put((long)0x1ebc,(char)0x1ebc);
    keysym2UCSHash.put((long)0x1ebd,(char)0x1ebd);
    keysym2UCSHash.put((long)0x1ebe,(char)0x1ebe);
    keysym2UCSHash.put((long)0x1ebf,(char)0x1ebf);
    keysym2UCSHash.put((long)0x1ec0,(char)0x1ec0);
    keysym2UCSHash.put((long)0x1ec1,(char)0x1ec1);
    keysym2UCSHash.put((long)0x1ec2,(char)0x1ec2);
    keysym2UCSHash.put((long)0x1ec3,(char)0x1ec3);
    keysym2UCSHash.put((long)0x1ec4,(char)0x1ec4);
    keysym2UCSHash.put((long)0x1ec5,(char)0x1ec5);
    keysym2UCSHash.put((long)0x1ec6,(char)0x1ec6);
    keysym2UCSHash.put((long)0x1ec7,(char)0x1ec7);
    keysym2UCSHash.put((long)0x1ec8,(char)0x1ec8);
    keysym2UCSHash.put((long)0x1ec9,(char)0x1ec9);
    keysym2UCSHash.put((long)0x1eca,(char)0x1eca);
    keysym2UCSHash.put((long)0x1ecb,(char)0x1ecb);
    keysym2UCSHash.put((long)0x1ecc,(char)0x1ecc);
    keysym2UCSHash.put((long)0x1ecd,(char)0x1ecd);
    keysym2UCSHash.put((long)0x1ece,(char)0x1ece);
    keysym2UCSHash.put((long)0x1ecf,(char)0x1ecf);
    keysym2UCSHash.put((long)0x1ed0,(char)0x1ed0);
    keysym2UCSHash.put((long)0x1ed1,(char)0x1ed1);
    keysym2UCSHash.put((long)0x1ed2,(char)0x1ed2);
    keysym2UCSHash.put((long)0x1ed3,(char)0x1ed3);
    keysym2UCSHash.put((long)0x1ed4,(char)0x1ed4);
    keysym2UCSHash.put((long)0x1ed5,(char)0x1ed5);
    keysym2UCSHash.put((long)0x1ed6,(char)0x1ed6);
    keysym2UCSHash.put((long)0x1ed7,(char)0x1ed7);
    keysym2UCSHash.put((long)0x1ed8,(char)0x1ed8);
    keysym2UCSHash.put((long)0x1ed9,(char)0x1ed9);
    keysym2UCSHash.put((long)0x1eda,(char)0x1eda);
    keysym2UCSHash.put((long)0x1edb,(char)0x1edb);
    keysym2UCSHash.put((long)0x1edc,(char)0x1edc);
    keysym2UCSHash.put((long)0x1edd,(char)0x1edd);
    keysym2UCSHash.put((long)0x1ede,(char)0x1ede);
    keysym2UCSHash.put((long)0x1edf,(char)0x1edf);
    keysym2UCSHash.put((long)0x1ee0,(char)0x1ee0);
    keysym2UCSHash.put((long)0x1ee1,(char)0x1ee1);
    keysym2UCSHash.put((long)0x1ee2,(char)0x1ee2);
    keysym2UCSHash.put((long)0x1ee3,(char)0x1ee3);
    keysym2UCSHash.put((long)0x1ee4,(char)0x1ee4);
    keysym2UCSHash.put((long)0x1ee5,(char)0x1ee5);
    keysym2UCSHash.put((long)0x1ee6,(char)0x1ee6);
    keysym2UCSHash.put((long)0x1ee7,(char)0x1ee7);
    keysym2UCSHash.put((long)0x1ee8,(char)0x1ee8);
    keysym2UCSHash.put((long)0x1ee9,(char)0x1ee9);
    keysym2UCSHash.put((long)0x1eea,(char)0x1eea);
    keysym2UCSHash.put((long)0x1eeb,(char)0x1eeb);
    keysym2UCSHash.put((long)0x1eec,(char)0x1eec);
    keysym2UCSHash.put((long)0x1eed,(char)0x1eed);
    keysym2UCSHash.put((long)0x1eee,(char)0x1eee);
    keysym2UCSHash.put((long)0x1eef,(char)0x1eef);
    keysym2UCSHash.put((long)0x1ef0,(char)0x1ef0);
    keysym2UCSHash.put((long)0x1ef1,(char)0x1ef1);
    keysym2UCSHash.put((long)0x1ef4,(char)0x1ef4);
    keysym2UCSHash.put((long)0x1ef5,(char)0x1ef5);
    keysym2UCSHash.put((long)0x1ef6,(char)0x1ef6);
    keysym2UCSHash.put((long)0x1ef7,(char)0x1ef7);
    keysym2UCSHash.put((long)0x1ef8,(char)0x1ef8);
    keysym2UCSHash.put((long)0x1ef9,(char)0x1ef9);
    keysym2UCSHash.put((long)0x1efa,(char)0x01a0);
    keysym2UCSHash.put((long)0x1efb,(char)0x01a1);
    keysym2UCSHash.put((long)0x1efc,(char)0x01af);
    keysym2UCSHash.put((long)0x1efd,(char)0x01b0);
    keysym2UCSHash.put((long)0x20a0,(char)0x20a0);
    keysym2UCSHash.put((long)0x20a1,(char)0x20a1);
    keysym2UCSHash.put((long)0x20a2,(char)0x20a2);
    keysym2UCSHash.put((long)0x20a3,(char)0x20a3);
    keysym2UCSHash.put((long)0x20a4,(char)0x20a4);
    keysym2UCSHash.put((long)0x20a5,(char)0x20a5);
    keysym2UCSHash.put((long)0x20a6,(char)0x20a6);
    keysym2UCSHash.put((long)0x20a7,(char)0x20a7);
    keysym2UCSHash.put((long)0x20a8,(char)0x20a8);
    keysym2UCSHash.put((long)0x20a9,(char)0x20a9);
    keysym2UCSHash.put((long)0x20aa,(char)0x20aa);
    keysym2UCSHash.put((long)0x20ab,(char)0x20ab);
    keysym2UCSHash.put((long)0x20ac,(char)0x20ac);
    keysym2UCSHash.put((long)0x1004FF08,(char)0x0008);
    keysym2UCSHash.put((long)0x1004FF1B,(char)0x001b);
    keysym2UCSHash.put((long)0x1004FFFF,(char)0x007f);
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_a),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_A,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_b),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_B,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_c),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_C,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_d),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_D,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_e),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_E,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_f),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_g),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_G,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_h),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_H,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_i),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_I,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_j),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_J,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_k),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_K,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_l),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_L,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_m),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_M,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_n),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_N,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_o),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_O,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_p),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_P,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_q),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Q,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_r),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_R,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_s),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_S,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_t),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_T,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_u),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_U,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_v),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_V,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_w),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_W,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_x),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_X,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_y),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Y,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_z),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_Z,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_BackSpace),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SPACE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Tab),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_ISO_Left_Tab),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Clear),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLEAR,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Return),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Linefeed),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Pause),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F21),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R1),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAUSE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Scroll_Lock),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F23),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R3),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SCROLL_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Escape),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ESCAPE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_BackSpace),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SPACE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Clear),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLEAR,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Escape),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ESCAPE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Shift_L),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SHIFT,java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Shift_R),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SHIFT,java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Control_L),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTROL,java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Control_R),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTROL,java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Alt_L),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT,java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Alt_R),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT,java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Meta_L),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_META,java.awt.event.KeyEvent.KEY_LOCATION_LEFT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Meta_R),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_META,java.awt.event.KeyEvent.KEY_LOCATION_RIGHT));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Caps_Lock),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CAPS_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Print),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F22),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R2),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PRINTSCREEN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Cancel),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CANCEL,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Help),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HELP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Num_Lock),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUM_LOCK,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Cancel),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CANCEL,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Help),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HELP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Home),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R7),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Page_Up),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Prior),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R9),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Page_Down),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Next),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R15),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_End),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R13),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Insert),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Delete),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Home),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_HOME,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Page_Up),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Prior),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Page_Down),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Next),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_End),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Insert),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Delete),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_PageUp),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Prior),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_PageDown),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Next),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PAGE_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_EndLine),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_END,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Insert),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INSERT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Delete),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DELETE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Left),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Up),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Right),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Down),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Left),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_LEFT,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Up),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_UP,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Right),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_RIGHT,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Down),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_KP_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Left),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Up),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Right),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Down),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOWN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Begin),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BEGIN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Begin),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BEGIN,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_0),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_0,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_1),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_1,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_2),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_2,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_3),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_3,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_4),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_4,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_5),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_5,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_6),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_6,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_7),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_7,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_8),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_8,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_9),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_9,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_space),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SPACE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_exclam),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EXCLAMATION_MARK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_quotedbl),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_QUOTEDBL,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_numbersign),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMBER_SIGN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dollar),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DOLLAR,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_ampersand),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AMPERSAND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_apostrophe),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_QUOTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_parenleft),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_parenright),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_asterisk),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ASTERISK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_plus),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PLUS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_comma),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COMMA,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_minus),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MINUS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_period),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PERIOD,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_slash),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SLASH,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_colon),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COLON,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_semicolon),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SEMICOLON,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_less),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_LESS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_equal),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_greater),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_GREATER,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_at),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_bracketleft),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_OPEN_BRACKET,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_backslash),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_SLASH,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_bracketright),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CLOSE_BRACKET,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_asciicircum),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CIRCUMFLEX,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_underscore),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDERSCORE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Super_L),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_WINDOWS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Super_R),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_WINDOWS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Menu),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONTEXT_MENU,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_grave),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BACK_QUOTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_braceleft),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BRACELEFT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_braceright),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_BRACERIGHT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_exclamdown),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INVERTED_EXCLAMATION_MARK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_0),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD0,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_1),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD1,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_2),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD2,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_3),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD3,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_4),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD4,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_5),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD5,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_6),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD6,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_7),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD7,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_8),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD8,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_9),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_NUMPAD9,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Space),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SPACE,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Tab),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_TAB,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Enter),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ENTER,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Equal),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R4),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_EQUALS,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Multiply),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F26),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R6),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_MULTIPLY,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Add),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ADD,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Separator),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SEPARATOR,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Subtract),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SUBTRACT,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F24),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_SUBTRACT,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Decimal),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DECIMAL,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_KP_Divide),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F25),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_R5),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DIVIDE,java.awt.event.KeyEvent.KEY_LOCATION_NUMPAD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F1),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F1,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F2),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F2,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F3),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F3,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F4),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F4,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F5),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F5,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F6),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F6,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F7),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F7,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F8),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F8,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F9),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F9,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F10),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F10,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F11),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F11,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_F12),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F12,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_F36),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F11,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_F37),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_F12,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Execute),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ACCEPT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Kanji),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CONVERT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Henkan_Mode),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_INPUT_METHOD_ON_OFF,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Multi_key),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COMPOSE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Mode_switch),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT_GRAPH,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_ISO_Level3_Shift),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_ALT_GRAPH,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Redo),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AGAIN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Undo),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L4),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L6),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L8),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L10),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_Find),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L9),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_L3),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PROPS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Again),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_AGAIN,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Undo),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Copy),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Paste),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Cut),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Find),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_FIND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Props),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PROPS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_Stop),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_STOP,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.apXK_Copy),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.apXK_Cut),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.apXK_Paste),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Copy),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_COPY,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Cut),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_CUT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Paste),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_PASTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.osfXK_Undo),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDO,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_grave),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_acute),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_circumflex),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_tilde),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_macron),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_MACRON,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_breve),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_BREVE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_abovedot),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVEDOT,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_diaeresis),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_abovering),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVERING,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_doubleacute),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DOUBLEACUTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_caron),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CARON,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_cedilla),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_ogonek),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_OGONEK,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_iota),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_IOTA,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_voiced_sound),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_VOICED_SOUND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.XK_dead_semivoiced_sound),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_SEMIVOICED_SOUND,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Grave),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Circum),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Tilde),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Acute),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Diaeresis),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.SunXK_FA_Cedilla),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_ring_accent),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ABOVERING,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_circumflex_accent),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_cedilla_accent),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CEDILLA,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_acute_accent),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_grave_accent),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_tilde),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.DXK_diaeresis),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.hpXK_mute_acute),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_ACUTE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.hpXK_mute_grave),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_GRAVE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.hpXK_mute_asciicircum),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.hpXK_mute_diaeresis),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_DIAERESIS,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XKeySymConstants.hpXK_mute_asciitilde),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_DEAD_TILDE,java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
    keysym2JavaKeycodeHash.put(Long.valueOf(XConstants.NoSymbol),new Keysym2JavaKeycode(java.awt.event.KeyEvent.VK_UNDEFINED,java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN));
    javaKeycode2KeysymHash.put(java.awt.event.KeyEvent.VK_CAPS_LOCK,XKeySymConstants.XK_Caps_Lock);
    javaKeycode2KeysymHash.put(java.awt.event.KeyEvent.VK_NUM_LOCK,XKeySymConstants.XK_Num_Lock);
    javaKeycode2KeysymHash.put(java.awt.event.KeyEvent.VK_SCROLL_LOCK,XKeySymConstants.XK_Scroll_Lock);
    javaKeycode2KeysymHash.put(java.awt.event.KeyEvent.VK_KANA_LOCK,XKeySymConstants.XK_Kana_Lock);
  }
}
