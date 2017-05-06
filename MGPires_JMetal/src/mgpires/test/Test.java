package mgpires.test;

import java.io.FileNotFoundException;
import java.io.IOException;


public class Test {
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        /* inicio teste - testando visibilidade da variavel i        
        
        for (int i = 0; i < 10; i++)
            System.out.print(i + " ");
         
        // i nao eh visivel, pois foi criado dentro do for 
        //System.out.println("\n" + i);
           
        fim teste */
        
        /* inicio teste - testando funcao de comparacao de vetores inteiros
        int[] vetor1 = new int[3];
        int[] vetor2 = new int[3];
        
        vetor1[0] = 1;
        vetor1[1] = 2;
        vetor1[2] = 3;
        
        vetor2[0] = 4;
        vetor2[1] = 5;
        vetor2[2] = 6;
        
        if (Arrays.equals(vetor1, vetor2)) 
            System.out.println("Os vetores sao iguais...");
        else
            System.out.println("Os vetores NAO sao iguais...");
        
        fim teste */
        
        /* inicio teste - testando gerador de numero aleatorio
        for (int i = 0; i < 100; i++)
            System.out.println(PseudoRandom.randDouble());
        fim teste */
        
        /* inicio teste - verificando a disponibilidade de processadores
        
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors = " + cores);
        
        //fim teste */
        
        /* inicio teste - teste de arredondamento
        int nro1 = (int) Math.ceil(10 / 2.0);
        int nro2 = (int) Math.ceil(5 / 2.0);        
        System.out.println("Nro 1 = " + nro1 + " Nro 2 = " + nro2);
        
        nro1 = (int) Math.round(10 / 2.0);
        nro2 = (int) Math.round(5 / 2.0);
        System.out.println("Nro 1 = " + nro1 + " Nro 2 = " + nro2);      
        
        fim teste */
    
        /* inicio teste - testando leitura de arquivo
        
        ReadResultsFolds.printFinalResult("iris", "./result/iris/");
        
        fim teste */
        
        /* inicio teste - varrendo vetor string
        String[] vector = new String[]{"NSGAII", "SPEA2", "MOCell", "SMPSO", "GDE3"};
        
        for (String vector1 : vector) {
            System.out.println(vector1);
        }
        
        fim teste */
        
        /* inicio teste - calculando tempo gasto
        long initTime = System.currentTimeMillis();
        
        System.out.println(initTime);
        
        Thread.sleep(10002);
        
        double estimatedTime = System.currentTimeMillis() - initTime;         
        
        double timeSeconds = estimatedTime / 1000; // converted in seconds
        
        double timeMinutes = timeSeconds / 60;  // converted in minutes
        
        System.out.println("Milisegundos = " + estimatedTime + "\nSegundos = " + timeSeconds +
           "\nMinutos = " + timeMinutes);
        
        fim teste */
        
        // inicio teste - testando notacao cientifica
        double valor = -1.5894573E-8;
        System.out.println(valor);
        
        double valor2 = -0.000000015894573;
        System.out.println(valor2);
        
        String valor3 = "7.0E-4";
        double valor4 = Double.parseDouble(valor3);
        double valor5 = valor4 / 0.0001;
        System.out.println(valor3 + " " + valor4 + " " + valor5);
        
        
    }     
    
}
