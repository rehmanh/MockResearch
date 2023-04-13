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
			getMockitoFunctionsFromProject(projects);
		} catch (CoreException e) {
			System.out.println(e.toString());
		}

		return null;
	}

	private boolean Import_mock(ICompilationUnit unit) throws CoreException {
		if (unit.getImports().length <= 0) {
			return false;
		}
		for (IImportDeclaration import_mock : unit.getImports()) {
			if (import_mock.getElementName().contains("mockito")) {
				return true;
			}
		}
		return false;
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
	
	private void getMockitoFunctionsFromProject(IProject[] projects) throws CoreException {
		// declare array for mocked object visitors and errors
		ArrayList<String> mockedObjectVisitorList = new ArrayList<>();
		ArrayList<String> errorList = new ArrayList<>();
		
		Map<String, String> nodeValues = new HashMap<String, String>();
		
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
									MockAPIVisitor mockApiVisitor = new MockAPIVisitor();
									ast.accept(mockApiVisitor);
									
//									mockedObjectVisitorList.add(String.format("%s | %s",
//											mockApiVisitor.getMethods()
//											));
									
//									Map<ITypeBinding, String> objectToTestCaseMap = mockObjectVisitor.getTypeToTestCaseMap();
//									Map<ITypeBinding, Boolean> objectToMockMap = mockObjectVisitor.getTypeToMockMap();
//									
//									for (ITypeBinding mockedObject : objectToTestCaseMap.keySet()) {
//										mockedObjectVisitorList.add(String.format("%s | %s | %s | %s \n", 
//												unit.getPath().toString(), 
//												objectToTestCaseMap.get(mockedObject).toString(),
//												mockedObject.getQualifiedName(),
//												objectToMockMap.get(mockedObject)
//												));
//									}
								} catch (NullPointerException npe) {
									String error = unit.getPath().toString() + "\n";
									System.err.println(error);
									errorList.add(error);
								}
							}
						}
					}
				}
			}
		}
		System.out.println(mockedObjectVisitorList.toString());
	}

	private void GetMockitoEasyMock_API(IProject[] projects) throws CoreException {
		ArrayList<String> MockedObjectVisitorList = new ArrayList<>();
		ArrayList<String> err_arr = new ArrayList<>();

//		go throw all the project
		for (IProject project : projects) {

			if (project.isNatureEnabled(JDT_NATURE)) {

				IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
				for (IPackageFragment mypackage : packages) {
					if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
						System.out.println("\n\n\n!!!!!Getting compilation Units now!!!!!\n\n\n");
						for (ICompilationUnit unit : mypackage.getCompilationUnits()) {// this is file level

							// now create the AST for the ICompilationUnits
							CompilationUnit parse = parse(unit);
//							if (Import_mock(unit)) {

							try {
								TestCaseObjectVisitor mockobjectvisitor = new TestCaseObjectVisitor();

								parse.accept(mockobjectvisitor);


							    Map<ITypeBinding, String> objectToTestCaseMap = mockobjectvisitor.getTypeToTestCaseMap();
							    Map<ITypeBinding, Boolean> objectToMockMap = mockobjectvisitor.getTypeToMockMap();
								for (ITypeBinding mockedObject :objectToTestCaseMap.keySet() ) {
									MockedObjectVisitorList.add(unit.getPath().toString() + "|"
											+ objectToTestCaseMap.get(mockedObject) + "|" +  mockedObject.getQualifiedName() + "|"
											+ objectToMockMap.get(mockedObject)+"\n");
								}
//									

							} catch (NullPointerException e) {
								System.err.println(unit.getPath().toString());
								err_arr.add(unit.getPath().toString() + '\n');
							}

//							}

						}
						
					}
					
				}
				break;
			}
		}


		//String MockObjectPath="C:\\Users\\10590\\OneDrive - stevens.edu\\PHD\\2023 aSpring\\task\\mock-test case analysis\\"+projects[0].getName() + ".csv";
		//String MockObjectPath= "/Users/rehmanh/Desktop/" + projects[0].getName() + ".csv";
		String MockObjectPath= projects[0].getName() + ".csv";
		print_arr_to_csv(MockedObjectVisitorList,MockObjectPath);
		
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
//		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
