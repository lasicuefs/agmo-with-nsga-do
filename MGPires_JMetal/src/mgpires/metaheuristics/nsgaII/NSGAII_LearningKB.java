package mgpires.metaheuristics.nsgaII;

import static java.lang.Math.round;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.wrapper.XInt;
import jmetal.util.wrapper.XReal;
import mgpires.algorithms.Rules;
import mgpires.core.PartitionFuzzy;
import mgpires.operators.crossover.SinglePointCrossoverToRules;
import mgpires.problems.LearningKB;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII_LearningKB extends Algorithm {

/**
 * Constructor
 * @param problem Problem to solve
 */
public NSGAII_LearningKB(Problem problem) {
    super (problem) ;
} 

/**    
 * Runs the NSGA-II algorithm.
 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
 * as a result of the algorithm execution
 * @throws JMException 
 * @throws ClassNotFoundException 
 */
@Override
public SolutionSet execute() throws JMException, ClassNotFoundException {
  
    int populationSize;
    int maxEvaluations;
    int evaluations;   
    int requiredEvaluations; // Use in the example of use of the indicators object (see below)
    int generations;
    
    double probabilityMutationRules;
    
    QualityIndicator indicators; // QualityIndicator object

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;
    
    Operator mutationAddRules, mutationChangeRules, mutationChangeFunctions;
    Operator crossoverOperatorRules, crossoverOperatorFunctions;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize"));
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations"));
    indicators = (QualityIndicator) getInputParameter("indicators");
    probabilityMutationRules = ((Double) getInputParameter("probabilityMutationRules"));

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;
    generations = 0;
    requiredEvaluations = 0;

    //Read the operators
    mutationAddRules            = operators_.get("mutationAddRules");
    mutationChangeRules         = operators_.get("mutationChangeRules");
    mutationChangeFunctions     = operators_.get("mutationChangeFunctions");
    crossoverOperatorRules      = operators_.get("crossoverRules");
    crossoverOperatorFunctions  = operators_.get("crossoverFunctions");
    selectionOperator           = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    newSolution = new Solution(problem_);
    createUniformSolution(newSolution);
    problem_.evaluate(newSolution);
    evaluations++;
    population.add(newSolution);
    
    //SolutionType aux;    
    for (int i = 1; i < populationSize; i++) {               
        newSolution = new Solution(problem_);                        
        problem_.evaluate(newSolution);              
        //problem_.evaluateConstraints(newSolution);
        evaluations++;
        population.add(newSolution);              
    }
    generations++;
    //System.out.println(evaluations + " " + generations);
    
    /* aqui eu estava tentando adicionar dois novos atributos a ArrayIntAndRealSolutionType,
    que foi o tipo que eu criei. Estes dois atributos serviriam como informacaoes adicionais 
    sobre cada cromossomo. No entanto, nao funcionou, pois a minha populacao eh um objeto
    da classe SolutionSet, que possui uma lista de objetos Solution. Cada ojbeto Solution tem
    um atributo type_ da classe SolutionType, que diz sobre o tipo da solucao. Sendo assim,
    todos os objetos Solution tem o mesmo atributo type_, logo, nao consegui fazer que cada
    objeto tivesse valores diferentes para estes atributos. 
    
    O correto seria inserir estes atributos na classe Solution, ou fazer outra classe que
    herdasse de Solution. Mas achei que ia dar muito trabalho, e acabei colocando estas 
    informacaoes dentro Variables, que eh um atributo herdadado  da classe SolutionType
    */    
    //System.out.println(((ArrayIntAndRealSolutionType)population.get(0).getType()).getNumberOfRules_() + " " +
      //                 ((ArrayIntAndRealSolutionType)population.get(0).getType()).getIntLength_());
    
    //System.out.println(((ArrayIntAndRealSolutionType)population.get(1).getType()).getNumberOfRules_() + " " +
      //                 ((ArrayIntAndRealSolutionType)population.get(1).getType()).getIntLength_());   
    
    
    //printPopulation(population);            
    
    /* testing crossover to rules
    Solution[] parents = new Solution[2];
    parents[0] = new Solution(population.get(0));
    parents[1] = new Solution(population.get(1));   
    
    Solution[] offSpringCrossover;    
    offSpringCrossover = (Solution[]) crossoverOperatorRules.execute(parents); 
    problem_.evaluate(offSpringCrossover[0]);
    problem_.evaluate(offSpringCrossover[1]);
    
    System.out.print("After crossover");
    printOffSpring(offSpringCrossover);       
    
    createDuplicateRules(offSpringCrossover[0]);
    problem_.evaluate(offSpringCrossover[0]);
    System.out.print("After create duplicate rules");
    printOffSpring(offSpringCrossover);   
    
    Rules.deleteDuplicateRules(offSpringCrossover[0]);            
    problem_.evaluate(offSpringCrossover[0]);
    System.out.print("After delete duplicate rules");
    printOffSpring(offSpringCrossover);    
    
    //>>>> end of test crossover to rules
    */
    
    /* testing mutationAddRules
    Solution[] offSpringMutation = new Solution[2];
    offSpringMutation[0] = new Solution(population.get(0));
    offSpringMutation[1] = new Solution(population.get(1));
    
    mutationAddRules.execute(offSpringMutation[0]);
    mutationAddRules.execute(offSpringMutation[1]); 
    problem_.evaluate(offSpringMutation[0]);
    problem_.evaluate(offSpringMutation[1]);

    System.out.println("After mutation add rules");
    printOffSpring(offSpringMutation);
    
    //>>>>> end of test mutationAddRules 
    */
    
    /* testing mutationChangeRules
    Solution[] offSpringMutation = new Solution[2];
    offSpringMutation[0] = new Solution(population.get(0));
    offSpringMutation[1] = new Solution(population.get(1));
    
    mutationChangeRules.execute(offSpringMutation[0]);
    mutationChangeRules.execute(offSpringMutation[1]);    

    printOffSpring(offSpringMutation);
    
    // >>>>> end of test mutationChangeRules */
    
    /* testing mutationChangeFunctions
    Solution[] offSpringMutation = new Solution[2];
    offSpringMutation[0] = new Solution(population.get(0));
    offSpringMutation[1] = new Solution(population.get(1));
    
    mutationChangeFunctions.execute(offSpringMutation[0]);
    mutationChangeFunctions.execute(offSpringMutation[1]);    

    printOffSpring(offSpringMutation);
    
    // >>>>> end of test mutationChangeFunctions */
        
    System.out.println("Running learning KB...");
    // Generations     
    while (evaluations < maxEvaluations) {        
        // Create the offSpring solutionSet      
        offspringPopulation = new SolutionSet(populationSize);
        Solution[] parents = new Solution[2];
        for (int i = 0; i < (populationSize / 2); i++) {
            if (evaluations < maxEvaluations) {
                //obtain parents
                parents[0] = (Solution) selectionOperator.execute(population);
                parents[1] = (Solution) selectionOperator.execute(population);                               
                
                Solution[] offSpring = (Solution[]) crossoverOperatorRules.execute(parents);                
                
                Rules.deleteDuplicateRules(offSpring[0]);
                Rules.deleteDuplicateRules(offSpring[1]);                
                
                offSpring = (Solution[]) crossoverOperatorFunctions.execute(offSpring);
                
                // if the crossover was applied
                if (((SinglePointCrossoverToRules)crossoverOperatorRules).isFlag_() == true) {
                      
                    if (PseudoRandom.randDouble() <= probabilityMutationRules ) {
                        mutationAddRules.execute(offSpring[0]);
                        mutationAddRules.execute(offSpring[1]);
                    
                        mutationChangeRules.execute(offSpring[0]);
                        mutationChangeRules.execute(offSpring[1]);  
                        
                        Rules.deleteDuplicateRules(offSpring[0]);
                        Rules.deleteDuplicateRules(offSpring[1]);
                    }   
                    ((SinglePointCrossoverToRules)crossoverOperatorRules).setFlag_(false);
                }
                else {
                    mutationAddRules.execute(offSpring[0]);
                    mutationAddRules.execute(offSpring[1]);
                    
                    mutationChangeRules.execute(offSpring[0]);
                    mutationChangeRules.execute(offSpring[1]);
                    
                    Rules.deleteDuplicateRules(offSpring[0]);
                    Rules.deleteDuplicateRules(offSpring[1]);                    
                }
                    
                mutationChangeFunctions.execute(offSpring[0]);
                mutationChangeFunctions.execute(offSpring[1]);                
                        
                problem_.evaluate(offSpring[0]);
                problem_.evaluate(offSpring[1]);
                
                offspringPopulation.add(offSpring[0]);
                offspringPopulation.add(offSpring[1]);
                evaluations += 2;
            } // if               
        } // for
        
        generations++;

        // Create the solutionSet union of solutionSet and offSpring
        union = ((SolutionSet) population).union(offspringPopulation);

        // Ranking the union
        Ranking ranking = new Ranking(union);

        int remain = populationSize;
        int index = 0;
        SolutionSet front = null;
        population.clear();

        // Obtain the next front
        front = ranking.getSubfront(index);

        while ((remain > 0) && (remain >= front.size())) {
            //Assign crowding distance to individuals
            distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            //Add the individuals of this front
            for (int k = 0; k < front.size(); k++) {
                population.add(front.get(k));
            } // for

            //Decrement remain
            remain = remain - front.size();

            //Obtain the next front
            index++;
            if (remain > 0) {
                front = ranking.getSubfront(index);
            } // if        
        } // while

        // Remain is less than front(index).size, insert only the best one
        if (remain > 0) {  // front contains individuals to insert                        
            distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
            front.sort(new CrowdingComparator());
            for (int k = 0; k < remain; k++) {
                population.add(front.get(k));
            } // for

            remain = 0;
        } // if                               

        // This piece of code shows how to use the indicator object into the code
        // of NSGA-II. In particular, it finds the number of evaluations required
        // by the algorithm to obtain a Pareto front with a hypervolume higher
        // than the hypervolume of the true Pareto front.
        if ((indicators != null) && (requiredEvaluations == 0)) {
            double HV = indicators.getHypervolume(population);
            if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
                requiredEvaluations = evaluations;
            } // if
        } // if
        
        //System.out.println(evaluations + " " + generations);
    } // while

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);  
    
    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    
    return ranking.getSubfront(0);       
    
} // execute  
    
