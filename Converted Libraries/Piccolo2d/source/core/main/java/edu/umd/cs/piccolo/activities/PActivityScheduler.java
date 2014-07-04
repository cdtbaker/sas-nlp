package edu.umd.cs.piccolo.activities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.util.PUtil;
/** 
 * <b>PActivityScheduler</b> is responsible for maintaining a list of
 * activities. It is given a chance to process these activities from the PRoot's
 * processInputs() method. Most users will not need to use the
 * PActivityScheduler directly, instead you should look at:
 * <ul>
 * <li>PNode.addActivity - to schedule a new activity
 * <li>PActivity.terminate - to terminate a running activity
 * <li>PRoot.processInputs - already calls processActivities for you.
 * </ul>
 * @version 1.0
 * @author Jesse Grosjean
 */
public class PActivityScheduler implements Serializable {
  private static final long serialVersionUID=1L;
  private transient Timer activityTimer=null;
  private final PRoot root;
  private final List activities;
  private boolean activitiesChanged;
  private boolean animating;
  private final ArrayList processingActivities;
  /** 
 * Constructs an instance of PActivityScheduler. All activities it will
 * schedule will take place on children of the rootNode provided.
 * @param rootNode root node of all activities to be performed. All nodes
 * being animated should have this node as an ancestor.
 */
  public PActivityScheduler(  final PRoot rootNode){
    root=rootNode;
    activities=new ArrayList();
    processingActivities=new ArrayList();
  }
  /** 
 * Returns the node from which all activities will be attached.
 * @return this scheduler's associated root node
 */
  public PRoot getRoot(){
    return root;
  }
  /** 
 * Adds the given activity to the scheduler if not already found.
 * @param activity activity to be scheduled
 */
  public void addActivity(  final PActivity activity){
    addActivity(activity,false);
  }
  /** 
 * Add this activity to the scheduler. Sometimes it's useful to make sure
 * that an activity is run after all other activities have been run. To do
 * this set processLast to true when adding the activity.
 * @param activity activity to be scheduled
 * @param processLast whether or not this activity should be performed after
 * all other scheduled activities
 */
  public void addActivity(  final PActivity activity,  final boolean processLast){
    if (activities.contains(activity)) {
      return;
    }
    activitiesChanged=true;
    if (processLast) {
      activities.add(0,activity);
    }
 else {
      activities.add(activity);
    }
    activity.setActivityScheduler(this);
    if (!getActivityTimer().isRunning()) {
      startActivityTimer();
    }
  }
  /** 
 * Removes the given activity from the scheduled activities. Does nothing if
 * it's not found.
 * @param activity the activity to be removed
 */
  public void removeActivity(  final PActivity activity){
    if (!activities.contains(activity)) {
      return;
    }
    activitiesChanged=true;
    activities.remove(activity);
    if (activities.size() == 0) {
      stopActivityTimer();
    }
  }
  /** 
 * Removes all activities from the list of scheduled activities.
 */
  public void removeAllActivities(){
    activitiesChanged=true;
    activities.clear();
    stopActivityTimer();
  }
  /** 
 * Returns a reference to the current activities list. Handle with care.
 * @return reference to the current activities list.
 */
  public List getActivitiesReference(){
    return activities;
  }
  /** 
 * Process all scheduled activities for the given time. Each activity is
 * given one "step", equivalent to one frame of animation.
 * @param currentTime the current unix time in milliseconds.
 */
  public void processActivities(  final long currentTime){
    final int size=activities.size();
    if (size > 0) {
      processingActivities.addAll(activities);
      for (int i=size - 1; i >= 0; i--) {
        final PActivity each=(PActivity)processingActivities.get(i);
        each.processStep(currentTime);
      }
      processingActivities.clear();
    }
  }
  /** 
 * Return true if any of the scheduled activities are animations.
 * @return true if any of the scheduled activities are animations.
 */
  public boolean getAnimating(){
    if (activitiesChanged) {
      animating=false;
      for (int i=0; i < activities.size(); i++) {
        final PActivity each=(PActivity)activities.get(i);
        animating|=each.isAnimation();
      }
      activitiesChanged=false;
    }
    return animating;
  }
  /** 
 * Starts the current activity timer. Multiple calls to this method are
 * ignored.
 */
  protected void startActivityTimer(){
    getActivityTimer().start();
  }
  /** 
 * Stops the current activity timer.
 */
  protected void stopActivityTimer(){
    getActivityTimer().stop();
  }
  /** 
 * Returns the activity timer. Creating it if necessary.
 * @return a Timer instance.
 */
  protected Timer getActivityTimer(){
    if (activityTimer == null) {
      activityTimer=root.createTimer(PUtil.ACTIVITY_SCHEDULER_FRAME_DELAY,new ActionListener(){
        public void actionPerformed(        final ActionEvent e){
          root.processInputs();
        }
      }
);
    }
    return activityTimer;
  }
}
