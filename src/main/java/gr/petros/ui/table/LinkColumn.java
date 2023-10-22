package gr.petros.ui.table;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public abstract class LinkColumn<T> extends AbstractColumn<T, String> {

    private final Buttons.Type type;
    private final Buttons.Size size;
    private final IconType iconType;

    public LinkColumn(IModel<String> displayModel, Buttons.Type type) {
        this(displayModel, type, Buttons.Size.Small, null);
    }

    public LinkColumn(IModel<String> displayModel, Buttons.Type type, Buttons.Size size) {
        this(displayModel, type, size, null);
    }

    public LinkColumn(IModel<String> displayModel, Buttons.Type type, Buttons.Size size, IconType iconType) {
        super(displayModel, null);
        this.type = type;
        this.size = size;
        this.iconType = iconType;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {

        LinkPanel panel = new LinkPanel(componentId);
        BootstrapAjaxLink<Void> link = new BootstrapAjaxLink<>("id", type) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LinkColumn.this.onClick(target, rowModel);
            }
        };
        link.setSize(size).setIconType(iconType);
        item.add(panel.add(link));

    }

    protected abstract void onClick(AjaxRequestTarget target, IModel<T> rowModel);
}
