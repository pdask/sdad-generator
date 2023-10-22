package gr.petros.model;

import com.x5.util.AccessAsPojo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@AccessAsPojo
public class JavaObjectField implements Serializable {

    public String name;
    public String name_gr;
    public String java_type;
    public Integer size;
    public Component component;
    public List<Component> componentChoices;

    public String wicket_id;
    public String component_declaration;
    public String component_creation;
    public String component_html;

    //from specific


    //search specific
    public String criterion = Criterion.exact;
    public List<String> criterionChoices;
    public boolean column = true;

    public String wicket_id_min;
    public String component_declaration_min;
    public String component_creation_min;
    public String component_html_min;

    public String wicket_id_max;
    public String component_declaration_max;
    public String component_creation_max;
    public String component_html_max;

    public JavaObjectField(String name, String name_gr, String type, boolean isEnum, boolean isEntity, Integer size) {
        this.name = name;
        this.name_gr = new String(name_gr.getBytes(), StandardCharsets.UTF_8);
        this.java_type = type;
        this.size = size;


        if (isEnum) {
            component = Component.SdadEnumDropDownField;
            componentChoices = List.of(Component.SdadEnumDropDownField, Component.SdadEnumMultiSelect);
            criterionChoices = List.of(Criterion.none, Criterion.exact, Criterion.in);
        } else if (isEntity) {
            if (type.equals("FMitrooForeas")) {
                component = Component.SmartForeasAutoComplete;
                componentChoices = List.of(Component.SmartForeasAutoComplete, Component.SdadAutoCompleteField, Component.SdadEntityDropDownField);
            } else if (type.equals("FMitrooDiefthynsiProsopikou")) {
                component = Component.SmartDiefthynsiProsopikouDropDown;
                componentChoices = List.of(Component.SmartDiefthynsiProsopikouDropDown, Component.SdadAutoCompleteField, Component.SdadEntityDropDownField);
            } else if (type.equals("FMitrooYpodiefthynsiProsopikou")) {
                component = Component.SmartYpodiefthynsiProsopikouDropDown;
                componentChoices = List.of(Component.SmartYpodiefthynsiProsopikouDropDown, Component.SdadAutoCompleteField, Component.SdadEntityDropDownField);
            } else {
                component = Component.SdadEntityDropDownField;
                componentChoices = List.of(Component.SdadAutoCompleteField, Component.SdadEntityDropDownField);
            }
            criterionChoices = List.of(Criterion.none, Criterion.exact);
        } else if (type.equals("Diastima")) {
            component = Component.DiastimaTextField;
            componentChoices = List.of(Component.DiastimaTextField);
            criterionChoices = List.of(Criterion.none, Criterion.exact);
        } else if (type.equals("String")) {
            if (size != null && size > 50) {
                component = Component.SdadTextArea;
            } else {
                component = Component.SdadTextField;
            }
            componentChoices = List.of(Component.SdadTextArea, Component.SdadTextField);
            criterionChoices = List.of(Criterion.none, Criterion.exact, Criterion.contains);
        } else if (type.equals("Integer") || type.equals("Long") || type.equals("BigDecimal")) {
            component = Component.SdadTextField;
            componentChoices = List.of(Component.SdadTextField);
            criterionChoices = List.of(Criterion.none, Criterion.exact, Criterion.range);
        } else if (type.equals("LocalDate")) {
            component = Component.SdadDateField;
            componentChoices = List.of(Component.SdadDateField);
            criterionChoices = List.of(Criterion.none, Criterion.exact, Criterion.range);
        } else if (type.equals("Date")) {
            component = Component.SdadTimestampField;
            componentChoices = List.of(Component.SdadTimestampField);
            criterionChoices = List.of(Criterion.none, Criterion.exact, Criterion.range);
        } else if (type.equals("Boolean")) {
            component = Component.SdadCheckBox;
            componentChoices = List.of(Component.SdadCheckBox, Component.SdadYesNoField);
            criterionChoices = List.of(Criterion.none, Criterion.exact);
        } else {
            component = Component.SdadTextField;
            componentChoices = List.of(Component.SdadTextField);
            criterionChoices = List.of(Criterion.none, Criterion.exact);
        }
    }

    public JavaObjectField copy() {
        JavaObjectField copy = new JavaObjectField();
        copy.name = name;
        copy.name_gr = name_gr;
        copy.java_type = java_type;
        copy.size = size;
        copy.component = component;
        copy.componentChoices = new ArrayList<>(componentChoices);
        return copy;
    }

    public void updateForForm() {
        wicket_id = name;
        component_declaration = component.componentDeclaration(name, java_type);
        component_creation = component.componentCreation(name, wicket_id, java_type);
        component_html = component.componentHtml(wicket_id);

    }

    public void updateForSearch() {
        if (!Objects.equals(criterion, Criterion.range)) {
            wicket_id = criterion + "." + name;
            component_declaration = component.componentDeclaration(name, java_type);
            component_creation = component.componentCreation(name, wicket_id, java_type);
            component_html = component.componentHtml(wicket_id);
        } else {

            wicket_id_min = "min" + "." + name;
            component_declaration_min = component.componentDeclaration(name + "Apo", java_type);
            component_creation_min = component.componentCreation(name + "Apo", wicket_id_min, java_type);
            component_html_min = component.componentHtml(wicket_id_min);

            wicket_id_max = "max" + "." + name;
            component_declaration_max = component.componentDeclaration(name + "Eos", java_type);
            component_creation_max = component.componentCreation(name + "Eos", wicket_id_max, java_type);
            component_html_max = component.componentHtml(wicket_id_max);
        }
    }

}
