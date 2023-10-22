package gr.petros.ui;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import de.agilecoders.wicket.core.markup.html.bootstrap.panel.BootstrapGenericPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.fileUpload.DropZoneConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.fileUpload.DropZoneFileUpload;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInput;
import gr.petros.model.JavaObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileUploadPanel extends BootstrapGenericPanel<JavaObject> {
    public FileUploadPanel(String id, IModel<JavaObject> model) {
        super(id, model, Model.of("Επιλέξτε αρχείο"));

        add(new BootstrapFileInput("file") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);

                getModelObject().get(0);
            }
        });


    }

    void oldImpl() {
        DropZoneFileUpload fileUpload = new DropZoneFileUpload("file", new Model<>(), new DropZoneConfig().withMaxFileSize(1000).withParallelUploads(1)) {
            @Override
            protected void onUpload(AjaxRequestTarget target, Map<String, List<FileItem>> fileMap) {
                try {
                    CompilationUnit unit = StaticJavaParser.parse(fileMap.get("file").get(0).getInputStream());
                    FileUploadPanel.this.getModel().setObject(new JavaObject(unit));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                step = MainPanel.Step.ShowSql;
//                target.add(container);
            }
        };
        add(fileUpload);
    }
}
