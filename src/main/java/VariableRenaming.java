import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;

public class VariableRenaming extends VoidVisitorAdapter<Object> {
    private final Common mCommon;
    private File mJavaFile = null;
    private String mSavePath = "";
    private final ArrayList<Node> mVariableNodes = new ArrayList<>();
    private String mNewVariableName = "";

    VariableRenaming() {
        //System.out.println("\n[ VariableRenaming ]\n");
        mCommon = new Common();
    }

    public void inspectSourceCode(File javaFile) {
        this.mJavaFile = javaFile;
        mSavePath = Common.mRootOutputPath + this.getClass().getSimpleName() + "/";
        CompilationUnit root = mCommon.getParseUnit(mJavaFile);
        if (root != null) {
            this.visit(root.clone(), null);
        }
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateVariableRenaming(com);
        mCommon.applyToPlace(this, mSavePath, com, mJavaFile, mVariableNodes);
        super.visit(com, obj);
    }

    private void locateVariableRenaming(CompilationUnit com) {
        final int[] variableId = {0};
        mNewVariableName = "var";
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (isTargetVariable(node)) {
                    mVariableNodes.add(node);
                    if (node.toString().equals(mNewVariableName)) {
                        variableId[0]++;
                        mNewVariableName = "var" + variableId[0];
                    }
                }
            }
        }.visitPreOrder(com);
        //System.out.println("TargetVariable : " + mVariableList);
    }

    private boolean isTargetVariable(Node node) {
        return (node instanceof SimpleName &&
                (node.getParentNode().orElse(null) instanceof Parameter
                        || node.getParentNode().orElse(null) instanceof VariableDeclarator));
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node varNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                String oldName = varNode.toString();
                if (node.toString().equals(oldName)) {
                    if (node instanceof SimpleName
                            && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                            && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                        ((SimpleName) node).setIdentifier(mNewVariableName);
                    }
                }
            }
        }.visitPreOrder(com);
        return com;
    }
}
