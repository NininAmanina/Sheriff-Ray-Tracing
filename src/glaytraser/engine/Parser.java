package glaytraser.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Parser {
    public static final Node parseScene(String fileName) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        Node root = PrimitiveManager.createRoot();
        
        return root;
    }
}
