package gr.petros.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonGroup;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.panel.BootstrapGenericPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.panel.PanelType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.BootstrapDefaultDataTable;
import gr.petros.core.ChunkFactory;
import gr.petros.model.Component;
import gr.petros.model.JavaObject;
import gr.petros.model.JavaObjectField;
import gr.petros.ui.table.CheckBoxColumn;
import gr.petros.ui.table.DropDownColumn;
import gr.petros.ui.table.LinkColumn;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CreateSearchPanel extends BootstrapGenericPanel<JavaObject> {
    BootstrapDefaultDataTable<JavaObjectField, String> search_fields;

    public CreateSearchPanel(String id, IModel<JavaObject> model) {
        super(id, model, Model.of("Επιλέξτε τα πεδία της φόρμας"), PanelType.Default);

        List<IColumn<JavaObjectField, String>> columns = List.of(new PropertyColumn<>(Model.of("Όνομα"), "name"),
                new PropertyColumn<>(Model.of("Μετάφραση"), "name_gr"),
                new PropertyColumn<>(Model.of("Τύπος"), "java_type"),

                new DropDownColumn<>(Model.of("Component"), "component", (IChoiceRenderer<Component>) Enum::name) {

                    @Override
                    protected List<Component> choices(IModel<JavaObjectField> rowModel) {
                        return rowModel.getObject().componentChoices;
                    }
                },
//                new PropertyColumn<>(Model.of("Μέγιστος Αριθμός Χαρακτήρων"), "size"),
                new DropDownColumn<>(Model.of("Κριτήριο"), "criterion", (IChoiceRenderer<String>) s -> s) {
                    @Override
                    protected List<String> choices(IModel<JavaObjectField> rowModel) {
                        return rowModel.getObject().criterionChoices;
                    }
                },
                new CheckBoxColumn<>(Model.of("Στήλη"), "column"),
                new LinkColumn<>(Model.of("Αφαίρεση"), Buttons.Type.Danger, Buttons.Size.Mini, GlyphIconType.minus) {
                    @Override
                    protected void onClick(AjaxRequestTarget target, IModel<JavaObjectField> rowModel) {
                        getModel().getObject().form_fields.remove(rowModel.getObject());
                        target.add(search_fields);
                    }
                });

        ISortableDataProvider<JavaObjectField, String> dataProvider = new SortableDataProvider<>() {
            @Override
            public Iterator<? extends JavaObjectField> iterator(long first, long count) {
                JavaObject obj = model.getObject();
                return obj == null ? Collections.emptyIterator() : obj.search_fields.iterator();
            }

            @Override
            public long size() {
                JavaObject obj = model.getObject();
                return obj == null ? 0 : obj.search_fields.size();
            }

            @Override
            public IModel<JavaObjectField> model(JavaObjectField object) {
                return Model.of(object);
            }
        };
        search_fields = new BootstrapDefaultDataTable<>("search_fields", columns, dataProvider, 200);
        search_fields.bordered().striped();
        search_fields.setOutputMarkupPlaceholderTag(true);
        add(search_fields);

    }

    @Override
    protected Panel newFooterPanel(String id, IModel<JavaObject> model) {
        ButtonGroup buttonGroup = new ButtonGroup(id) {
            @Override
            protected List<AbstractLink> newButtons(String buttonMarkupId) {

                IModel<File> fileModel = new LoadableDetachableModel<File>() {

//                    @Override
//                    public File getObject() {
//                        return ChunkFactory.createFile(model.getObject(), "FormJava#1");
//                    }

                    @Override
                    protected File load() {
                        return ChunkFactory.createFile(model.getObject(), "SearchJava#1");
                    }
                };
                return List.of(new BootstrapAjaxLink<Void>(buttonMarkupId, Buttons.Type.Primary) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                CreateSearchPanel.this.getModel().getObject().reloadSearchFields();
                                target.add(search_fields);
                            }
                        }.setLabel(Model.of("Reload")),

                        new BootstrapDownloadLink(buttonMarkupId, fileModel,//() -> ChunkFactory.createFile(model.getObject(), "FormJava#1"),
                                () -> String.format("%sSearchPanel.java", CreateSearchPanel.this.getModelObject().name), Buttons.Type.Primary) {
                            @Override
                            public void onClick() {
                                model.getObject().updateSearchFields();
                                super.onClick();
                            }
                        }.setLabel(Model.of("Download Java")),

                        new BootstrapDownloadLink(buttonMarkupId, () -> ChunkFactory.createFile(model.getObject(), "SearchHtml#1"),
                                () -> String.format("%sSearchPanel.html", CreateSearchPanel.this.getModelObject().name), Buttons.Type.Primary) {
                            @Override
                            public void onClick() {
                                model.getObject().updateSearchFields();
                                super.onClick();
                            }
                        }.setLabel(Model.of("Download Html")),

                        new BootstrapDownloadLink(buttonMarkupId, () -> ChunkFactory.createFile(model.getObject(), "SearchProperties#1"),
                                () -> String.format("%sSearchPanel.utf8.properties", CreateSearchPanel.this.getModelObject().name), Buttons.Type.Primary) {
                            @Override
                            public void onClick() {
                                model.getObject().updateSearchFields();
                                super.onClick();
                            }
                        }.setLabel(Model.of("Download Properties")));
            }
        };
        buttonGroup.setDefaultModel(new Model<>());
        buttonGroup.setDefaultModelObject("something");
        return buttonGroup;
    }
}
