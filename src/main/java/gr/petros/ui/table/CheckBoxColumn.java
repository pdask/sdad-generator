package gr.petros.ui.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class CheckBoxColumn<T, C> extends PropertyColumn<T, String> {

    public CheckBoxColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {

        CheckBoxPanel panel = new CheckBoxPanel(componentId);
        CheckBox checkbox = new CheckBox("id", PropertyModel.of(rowModel, getPropertyExpression()));
        item.add(panel.add(checkbox.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        })));

    }

}
