import edu.stanford.nlp.parser.nndep.DependencyParser;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.io.*;

/**
 * Created by abhisheksinha on 3/20/17.
 * Modified by Victoria A. Lestari 3/25/17

 javac -cp stanford-corenlp-full-2016-10-31/stanford-corenlp-3.7.0.jar DependencyParserAPIUsage.java

 */
public class DependencyParserAPIUsage {
    int[] wsjCases = {1000, 2000, 3000, 4000, 5000, 7000, 10000, 12000, 14000};
    int[] brownCases = {21000, 1000, 2000, 3000, 4000, 5000, 7000, 10000, 13000, 17000}; 
    final String DIR = System.getProperty("user.dir");

    public void train(String trainPath, String modelPath, String embeddingPath, 
        String testPath, String testAnnotationsPath, String logFile) throws Exception
    { 
        // create a file
        File fil = new File(logFile);
        fil.createNewFile();
        FileOutputStream f = new FileOutputStream(fil);
        System.setErr(new PrintStream(f));

        // Configuring propreties for the parser. A full list of properties can be found
        // here https://nlp.stanford.edu/software/nndep.shtml
        Properties prop = new Properties();
        prop.setProperty("maxIter", "200");
        DependencyParser p = new DependencyParser(prop);

        // Argument 1 - Training Path
        // Argument 2 - Dev Path (can be null)
        // Argument 3 - Path where model is saved
        // Argument 4 - Path to embedding vectors (can be null)
        p.train(trainPath, null, modelPath, embeddingPath);

        // Load a saved path
        DependencyParser model = DependencyParser.loadFromModelFile(modelPath);

        // Test model on test data, write annotations to testAnnotationsPath
        System.out.println(model.testCoNLL(testPath, testAnnotationsPath));
    }

    public void appendFiles(String fileA, String fileB, String result) throws Exception
    {
        File file1 = new File(fileA);
        File file2 = new File(fileB);
        File fileo = new File(result);

        // long time1 = System.currentTimeMillis();

        FileInputStream is1 = new FileInputStream(file1);
        FileInputStream is2 = new FileInputStream(file2);
        FileOutputStream os = new FileOutputStream(fileo);

        FileChannel fc1 = is1.getChannel();
        FileChannel fc2 = is2.getChannel();
        FileChannel fco = os.getChannel();

        fco.transferFrom(fc1, 0, fc1.size());
        fco.transferFrom(fc2, fc1.size() - 1, fc2.size());        
    }

    public void retrain(String seedPath, String annotatedPath, String embeddingPath, String retrainPath,
        String seedModelPath, String modelPath2, String testPath, String resultAnnotationsPath, String logFile) throws Exception
    {
        // concat seed + annotated --> retrain
        appendFiles(seedPath, annotatedPath, retrainPath);
        
        // create a file
        File fil = new File(logFile);
        fil.createNewFile();
        FileOutputStream f = new FileOutputStream(fil);
        System.setErr(new PrintStream(f));

        // Configuring propreties for the parser. A full list of properties can be found
        // here https://nlp.stanford.edu/software/nndep.shtml
        Properties prop = new Properties();
        prop.setProperty("maxIter", "200");
        DependencyParser p = new DependencyParser(prop);

        // Argument 1 - Training Path
        // Argument 2 - Dev Path (can be null)
        // Argument 3 - Path where model is saved
        // Argument 4 - Path to embedding vectors (can be null)
        p.train(retrainPath, null, modelPath2, embeddingPath, seedModelPath);

        // Load a saved path
        DependencyParser model = DependencyParser.loadFromModelFile(modelPath2);

        // Test model on test data, write annotations to testAnnotationsPath
        System.out.println(model.testCoNLL(testPath, resultAnnotationsPath));
    }

