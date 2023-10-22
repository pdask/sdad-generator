package gr.petros.ui;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.layout.ContainerBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.fileUpload.DropZoneConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.fileUpload.DropZoneFileUpload;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInput;
import gr.petros.model.JavaObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPanel extends GenericPanel<JavaObject> {

    private enum Step {
        Upload,
        //        ShowFields,
        ShowSql,
        GenerateForm,
        GenerateSearch,
        ;

        public Step next() {
            switch (this) {

                case Upload:
                    return ShowSql;
                case ShowSql:
                    return GenerateForm;
                case GenerateForm:
                    return GenerateSearch;
                case GenerateSearch:
                    return null;
            }

            return null;
        }

        public Step previous() {
            switch (this) {

                case Upload:
                    return null;
                case ShowSql:
                    return Upload;
                case GenerateForm:
                    return ShowSql;
                case GenerateSearch:
                    return GenerateForm;
            }

            return null;
        }
    }

    private Step step = Step.Upload;


    public MainPanel(String id) {
        super(id, new Model<>());

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.add(new ContainerBehavior());
        add(container);

        // UPLOAD
        DropZoneFileUpload fileUpload = new DropZoneFileUpload("fileUpload", new Model<>(), new DropZoneConfig().withMaxFileSize(1000).withParallelUploads(1)) {
            @Override
            protected void onUpload(AjaxRequestTarget target, Map<String, List<FileItem>> fileMap) {
                try {
                    CompilationUnit unit = StaticJavaParser.parse(fileMap.get("file").get(0).getInputStream());
                    MainPanel.this.getModel().setObject(new JavaObject(unit));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                step = Step.ShowSql;
                target.add(container);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(step == Step.Upload);
            }
        };
        fileUpload.setOutputMarkupPlaceholderTag(true);
//        container.add(fileUpload);


        container.add(new BootstrapFileInput("fileUpload", new LoadableDetachableModel<>() {

            @Override
            protected List<FileUpload> load() {
                return new ArrayList<>();
            }

            @Override
            public void setObject(List<FileUpload> object) {
                super.setObject(object);
            }
        }) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                try {
                    CompilationUnit unit = StaticJavaParser.parse(getModelObject().get(0).getInputStream());
                    MainPanel.this.getModel().setObject(new JavaObject(unit));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                step = Step.ShowSql;
                target.add(container);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(step == Step.Upload);
            }
        }.setOutputMarkupPlaceholderTag(true));

        // SQL
        container.add(new SqlPanel("sql", getModel()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(step == Step.ShowSql);
            }
        });

        // SHOW FIELDS

        container.add(new CreateFormPanel("form", getModel()) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(step == MainPanel.Step.GenerateForm);
            }

        });

        container.add(new CreateSearchPanel("search", getModel()) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(step == Step.GenerateSearch);
            }

        });

        //
        container.add(new BootstrapAjaxLink<Void>("nextStep", Buttons.Type.Primary) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        step = step.next();
                        target.add(container);
                    }

                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(step != Step.Upload && null != step.next());
                    }
                }.setLabel(Model.of("Next")).setOutputMarkupPlaceholderTag(true),
                new BootstrapAjaxLink<Void>("previousStep", Buttons.Type.Primary) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        step = step.previous();
                        target.add(container);
                    }

                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(step != Step.Upload && null != step.previous());
                    }
                }.setLabel(Model.of("Previous")).setOutputMarkupPlaceholderTag(true));
    }
}
