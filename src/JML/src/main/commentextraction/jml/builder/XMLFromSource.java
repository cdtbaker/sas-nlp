package main.commentextraction.jml.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.commentextraction.jml.objects.classlevel.JMLClass;
import main.commentextraction.jml.objects.classlevel.JMLInterface;
import main.commentextraction.jml.objects.classlevel.JMLSuperClass;
import main.commentextraction.jml.objects.classlevel.JMLSuperInterface;
import main.commentextraction.jml.objects.comments.JMLComment;
import main.commentextraction.jml.objects.comments.JMLJavadoc;
import main.commentextraction.jml.objects.comments.JavadocTag;
import main.commentextraction.jml.objects.framework.JMLElement;
import main.commentextraction.jml.objects.scopes.JMLAnnonClass;
import main.commentextraction.jml.objects.scopes.JMLMethod;
import main.commentextraction.jml.objects.scopes.JMLScope;
import main.commentextraction.jml.objects.sourcelevel.JMLImport;
import main.commentextraction.jml.objects.statement.JMLVariable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class XMLFromSource {

	public static JMLElement getXMLFromFile(String path, boolean lineNumbers,boolean commentsInLine) {
		CompilationUnit c = null;
		String sourceCode = null;
		try {
			sourceCode = getFileText(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		c = getCompilationUnit(sourceCode);

		JMLElement root = null;
		root = createXML(c, sourceCode, lineNumbers,commentsInLine);

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
			final boolean lineNumbers,final boolean commentsInLine) {

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

				checkForComments(node);
				if (node.isInterface()) {
					JMLInterface in = new JMLInterface(node.getName()
							.toString());

					for (Object n : node.superInterfaceTypes()) {
						Type t = (Type) n;
						String name = t.toString();
						int index = name.indexOf('<');
						if (index > -1) {
							name = name.substring(0, index);
						}

						JMLSuperInterface si = new JMLSuperInterface(name);
						if (t.isParameterizedType()) {
							ParameterizedType pt = (ParameterizedType) t;
							for (Object o : pt.typeArguments()) {
								Type ta = (Type) o;
								si.addTypeParam(ta.toString());
							}
						}
						in.addContent(si);
					}
					addComments(in);
					b.addBlock(in);

					for (Object o : node.typeParameters()) {
						TypeParameter tp = (TypeParameter) o;
						String name = tp.getName().toString();
						in.addTypeParam(name);

					}

				} else {
					JMLClass jc = new JMLClass(node.getName().toString());
					if (node.getSuperclassType() != null) {
						String name = node.getSuperclassType().toString();
						int index = name.indexOf('<');
						if (index > -1) {
							name = name.substring(0, index);
						}
						JMLSuperClass sc = new JMLSuperClass(name);
						if (node.getSuperclassType().isParameterizedType()) {
							ParameterizedType pt = (ParameterizedType) node
									.getSuperclassType();
							for (Object o : pt.typeArguments()) {
								Type t = (Type) o;
								sc.addTypeParam(t.toString());
							}
						}
						jc.addContent(sc);
					}
					if (lineNumbers) {
						jc.addAttribute("line", getLineNumber(node));
					}
					addComments(jc);
					b.addBlock(jc);

					for (Object o : node.typeParameters()) {
						TypeParameter tp = (TypeParameter) o;
						String name = tp.getName().toString();
						jc.addTypeParam(name);

					}

					for (Object n : node.superInterfaceTypes()) {
						Type t = (Type) n;
						String name = t.toString();
						int index = name.indexOf('<');
						if (index > -1) {
							name = name.substring(0, index);
						}

						JMLSuperInterface si = new JMLSuperInterface(name);
						if (t.isParameterizedType()) {
							ParameterizedType pt = (ParameterizedType) t;
							for (Object o : pt.typeArguments()) {
								Type ta = (Type) o;
								si.addTypeParam(ta.toString());
							}
						}
						jc.addContent(si);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				if (node.getBody() != null) {
					md = node;
					checkForComments(node);
				} else {
					String name = node.getName().toString();
					String type = node.getReturnType2().toString();
					JMLMethod m = new JMLMethod(name, type);
					if (lineNumbers) {
						m.addAttribute("line", getLineNumber(node));
					}

					b.addLine(m);
				}
				return super.visit(node);
			}

			private void checkForComments(ASTNode node) {
				List<Comment> cList = new ArrayList<Comment>();
				int linesDone = 0;
				for (Object com : c.getCommentList()) {
					if (((Comment) com).getParent() == null) {
						cList.add((Comment) com);
					}
				}
				for (Object com : cList) {
					if (linesDone > 0) {
						linesDone--;
						continue;
					}
					ASTNode withoutSignature = NodeFinder.perform(
							((Comment) com).getAlternateRoot(),
							((Comment) com).getStartPosition(), 0);

					if (node instanceof TypeDeclaration) {
						if (withoutSignature.getParent() != null
								&& withoutSignature.getParent().toString()
										.equals(node.getParent().toString())) {

							linesDone = addComment(com);

						}
					} else {

						int mStart = c.getLineNumber(node.getStartPosition());
						int mEnd = c.getLineNumber(node.getStartPosition()
								+ node.getLength());
						int cStart = c.getLineNumber(((Comment) com)
								.getStartPosition());

						if (withoutSignature.getParent() != null
								&& cStart > mStart && cStart < mEnd) {

							linesDone = addComment(com);

						}

					}
				}
			}

			public int addComment(Object com) {
				int linesDone = 0;
				int lineNo = c.getLineNumber(((Comment) com).getStartPosition());
				String nextComments = "";
				if (com instanceof LineComment) {
					linesDone = comentNoOfLines(lineNo);
					nextComments = getNextLines(lineNo, linesDone);

				}
				commentsToAdd.add(new JMLComment(getCommentText((ASTNode) com)
						+ nextComments));
				commentsToAdd.get(commentsToAdd.size() - 1).addAttribute(
						"line", String.valueOf(1 + lineNo));

				return linesDone;

			}
			/*commentsInLine true means line comments must be in line to be seen as block
			 * e.g
			 * int c; //c is an 
		     * \//integer
			 * is not seen as a block if true
			 * 
			 * tab is classed as 4 characters(default) if tab is not 4 characters
			 * will not detect comments as inline
			 */
			public int comentNoOfLines(int start) {

				int line = start;
				if (!commentsInLine) {

					while (source[line].trim().startsWith("//")) {
						line++;
					}

					return line - start;
				} else {
					int startChar = indexOfInclTab(source[line -1]);

					if(startChar == -1){
						return 0;
					}
					if(line<source.length-1){

					while(indexOfInclTab(source[line]) == startChar && source[line].trim().startsWith("//")){
						
						
						line++;
						if(line == source.length-1){
							break;
						}
					}
					}
					return line - start;
				}
			}
			
			public int indexOfInclTab(String line){
				int commentPos = line.indexOf("//");
				if(commentPos == -1){
					return -1;
				}
				line= line.substring(0, commentPos);
				int count = 0;
				for(int i =0; i<line.toCharArray().length;i++){
					if(line.toCharArray()[i] == '\t'){
						count++;
					}
				}
				return line.length()-count+(count*4);
			}

			public String getNextLines(int startPos, int noOfLines) {

				String s = "";

				int line = startPos;
				for (int i = 0; i < noOfLines; i++) {
					s = s + "\n";
					s = s + source[line].trim().replaceAll("//", "");
					line++;
				}

				return StringEscapeUtils.escapeXml(s);
			}

			public String getCommentText(ASTNode comment) {
				int start = c.getLineNumber(comment.getStartPosition());
				int end = c.getLineNumber(comment.getStartPosition()
						+ comment.getLength() - 1);

				StringBuilder sb = new StringBuilder();
				if (comment instanceof BlockComment
						|| comment instanceof Javadoc) {
					for (int i = start - 1; i < end - 1; i++) {
						sb.append(source[i].trim().replaceAll("\\*", "")
								.replaceAll("/", ""));
						if (i != end - 2) {
							sb.append("\n");
						}
					}
				} else {
					sb.append(source[start - 1].trim().substring(
							source[start - 1].trim().indexOf("//") + 2));
				}
				return StringEscapeUtils.escapeXml(sb.toString());
			}

			public void addComments(JMLElement m) {
				for (JMLElement com : commentsToAdd) {
					m.addContent(com);
				}
				commentsToAdd.clear();
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
					addComments(m);

					for (Object o : md.parameters()) {
						SingleVariableDeclaration svd = (SingleVariableDeclaration) o;
						String type = svd.getType().toString();
						String name = svd.getName().toString();
						m.addParam(type, name);
					}

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
					j = new JMLJavadoc(StringEscapeUtils.escapeXml(
							tags.get(0).toString().trim().substring(2))
							.replaceAll("\\*", ""));
				} else {
					j = new JMLJavadoc("");
				}
				for (int i = 1; i < tags.size(); i++) {
					j.addContent(new JavadocTag(tags.get(i).getTagName()
							.substring(1), StringEscapeUtils.escapeXml(tags.get(i).toString().trim()
							.substring(2 + tags.get(i).getTagName().length())
							.replaceAll("\\*", ""))));

				}
				j.addAttribute("line", String.valueOf(c.getLineNumber(node
						.getStartPosition())));
				b.addLine(j);
				return super.visit(node);
			}

			@Override
			public boolean visit(FieldDeclaration node) {
				String type = node.getType().toString();
				String name = ((VariableDeclarationFragment) node.fragments()
						.get(0)).getName().toString();
				name = StringEscapeUtils.escapeXml(name);
				type = StringEscapeUtils.escapeXml(type);
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
				name = StringEscapeUtils.escapeXml(name);
				type = StringEscapeUtils.escapeXml(type);

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
