import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class TryCatch extends VoidVisitorAdapter<Object> {
    private final Common mCommon;
    private File mJavaFile = null;
    private String mSavePath = "";
    private final ArrayList<Node> mDummyNodes = new ArrayList<>();

    TryCatch() {
        //System.out.println("\n[ TryCatch ]\n");
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
        mDummyNodes.add(new EmptyStmt());
        mCommon.applyToPlace(this, mSavePath, com, mJavaFile, mDummyNodes);
        super.visit(com, obj);
    }

    public CompilationUnit applyTransformation(CompilationUnit com) {
        if (com.findAll(TryStmt.class).size() > 0
                || com.findAll(MethodCallExpr.class).size() == 0) {
            return com;
        }

        if (com.findFirst(MethodDeclaration.class).isPresent() &&
                com.findFirst(MethodDeclaration.class).flatMap(MethodDeclaration::getBody).isPresent()) {
            BlockStmt blockStmt = new BlockStmt();
            BlockStmt tcNodes = new BlockStmt();
            for (Statement statement : com.findFirst(MethodDeclaration.class)
                    .flatMap(MethodDeclaration::getBody).get().getStatements()) {
                boolean flag = true;
                if (mCommon.isNotPermeableStatement(statement)
                        || statement.findAll(MethodCallExpr.class).size() == 0) {
                    flag = false;
                } else if (statement instanceof ExpressionStmt) {
                    for (Node node : statement.getChildNodes()) {
                        if (node.findFirst(VariableDeclarator.class).isPresent()) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    tcNodes.addStatement(statement);
                }
                blockStmt.addStatement(statement);
            }

            if (tcNodes.getStatements().size() > 0) {
                int min = 0, max = tcNodes.getStatements().size() - 1;
                int place = new Random().nextInt(max - min + 1) + min;
                Statement tcStmt = tcNodes.getStatements().get(place);
                blockStmt.replace(tcStmt, getTryCatchStatement(tcStmt));
            }

            MethodDeclaration md = com.findFirst(MethodDeclaration.class).get();
            md.setBody(blockStmt);
        }
        return com;
    }

    private Statement getTryCatchStatement(Statement stmt) {
        String tryStr = "try {\n" +
                stmt + "\n" +
                "} catch (Exception ex) {\n" +
                "ex.printStackTrace();\n" +
                "}";
        return StaticJavaParser.parseStatement(tryStr);
    }
}
