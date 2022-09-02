import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public final class Common {
    static String mRootInputPath = "";
    static String mRootOutputPath = "";

    public CompilationUnit getParseUnit(File javaFile) {
        CompilationUnit root = null;
        try {
            StaticJavaParser.getConfiguration().setAttributeComments(false);
            String txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            if (!txtCode.startsWith("class")) txtCode = "class T { \n" + txtCode + "\n}";
            root = StaticJavaParser.parse(txtCode);
        } catch (Exception ignore) {}
        return root;
    }

    public void applyToPlace(Object obj, String savePath,
                             CompilationUnit com, File javaFile, ArrayList<Node> nodeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            CompilationUnit newCom = applyByObj(obj, com.clone(), javaFile, node.clone());
            if (newCom != null && this.checkTransformation(com, newCom)) {
                this.saveTransformation(savePath, newCom, javaFile, String.valueOf(i + 1));
            }
        }
    }

    private CompilationUnit applyByObj(Object obj, CompilationUnit com, File javaFile, Node node) {
        CompilationUnit newCom = null;
        try {
            if (obj instanceof VariableRenaming) {
                newCom = ((VariableRenaming) obj).applyTransformation(com, node);
            } else if (obj instanceof BooleanExchange) {
                newCom = ((BooleanExchange) obj).applyTransformation(com, node);
            } else if (obj instanceof LoopExchange) {
                newCom = ((LoopExchange) obj).applyTransformation(com, node);
            } else if (obj instanceof SwitchToIf) {
                newCom = ((SwitchToIf) obj).applyTransformation(com, node);
            } else if (obj instanceof ReorderCondition) {
                newCom = ((ReorderCondition) obj).applyTransformation(com, node);
            } else if (obj instanceof PermuteStatement) {
                newCom = ((PermuteStatement) obj).applyTransformation(com);
            } else if (obj instanceof UnusedStatement) {
                newCom = ((UnusedStatement) obj).applyTransformation(com);
            } else if (obj instanceof LogStatement) {
                newCom = ((LogStatement) obj).applyTransformation(com);
            } else if (obj instanceof TryCatch) {
                newCom = ((TryCatch) obj).applyTransformation(com);
            }
        } catch (Exception ex) {
            System.out.println("\n" + "Exception: " + javaFile.getPath());
            ex.printStackTrace();
        }
        return newCom;
    }

    private Boolean checkTransformation(CompilationUnit bRoot, CompilationUnit aRoot) {
        MethodDeclaration mdBefore = (MethodDeclaration) (bRoot.getChildNodes().get(0)).getChildNodes().get(1);
        String mdBeforeStr = mdBefore.toString().replaceAll("\\s+", "");
        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
        String mdAfterStr = mdAfter.toString().replaceAll("\\s+", "");
        return mdBeforeStr.compareTo(mdAfterStr) != 0;
    }

    public void saveTransformation(String savePath, CompilationUnit aRoot, File javaFile, String place) {
        String output_dir = savePath + javaFile.getPath().replaceFirst(Common.mRootInputPath, "");
        output_dir = output_dir.substring(0, output_dir.lastIndexOf(".java")) + "_" + place + ".java";
        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
        this.writeSourceCode(mdAfter, output_dir);
    }

    private void writeSourceCode(MethodDeclaration md, String codePath) {
        File targetFile = new File(codePath).getParentFile();
        if (targetFile.exists() || targetFile.mkdirs()) {
            try (PrintStream ps = new PrintStream(codePath)) {
                String tfSourceCode = md.toString();
                ps.println(tfSourceCode);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isNotPermeableStatement(Node node) {
        return (node instanceof EmptyStmt
                || node instanceof LabeledStmt
                || node instanceof BreakStmt
                || node instanceof ContinueStmt
                || node instanceof ReturnStmt);
    }
}