/**
 * printOffSpring print the offspring
 * @param offSpring is one vector Solution with 2 offspring
 * @throws jmetal.util.JMException 
 * @author Matheus Giovanni Pires              
 * @email  mgpires@ecomp.uefs.br
 * @data   2015/06/30  
*/ 
public void printOffSpring(Solution[] offSpring) throws JMException {   

    if (problem_.getSolutionType().getClass() == ArrayIntAndRealSolutionType.class) {     
        System.out.println("\nPrinting the offspring population...");
        int numberOffspring = offSpring.length;
        
        for (int i = 0; i < numberOffspring; i++) {
            ArrayInt inteiros = (ArrayInt)offSpring[i].getDecisionVariables()[0];
            ArrayReal reais = (ArrayReal)offSpring[i].getDecisionVariables()[1];
            int rules = (int)offSpring[i].getDecisionVariables()[2].getValue();            
            int length = rules * offSpring[i].numberOfVariables();
            
            System.out.println("Chromosome " + i + " Number of variables: " + offSpring[i].numberOfVariables() +
                    " Rules: " + rules + " Int length: " + length + " Accuracy: " + offSpring[i].getObjective(0) +
                " Complexity: " + offSpring[i].getObjective(1)); 
            
            int size = inteiros.getLength();        
            for (int k = 0; k < size; k++)
                System.out.print(inteiros.getValue(k) + " ");

            System.out.print("\n");
            size = reais.getLength();

            for (int k = 0; k < size; k++)
                System.out.println(reais.getValue(k));  
            
            System.out.print("\n");
        }
    }
    else {
        System.out.println("Print population error: solution type " + problem_.getSolutionType().getClass() + " invalid");
        System.exit(-1);
    }    
} // end printOffSpring method
/**
 * printOffSpring print an offspring
 * @param offSpring is an Solution object
 * @throws jmetal.util.JMException 
 * @author Matheus Giovanni Pires              
 * @email  mgpires@ecomp.uefs.br
 * @data   2015/07/29  
*/ 
public void printOffSpring(Solution offSpring) throws JMException {   

    if (problem_.getSolutionType().getClass() == ArrayIntAndRealSolutionType.class) {     
        //System.out.println("\nPrinting the offspring...");
        
        ArrayInt inteiros = (ArrayInt)offSpring.getDecisionVariables()[0];
        ArrayReal reais = (ArrayReal)offSpring.getDecisionVariables()[1];
        int rules = (int)offSpring.getDecisionVariables()[2].getValue();            
        int length = rules * offSpring.numberOfVariables();

        System.out.println("\nNumber of variables: " + offSpring.numberOfVariables() +
            " Rules: " + rules + " Int length: " + length + " Accuracy: " + offSpring.getObjective(0) +
            " Complexity: " + offSpring.getObjective(1)); 

        int size = inteiros.getLength();        
        for (int k = 0; k < size; k++)
            System.out.print(inteiros.getValue(k) + " ");

        System.out.print("\n");
        size = reais.getLength();

        for (int k = 0; k < size; k++)
            System.out.println(reais.getValue(k));  

        System.out.print("\n");
        
    }
    else {
        System.out.println("printOffSpring method error: solution type " + problem_.getSolutionType().getClass() + " invalid");
        System.exit(-1);
    }    
} // end printOffSpring method

