
public class Test {
	
	public static void testPos(int i){
		//should not error or warning
		int x = 1;
		//should not error or warning
		int y = x;
		//should error as z expected pos
		x = -1;
		//should warning as i is unknown before runtime
		x = i;
	}
	
	public static void testNeg(int i){
		//should not error or warning
		int x = -1;
		//should not error or warning
		int y = x;
		//should error as z expected neg
		x = 1;
		//should warning as i is unknown before runtime
		x = i;
	}

	public static void testZero(int i){
		//should not error or warning
		int x = 0;
		//should not error or warning
		int y = x;
		//should error as z expected zero
		x = 1;
		//should warning as i is unknown before runtime
		x = i;
	}
	
	public static void testEvaluation(int i){
		//should not error as expression is basic
		int x = 100*10;
		//NOT AS EXPECTED 
		//cannot guarentee state as -5 is given state of neg
		//+ 6 is given state of pos and (neg+pos) is unknown
		x = 100+55/5*(-1*-5+6);
		//NOT AS EXPECTED error states x<0 when x==0
		x = 100+50*-2;
		int y = 100;
		//should not error or warning
		x = y+50;
	}
}
