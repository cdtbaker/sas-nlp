package testanalysis;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class TestAnalysis extends AbstractCrystalMethodAnalysis {
	long startTime;
	long endTime;
	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		startTime = System.currentTimeMillis();
		System.out.println("\nRunning: "+ this.getName());
		super.beforeAllMethods(compUnit, rootNode);
	}
	
	@Override
	public void afterAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		endTime = System.currentTimeMillis();
		System.out.println("\nEnd of " + getName() + ", \nTime Taken: "
				+ (endTime - startTime) + "ms");
		super.afterAllMethods(compUnit, rootNode);
	}



	@Override
	public void analyzeMethod(MethodDeclaration d) {
		System.out.println("Method: " + d.getName().toString() );
		d.accept(new TestVisitor());
		
	}
	@Override
	public String getName(){
		return "Test analysis";
	}
	
	private class TestVisitor extends ASTVisitor {
		
		
	}

}