/**
* Prints the population, which is one object from SolutionSet
* @param pop
* @throws jmetal.util.JMException 
* @author Matheus Giovanni Pires              
* @email  mgpires@ecomp.uefs.br
* @data   2015/06/30    
*/
public void printPopulation(SolutionSet pop) throws JMException {       

    if (problem_.getSolutionType().getClass() == ArrayIntAndRealSolutionType.class) {        
        System.out.println("\nPrinting the population...");
        int sizeOfPopulation = pop.getMaxSize();             
        int size;
        
        for (int i = 0; i < sizeOfPopulation; i++) {   
            //System.out.println("Chromosome " + i + " Rules: " + ((ArrayIntAndRealSolutionType)pop.get(i).getType()).getNumberOfRules_() +
              //  " Int length: " + ((ArrayIntAndRealSolutionType)pop.get(i).getType()).getIntLength_());            

            ArrayInt inteiros = (ArrayInt)pop.get(i).getDecisionVariables()[0];                
            ArrayReal reais = (ArrayReal)pop.get(i).getDecisionVariables()[1]; 
            int rules = (int)pop.get(i).getDecisionVariables()[2].getValue();            
            int length = rules * pop.get(i).numberOfVariables();  
            
            System.out.println("Chromosome " + i + " Number of variables: " + pop.get(i).numberOfVariables() +
                " Rules: " + rules + " Int length: " + length + " Accuracy: " + pop.get(i).getObjective(0) +
                " Complexity: " + pop.get(i).getObjective(1));                
            
            //System.out.println("Chromosome " + i + " Number of variables: " + pop.get(i).numberOfVariables() +
              //  " Rules: " + rules + " Int length: " + length + " Accuracy: " + pop.get(i).getObjective(0));
            
            //System.out.println("Chromosome " + i + " Number of variables: " + pop.get(i).numberOfVariables() +
                //" Rules: " + rules + " Int length: " + length + " Complexity: " + pop.get(i).getObjective(0));

            size = inteiros.getLength();
            for (int k = 0; k < size; k++)
                System.out.print(inteiros.getValue(k) + " ");            

            System.out.print("\n");
            size = reais.getLength();
            for (int k = 0; k < size; k++)
                System.out.println(reais.getValue(k));
            
            System.out.print("\n");
        }
    }
    else {
        System.out.println("Print population error: solution type " + problem_.getSolutionType().getClass() + " invalid");
        System.exit(-1);
    }
} // end printPopulation method

