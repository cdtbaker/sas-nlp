package annotationtest;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeFlowAnalysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

import edu.cmu.cs.crystal.AbstractCompilationUnitAnalysis;

public class AnnotationTestAnalysis extends AbstractCompilationUnitAnalysis {

	private CompilationUnit compUnit;

	@Override
	public void analyzeCompilationUnit(CompilationUnit c) {

		this.compUnit = c;
		c.accept(new AnnoVisitor());

	}

	private class AnnoVisitor extends ASTVisitor {
		@Override
		public void endVisit(MarkerAnnotation node) {
			// annotation with no params
			System.out.println("Marker anno:" + node.getTypeName());
		}

		@Override
		public void endVisit(NormalAnnotation node) {
			// annotation with multiple params
			System.out.println("Normal anno:" + node.getTypeName());
			if (node.getTypeName().toString().equals("Positive")
					|| node.getTypeName().toString().equals("Negative")
					|| node.getTypeName().toString().equals("Zero")) {

				PositiveNegativeFlowAnalysis fl = new PositiveNegativeFlowAnalysis(
						node);
				fl.runAnalysis(getReporter(), getInput(),
						compUnit.getTypeRoot(), compUnit);
			}
		}

		@Override
		public void endVisit(SingleMemberAnnotation node) {
			// annotation with one param
			System.out.println("Single mem anno:" + node.getTypeName());
		}

		public MethodDeclaration getMethod(ASTNode ast) {
			while (!(ast instanceof MethodDeclaration)) {
				ast = ast.getParent();
			}
			return (MethodDeclaration) ast;
		}
	}

}
