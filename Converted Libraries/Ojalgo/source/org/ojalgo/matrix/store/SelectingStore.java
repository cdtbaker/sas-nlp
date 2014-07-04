package org.ojalgo.matrix.store;
abstract class SelectingStore<N extends Number> extends LogicalStore<N> {
  protected SelectingStore(  final int rowCount,  final int columnCount,  final MatrixStore<N> base){
    super(rowCount,columnCount,base);
  }
}
