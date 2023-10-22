package gr.petros.core;

import com.x5.template.Chunk;
import com.x5.template.Theme;
import gr.petros.model.JavaObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SearchPropertyFactory {

    public static void create(JavaObject javaObject) {
        Theme theme = new Theme("examples");
        Chunk chunk = theme.makeChunk("Properties#1");
        chunk.set("obj", javaObject);

        try (PrintWriter writer = new PrintWriter(new FileWriter(javaObject.name + "SearchPanel.utf8.properties"))) {
            writer.print(chunk);
            System.out.println("Search Property file created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create Search Property file: " + e.getMessage());
        }
    }
}
