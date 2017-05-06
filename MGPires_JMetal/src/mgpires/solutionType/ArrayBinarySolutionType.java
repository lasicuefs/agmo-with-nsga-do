package mgpires.solutionType;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.Binary;

 /** 
 * Class representing the solution type of solutions composed of a binary string.
 * Created by Matheus G. Pires - 2015/05/26 - mgpires@ecomp.uefs.br
 * 
 * ASSUMPTIONs:
 * - The binaryStringLength_ field must contain the number of bits of the binary
 * string.
 */
public class ArrayBinarySolutionType extends SolutionType {

    private final int binaryStringLength_ ;

    /**
     * Constructor
     * @param problem Problem being solved	
     * @param binaryStringLength Length of the binary string
     */
    public ArrayBinarySolutionType(Problem problem,int binaryStringLength) {
            super(problem) ;
            binaryStringLength_ = binaryStringLength ;		
    } // Constructor

    /**
     * Creates the variables of the solution
     * @return One binary string
     * @throws ClassNotFoundException
     */
    @Override
    public Variable[] createVariables() throws ClassNotFoundException {
            Variable [] variables = new Variable[1];

        variables[0] = new Binary(binaryStringLength_); 
        return variables ;
    } // createVariables
        
} // ArrayBinarySolutionType