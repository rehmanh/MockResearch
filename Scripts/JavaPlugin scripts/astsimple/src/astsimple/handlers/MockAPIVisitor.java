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

	private String className = null;
	private List<Annotation> annotations = new ArrayList<Annotation>();
	private List<String> mockedClassVariables = new ArrayList<String>();
	
	
	@Override
	public boolean visit(MethodDeclaration node) {
		return super.visit(node);
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		// will give us the referenced Java class for recording
		this.className = node.getName().toString();
		return super.visit(node);
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		this.annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		this.annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		this.annotations.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		/**
		 * Check if the FieldDeclaration contains a mockito annotation
		 * for example `@Mock`
		 * or `@Captor`
		 * or `@Spy`
		 */
		for (Object obj : node.modifiers()) {
			if (obj instanceof Annotation) {
				Annotation annotation = (Annotation) obj;
				if (node.getType().resolveBinding() != null
						&& (annotation.resolveTypeBinding().getQualifiedName().toLowerCase().contains("mock") 
								|| annotation.resolveTypeBinding().getQualifiedName().toLowerCase().contains("spy") 
								|| annotation.resolveTypeBinding().getQualifiedName().toLowerCase().contains("captor"))) {
					// TODO trigger recording process
					
					this.mockedClassVariables.add(node.getType().resolveBinding().getQualifiedName());
				}
			}
		}
		
		/**
		 * Check if the FieldDeclaration contains a mockito API call in the variable assignment
		 * for example `private Car mockCar = mock(Car.class);`
		 */
		for (Object obj : node.fragments()) {
			if (obj instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment declaration = (VariableDeclarationFragment) obj;
				if (declaration.getInitializer() != null 
						&& declaration.getInitializer().toString().toLowerCase().contains("mock")) {
					// TODO trigger recording process
					
					this.mockedClassVariables.add(declaration.getInitializer().toString());
				}	
			}
		}
		
		return super.visit(node);
	}
	
	public String getClassName() {
		return this.className;
	}
	
	
	public List<Annotation> getAnnotations() {
		return this.annotations;
	}
	
	public List<String> getMockedClassVariables() {
		return this.mockedClassVariables;
	}
	
	
}