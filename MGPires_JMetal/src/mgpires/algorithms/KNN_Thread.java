package mgpires.algorithms;

import java.io.IOException;
import java.util.concurrent.Callable;
import jmetal.util.JMException;
import mgpires.core.Samples;

public class KNN_Thread implements Callable {
    
    private final String datasetLocation, datasetName, pathResult;        
    private Samples samples;
    private final String stratificationDataset;
    
    int indexFold, indexThread;
    
    public KNN_Thread(String datasetLocation, String datasetName, String pathResult, 
       String stratificationDataset, int indexFold, int indexThread) {
        this.datasetLocation       = datasetLocation;
        this.datasetName           = datasetName;
        this.pathResult            = pathResult;        
        this.stratificationDataset = stratificationDataset;
        this.indexFold             = indexFold;
        this.indexThread           = indexThread;               
    }    

    @Override
    public Object call() throws Exception {
        try {
            execute();
        } catch (JMException | ClassNotFoundException | IOException ex) {
            System.err.println("KNN_Thread class > run method error: " + ex);
            System.exit(-1);
        }
        return 1;
    }
    
    public void execute() throws JMException, ClassNotFoundException, IOException {
        
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, stratificationDataset, indexFold);
        samples.setTypeDataSet("classification");        
        // I don't remember where this parameter is used, so, I created "none" for this case
        samples.setTypeProcedure("none");        

        double[][] distance = samples.getEuclideanDistanceMatrix("knn");   
        
        long initTime = System.currentTimeMillis();
        
        double accuracy = KNN.calcAccuracy(distance, samples);
        
        double estimatedTime = System.currentTimeMillis() - initTime;
        double aux = estimatedTime * 0.001; // converted in seconds        
        double timeKNN = aux / 60.0;  // converted in minutes
                
        System.out.println("\nFold = " + indexFold + " Accuracy = " + accuracy + " Time = " + timeKNN);
        
        Printer.printResultToFile(datasetName, pathResult, indexFold, accuracy, timeKNN);
        
      
    } // end of main method

} // end KNN_Thread class