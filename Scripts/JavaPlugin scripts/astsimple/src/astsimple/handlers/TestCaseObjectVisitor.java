package astsimple.handlers;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class TestCaseObjectVisitor extends ASTVisitor {

    private String currentTestCaseName;
    private Map<ITypeBinding, String> typeToTestCaseMap = new HashMap<>();
    private Map<ITypeBinding, Boolean> typeToMockMap = new HashMap<>();

    public Map<ITypeBinding, String> getTypeToTestCaseMap() {
        return typeToTestCaseMap;
    }

    public Map<ITypeBinding, Boolean> getTypeToMockMap() {
        return typeToMockMap;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        // 每当遇到一个新的测试用例时，就更新 currentTestCaseName
        currentTestCaseName = node.getName().getFullyQualifiedName();
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // 每当遇到一个新的测试方法时，就更新 currentTestCaseName
        currentTestCaseName = node.getName().getFullyQualifiedName();
        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        // 如果该方法调用的是构造函数，就记录它创建的对象所属的类以及所属的测试用例名称
        if (node.getName().getIdentifier().equals("<init>")) {
            ITypeBinding createdType = node.resolveMethodBinding().getDeclaringClass();
            typeToTestCaseMap.put(createdType, currentTestCaseName);
            typeToMockMap.put(createdType, isMock(node));
        }
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        // 记录创建的对象所属的类以及所属的测试用例名称
        ITypeBinding createdType = node.resolveTypeBinding();
        typeToTestCaseMap.put(createdType, currentTestCaseName);
        typeToMockMap.put(createdType, isMock(node));
        return true;
    }

    private boolean isMock(MethodInvocation node) {
        // 判断是否是 Mockito 的 mock 方法
        return node.resolveMethodBinding().getDeclaringClass().getQualifiedName().startsWith("org.mockito.Mockito.mock");
    }

    private boolean isMock(ClassInstanceCreation node) {
        // 判断是否是 Mockito 的 mock 方法
        return node.getType().toString().equals("Mockito.mock");
    }

}