/**
 * This method duplicates rules in chromosome (solution). It was done only to test
 * the deleteDuplicateRules method.
 * @param offSpring
 * @throws JMException 
 */
private void createDuplicateRules(Solution offSpring) throws JMException {
    
    if (offSpring.getType().getClass() == ArrayIntAndRealSolutionType.class) {        
    
        XInt offs = new XInt(offSpring);
        XInt auxOff = new XInt(offSpring);

        int numberOfRules = (int)offSpring.getDecisionVariables()[2].getValue();
        int numberOfVariables = offSpring.numberOfVariables();

        int idxRule = round(numberOfRules / 2);
        int maxLength = ((ArrayIntAndRealSolutionType)offSpring.getType()).getMaxLengthOfArrayInt_();
        int begin;

        if (idxRule == 0)
            begin = 0;
        else
            begin = idxRule * numberOfVariables;     

        int idx = 0;
        for (int j = begin; j < maxLength; j++) {                    
            offs.setValue(j, auxOff.getValue(idx));                    
            idx++;
        }   
    }
    else {
        System.out.println("NSGAII_LearningKB class > createDuplicateRules method error: solution type " + 
            offSpring.getType().getClass() + " invalid");
        System.exit(-1);        
    }
} // end of createDuplicateRules method

/**
 * This method creates a chromosome (solution) with uniform partition fuzzy
 * @param solution Is the chromosome
 * @throws JMException 
 */
private void createUniformSolution(Solution solution) throws JMException {
    
    if (solution.getType().getClass() == ArrayIntAndRealSolutionType.class) {    
    
        XReal x = new XReal(solution);    
        PartitionFuzzy partition = ((LearningKB)problem_).getPartition_();       
        int[] vetNumberOfSets = partition.getNumberOfFuzzySets_();   

        int idx = 0;
        for (int var = 0; var < vetNumberOfSets.length - 1; var++) {        
            for (int set = 0; set < vetNumberOfSets[var]; set++) {
                x.setValue(idx, partition.getValuePartition(var, set, 1, "input"));                
                idx++;
            }
        }

        if ((partition.getTypeOfOutputVariable_().equalsIgnoreCase("continuous"))) {        
            /* vet[vet.length - 1] gets the number of fuzzy sets of the
             last variable (output)
            */ 
            for (int set = 0; set < vetNumberOfSets[vetNumberOfSets.length - 1]; set++) {            
                x.setValue(idx, partition.getValuePartition(vetNumberOfSets.length - 1, set, 1, "output"));
                idx++;
            }    
        }
    }
    else {
        System.out.println("NSGAII_LearningKB class > createUniformSolution method error: solution type " + 
            solution.getType().getClass() + " invalid");
        System.exit(-1);
    }
} //end createUniformSolution method
    
} // NSGAII_LearningKB
