package gr.petros.ui;

import com.x5.template.Chunk;
import de.agilecoders.wicket.core.markup.html.bootstrap.block.Code;
import de.agilecoders.wicket.core.markup.html.bootstrap.block.CodeBehavior;
import gr.petros.core.ChunkFactory;
import gr.petros.model.JavaObject;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class SqlPanel extends GenericPanel<JavaObject> {

    transient Chunk chunk;

    public SqlPanel(String id, IModel<JavaObject> model) {
        super(id, model);//, Model.of("Sql"), PanelType.Default);
        setOutputMarkupPlaceholderTag(true);


        LoadableDetachableModel<String> sqlModel = LoadableDetachableModel.of(() -> {
            JavaObject obj = model.getObject();
            if (obj == null) {
                return null;
            }
            this.chunk = ChunkFactory.createChunk(model.getObject(), "Sql#1");
            return chunk.toString();
        });
        add(new Code("code", sqlModel).setLanguage(CodeBehavior.Language.SQL));
        add(new DownloadLink("download", () -> chunk.toString().getBytes()));
    }
}
