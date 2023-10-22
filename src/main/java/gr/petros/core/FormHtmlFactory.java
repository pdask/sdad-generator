package gr.petros.core;

import com.x5.template.Chunk;
import com.x5.template.Theme;
import gr.petros.model.JavaObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FormHtmlFactory {

    public static void create(JavaObject javaObject) {
        Theme theme = new Theme("examples");
        Chunk chunk = theme.makeChunk("FormHtml#1");
        chunk.set("obj", javaObject);

        try (PrintWriter writer = new PrintWriter(new FileWriter(javaObject.name + "Panel.html"))) {
            writer.print(chunk);
            System.out.println("Form Html file created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create Form Html file: " + e.getMessage());
        }
    }

}
