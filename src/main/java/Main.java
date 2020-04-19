public class Main {
    public static void main(String[] args) {
        /* root folder for input  -> '~/methods'
         * root folder for output -> '~/transforms'
         *
         * extracted single method of project should be in 'methods' folder
         * separate folder for each refactoring will be created in 'transforms' folder
         */

        String inpPath = "/Users/rabin/Desktop/RA/DummyData/java/methods/";
        String outPath = "/Users/rabin/Desktop/RA/DummyData/java/transforms/";

        if (args.length == 2) {
            inpPath = args[0];
            outPath = args[1];
        }

        new ASTExplorer(inpPath,outPath).call();
    }
}
