package com.sun.media.sound;
/** 
 * LFO control signal generator.
 * @author Karl Helgason
 */
public class SoftLowFrequencyOscillator implements SoftProcess {
  private int max_count=10;
  private int used_count=0;
  private double[][] out=new double[max_count][1];
  private double[][] delay=new double[max_count][1];
  private double[][] delay2=new double[max_count][1];
  private double[][] freq=new double[max_count][1];
  private int[] delay_counter=new int[max_count];
  private double[] sin_phase=new double[max_count];
  private double[] sin_stepfreq=new double[max_count];
  private double[] sin_step=new double[max_count];
  private double control_time=0;
  private double sin_factor=0;
  private static double PI2=2.0 * Math.PI;
  public SoftLowFrequencyOscillator(){
    for (int i=0; i < sin_stepfreq.length; i++) {
      sin_stepfreq[i]=Double.NEGATIVE_INFINITY;
    }
  }
  public void reset(){
    for (int i=0; i < used_count; i++) {
      out[i][0]=0;
      delay[i][0]=0;
      delay2[i][0]=0;
      freq[i][0]=0;
      delay_counter[i]=0;
      sin_phase[i]=0;
      sin_stepfreq[i]=Double.NEGATIVE_INFINITY;
      sin_step[i]=0;
    }
    used_count=0;
  }
  public void init(  SoftSynthesizer synth){
    control_time=1.0 / synth.getControlRate();
    sin_factor=control_time * 2 * Math.PI;
    for (int i=0; i < used_count; i++) {
      delay_counter[i]=(int)(Math.pow(2,this.delay[i][0] / 1200.0) / control_time);
      delay_counter[i]+=(int)(delay2[i][0] / (control_time * 1000));
    }
    processControlLogic();
  }
  public void processControlLogic(){
    for (int i=0; i < used_count; i++) {
      if (delay_counter[i] > 0) {
        delay_counter[i]--;
        out[i][0]=0.5;
      }
 else {
        double f=freq[i][0];
        if (sin_stepfreq[i] != f) {
          sin_stepfreq[i]=f;
          double fr=440.0 * Math.exp((f - 6900.0) * (Math.log(2) / 1200.0));
          sin_step[i]=fr * sin_factor;
        }
        double p=sin_phase[i];
        p+=sin_step[i];
        while (p > PI2)         p-=PI2;
        out[i][0]=0.5 + Math.sin(p) * 0.5;
        sin_phase[i]=p;
      }
    }
  }
  public double[] get(  int instance,  String name){
    if (instance >= used_count)     used_count=instance + 1;
    if (name == null)     return out[instance];
    if (name.equals("delay"))     return delay[instance];
    if (name.equals("delay2"))     return delay2[instance];
    if (name.equals("freq"))     return freq[instance];
    return null;
  }
}