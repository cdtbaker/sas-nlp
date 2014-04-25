public class Test {

	
	int field1;
	String field2;
	
	/**
	 * JAVADOC 1 - Attached to main method
	 * 
	 */
	public static void main(String[] args){
		
		//line comment inside main method
		
		int varMain1;
		int varMain2;
		
		if(true){
			int varInScope1;
		}
				
	}
	
	/*
	 * Block comment inside class
	 */	
	public static void method2(){
		int varMethod2;
	}
	
	/**
	 * JAVADOC 2 - Attached to method 3 with tags
	 * @param param1
	 * @param param2
	 * @param param3
	 * @return false
	 */
	public static boolean method3(int param1, int param2, int param3){
		int varMethod3;
		return false;
	}
	
	/**
	 * JAVADOC 3 - Not attached to field or method
	 */
	
}

