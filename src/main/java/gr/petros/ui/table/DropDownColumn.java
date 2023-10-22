package gr.petros.ui.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public abstract class DropDownColumn<T, C> extends PropertyColumn<T, String> {
    private final IChoiceRenderer<C> choiceRenderer;

    public DropDownColumn(IModel<String> displayModel, String propertyExpression, IChoiceRenderer<C> choiceRenderer) {
        super(displayModel, propertyExpression);
        this.choiceRenderer = choiceRenderer;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {

        DropDownPanel panel = new DropDownPanel(componentId);
        DropDownChoice<C> dropdown = new DropDownChoice<>("id", PropertyModel.of(rowModel, getPropertyExpression()), choices(rowModel), choiceRenderer);
        item.add(panel.add(dropdown.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                C modelobject = dropdown.getModelObject();
                int x = 999;
            }
        })));

    }

    protected abstract List<C> choices(IModel<T> rowModel);
}
