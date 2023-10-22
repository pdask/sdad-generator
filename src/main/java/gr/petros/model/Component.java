package gr.petros.model;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Component {
    SdadTextField, SdadTextArea,

    SdadYesNoField(false, false), SdadCheckBox(false, false),
    SdadTimestampField(false, false),
    SdadDateField(false, false),

    SdadEnumDropDownField(true, true), SdadEnumMultiSelect(true, true),
    DiastimaTextField(false, false),

    SmartKatigoriaForeaDropDown(false, false), SmartForeasAutoComplete(false, false), SmartDiefthynsiProsopikouDropDown(false, false),
    SmartYpodiefthynsiProsopikouDropDown(false, false), SdadAutoCompleteField(true, true), SdadEntityDropDownField(true, false);


    private final boolean parameterized;
    private final boolean usesJavaType;

    Component() {
        this(true, false);
    }

    Component(boolean parameterized, boolean usesJavaType) {
        this.parameterized = parameterized;
        this.usesJavaType = usesJavaType;
    }

    public String componentDeclaration(String name, String javaType) {
        return String.format("private %s %s;", name() + (parameterized ? "<" + javaType + ">" : ""), name);
    }

    public String componentCreation(String name, String wicketId, String javaType) {
        String args = Stream.of("\"" + wicketId + "\"", usesJavaType ? javaType + ".class" : "").filter(StringUtils::isNotBlank).collect(Collectors.joining(", "));
        return String.format("%s = new %s(%s);", name, name() + (parameterized ? "<>" : ""), args);
    }

    public String componentHtml(String wicket_id) {
        String openingTag = null, closingTag = "";
        switch (this) {

            case SdadTextField:
            case SdadTimestampField:
            case SdadDateField:
            case DiastimaTextField:
                openingTag = "<input type=\"text\" wicket:id=\"%s\">";
                break;
            case SdadTextArea:
                openingTag = "<textarea wicket:id=\"%s\">";
                closingTag = "</textarea>";
                break;
            case SdadYesNoField:
                openingTag = "<span wicket:id=\"%s\">";
                closingTag = "</span>";
                break;
            case SdadCheckBox:
                openingTag = "<input type=\"checkbox\" wicket:id=\"%s\">";
                break;
            case SdadEnumDropDownField:
            case SdadEnumMultiSelect:
            case SmartKatigoriaForeaDropDown:
            case SmartDiefthynsiProsopikouDropDown:
            case SmartYpodiefthynsiProsopikouDropDown:
            case SdadEntityDropDownField:
                openingTag = "<select wicket:id=\"%s\">";
                closingTag = "</select>";
                break;
            case SmartForeasAutoComplete:
            case SdadAutoCompleteField:
                openingTag = "<div wicket:id=\"%s\">";
                closingTag = "</div>";
                break;
        }
        return String.format(openingTag, wicket_id) + closingTag;
    }
}
