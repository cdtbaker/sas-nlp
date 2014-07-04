package org.apache.commons.math3.util;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.junit.Assert;
import org.junit.Test;
/** 
 */
public class MultidimensionalCounterTest {
  @Test public void testPreconditions(){
    MultidimensionalCounter c;
    try {
      c=new MultidimensionalCounter(0,1);
      Assert.fail("NotStrictlyPositiveException expected");
    }
 catch (    NotStrictlyPositiveException e) {
    }
    try {
      c=new MultidimensionalCounter(2,0);
      Assert.fail("NotStrictlyPositiveException expected");
    }
 catch (    NotStrictlyPositiveException e) {
    }
    try {
      c=new MultidimensionalCounter(-1,1);
      Assert.fail("NotStrictlyPositiveException expected");
    }
 catch (    NotStrictlyPositiveException e) {
    }
    c=new MultidimensionalCounter(2,3);
    try {
      c.getCount(1,1,1);
      Assert.fail("DimensionMismatchException expected");
    }
 catch (    DimensionMismatchException e) {
    }
    try {
      c.getCount(3,1);
      Assert.fail("OutOfRangeException expected");
    }
 catch (    OutOfRangeException e) {
    }
    try {
      c.getCount(0,-1);
      Assert.fail("OutOfRangeException expected");
    }
 catch (    OutOfRangeException e) {
    }
    try {
      c.getCounts(-1);
      Assert.fail("OutOfRangeException expected");
    }
 catch (    OutOfRangeException e) {
    }
    try {
      c.getCounts(6);
      Assert.fail("OutOfRangeException expected");
    }
 catch (    OutOfRangeException e) {
    }
  }
  @Test public void testIteratorPreconditions(){
    MultidimensionalCounter.Iterator iter=(new MultidimensionalCounter(2,3)).iterator();
    try {
      iter.getCount(-1);
      Assert.fail("IndexOutOfBoundsException expected");
    }
 catch (    IndexOutOfBoundsException e) {
    }
    try {
      iter.getCount(2);
      Assert.fail("IndexOutOfBoundsException expected");
    }
 catch (    IndexOutOfBoundsException e) {
    }
  }
  @Test public void testMulti2UniConversion(){
    final MultidimensionalCounter c=new MultidimensionalCounter(2,4,5);
    Assert.assertEquals(c.getCount(1,2,3),33);
  }
  @Test public void testAccessors(){
    final int[] originalSize=new int[]{2,6,5};
    final MultidimensionalCounter c=new MultidimensionalCounter(originalSize);
    final int nDim=c.getDimension();
    Assert.assertEquals(nDim,originalSize.length);
    final int[] size=c.getSizes();
    for (int i=0; i < nDim; i++) {
      Assert.assertEquals(originalSize[i],size[i]);
    }
  }
  @Test public void testIterationConsistency(){
    final MultidimensionalCounter c=new MultidimensionalCounter(2,3,4);
    final int[][] expected=new int[][]{{0,0,0},{0,0,1},{0,0,2},{0,0,3},{0,1,0},{0,1,1},{0,1,2},{0,1,3},{0,2,0},{0,2,1},{0,2,2},{0,2,3},{1,0,0},{1,0,1},{1,0,2},{1,0,3},{1,1,0},{1,1,1},{1,1,2},{1,1,3},{1,2,0},{1,2,1},{1,2,2},{1,2,3}};
    final int totalSize=c.getSize();
    final int nDim=c.getDimension();
    final MultidimensionalCounter.Iterator iter=c.iterator();
    for (int i=0; i < totalSize; i++) {
      if (!iter.hasNext()) {
        Assert.fail("Too short");
      }
      final int uniDimIndex=iter.next();
      Assert.assertEquals("Wrong iteration at " + i,i,uniDimIndex);
      for (int dimIndex=0; dimIndex < nDim; dimIndex++) {
        Assert.assertEquals("Wrong multidimensional index for [" + i + "]["+ dimIndex+ "]",expected[i][dimIndex],iter.getCount(dimIndex));
      }
      Assert.assertEquals("Wrong unidimensional index for [" + i + "]",c.getCount(expected[i]),uniDimIndex);
      final int[] indices=c.getCounts(uniDimIndex);
      for (int dimIndex=0; dimIndex < nDim; dimIndex++) {
        Assert.assertEquals("Wrong multidimensional index for [" + i + "]["+ dimIndex+ "]",expected[i][dimIndex],indices[dimIndex]);
      }
    }
    if (iter.hasNext()) {
      Assert.fail("Too long");
    }
  }
}