    // ==================================================================================================
    // In-domain Scenario for WSJ/WSJ
    // ==================================================================================================
    public void createScenarioInDomain() throws Exception
    {
        // Test Data Path
        String testPath = DIR + "/wsj_23.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        for (int x : wsjCases){
            System.out.println(x);
            //  Training Data path
            String trainPath = DIR + "/wsj/wsj_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_wsj_wsj_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String testAnnotationsPath = DIR + "/annotations/annotations_wsj_wsj_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_wsj_wsj_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, testPath, testAnnotationsPath, logFile);
            break;
        }
    }
    // ==================================================================================================
    // In-domain Scenario for Brown/Brown
    // ==================================================================================================
    public void createScenarioInDomainBrown() throws Exception
    {
        // Test Data Path
        String testPath = DIR + "/brown-test.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        for (int x : brownCases){
            System.out.println(x);
            //  Training Data path
            String trainPath = DIR + "/brown/brown_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_brown_brown_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String testAnnotationsPath = DIR + "/annotations/annotations_brown_brown_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_brown_brown_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, testPath, testAnnotationsPath, logFile);
        }
    }

    // ==================================================================================================
    // No self-retrain scenario with WSJ/Brown
    // ==================================================================================================
    public void createScenarioWSJBrown() throws Exception
    {
        // Test Data Path
        String testPath = DIR + "/brown-test.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        for (int x : wsjCases){
            System.out.println(x);
            //  Training Data path
            String trainPath = DIR + "/wsj/wsj_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_wsj_brown_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String testAnnotationsPath = DIR + "/annotations/annotations_wsj_brown_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_wsj_brown_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, testPath, testAnnotationsPath, logFile);
        }
    }

    // ==================================================================================================
    // No self-retrain for Brown/WSJ
    // ==================================================================================================
    public void createScenarioBrownWSJ() throws Exception
    {
        int[] brownCases = {21000, 1000}; 
        // Test Data Path
        String testPath = DIR + "/wsj_23.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        for (int x : brownCases){
            System.out.println(x);
            //  Training Data path
            String trainPath = DIR + "/brown/brown_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_brown_wsj_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String testAnnotationsPath = DIR + "/annotations/annotations_brown_wsj_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_brown_wsj_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, testPath, testAnnotationsPath, logFile);
        }
    }

    public void createScenarioBrownWSJRetrain() throws Exception
    {
        int[] brownCases = {21000};
        // Test Data Path
        String testPath = DIR + "/wsj_23.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";
        String selfRetrainPath = DIR + "/wsj_02_10.conllx";

        for (int x : brownCases){
            System.out.println(x);
            //  Training Data path
            String trainPath = DIR + "/brown/brown_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_brown_wsj_init_retrain_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String annotatedPath = DIR + "/annotations/annotations_brown_wsj_retrain_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_brown_wsj_retrain_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, selfRetrainPath, annotatedPath, logFile);

            // String seedPath, String annotatedPath, String embeddingPath, String retrainPath,
            // String modelPath, String testPath, String resultAnnotationsPath, String logFile
            String retrainPath = DIR + "/retrain/retrain_brown_wsj_" + x + ".conllx";
            String modelPath2 = DIR + "/models/model_brown_wsj_final_retrain_" + x + ".conllx";
            String annotatedPath2 = DIR + "/annotations/annotations_brown_wsj_final_retrain_" + x + ".conllx";

            retrain(trainPath, annotatedPath, embeddingPath, retrainPath, modelPath, modelPath2, testPath, annotatedPath2, logFile);
        }
    }

    // ==================================================================================================
    // Retrain Scenario with WSJ/Brown
    // ==================================================================================================
    public void createScenarioWSJBrownRetrain() throws Exception
    {   
        String selfRetrainPath = DIR + "/brown-train.conllx";
        // Test Data Path
        String testPath = DIR + "/brown-test.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        for (int x : wsjCases){
            System.out.println(x);
            // //  Training Data path
            String trainPath = DIR + "/wsj/wsj_" + x + ".conllx";
            
            // Path where model is to be saved
            String modelPath = DIR + "/models/model_wsj_brown_init_retrain_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String annotatedPath = DIR + "/annotations/annotations_wsj_brown_init_retrain_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_wsj_brown_retrain_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, selfRetrainPath, annotatedPath, logFile);
            
            // String seedPath, String annotatedPath, String embeddingPath, String retrainPath,
            // String modelPath, String testPath, String resultAnnotationsPath, String logFile
            String retrainPath = DIR + "/retrain/retrain_wsj_brown_" + x + ".conllx";
            String modelPath2 = DIR + "/models/model_wsj_brown_final_retrain_" + x + ".conllx";
            String annotatedPath2 = DIR + "/annotations/annotations_wsj_brown_final_retrain_" + x + ".conllx";

            retrain(trainPath, annotatedPath, embeddingPath, retrainPath, modelPath, modelPath2, testPath, annotatedPath2, logFile);
        }
    }
    

    // ==================================================================================================
    // Retrain Scenario with WSJ/Brown
    // ==================================================================================================
    public void createScenarioWSJBrownRetrainFixedSeed() throws Exception
    {   
        int[] brownCases = {21000}; 
        
        // Test Data Path
        String testPath = DIR + "/brown-test.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        String trainPath = DIR + "/wsj/wsj_10000.conllx";

        for (int x : brownCases){
            System.out.println(x);
            String selfRetrainPath = DIR + "/brown/brown_" + x +".conllx";

            // Path where model is to be saved
            String modelPath = DIR + "/models/model_wsj_brown_init_retrain_fs_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String annotatedPath = DIR + "/annotations/annotations_wsj_brown_init_retrain_fs_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_wsj_brown_retrain_fs_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, selfRetrainPath, annotatedPath, logFile);
            
            // String seedPath, String annotatedPath, String embeddingPath, String retrainPath,
            // String modelPath, String testPath, String resultAnnotationsPath, String logFile
            String retrainPath = DIR + "/retrain/retrain_fs_wsj_brown_" + x + ".conllx";
            String modelPath2 = DIR + "/models/model_wsj_brown_final_retrain_fs_" + x + ".conllx";
            String annotatedPath2 = DIR + "/annotations/annotations_wsj_brown_final_retrain_fs_" + x + ".conllx";

            retrain(trainPath, annotatedPath, embeddingPath, retrainPath, modelPath, modelPath2, testPath, annotatedPath2, logFile);
        }
    }

    // ==================================================================================================
    // Retrain Scenario with Brown/WSJ
    // ==================================================================================================
    public void createScenarioBrownWSJRetrainFixedSeed() throws Exception
    {          
        // Test Data Path
        String testPath = DIR + "/wsj_23.conllx";
        // Path to embedding vectors file
        String embeddingPath = DIR + "/en-cw.txt";

        String trainPath = DIR + "/brown-train.conllx";

        for (int x : wsjCases){
            System.out.println(x);
            String selfRetrainPath = DIR + "/wsj/wsj_" + x +".conllx";

            // Path where model is to be saved
            String modelPath = DIR + "/models/model_brown_wsj_init_retrain_fs_" + x + ".conllx";
            
            // Path where test data annotations are stored
            String annotatedPath = DIR + "/annotations/annotations_brown_wsj_init_retrain_fs_" + x + ".conllx";
            
            String logFile = DIR + "/logs/log_brown_wsj_retrain_fs_" + x + ".txt";

            train(trainPath, modelPath, embeddingPath, selfRetrainPath, annotatedPath, logFile);
            
            // String seedPath, String annotatedPath, String embeddingPath, String retrainPath,
            // String modelPath, String testPath, String resultAnnotationsPath, String logFile
            String retrainPath = DIR + "/retrain/retrain_fs_brown_wsj_" + x + ".conllx";
            String modelPath2 = DIR + "/models/model_brown_wsj_final_retrain_fs_" + x + ".conllx";
            String annotatedPath2 = DIR + "/annotations/annotations_brown_wsj_final_retrain_fs_" + x + ".conllx";

            retrain(trainPath, annotatedPath, embeddingPath, retrainPath, modelPath, modelPath2, testPath, annotatedPath2, logFile);
        }
    }


    public static void main(String[] args) throws Exception{
        DependencyParserAPIUsage d = new DependencyParserAPIUsage();
        // creating directories
        try{
            String DIR = d.DIR;
            File f1 = new File(DIR + "/models");
            f1.mkdir();

            f1 = new File(DIR + "/logs");
            f1.mkdir();

            f1 = new File(DIR + "/annotations");
            f1.mkdir();

            f1 = new File(DIR + "/retrain");
            f1.mkdir();

        }catch(Exception e){
            System.err.println(e);
        }

        if (args.length != 1){
            System.out.println("Supply one command line argument.");
            System.out.println("1 - source: wsj, target: wsj, mode: in-domain.");
            System.out.println("2 - source: wsj, target: brown, mode: no self-training.");
            System.out.println("3 - source: wsj, target: brown, mode: with self-training.");
            System.out.println("4 - source: wsj, target: brown, mode: increasing self-training set.");
            System.out.println("5 - source: brown, target: brown, mode: in-domain.");
            System.out.println("6 - source: brown, target: wsj, mode: no self-training.");
            System.out.println("7 - source: brown, target: wsj, mode: with self-training.");
            System.out.println("8 - source: brown, target: wsj, mode: increasing self-training set.");
            System.out.println();
            System.out.println("Example: java DependencyParserAPIUsage 1");
        }
        else{
            if (args[0].equals("1")){
                d.createScenarioInDomain();
            }
            else if (args[0].equals("2")){
                d.createScenarioWSJBrown();
            }
            else if (args[0].equals("3")){
                d.createScenarioWSJBrownRetrain();
            }
            else if (args[0].equals("4")){
                d.createScenarioWSJBrownRetrainFixedSeed();
            }
            else if (args[0].equals("5")){
                d.createScenarioInDomainBrown();
            }
            else if (args[0].equals("6")){
                d.createScenarioBrownWSJ();
            }
            else if (args[0].equals("7")){
                d.createScenarioBrownWSJRetrain();
            }
            else if (args[0].equals("8")){
                d.createScenarioBrownWSJRetrainFixedSeed();
            }
            else{
                System.out.println("Supply the argument with only number 1-8.");
            }
        }
    }
}
