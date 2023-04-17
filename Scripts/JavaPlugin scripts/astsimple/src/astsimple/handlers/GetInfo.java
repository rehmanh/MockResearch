package astsimple.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

//import astsimple.handlers.MockitoVisitor;
//import astsimple.handlers.MockitoVisitor.MockitoMockInfo;

public class GetInfo extends AbstractHandler {
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		try {
			//GetMockitoEasyMock_API(projects);
			parseFilesWithMockImport(projects);
		} catch (CoreException e) {
			System.out.println(e.toString());
		} finally {
			System.out.println("\n\n!!!Done!!!\n\n");
		}

		return null;
	}
	
	private boolean isMockitoImportUsed(ICompilationUnit unit) throws CoreException {
		if (unit.getImports().length > 0) {
			for (IImportDeclaration importDeclaration : unit.getImports()) {
				if (importDeclaration.getElementName().contains("mockito")) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;
	}
	
	private void parseFilesWithMockImport(IProject[] projects) throws CoreException {
		// parse through each project in the workspace
		for (IProject project : projects) {
			System.out.println(String.format("!!!Parsing the project %s!!!", project.toString()));
			// check if project is java nature
			if (project.isNatureEnabled(JDT_NATURE)) {
				// get all the packages from the project
				IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
				// parse through each package
				for (IPackageFragment projPackage : packages) {
					if (projPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
						// need to get files in package
						for (ICompilationUnit unit : projPackage.getCompilationUnits()) {
							// create AST for the source file
							CompilationUnit ast = parse(unit);
							if (isMockitoImportUsed(unit)) {
								try {
									visitFileWithMockImport(ast);
								} catch (NullPointerException npe) {
									String error = unit.getPath().toString() + "\n";
									System.err.println(error);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void visitFileWithMockImport(CompilationUnit ast) throws NullPointerException {
		// visit the file that leverages the mockito framework
		MockAPIVisitor mockApiVisitor = new MockAPIVisitor();
		ast.accept(mockApiVisitor);
		
//		mockedObjectVisitorList.add(String.format("%s | %s",
//				mockApiVisitor.getMethods()
//				));
		
//		Map<ITypeBinding, String> objectToTestCaseMap = mockObjectVisitor.getTypeToTestCaseMap();
//		Map<ITypeBinding, Boolean> objectToMockMap = mockObjectVisitor.getTypeToMockMap();
//		
//		for (ITypeBinding mockedObject : objectToTestCaseMap.keySet()) {
//			mockedObjectVisitorList.add(String.format("%s | %s | %s | %s \n", 
//					unit.getPath().toString(), 
//					objectToTestCaseMap.get(mockedObject).toString(),
//					mockedObject.getQualifiedName(),
//					objectToMockMap.get(mockedObject)
//					));
//		}
//		String MockObjectPath= projects[0].getName() + ".csv";
//		print_arr_to_csv(MockedObjectVisitorList,MockObjectPath);
	}


	private void print_arr_to_csv(ArrayList<String> data, String path) {
		if (data.size() > 0) {
			try (FileOutputStream fos = new FileOutputStream(path)) {
				fos.write("file_path,method,api\n".getBytes());
				for (String x : data) {
					fos.write(x.getBytes());
				}

				// Flush the written bytes to the file
				fos.flush();

				System.out.println(
						"Text has  been  written to " + (new File(path)).getAbsolutePath() + '\t' + data.size());

			} catch (Exception e2) {
				System.out.println("\n\n\n!!!!!There was some error in writing to file!!!!!\n\n\n");
				e2.printStackTrace();

			}
		}

	}

	@SuppressWarnings("deprecation")
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
