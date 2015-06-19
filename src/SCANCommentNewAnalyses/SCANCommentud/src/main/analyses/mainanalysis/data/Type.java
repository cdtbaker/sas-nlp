package main.analyses.mainanalysis.data;

import main.analyses.abstraction.AbstractCommentAnalysis;

public class Type {
	
	public Class<? extends AbstractCommentAnalysis<?,?>> typeClass;
	private String typeName;
	
	public Type(Class<? extends AbstractCommentAnalysis<?,?>> typeClass, String typeName) {
		this.typeClass = typeClass;
		this.typeName = typeName;
	}

	public Class<? extends AbstractCommentAnalysis<?,?>> getTypeClass() {
		return typeClass;
	}

	public String getTypeName() {
		return typeName;
	}
	
	

}
