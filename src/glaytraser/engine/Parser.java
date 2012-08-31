package glaytraser.engine;

import glaytraser.math.AbstractTriple;
import glaytraser.math.Pair;
import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

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
    private static final String RENDER = "render";

    // Java Regexes
    private static final String SPACE = " +";
    private static final String STRING = "([\\w\\p{Punct}]+)";
    private static final String DOUBLE = "(\\-?\\d+(\\.\\d+)?)";
    private static final String PAIR = "\\{" + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + "\\}";
    private static final String TRIPLE = "\\{" + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + "\\}";
    private static final String REGEX_NODE = NODE + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_BOX = BOX + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_SPHERE = SPHERE + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_MATERIAL = MATERIAL + SPACE + STRING + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_AMBIENT_LIGHT = AMBIENT_LIGHT + SPACE + TRIPLE;
    private static final String REGEX_POINT_LIGHT = POINT_LIGHT + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + STRING;
    private static final String REGEX_SURFACE_PROPERTY = SURFACE_PROPERTY + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_RENDER = RENDER + SPACE + STRING + SPACE +
                                               "size" + SPACE + PAIR + SPACE +
                                               "eyepoint" + SPACE + TRIPLE + SPACE +
                                               "viewdirection" + SPACE + TRIPLE + SPACE +
                                               "updirection" + SPACE + TRIPLE + SPACE +
                                               "file" + SPACE + STRING + SPACE +
                                               "fov" + SPACE + DOUBLE;

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
                        addNode(s);
                        continue;
                    } else if(line.startsWith(BOX)) {
                        addBox(s);
                        continue;
                    } else if(line.startsWith(SPHERE)) {
                        addSphere(s);
                        continue;
                    } else if(line.startsWith(MATERIAL)) {
                        addMaterial(s);
                        continue;
                    } else if(line.startsWith(AMBIENT_LIGHT)) {
                        addAmbientLight(s);
                        continue;
                    } else if(line.startsWith(POINT_LIGHT)) {
                        addPointLight(s);
                        continue;
                    } else if(line.startsWith(SURFACE_PROPERTY)) {
                        addSurfaceProperty(line, s);
                        continue;
                    } else if(line.startsWith(TRANSLATE) || line.startsWith(ROTATE) || line.startsWith(SCALE)) {
                        System.out.println("Unimplemented command:\n" + line);
                    } else if(line.startsWith(RENDER)) {
                        render(s);
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

    private static void render(Scanner s) {
        s.findInLine(REGEX_RENDER);
        MatchResult result = s.match();
        int index = 1;
        final String node = result.group(index);
        Pair size = new Pair();
        index = getPair(size, ++index, result);
        Point cameraPoint = new Point();
        index = getTriple(cameraPoint, index, result);
        Vector cameraDirection = new Vector();
        index = getTriple(cameraDirection, index, result);
        Vector cameraUp = new Vector();
        index = getTriple(cameraUp, index, result);
        final String file = result.group(index);
        double fov = getDouble(++index, result);
        Camera camera = Camera.init(size, cameraPoint, cameraDirection, cameraUp, file, fov);
        camera.setPixel(Renderer.renderScene(PrimitiveManager.getNode(node)));
    }

    private static void addSurfaceProperty(String line, Scanner s) {
        s.findInLine(REGEX_SURFACE_PROPERTY);
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
    }

    private static void addPointLight(Scanner s) {
        s.findInLine(REGEX_POINT_LIGHT);
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
    }

    private static void addAmbientLight(Scanner s) {
        s.findInLine(REGEX_AMBIENT_LIGHT);
        MatchResult result = s.match();
        final RGBTriple light = new RGBTriple();
        getTriple(light, 1, result);
        System.out.println("Adding ambient " + light);
        LightManager.addAmbientLightSource(light);
    }

    private static void addMaterial(Scanner s) {
        s.findInLine(REGEX_MATERIAL);
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
    }

    private static void addSphere(Scanner s) {
        s.findInLine(REGEX_SPHERE);
        MatchResult result = s.match();
        final String parent = result.group(1);
        final String STRING = result.group(2);
        final Point point = new Point();
        int index = getTriple(point, 3, result);
        double radius = getDouble(index, result);
        System.out.println("Adding sphere " + STRING + " to " + parent + " of radius " + radius + " at " + point);
        PrimitiveManager.createSphere(parent, STRING, point, radius);
    }

    private static void addBox(Scanner s) {
        s.findInLine(REGEX_BOX);
        MatchResult result = s.match();
        final String parent = result.group(1);
        final String STRING = result.group(2);
        System.out.println("Unimplemented: Adding box " + STRING + " to " + parent);
        PrimitiveManager.addNode(parent, STRING);
    }

    private static void addNode(Scanner s) {
        s.findInLine(REGEX_NODE);
        MatchResult result = s.match();
        final String parent = result.group(1);
        final String STRING = result.group(2);
        System.out.println("Adding node " + STRING + " to " + parent);
        PrimitiveManager.addNode(parent, STRING);
    }

    private static int getTriple(final AbstractTriple t, final int startIndex, final MatchResult result) {
        int index = startIndex;
        for(int i = 0; i < 3; ++i) {
            t.set(i, getDouble(index, result));
            index += 2; // matching doubles with the expression above requires skipping by 2
        }
        return index;
    }

    private static int getPair(final Pair p, final int startIndex, final MatchResult result) {
        int index = startIndex;
        for(int i = 0; i < 2; ++i) {
            p.set(i, getDouble(index, result));
            index += 2; // matching doubles with the expression above requires skipping by 2
        }
        return index;
    }

    private static double getDouble(final int index, final MatchResult result) {
        return Double.parseDouble(result.group(index));
    }
}
