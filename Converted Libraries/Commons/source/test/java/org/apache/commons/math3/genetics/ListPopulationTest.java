package org.apache.commons.math3.genetics;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Test;
public class ListPopulationTest {
  @Test public void testGetFittestChromosome(){
    Chromosome c1=new Chromosome(){
      public double fitness(){
        return 0;
      }
    }
;
    Chromosome c2=new Chromosome(){
      public double fitness(){
        return 10;
      }
    }
;
    Chromosome c3=new Chromosome(){
      public double fitness(){
        return 15;
      }
    }
;
    ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(c1);
    chromosomes.add(c2);
    chromosomes.add(c3);
    ListPopulation population=new ListPopulation(chromosomes,10){
      public Population nextGeneration(){
        return null;
      }
    }
;
    Assert.assertEquals(c3,population.getFittestChromosome());
  }
  @Test public void testChromosomes(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    final ListPopulation population=new ListPopulation(10){
      public Population nextGeneration(){
        return null;
      }
    }
;
    population.addChromosomes(chromosomes);
    Assert.assertEquals(chromosomes,population.getChromosomes());
    Assert.assertEquals(chromosomes.toString(),population.toString());
    population.setPopulationLimit(50);
    Assert.assertEquals(50,population.getPopulationLimit());
  }
  @Test(expected=NotPositiveException.class) public void testSetPopulationLimit(){
    final ListPopulation population=new ListPopulation(10){
      public Population nextGeneration(){
        return null;
      }
    }
;
    population.setPopulationLimit(-50);
  }
  @Test(expected=NotPositiveException.class) public void testConstructorPopulationLimitNotPositive(){
    new ListPopulation(-10){
      public Population nextGeneration(){
        return null;
      }
    }
;
  }
  @Test(expected=NotPositiveException.class) public void testChromosomeListConstructorPopulationLimitNotPositive(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    new ListPopulation(chromosomes,-10){
      public Population nextGeneration(){
        return null;
      }
    }
;
  }
  @Test(expected=NumberIsTooLargeException.class) public void testConstructorListOfChromosomesBiggerThanPopulationSize(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    new ListPopulation(chromosomes,1){
      public Population nextGeneration(){
        return null;
      }
    }
;
  }
  @Test(expected=NumberIsTooLargeException.class) public void testAddTooManyChromosomes(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    final ListPopulation population=new ListPopulation(2){
      public Population nextGeneration(){
        return null;
      }
    }
;
    population.addChromosomes(chromosomes);
  }
  @Test(expected=NumberIsTooLargeException.class) public void testAddTooManyChromosomesSingleCall(){
    final ListPopulation population=new ListPopulation(2){
      public Population nextGeneration(){
        return null;
      }
    }
;
    for (int i=0; i <= population.getPopulationLimit(); i++) {
      population.addChromosome(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    }
  }
  @Test(expected=UnsupportedOperationException.class) public void testIterator(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    final ListPopulation population=new ListPopulation(10){
      public Population nextGeneration(){
        return null;
      }
    }
;
    population.addChromosomes(chromosomes);
    final Iterator<Chromosome> iter=population.iterator();
    while (iter.hasNext()) {
      iter.next();
      iter.remove();
    }
  }
  @Test(expected=NumberIsTooSmallException.class) public void testSetPopulationLimitTooSmall(){
    final ArrayList<Chromosome> chromosomes=new ArrayList<Chromosome>();
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(3)));
    final ListPopulation population=new ListPopulation(chromosomes,3){
      public Population nextGeneration(){
        return null;
      }
    }
;
    population.setPopulationLimit(2);
  }
}
