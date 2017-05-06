package mgpires.core;

/** This class was implemented only to calculate the membership degree of the
 * samples, in according to partition fuzzy of the variables. Here, only triangular
 * membership are considered.
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2014/10/03
 * @modified 2015/07/17
 */
public class MembershipDegree {
    
    public static double calcMembershipWithLateralDisplacement(String sample, int variable, int set, 
            double[][][] partitionFuzzy, String typeVariable, double displacement) {
        
        double result = 0;  
        double[] points = new double[3];
        
        
        // Currently, I am considering problems with only one output variable!
        if (typeVariable.equals("output")) {
            points[0] = partitionFuzzy[0][set][0];
            points[1] = partitionFuzzy[0][set][1];
            points[2] = partitionFuzzy[0][set][2];
        }
        else {
            points[0] = partitionFuzzy[variable][set][0];
            points[1] = partitionFuzzy[variable][set][1];
            points[2] = partitionFuzzy[variable][set][2];
        }    
        
        /* If displacement is smaller than 0 (negative), the fuzzy set displacement 
        must be on left. If is greater than 0 (positive), must be on right        
        */
        if (displacement < 0) {
                points[0] = points[0] - displacement;
                points[1] = points[1] - displacement;
                points[2] = points[2] - displacement;
        }
        else {
                points[0] = points[0] + displacement;
                points[1] = points[1] + displacement;
                points[2] = points[2] + displacement;
        }
       
        result = calcMembershipTriangularFunction(sample, points);
        return result;        
    }
    
    // Calculate the membership function of the one triangular function
    // caculate the membership degree of the sample in the set of the variable
    public static double calcMembership(String sample, int variable, int set, double[][][] partitionFuzzy, String typeVariable) {
        double result = 0;
        double[] points = new double[3];       
        
        // Currently, I am considering problems with only one output variable!
        if (typeVariable.equals("output")) {
            points[0] = partitionFuzzy[0][set][0];
            points[1] = partitionFuzzy[0][set][1];
            points[2] = partitionFuzzy[0][set][2];           
        }
        else {
            points[0] = partitionFuzzy[variable][set][0];
            points[1] = partitionFuzzy[variable][set][1];
            points[2] = partitionFuzzy[variable][set][2];            
        }
        
        result = calcMembershipTriangularFunction(sample, points);
        return result;              
    }
    
    // Calculate the membership function to discrete output variable
    public static double calcMembership(String sample, String output) {        
        // if the sample is equal the output (class) then membership value is
        // 1.0, else 0.0
        if (sample.equals(output))
            return 1.0;
        else
            return 0.0;      
    } 
    
    public static double calcMembershipTriangularFunction(String sample, double[] points) {
        
        double value, result = 0;        
        value = Double.parseDouble(sample);
        
        /* this code is wrong!
        if (value <= point1)
            return 0;
        else if ((value > point1) && (value <= point2))
            return ((value - point1) / (point2 - point1));
        else if ((value > point2) && (value < point3))
            return ((point3 - value) / (point3 - point2));
        else
            return 0;
        */ 
        
        /* modified by Matheus Giovanni Pires - 2014/10/03
         it was necessary to divide the if's because of:
         if one fuzzy set has 0.1 0.1 1.3 points, the value 0.1 must have 
         membership degree equal 1
         in the prior code, the membership degree was zero!        
        */

        if (value <= points[0])
            result = 0;
        
        if ((value >= points[0]) && (value <= points[1])) {
            // if point2 - point1 == 0, error division by zero
            if ((points[1] - points[0]) == 0)
                result = 1;
            else
                result = ((value - points[0]) / (points[1] - points[0]));
        }
        
        if ((value >= points[1]) && (value <= points[2]))
            // if point3 - point2 == 0, error division by zero
            if ((points[2] - points[1]) == 0)
                result = 1;
            else
                result = ((points[2] - value) / (points[2] - points[1]));
        
        if (value > points[2])
            result = 0;
        
        return result;
    }
    
} // end of MembershipDegree class
