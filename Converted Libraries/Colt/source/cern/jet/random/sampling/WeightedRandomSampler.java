package cern.jet.random.sampling;
import cern.colt.list.BooleanArrayList;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;
/** 
 * Conveniently computes a stable subsequence of elements from a given input sequence;
 * Picks (samples) exactly one random element from successive blocks of <tt>weight</tt> input elements each.
 * For example, if weight==2 (a block is 2 elements), and the input is 5*2=10 elements long, then picks 5 random elements from the 10 elements such that
 * one element is randomly picked from the first block, one element from the second block, ..., one element from the last block.
 * weight == 1.0 --> all elements are picked (sampled). weight == 10.0 --> Picks one random element from successive blocks of 10 elements each. Etc.
 * The subsequence is guaranteed to be <i>stable</i>, i.e. elements never change position relative to each other.
 * @author  wolfgang.hoschek@cern.ch
 * @version 1.0, 02/05/99
 */
public class WeightedRandomSampler extends cern.colt.PersistentObject {
  protected int skip;
  protected int nextTriggerPos;
  protected int nextSkip;
  protected int weight;
  protected Uniform generator;
  static final int UNDEFINED=-1;
  /** 
 * Calls <tt>BlockedRandomSampler(1,null)</tt>.
 */
  public WeightedRandomSampler(){
    this(1,null);
  }
  /** 
 * Chooses exactly one random element from successive blocks of <tt>weight</tt> input elements each.
 * For example, if weight==2, and the input is 5*2=10 elements long, then chooses 5 random elements from the 10 elements such that
 * one is chosen from the first block, one from the second, ..., one from the last block.
 * weight == 1.0 --> all elements are consumed (sampled). 10.0 --> Consumes one random element from successive blocks of 10 elements each. Etc.
 * @param weight the weight.
 * @param randomGenerator a random number generator. Set this parameter to <tt>null</tt> to use the default random number generator.
 */
  public WeightedRandomSampler(  int weight,  RandomEngine randomGenerator){
    if (randomGenerator == null)     randomGenerator=cern.jet.random.AbstractDistribution.makeDefaultGenerator();
    this.generator=new Uniform(randomGenerator);
    setWeight(weight);
  }
  /** 
 * Returns a deep copy of the receiver.
 */
  public Object clone(){
    WeightedRandomSampler copy=(WeightedRandomSampler)super.clone();
    copy.generator=(Uniform)this.generator.clone();
    return copy;
  }
  /** 
 * Not yet commented.
 * @param weight int
 */
  public int getWeight(){
    return this.weight;
  }
  /** 
 * Chooses exactly one random element from successive blocks of <tt>weight</tt> input elements each.
 * For example, if weight==2, and the input is 5*2=10 elements long, then chooses 5 random elements from the 10 elements such that
 * one is chosen from the first block, one from the second, ..., one from the last block.
 * @return <tt>true</tt> if the next element shall be sampled (picked), <tt>false</tt> otherwise.
 */
  public boolean sampleNextElement(){
    if (skip > 0) {
      skip--;
      return false;
    }
    if (nextTriggerPos == UNDEFINED) {
      if (weight == 1)       nextTriggerPos=0;
 else       nextTriggerPos=generator.nextIntFromTo(0,weight - 1);
      nextSkip=weight - 1 - nextTriggerPos;
    }
    if (nextTriggerPos > 0) {
      nextTriggerPos--;
      return false;
    }
    nextTriggerPos=UNDEFINED;
    skip=nextSkip;
    return true;
  }
  /** 
 * Not yet commented.
 * @param weight int
 */
  public void setWeight(  int weight){
    if (weight < 1)     throw new IllegalArgumentException("bad weight");
    this.weight=weight;
    this.skip=0;
    this.nextTriggerPos=UNDEFINED;
    this.nextSkip=0;
  }
  /** 
 * Not yet commented.
 */
  public static void test(  int weight,  int size){
    WeightedRandomSampler sampler=new WeightedRandomSampler();
    sampler.setWeight(weight);
    cern.colt.list.IntArrayList sample=new cern.colt.list.IntArrayList();
    for (int i=0; i < size; i++) {
      if (sampler.sampleNextElement())       sample.add(i);
    }
    System.out.println("Sample = " + sample);
  }
  /** 
 * Chooses exactly one random element from successive blocks of <tt>weight</tt> input elements each.
 * For example, if weight==2, and the input is 5*2=10 elements long, then chooses 5 random elements from the 10 elements such that
 * one is chosen from the first block, one from the second, ..., one from the last block.
 * @param acceptList a bitvector which will be filled with <tt>true</tt> where sampling shall occur and <tt>false</tt> where it shall not occur.
 */
  private void xsampleNextElements(  BooleanArrayList acceptList){
    int length=acceptList.size();
    boolean[] accept=acceptList.elements();
    for (int i=0; i < length; i++) {
      if (skip > 0) {
        skip--;
        accept[i]=false;
        continue;
      }
      if (nextTriggerPos == UNDEFINED) {
        if (weight == 1)         nextTriggerPos=0;
 else         nextTriggerPos=generator.nextIntFromTo(0,weight - 1);
        nextSkip=weight - 1 - nextTriggerPos;
      }
      if (nextTriggerPos > 0) {
        nextTriggerPos--;
        accept[i]=false;
        continue;
      }
      nextTriggerPos=UNDEFINED;
      skip=nextSkip;
      accept[i]=true;
    }
  }
}
