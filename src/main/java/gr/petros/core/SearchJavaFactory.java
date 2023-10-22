package gr.petros.core;

import com.x5.template.Chunk;
import com.x5.template.Theme;
import gr.petros.model.JavaObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SearchJavaFactory {

    public static void create(JavaObject javaObject) {
        Theme theme = new Theme("examples");
        Chunk chunk = theme.makeChunk("SearchJava#1");
        chunk.set("obj", javaObject);

        try (PrintWriter writer = new PrintWriter(new FileWriter(javaObject.getName() + "SearchPanel.java"))) {
            writer.print(chunk);
            System.out.println("Search Java file created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create Java Search file: " + e.getMessage());
        }
    }

}
