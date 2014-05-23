package com.jml.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.jml.objects.classlevel.JMLClass;
import com.jml.objects.classlevel.JMLInterface;
import com.jml.objects.classlevel.JMLSuperInterface;
import com.jml.objects.comments.JMLComment;
import com.jml.objects.comments.JMLJavadoc;
import com.jml.objects.comments.JavadocTag;
import com.jml.objects.framework.JMLElement;
import com.jml.objects.scopes.JMLAnnonClass;
import com.jml.objects.scopes.JMLMethod;
import com.jml.objects.scopes.JMLScope;
import com.jml.objects.sourcelevel.JMLImport;
import com.jml.objects.statement.JMLVariable;

public class XMLFromSource {

	public static JMLElement getXMLFromFile(String path, boolean lineNumbers) {
		CompilationUnit c = null;
		String sourceCode = null;
		try {
			sourceCode = getFileText(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		c = getCompilationUnit(sourceCode);

		JMLElement root = null;
		root = createXML(c, sourceCode, lineNumbers);

		return root;
	}

	private static String getFileText(String s) throws IOException {
		StringBuilder sb = new StringBuilder();

		FileReader fr = new FileReader(s);
		BufferedReader br = new BufferedReader(fr);
		String add = br.readLine();

		while (add != null) {
			sb.append(add + "\n");
			add = br.readLine();
		}
		br.close();
		return sb.toString();
	}

	private static CompilationUnit getCompilationUnit(String s) {
		ASTParser p = ASTParser.newParser(AST.JLS4);
		p.setSource(s.toCharArray());
		p.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit c = (CompilationUnit) p.createAST(null);
		return c;
	}

	public static JMLElement createXML(final CompilationUnit c, String sc,
			final boolean lineNumbers) {

		String s = "";
		if (c.getPackage() != null) {
			s = c.getPackage().toString();
			s = s.substring(s.indexOf(" ") + 1, s.indexOf(";"));
		}
		final XMLBuilder b = new XMLBuilder(s);

		final String[] source = sc.split("\n");

		@SuppressWarnings("unchecked")
		List<ImportDeclaration> imports = (List<ImportDeclaration>) c.imports();
		for (ImportDeclaration id : imports) {
			b.addLine(new JMLImport(id.getName().toString()));
		}

		c.accept(new ASTVisitor() {
			List<JMLElement> commentsToAdd = new ArrayList<JMLElement>();

			private MethodDeclaration md;

			@Override
			public boolean visit(TypeDeclaration node) {
				if (node.isInterface()) {
					JMLInterface in = new JMLInterface(node.getName()
							.toString());
					for (Object n : node.superInterfaceTypes()) {
						String name = ((SimpleType) n).getName().toString();
						in.addSuperInterface(name);
					}
					b.addBlock(in);
				} else {

					JMLClass jc = new JMLClass(node.getName().toString());
					if (node.getSuperclassType() != null) {
						jc.addSuperclass(node.getSuperclassType().toString());
					}
					if (lineNumbers) {
						jc.addAttribute("line", getLineNumber(node));
					}
					b.addBlock(jc);
					for (Object n : node.superInterfaceTypes()) {
						String name = ((SimpleType) n).getName().toString();
						b.addLine(new JMLSuperInterface(name));
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				if (node.getBody() != null) {
					md = node;
					checkForComments(node);
				}else{
					String name = node.getName().toString();
					String type = node.getReturnType2().toString();
					JMLMethod m = new JMLMethod(name,type);
					b.addLine(m);
				}
				return super.visit(node);
			}

			private void checkForComments(ASTNode node) {
				List<Comment> cList = new ArrayList<Comment>();
				for (Object com : c.getCommentList()) {
					if (((Comment) com).getParent() == null) {
						cList.add((Comment) com);
					}
				}
				for (Object com : cList) {
					ASTNode withoutSignature = NodeFinder.perform(
							((Comment) com).getAlternateRoot(),
							((Comment) com).getStartPosition(), 0);
					if (node.equals(withoutSignature.getParent())) {
						commentsToAdd.add(new JMLComment(
								getCommentText((ASTNode) com)));

					}
				}
			}

			public String getCommentText(ASTNode comment) {
				int start = c.getLineNumber(comment.getStartPosition());
				int end = c.getLineNumber(comment.getLength()) + start - 1;

				StringBuilder sb = new StringBuilder();
				for (int i = start - 1; i < end; i++) {
					sb.append(source[i]);
				}
				return sb.toString();
			}

			@Override
			public boolean visit(Block node) {
				if (md != null) {
					JMLMethod m = null;
					if (md.getReturnType2() != null) {
						m = new JMLMethod(md.getName().toString(), md
								.getReturnType2().toString());
					} else {
						m = new JMLMethod(md.getName().toString());
					}
					if (lineNumbers) {
						m.addAttribute("line", getLineNumber(node));
					}
					b.addBlock(m);
					for (JMLElement com : commentsToAdd) {
						m.addContent(com);
					}
					commentsToAdd.clear();
					md = null;
				} else {
					JMLScope scope = new JMLScope();
					if (lineNumbers) {
						scope.addAttribute("line", getLineNumber(node));
					}
					b.addBlock(scope);
				}
				return super.visit(node);
			}

			@Override
			public void endVisit(AnonymousClassDeclaration node) {
				b.endBlock();
				super.endVisit(node);
			}

			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				JMLAnnonClass ac = new JMLAnnonClass();
				if (lineNumbers) {
					ac.addAttribute("line", getLineNumber(node));
				}
				b.addBlock(ac);
				return super.visit(node);
			}

			@Override
			public void endVisit(TypeDeclaration node) {
				b.endBlock();
				super.endVisit(node);
			}

			@Override
			public void endVisit(Block node) {
				b.endBlock();
				super.endVisit(node);
			}

			@Override
			public boolean visit(Javadoc node) {
				@SuppressWarnings("unchecked")
				List<TagElement> tags = node.tags();
				JMLJavadoc j = null;
				if (!tags.isEmpty()) {
					j = new JMLJavadoc(tags.get(0).toString());
				} else {
					j = new JMLJavadoc("");
				}
				for (int i = 1; i < tags.size(); i++) {
					j.addContent(new JavadocTag(tags.get(i).getTagName(), tags
							.get(i).toString()
							.replaceAll(tags.get(i).getTagName() + " ", "")));

				}
				b.addLine(j);
				return super.visit(node);
			}

			@Override
			public boolean visit(FieldDeclaration node) {
				String type = node.getType().toString();
				String name = ((VariableDeclarationFragment) node.fragments()
						.get(0)).getName().toString();
				JMLVariable v = new JMLVariable(name, type);
				if (lineNumbers) {
					v.addAttribute("line", getLineNumber(node));
				}
				b.addLine(v);

				return super.visit(node);
			}

			@Override
			public void endVisit(VariableDeclarationStatement node) {
				String type = node.getType().toString();
				String name = ((VariableDeclarationFragment) node.fragments()
						.get(0)).getName().toString();

				JMLVariable v = new JMLVariable(name, type);
				if (lineNumbers) {
					v.addAttribute("line", getLineNumber(node));
				}
				b.addLine(v);
				super.endVisit(node);
			}

			private String getLineNumber(ASTNode n) {
				return String.valueOf(c.getLineNumber(n.getStartPosition()));
			}
		});

		return b.getRoot();
	}
}
