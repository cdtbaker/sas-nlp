package org.ojalgo.matrix.store;
abstract class ShadingStore<N extends Number> extends LogicalStore<N> {
  protected ShadingStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    super(aRowDim,aColDim,aBase);
  }
}
