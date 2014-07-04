package edu.umd.cs.piccolo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PObjectOutputStream;
/** 
 * <b>PLayer</b> is a node that can be viewed directly by multiple camera nodes.
 * Generally child nodes are added to a layer to give the viewing cameras
 * something to look at.
 * <P>
 * A single layer node may be viewed through multiple cameras with each camera
 * using its own view transform. This means that any node (since layers can have
 * children) may be visible through multiple cameras at the same time.
 * <p>
 * @see PCamera
 * @see edu.umd.cs.piccolo.event.PInputEvent
 * @see edu.umd.cs.piccolo.util.PPickPath
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PLayer extends PNode {
  /** 
 * Allows for future serialization code to understand versioned binary
 * formats.
 */
  private static final long serialVersionUID=1L;
  /** 
 * The property name that identifies a change in the set of this layer's
 * cameras (see {@link #getCamera getCamera}, {@link #getCameraCountgetCameraCount}, {@link #getCamerasReference getCamerasReference}). In
 * any property change event the new value will be a reference to the list
 * of cameras, but old value will always be null.
 */
  public static final String PROPERTY_CAMERAS="cameras";
  /** 
 * The property code that identifies a change in the set of this layer's
 * cameras (see {@link #getCamera getCamera}, {@link #getCameraCountgetCameraCount}, {@link #getCamerasReference getCamerasReference}). In
 * any property change event the new value will be a reference to the list
 * of cameras, but old value will always be null.
 */
  public static final int PROPERTY_CODE_CAMERAS=1 << 13;
  /** 
 * Cameras which are registered as viewers of this PLayer.
 */
  private transient List cameras;
  /** 
 * Creates a PLayer without any cameras attached to it.
 */
  public PLayer(){
    super();
    cameras=new ArrayList();
  }
  /** 
 * Get the list of cameras viewing this layer.
 * @return direct reference to registered cameras
 */
  public List getCamerasReference(){
    return cameras;
  }
  /** 
 * Get the number of cameras viewing this layer.
 * @return the number of cameras attached to this layer
 */
  public int getCameraCount(){
    if (cameras == null) {
      return 0;
    }
    return cameras.size();
  }
  /** 
 * Get the camera in this layer's camera list at the specified index.
 * @param index index of camera to fetch
 * @return camera at the given index
 */
  public PCamera getCamera(  final int index){
    return (PCamera)cameras.get(index);
  }
  /** 
 * Add a camera to this layer's camera list. This method it called
 * automatically when a layer is added to a camera.
 * @param camera the camera to add to this layer
 */
  public void addCamera(  final PCamera camera){
    addCamera(cameras.size(),camera);
  }
  /** 
 * Add a camera to this layer's camera list at the specified index. This
 * method it called automatically when a layer is added to a camera.
 * @param index index at which the camera should be inserted
 * @param camera Camera to add to layer
 */
  public void addCamera(  final int index,  final PCamera camera){
    cameras.add(index,camera);
    invalidatePaint();
    firePropertyChange(PROPERTY_CODE_CAMERAS,PROPERTY_CAMERAS,null,cameras);
  }
  /** 
 * Remove the camera from this layer's camera list.
 * @param camera the camera to remove from the layer, does nothing if not
 * found
 * @return camera that was passed in
 */
  public PCamera removeCamera(  final PCamera camera){
    if (cameras.remove(camera)) {
      invalidatePaint();
      firePropertyChange(PROPERTY_CODE_CAMERAS,PROPERTY_CAMERAS,null,cameras);
    }
    return camera;
  }
  /** 
 * Remove the camera at the given index from this layer's camera list.
 * @param index the index of the camera we wish to remove
 * @return camera that was removed
 */
  public PCamera removeCamera(  final int index){
    final PCamera result=(PCamera)cameras.remove(index);
    invalidatePaint();
    firePropertyChange(PROPERTY_CODE_CAMERAS,PROPERTY_CAMERAS,null,cameras);
    return result;
  }
  /** 
 * Override repaints and forward them to the cameras that are viewing this
 * layer.
 * @param localBounds bounds flagged as needing repainting
 * @param repaintSource the source of the repaint notification
 */
  public void repaintFrom(  final PBounds localBounds,  final PNode repaintSource){
    if (repaintSource != this) {
      localToParent(localBounds);
    }
    notifyCameras(localBounds);
    if (getParent() != null) {
      getParent().repaintFrom(localBounds,repaintSource);
    }
  }
  /** 
 * Dispatches repaint notification to all registered cameras.
 * @param parentBounds bounds needing repainting in parent coordinate system
 */
  protected void notifyCameras(  final PBounds parentBounds){
    final int count=getCameraCount();
    for (int i=0; i < count; i++) {
      final PCamera each=(PCamera)cameras.get(i);
      each.repaintFromLayer(parentBounds,this);
    }
  }
  /** 
 * Write this layer and all its children out to the given stream. Note that
 * the layer writes out any cameras that are viewing it conditionally, so
 * they will only get written out if someone else writes them
 * unconditionally.
 * @param out object to which the layer should be streamed
 * @throws IOException may occur while serializing to stream
 */
  private void writeObject(  final ObjectOutputStream out) throws IOException {
    if (!(out instanceof PObjectOutputStream)) {
      throw new RuntimeException("May not serialize PLayer to a non PObjectOutputStream");
    }
    out.defaultWriteObject();
    final int count=getCameraCount();
    for (int i=0; i < count; i++) {
      ((PObjectOutputStream)out).writeConditionalObject(cameras.get(i));
    }
    out.writeObject(Boolean.FALSE);
  }
  /** 
 * Deserializes PLayer from the provided ObjectInputStream.
 * @param in stream from which PLayer should be read
 * @throws IOException since it involves quite a bit of IO
 * @throws ClassNotFoundException may occur is serialized stream has been
 * renamed after serialization
 */
  private void readObject(  final ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    cameras=new ArrayList();
    while (true) {
      final Object each=in.readObject();
      if (each != null) {
        if (each.equals(Boolean.FALSE)) {
          break;
        }
 else {
          cameras.add(each);
        }
      }
    }
  }
}
