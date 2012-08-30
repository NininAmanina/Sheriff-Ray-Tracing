package glaytraser.engine;

import glaytraser.math.AbstractTriple;
import glaytraser.math.Point;
import glaytraser.math.RGBTriple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Parser {
    private static final String EXIT = "exit";
    private static final String COMMENT = "#";
    private static final String SPHERE = "sphere";
    private static final String BOX = "box";
    private static final String NODE = "transform";
    private static final String MATERIAL = "material";
    private static final String POINT_LIGHT = "point_light";
    private static final String AMBIENT_LIGHT = "ambient_light";
    private static final String SURFACE_PROPERTY = "surfaceproperty";
    private static final String TRANSLATE = "translate";
    private static final String ROTATE = "rotate";
    private static final String SCALE = "scale";
    private static final String NONE = "none";
    private static final String LINEAR = "linear";
    private static final String QUADRATIC = "quadratic";

    // Java Regexes
    private static final String SPACE = " +";
    private static final String STRING = "([\\w\\p{Punct}]+)";
    private static final String DOUBLE = "(\\-?\\d+(\\.\\d+)?)";
//    private static final String DOUBLE = "(\\-?\\d+\\.?\\d+)";
    private static final String TRIPLE = "\\{" + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + "\\}";

    public static final Node parseScene(String fileName) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
            try {
                Node root = PrimitiveManager.createRoot();
                String line;
                while((line = buffer.readLine()) != null) {
                    System.out.println(line);
                    if(line.length() == 0 || line.startsWith(COMMENT)) {
                        continue;
                    } else if(line.startsWith(EXIT)) {
                        break;
                    }

                    Scanner s = new Scanner(line);
                    if(line.startsWith(NODE)) {
                        s.findInLine(NODE + SPACE + STRING + SPACE + STRING);
                        MatchResult result = s.match();
                        final String parent = result.group(1);
                        final String STRING = result.group(2);
                        System.out.println("Adding node " + STRING + " to " + parent);
                        PrimitiveManager.addNode(parent, STRING);
                        continue;
                    } else if(line.startsWith(BOX)) {
                        s.findInLine(BOX + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE);
                        MatchResult result = s.match();
                        final String parent = result.group(1);
                        final String STRING = result.group(2);
                        System.out.println("Unimplemented: Adding box " + STRING + " to " + parent);
                        PrimitiveManager.addNode(parent, STRING);
                        continue;
                    } else if(line.startsWith(SPHERE)) {
                        s.findInLine(SPHERE + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE);
                        MatchResult result = s.match();
                        final String parent = result.group(1);
                        final String STRING = result.group(2);
                        final Point point = new Point();
                        int index = getTriple(point, 3, result);
                        double radius = getDouble(index, result);
                        System.out.println("Adding sphere " + STRING + " to " + parent + " of radius " + radius + " at " + point);
                        PrimitiveManager.createSphere(parent, STRING, point, radius);
                        continue;
                    } else if(line.startsWith(MATERIAL)) {
                        s.findInLine(MATERIAL + SPACE + STRING + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + DOUBLE);
                        MatchResult result = s.match();
                        final String STRING = result.group(1);
                        final RGBTriple diffuse = new RGBTriple();
                        final RGBTriple specular = new RGBTriple();
                        int index = getTriple(diffuse, 2, result);
                        index = getTriple(specular, index, result);
                        double phong = getDouble(index, result);
                        System.out.println("Adding material " + STRING + ": diffuse=" + diffuse + "; specular=" + specular +
                                           "; phong=" + phong);
                        Material.addMaterial(STRING, diffuse, specular, phong);
                        continue;
                    } else if(line.startsWith(AMBIENT_LIGHT)) {
                        s.findInLine(AMBIENT_LIGHT + SPACE + TRIPLE);
                        MatchResult result = s.match();
                        final RGBTriple light = new RGBTriple();
                        getTriple(light, 1, result);
                        System.out.println("Adding ambient " + light);
                        LightManager.addAmbientLightSource(light);
                        continue;
                    } else if(line.startsWith(POINT_LIGHT)) {
                        s.findInLine(POINT_LIGHT + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + STRING);
                        MatchResult result = s.match();
                        final Point point = new Point();
                        final RGBTriple light = new RGBTriple();
                        int index = getTriple(point, 1, result);
                        index = getTriple(light, index, result);
                        final String attenuation = result.group(index);
                        int intAtten;
                        if(NONE.equals(attenuation)) {
                            intAtten = 0;
                        } else if(LINEAR.equals(attenuation)) {
                            intAtten = 1;
                        } else if(QUADRATIC.equals(attenuation)) {
                            intAtten = 2;
                        } else {
                            throw new IllegalArgumentException("Invalid attenuation: " + attenuation + " -- must be \"" + NONE +
                                                               "\", \"" + LINEAR + "\", or \"" + QUADRATIC + "\"");
                        }
                        System.out.println("Adding point light " + light + " at " + point + " with attenuation " + intAtten);
                        LightManager.addPointLightSource(point, light, intAtten);
                        continue;
                    } else if(line.startsWith(SURFACE_PROPERTY)) {
                        s.findInLine(SURFACE_PROPERTY + SPACE + STRING + SPACE + STRING);
                        MatchResult result = s.match();
                        final String node = result.group(1);
                        final String material = result.group(2);
                        System.out.println("Adding material " + material + " to node " + node);
                        try {
                          PrimitiveManager.getNode(node).setMaterial(Material.getMaterial(material));
                        } catch(RuntimeException e) {
                            System.out.println("Error processing line:\n" + line);
                            throw e;
                        }
                        continue;
                    } else if(line.startsWith(TRANSLATE) || line.startsWith(ROTATE) || line.startsWith(SCALE)) {
                        System.out.println("Unimplemented command:\n" + line);
                    } else {
                        System.out.println("Unrecognised command:\n" + line);
                    }
                }
                return root;
            } catch(IOException e) {
                throw new RuntimeException(e.getLocalizedMessage());
            } finally {
                try {
                    buffer.close();
                } catch(IOException e) {
                    throw new RuntimeException(e.getLocalizedMessage());
                }
            }
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    private static int getTriple(final AbstractTriple t, final int startIndex, final MatchResult result) {
        int index = startIndex;
        for(int i = 0; i < 3; ++i) {
            t.set(i, getDouble(index, result));
            index += 2; // matching doubles with the expression above requires skipping by 2
        }
        return index;
    }

    private static double getDouble(final int index, final MatchResult result) {
        return Double.parseDouble(result.group(index));
    }
}
