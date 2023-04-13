package astsimple.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MockAPIVisitor extends ASTVisitor {

	private List<String> methods = new ArrayList<String>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		String methodName = node.getName().getFullyQualifiedName().toString(); 
		methods.add(methodName);
		System.out.println(methodName);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		System.out.println("AnnotationTypeDeclaration: " + node.getName());
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("TypeDeclaration: " + node.getName());
		return true;
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		System.out.println("NormalAnnotation: " + node.toString());
		return true;
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		System.out.println("MarkerAnnoration: " + node.toString());
		return true;
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		System.out.println("SingleMemberAnnotation: " + node.toString());
		return true;
	}
	
	public List<String> getMethods() {
		return methods;
	}
	
	
}
