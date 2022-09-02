import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class UnusedStatement extends VoidVisitorAdapter<Object> {
    private final Common mCommon;
    private File mJavaFile = null;
    private String mSavePath = "";
    private final ArrayList<Node> mDummyNodes = new ArrayList<>();

    UnusedStatement() {
        //System.out.println("\n[ UnusedStatement ]\n");
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
        if (com.findFirst(MethodDeclaration.class).isPresent() &&
                com.findFirst(MethodDeclaration.class).flatMap(MethodDeclaration::getBody).isPresent()) {
            BlockStmt blockStmt = new BlockStmt();
            for (Statement statement : com.findFirst(MethodDeclaration.class)
                    .flatMap(MethodDeclaration::getBody).get().getStatements()) {
                blockStmt.addStatement(statement);
            }
            int min = 0, max = blockStmt.getStatements().size() - 1;
            int place = new Random().nextInt(max - min + 1) + min;
            blockStmt.addStatement(place, getUnusedStatement());
            MethodDeclaration md = com.findFirst(MethodDeclaration.class).get();
            md.setBody(blockStmt);
        }
        return com;
    }

    private Statement getUnusedStatement() {
        String unusedStr = "if (false) { temp = 1; }";
        return StaticJavaParser.parseStatement(unusedStr);
    }
}
