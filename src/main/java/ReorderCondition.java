import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;

public class ReorderCondition extends VoidVisitorAdapter<Object> {
    private final Common mCommon;
    private File mJavaFile = null;
    private String mSavePath = "";
    private final ArrayList<Node> mOperatorNodes = new ArrayList<>();

    ReorderCondition() {
        //System.out.println("\n[ ReorderCondition ]\n");
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
        locateOperators(com);
        mCommon.applyToPlace(this, mSavePath, com, mJavaFile, mOperatorNodes);
        super.visit(com, obj);
    }

    private void locateOperators(CompilationUnit com) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof BinaryExpr && isAugmentationApplicable(((BinaryExpr) node).getOperator())) {
                    mOperatorNodes.add(node);
                }
            }
        }.visitPreOrder(com);
        //System.out.println("OperatorNodes : " + mOperatorNodes.size());
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node opNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.equals(opNode)) {
                    BinaryExpr replNode = (BinaryExpr) opNode.clone();
                    switch (((BinaryExpr) node).getOperator()) {
                        case LESS:
                            replNode.setOperator(BinaryExpr.Operator.GREATER);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case LESS_EQUALS:
                            replNode.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case GREATER:
                            replNode.setOperator(BinaryExpr.Operator.LESS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case GREATER_EQUALS:
                            replNode.setOperator(BinaryExpr.Operator.LESS_EQUALS);
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                        case EQUALS:
                        case NOT_EQUALS:
                        case OR:
                        case AND:
                        case PLUS:
                        case MULTIPLY:
                            replNode.setLeft(((BinaryExpr) node).getRight());
                            replNode.setRight(((BinaryExpr) node).getLeft());
                            break;
                    }
                    node.replace(replNode);
                }
            }
        }.visitPreOrder(com);
        return com;
    }

    private boolean isAugmentationApplicable(BinaryExpr.Operator op) {
        switch (op) {
            case LESS:
            case LESS_EQUALS:
            case GREATER:
            case GREATER_EQUALS:
            case EQUALS:
            case NOT_EQUALS:
            case OR:
            case AND:
            case PLUS:
            case MULTIPLY:
                return true;
        }
        return false;
    }

}
