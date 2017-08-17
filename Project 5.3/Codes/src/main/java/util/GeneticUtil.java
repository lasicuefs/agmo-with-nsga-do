package util;

import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by João Paulo on 27/07/2017.
 */
public class GeneticUtil {
    public static Hashtable<Integer, List<Double>> functionFileToObjectivesValue(File function) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(function));
        String line;
        Hashtable<Integer, List<Double>> objectivesPerValue = new Hashtable<>();

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty())
                continue;

            if (line.startsWith("Time")) {
                double time = Double.parseDouble(line.split(" ")[1]);
                List<Double> values = objectivesPerValue.get(-1);

                if (values == null)
                    values = new ArrayList<>();

                values.add(time);
                objectivesPerValue.put(-1, values);
            } else {
                String[] objectives = line.split(" ");
                for (int i = 0; i < objectives.length; i++) {
                    double value = Double.parseDouble(objectives[i]) * -1;
                    List<Double> values = objectivesPerValue.get(i);

                    if (values == null)
                        values = new ArrayList<>();

                    values.add(value);
                    objectivesPerValue.put(i, values);
                }
            }
        }

        return objectivesPerValue;
    }

    public static List<List<Double>> functionFileToSolutionValues(File function) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(function));
        String line;

        List<List<Double>> allValues = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty() || line.startsWith("Time"))
                continue;

            List<Double> values = new ArrayList<>();
            String[] objectives = line.split(" ");

            for (String objective : objectives) {
                double value = Double.parseDouble(objective) * -1;
                values.add(value);
            }

            allValues.add(values);
        }

        return allValues;
    }

    public static List<BitSet> variableFileToChromosomes(File variable) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(variable));
        String line;

        List<BitSet> chromosomes = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty() || line.startsWith("Time"))
                continue;

            String chromosome = line.split(" ")[0];

            BitSet bitSet = new BitSet(chromosome.length());
            for (int i = 0; i < chromosome.length(); i++) {
                char c = chromosome.charAt(i);
                if (c == '0')
                    bitSet.set(i, false);
                else
                    bitSet.set(i, true);
            }

            chromosomes.add(bitSet);
        }

        return chromosomes;
    }

    public static void createFileWithBitSet(BitSet selected, File file, File result) throws IOException {
        if (!result.exists())
            result.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(result));

        String line;
        int bit = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("@") || line.trim().isEmpty()) {
                writer.write(line);
                writer.write("\n");
            }

            else {
                if (selected.get(bit)) {
                    writer.write(line);
                    writer.write("\n");
                }

                bit++;
            }
        }

        reader.close();
        writer.flush();
        writer.close();
    }
}
