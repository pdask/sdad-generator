package gr.petros.ui.table;

import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.List;

public class EnumDropDownColumn<T, C extends Enum<C>> extends DropDownColumn<T, C> {
    private final Class<C> type;

    public EnumDropDownColumn(IModel<String> displayModel, String propertyExpression, Class<C> type) {
        super(displayModel, propertyExpression, new EnumChoiceRenderer<>());
        this.type = type;
    }

    public EnumDropDownColumn(IModel<String> displayModel, String propertyExpression, IChoiceRenderer<C> choiceRenderer, Class<C> type) {
        super(displayModel, propertyExpression, choiceRenderer);
        this.type = type;
    }

    @Override
    protected List<C> choices(IModel<T> rowModel) {
        return Arrays.asList(type.getEnumConstants());
    }
}
