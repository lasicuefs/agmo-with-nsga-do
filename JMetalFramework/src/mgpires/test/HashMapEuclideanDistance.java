
package mgpires.test;

import mgpires.core.Samples;

public class HashMapEuclideanDistance {
    public static void main(String[] args) {
         Samples samples;        
      
        String datasetName     = "satimage";
        String datasetLocation = "./dataset/" + datasetName + "/";        
                
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, "10", 1); 
        //samples.printTraSamples();
        // There are two options: classification or regression
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training"); 
        
        // criar a matriz de distancia e passar como parametro para o problema SelectInstances
        // na verdade vou criar um HashMap para armazenar as distancias entre as amostras
        //Map<String,Object> dist;        
        //dist = samples.getEuclideanDistance();
        //System.out.println("HashMap size = " + dist.size());
        //samples.printEuclideanDistance(dist);
        
        double[][] distance;// = new double[samples.getNumberOfTraSamples()][samples.getNumberOfTraSamples()];
        distance = samples.getEuclideanDistanceMatrix("train");
        samples.printEuclideanDistanceMatrix(distance, "train");
        
        
    }
    
}
