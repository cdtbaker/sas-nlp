package org.ojalgo.type;
public enum SQL {/** 
 * <ul>
 * <li>BIGINT</li>
 * </ul>
 */
BIGINT(java.lang.Long.class), /** 
 * <ul>
 * <li>VARBINARY</li>
 * <li>LONGVARBINARY</li>
 * <li>BINARY</li>
 * <li>LONG BINARY</li>
 * <li>IMAGE</li>
 * <li>UNIQUEIDENTIFIER</li>
 * </ul>
 */
BINARY(byte[].class), /** 
 * <ul>
 * <li>BIT</li>
 * </ul>
 */
BIT(java.lang.Boolean.class), /** 
 * <ul>
 * <li>VARCHAR</li>
 * <li>LONGVARCHAR</li>
 * <li>CHARACTER</li>
 * <li>CHAR</li>
 * <li>TEXT</li>
 * <li>UNIQUEIDENTIFIERSTR</li>
 * </ul>
 */
CHARACTER(java.lang.String.class), /** 
 * <ul>
 * <li>DATE</li>
 * </ul>
 */
DATE(java.sql.Date.class), /** 
 * <ul>
 * <li>DECIMAL</li>
 * <li>NUMERIC</li>
 * <li>MONEY</li>
 * <li>SMALLMONEY</li>
 * </ul>
 */
DECIMAL(java.math.BigDecimal.class), /** 
 * <ul>
 * <li>DOUBLE</li>
 * <li>FLOAT</li>
 * <li>DOUBLE PRECISION</li>
 * </ul>
 */
DOUBLE(java.lang.Double.class), /** 
 * <ul>
 * <li>INTEGER</li>
 * <li>INT</li>
 * </ul>
 */
INTEGER(java.lang.Integer.class), /** 
 * <ul>
 * <li>REAL</li>
 * </ul>
 */
REAL(java.lang.Float.class), /** 
 * <ul>
 * <li>SMALLINT</li>
 * </ul>
 */
SMALLINT(java.lang.Short.class), /** 
 * <ul>
 * <li>TIME</li>
 * </ul>
 */
TIME(java.sql.Time.class), /** 
 * <ul>
 * <li>TIMESTAMP</li>
 * <li>DATETIME</li>
 * <li>SMALLDATETIME</li>
 * </ul>
 */
TIMESTAMP(java.sql.Timestamp.class), /** 
 * <ul>
 * <li>TINYINT</li>
 * </ul>
 */
TINYINT(java.lang.Byte.class); private final Class<?> myJavaClass;
SQL(Class<?> aJavaClass){
  myJavaClass=aJavaClass;
}
public final Class<?> getJavaClass(){
  return myJavaClass;
}
}
