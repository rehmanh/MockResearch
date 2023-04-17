package astsimple.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class MockAPIVisitor extends ASTVisitor {

	private List<String> methods = new ArrayList<String>();
	private List<Annotation> annotations = new ArrayList<Annotation>();
	
	// one class can have several annotations
	private Map<String, List<Annotation>> classLevelAnnotations 
		= new HashMap<String, List<Annotation>>();
	
	@Override
	public boolean visit(MethodDeclaration node) {
		String methodName = node.getName().getFullyQualifiedName().toString(); 
		methods.add(methodName);
		//System.out.println("MethodDeclaration: " + methodName);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		//System.out.println("AnnotationTypeDeclaration: " + node.getName());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("TypeDeclaration: " + node.getName());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		//System.out.println("NormalAnnotation: " + node.toString());
		annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		//System.out.println("MarkerAnnoration: " + node.toString());
		annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		//System.out.println("SingleMemberAnnotation: " + node.toString());
		annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		System.out.println("FieldDeclaration: " + node.toString());
		// TODO if FieldDeclaration contains the mock API reference,
		// we want to begin the recording process
		
		
		// want to check if the FieldDeclaration contains mockito MarkerAnnotation
		for (Object obj : node.modifiers()) {
			if (obj instanceof Annotation) {
				Annotation annotation = (Annotation) obj;
				if (node.getType().resolveBinding() != null
						&& annotation.resolveTypeBinding().getQualifiedName().toLowerCase().contains("mock")) {
					// TODO trigger recording process
					System.out.println("!!!MOCK PRESENT IN: " + node.getType().resolveBinding().getQualifiedName() + " !!!");
				}
			}
		}
		
		// want to check of the VariableDeclarationFragment to see if the RHS contains a mock call
		for (Object obj : node.fragments()) {
			System.out.println(String.format("Fragments present are: %s", obj.toString()));
			if (obj instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment declaration = (VariableDeclarationFragment) obj;
				if (declaration.getInitializer().toString().toLowerCase().contains("mock")) {
					// TODO trigger recording process
					System.out.println("!!!MOCK PRESENT IN: " + declaration.getInitializer().toString() + " !!!");
				}	
			}
		}
		
		return super.visit(node);
	}
	
	public List<String> getMethods() {
		return methods;
	}
	
	
}