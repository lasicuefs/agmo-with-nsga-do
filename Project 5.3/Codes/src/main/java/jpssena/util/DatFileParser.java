package jpssena.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by João Paulo on 04/11/2017.
 */
@SuppressWarnings("Duplicates")
public class DatFileParser {
    private enum ReadState {
        START, RELATION, DATA
    }

    private List<String> attributes = new ArrayList<>();
    private Hashtable<String, Hashtable<Integer, String>> labelToNominal;
    private ReadState state = ReadState.START;

    private File original;
    private File fixed;

    private boolean isRNG = false;

    private BufferedWriter bw;
    private BufferedReader br;

    public DatFileParser(File original) {
        this.original = original;
        this.fixed = new File(original.getParentFile() + "\\" + original.getName().substring(0, original.getName().length() - 4) + ".arff");

        try {
            bw = new BufferedWriter(new FileWriter(fixed));
            br = new BufferedReader(new FileReader(original));
        } catch (IOException e1) {
            System.err.println("Unable to [read file or create new file] for: " + original.getName());
        }

        labelToNominal = new Hashtable<>();
    }

    public DatFileParser(File original, boolean isRNG) throws IOException {
        this(original);
        this.isRNG = isRNG;
    }

    public File fixDatFormat() {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@inputs") || line.startsWith("@output") || line.startsWith("@input") || line.startsWith("@outputs")) {
                    continue;
                }

                if (line.startsWith("@relation")) {
                    state = ReadState.RELATION;
                    writeToFile(line);
                    continue;
                } else if (line.startsWith("@data")) {
                    state = ReadState.DATA;
                    writeToFile(line);
                    continue;
                }

                if (state == ReadState.RELATION) {
                    line = processRelationLine(line);
                    writeToFile(line);
                } else if (state == ReadState.DATA) {
                    line = processDataLine(line);
                    writeToFile(line);
                }
            }

            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fixed;
    }


    private String processRelationLine(String line) {
        if (line.contains("real") || line.contains("integer")) {
            //Finds the start position and end position of the brackets
            int start = line.indexOf("[");
            int end = line.indexOf("]");

            //Splits the string extracting the value inside the brackets.
            String split = line.substring(start + 1, end);
            String remain = line.substring(0, start);

            //Places a space after the real or integer name
            if (!remain.endsWith(" "))
                remain = remain + " ";

            //Splits the interval into the 2 numbers
            String[] values = split.split(",");
            if (values.length != 2) {
                System.err.println("It's not a distribution of 2 values!");
                System.exit(-1);
            }

            //Appends to the String the values using the correct pattern
            remain = remain + "[" + values[0] + ", " + values[1] + "]";

            line = remain;
        }

        if (line.startsWith("@attribute")) {
            String[] values = line.split(" ");
            String attributeName = values[1];
            attributes.add(attributeName);

            if (line.contains("{")) {
                Hashtable<Integer, String> translationTable = new Hashtable<>();

                int startPosition = line.indexOf("{") + 1;
                int endPosition = line.indexOf("}");

                String classes = line.substring(startPosition, endPosition);
                String[] parts = classes.split(",");

                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    part = part.trim();

                    translationTable.put(i, part);
                }

                labelToNominal.put(attributeName, translationTable);
            }
        }

        return line;
    }

    private String processDataLine(String line) {
        StringBuilder builder = new StringBuilder();
        try {


            String[] values = line.split(" ");
            for (int i = 0; i < values.length; i++) {
                String attribute = attributes.get(i);
                String value = values[i];

                Hashtable<Integer, String> translationTable = labelToNominal.get(attribute);
                if (translationTable == null) {
                    if (i != 0) builder.append(", ");
                    builder.append(value);
                } else {
                    value = value.trim();
                    int decimal = Integer.parseInt(value);

                    if (i == (values.length - 1))
                        decimal = Integer.parseInt(value) - 1;

                    String nominal = translationTable.get(decimal);

                    if (i != 0) builder.append(", ");
                    builder.append(nominal);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Original NFM: " + original.getName());
        }
        return builder.toString();
    }

    private void writeToFile(String line) throws IOException {
        bw.write(line);
        bw.write("\n");
    }
}
