package glaytraser.engine;

import glaytraser.math.AbstractTriple;
import glaytraser.math.Pair;
import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Parser {
    private static final String EXIT = "exit";
    private static final String COMMENT = "#";

    // Nodes
    private static final String SPHERE = "sphere";
    private static final String TORUS = "torus";
    private static final String BOX = "box";
    private static final String POLYHEDRON = "polyhedron";
    private static final String NODE = "transform";
    private static final String INSTANCE = "instance";

    // CSG Nodes
    private static final String UNION = "union";
    private static final String INTERSECTION = "intersection";
    private static final String DIFFERENCE = "difference";

    // Support types
    private static final String MATERIAL = "material";
    private static final String SURFACE_PROPERTY = "surfaceproperty";

    // Light types
    private static final String POINT_LIGHT = "point_light";
    private static final String DIRECTIONAL_LIGHT = "directional_light";
    private static final String AMBIENT_LIGHT = "ambient_light";

    // Transformations
    private static final String TRANSLATE = "translate";
    private static final String ROTATE = "rotate";
    private static final String SCALE = "scale";

    // Attenuations
    private static final String NONE = "none";
    private static final String LINEAR = "linear";
    private static final String QUADRATIC = "quadratic";

    // Misc
    private static final String RENDER = "render";
    private static final String TRUE = "true";
    private static final String SUPER_SAMPLE = "super_sample";

    // Java Regexes
    private static final String SPACE = " +";
    private static final String STRING = "([\\w\\p{Punct}]+)";
    private static final String DOUBLE = "(\\-?\\d+(\\.\\d+)?)";
    private static final String PAIR = "\\{" + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + "\\}";
    private static final String TRIPLE = "\\{" + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + DOUBLE + SPACE + "\\}";
    private static final String REGEX_NODE = NODE + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_INSTANCE = INSTANCE + SPACE + STRING + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_UNION = UNION + SPACE + STRING + SPACE + STRING + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_INTERSECTION = INTERSECTION + SPACE + STRING + SPACE + STRING + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_DIFFERENCE = DIFFERENCE + SPACE + STRING + SPACE + STRING + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_BOX = BOX + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_POLYHEDRON1 = POLYHEDRON + SPACE + STRING + SPACE + STRING + SPACE + "\\{";
    private static final String REGEX_POLYHEDRON2 = "} {";
    private static final String REGEX_POLYHEDRON3 = "}";
    private static final String REGEX_SPHERE = SPHERE + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_TORUS = TORUS + SPACE + STRING + SPACE + STRING + SPACE + TRIPLE + SPACE + DOUBLE + SPACE +
                                              DOUBLE;
    private static final String REGEX_MATERIAL = MATERIAL + SPACE + STRING + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + DOUBLE;
    private static final String REGEX_AMBIENT_LIGHT = AMBIENT_LIGHT + SPACE + TRIPLE;
    private static final String REGEX_POINT_LIGHT = POINT_LIGHT + SPACE + TRIPLE + SPACE + TRIPLE + SPACE + STRING;
    private static final String REGEX_DIRECTIONAL_LIGHT = DIRECTIONAL_LIGHT + SPACE + TRIPLE + SPACE + TRIPLE;
    private static final String REGEX_SURFACE_PROPERTY = SURFACE_PROPERTY + SPACE + STRING + SPACE + STRING;
    private static final String REGEX_TRANSLATE = TRANSLATE + SPACE + STRING + SPACE + TRIPLE;
    private static final String REGEX_ROTATE = ROTATE + SPACE + STRING + SPACE + STRING + SPACE + DOUBLE;
    private static final String REGEX_SCALE = SCALE + SPACE + STRING + SPACE + TRIPLE + SPACE + TRIPLE;
    private static final String REGEX_SUPER_SAMPLE = SUPER_SAMPLE + SPACE + DOUBLE;
    private static final String REGEX_RENDER = RENDER + SPACE + STRING + SPACE +
                                               "size" + SPACE + PAIR + SPACE +
                                               "eyepoint" + SPACE + TRIPLE + SPACE +
                                               "fov" + SPACE + DOUBLE + SPACE + 
                                               "viewdirection" + SPACE + TRIPLE + SPACE +
                                               "updirection" + SPACE + TRIPLE + SPACE +
                                               "file" + SPACE + STRING;

    public static final Node parseScene(final String fileName) {
        try {
            final BufferedReader buffer = new BufferedReader(new FileReader(fileName));
            try {
                final Node root = PrimitiveManager.createRoot();
                String line;
                while((line = buffer.readLine()) != null) {
                    if(line.length() == 0 || line.startsWith(COMMENT)) {
                        System.out.println(line);
                        continue;
                    } else if(line.startsWith(EXIT)) {
                        System.out.println(line);
                        break;
                    }

                    Scanner s = new Scanner(line);
                    if(line.startsWith(NODE)) {
                        addNode(s);
                        continue;
                    } else if(line.startsWith(BOX)) {
                        addBox(s);
                        continue;
                    } else if(line.startsWith(POLYHEDRON)) {
                        addPolyhedron(buffer, s);
                        continue;
                    } else if(line.startsWith(SPHERE)) {
                        addSphere(s);
                        continue;
                    } else if(line.startsWith(INSTANCE)) {
                        addInstance(s);
                        continue;
                    } else if(line.startsWith(UNION)) {
                        addUnion(s);
                        continue;
                    } else if(line.startsWith(INTERSECTION)) {
                        addIntersection(s);
                        continue;
                    } else if(line.startsWith(DIFFERENCE)) {
                        addDifference(s);
                        continue;
                    } else if(line.startsWith(TORUS)) {
                        addTorus(s);
                        continue;
                    } else if(line.startsWith(MATERIAL)) {
                        addMaterial(line, s);
                        continue;
                    } else if(line.startsWith(AMBIENT_LIGHT)) {
                        addAmbientLight(s);
                        continue;
                    } else if(line.startsWith(POINT_LIGHT)) {
                        addPointLight(s);
                        continue;
                    } else if(line.startsWith(DIRECTIONAL_LIGHT)) {
                        addDirectionalLight(s);
                        continue;
                    } else if(line.startsWith(SURFACE_PROPERTY)) {
                        addSurfaceProperty(line, s);
                        continue;
                    } else if(line.startsWith(TRANSLATE)) {
                        translate(s);
                        continue;
                    } else if(line.startsWith(ROTATE)) {
                        rotate(s);
                        continue;
                    } else if(line.startsWith(SCALE)) {
                        scale(s);
                        continue;
                    } else if(line.startsWith(SUPER_SAMPLE)) {
                        superSample(s);
                        continue;
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

    private static void translate(final Scanner s) {
        s.findInLine(REGEX_TRANSLATE);
        final MatchResult result = s.match();
        final String name = result.group(1);
        final Vector vector = new Vector();
        getTriple(vector, 2, result);
        System.out.println("Translate " + name + " by "+ vector);
        PrimitiveManager.getNode(name).translate(vector);
    }

    private static void rotate(final Scanner s) {
        s.findInLine(REGEX_ROTATE);
        final MatchResult result = s.match();
        final String name = result.group(1);
        final String axis = result.group(2);
        if(axis.length() != 1) {
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
        final int axisIndex = axis.charAt(0) - 'x';
        if(axisIndex < 0 || axisIndex > 2) {
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
        final double angle = Math.toRadians(getDouble(3, result));
        System.out.println("Rotate " + name + " around axis " + axis + " by "+ angle);
        PrimitiveManager.getNode(name).rotate(axisIndex, angle);
    }

    private static void scale(final Scanner s) {
        s.findInLine(REGEX_SCALE);
        final MatchResult result = s.match();
        final String name = result.group(1);
        final Point point = new Point();
        int index = getTriple(point, 2, result);
        final Vector vector = new Vector();
        getTriple(vector, index, result);
        System.out.println("Scale " + name + " at point " + point + " by "+ vector);
        PrimitiveManager.getNode(name).scale(point, vector);
    }

    private static void superSample(final Scanner s) {
        s.findInLine(REGEX_SUPER_SAMPLE);
        final MatchResult result = s.match();
        final int sampleCount = (int) getDouble(1, result);
        System.out.println("Super-sampling at " + sampleCount + " per axis per pixel");
        Renderer.setSampleCount(sampleCount);
    }

    private static void render(final Scanner s) {
        s.findInLine(REGEX_RENDER);
        final MatchResult result = s.match();
        int index = 1;
        final String node = result.group(index);
        final Pair size = new Pair();
        index = getPair(size, ++index, result);
        final Point cameraPoint = new Point();
        index = getTriple(cameraPoint, index, result);
        double fov = getDouble(index, result);
        ++index;
        ++index;
        final Vector cameraDirection = new Vector();
        index = getTriple(cameraDirection, index, result);
        final Vector cameraUp = new Vector();
        index = getTriple(cameraUp, index, result);
        final String file = result.group(index);
        final Camera camera = Camera.init(size, cameraPoint, cameraDirection, cameraUp, file, fov);
        final BufferedImage image = new BufferedImage(camera.getWidth(), camera.getHeight(), BufferedImage.TYPE_INT_ARGB);
        camera.setPixel(Renderer.renderScene(PrimitiveManager.getNode(node), image));
    }

    private static void addSurfaceProperty(final String line, final Scanner s) {
        s.findInLine(REGEX_SURFACE_PROPERTY);
        final MatchResult result = s.match();
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

    private static void addPointLight(final Scanner s) {
        s.findInLine(REGEX_POINT_LIGHT);
        final MatchResult result = s.match();
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

    private static void addDirectionalLight(final Scanner s) {
        s.findInLine(REGEX_DIRECTIONAL_LIGHT);
        MatchResult result = s.match();
        final Vector direction = new Vector();
        int index = getTriple(direction, 1, result);
        final RGBTriple light = new RGBTriple();
        index = getTriple(light, index, result);
        System.out.println("Adding directional light " + light + " in direction " + direction);
        LightManager.addDirectionalLightSource(direction, light);
    }

    private static void addAmbientLight(final Scanner s) {
        s.findInLine(REGEX_AMBIENT_LIGHT);
        MatchResult result = s.match();
        final RGBTriple light = new RGBTriple();
        getTriple(light, 1, result);
        System.out.println("Adding ambient " + light);
        LightManager.addAmbientLightSource(light);
    }

    private static void addMaterial(final String line, final Scanner s) {
        final boolean isReflective = line.endsWith(TRUE);
        s.findInLine(REGEX_MATERIAL);
        final MatchResult result = s.match();
        final String name = result.group(1);
        final RGBTriple diffuse = new RGBTriple();
        final RGBTriple specular = new RGBTriple();
        int index = getTriple(diffuse, 2, result);
        index = getTriple(specular, index, result);
        final double phong = getDouble(index, result);
        System.out.println("Adding material " + name + ": diffuse=" + diffuse + "; specular=" + specular +
                           "; phong=" + phong + "; isReflective=" + isReflective);
        Material.addMaterial(name, diffuse, specular, phong, isReflective);
    }

    private static void addSphere(final Scanner s) {
        s.findInLine(REGEX_SPHERE);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final Point point = new Point();
        final int index = getTriple(point, 3, result);
        final double radius = getDouble(index, result);
        System.out.println("Adding sphere " + name + " to " + parent + " of radius " + radius + " at " + point);
        PrimitiveManager.addSphere(parent, name, point, radius);
    }

    private static void addTorus(final Scanner s) {
        s.findInLine(REGEX_TORUS);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final Point point = new Point();
        int index = getTriple(point, 3, result);
        final double toroidal = getDouble(index, result);
        ++index;
        ++index;
        final double polaroidal = getDouble(index, result);
        System.out.println("Adding torus " + name + " to " + parent + " of radii (" + toroidal + ", " + polaroidal + ") at "
                           + point);
        PrimitiveManager.createTorus(parent, name, point, toroidal, polaroidal);
    }

    private static void addBox(final Scanner s) {
        s.findInLine(REGEX_BOX);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final Point point = new Point();
        final int index = getTriple(point, 3, result);
        final double length = getDouble(index, result);
        System.out.println("Adding box " + name + " of length " + length + " to " + parent + " at " + point);
        PrimitiveManager.addBox(parent, name, point, length);
    }

    private static void addPolyhedron(final BufferedReader buffer, final Scanner s) {
        s.findInLine(REGEX_POLYHEDRON1);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        ArrayList<Point> point = new ArrayList<Point>();
        String line;
        try {
            // Extract the Points
            while((line = buffer.readLine()) != null && !line.equals(REGEX_POLYHEDRON2)) {
                final Scanner pointScanner = new Scanner(line);
                pointScanner.findInLine(TRIPLE);
                final MatchResult pointResult = pointScanner.match();
                final Point p = new Point();
                getTriple(p, 1, pointResult);
                point.add(p);
            }
            // Extract the polygons, as indices into the list of Point objects
            ArrayList<Integer []> polygon = new ArrayList<Integer []>();
            java.util.Vector<Integer> vertices = new java.util.Vector<Integer>();
            while((line = buffer.readLine()) != null && !line.equals(REGEX_POLYHEDRON3)) {
                vertices.clear();
                final Scanner pointScanner = new Scanner(line);
                while(pointScanner.hasNext()) {
                    pointScanner.next();
                    while(pointScanner.hasNextInt()) {
                        vertices.add(pointScanner.nextInt());
                    }
                }
                polygon.add((Integer []) vertices.toArray(new Integer[0]));
            }
            System.out.println("Adding polyhedron " + name + " with points " + point + " and polygons " + polygon + " to " +
                               parent);
            PrimitiveManager.addPolyhedron(parent, name, point, polygon);
        } catch(IOException e) {
            throw new RuntimeException("Error parsing polyhedron " + name);
        }
    }

    private static void addNode(final Scanner s) {
        s.findInLine(REGEX_NODE);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        System.out.println("Adding node " + name + " to " + parent);
        PrimitiveManager.addNode(parent, name);
    }

    private static void addInstance(final Scanner s) {
        s.findInLine(REGEX_INSTANCE);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final String A = result.group(3);
        System.out.println("Adding Instance(" + A + ")" + name + " to " + parent);
        PrimitiveManager.addInstance(parent, name, A);
    }

    private static void addUnion(final Scanner s) {
        s.findInLine(REGEX_UNION);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final String A = result.group(3);
        final String B = result.group(4);
        System.out.println("Adding U(" + A + ", " + B + ")" + name + " to " + parent);
        PrimitiveManager.addUnion(parent, name, A, B);
    }

    private static void addIntersection(final Scanner s) {
        s.findInLine(REGEX_INTERSECTION);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final String A = result.group(3);
        final String B = result.group(4);
        System.out.println("Adding I(" + A + ", " + B + ")" + name + " to " + parent);
        PrimitiveManager.addIntersection(parent, name, A, B);
    }

    private static void addDifference(final Scanner s) {
        s.findInLine(REGEX_DIFFERENCE);
        final MatchResult result = s.match();
        final String parent = result.group(1);
        final String name = result.group(2);
        final String A = result.group(3);
        final String B = result.group(4);
        System.out.println("Adding D(" + A + ", " + B + ")" + name + " to " + parent);
        PrimitiveManager.addDifference(parent, name, A, B);
    }

    private static int getTriple(final AbstractTriple<?> t, final int startIndex, final MatchResult result) {
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
