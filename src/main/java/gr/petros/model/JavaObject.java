package gr.petros.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.*;
import com.x5.util.AccessAsPojo;
import gr.petros.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@AccessAsPojo
public class JavaObject implements Serializable {

    public String name;
    public String id_type;
    public String subsystem;
    public String package_name;

    public List<JavaObjectField> fields = new ArrayList<>();
    public List<JavaObjectField> form_fields = new ArrayList<>();
    public List<JavaObjectField> search_fields = new ArrayList<>();

    public String sql_seq_increment;
    public List<String> sql_columns = new ArrayList<>();
    public List<String> sql_indexes = new ArrayList<>();
    public List<String> sql_unique_constraints = new ArrayList<>();

    public boolean show_active = false;


    public JavaObject(CompilationUnit unit) {
        package_name = unit.getPackageDeclaration().get().getNameAsString();

        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) unit.getType(0);
        name = clazz.getNameAsString();
        String[] parts = unit.getPackageDeclaration().get().getChildNodes().get(0).toString().split("\\.");
        subsystem = parts[parts.length - 1];

        show_active = name.contains("Lex");

        String sqlIdType;
        id_type = clazz.getMembers().stream()
                .filter(i -> i instanceof FieldDeclaration && Objects.equals(((FieldDeclaration) i).getVariable(0).getNameAsString(), "id"))
                .map(i -> ((FieldDeclaration) i).getVariable(0).getTypeAsString()).findFirst().get();
        if (id_type.toUpperCase().equals("LONG")) {
            sql_seq_increment = "10";
            sqlIdType = "numeric(18)";
        } else {
            sql_seq_increment = "5";
            sqlIdType = "numeric(9)";
        }

        sql_columns.add("optLock numeric(9) constraint DV_" + name + "_OPT_LOCK default 0 not null");
        sql_columns.add("rowArchived bit constraint DV_" + name + "_ROW_ARCHIVED default 0 not null");
        sql_columns.add("rowInsertedBy numeric(9) not null");
        sql_columns.add("rowInsertedOn datetime2(3) not null");
        if (clazz.getExtendedTypes().stream().anyMatch(e -> Objects.equals(e.getNameAsString(), "SdadUpdatableEntity"))) {
            sql_columns.add("rowUpdatedBy numeric(9)");
            sql_columns.add("rowUpdatedOn datetime2(3)");
        }
        if (clazz.getExtendedTypes().stream().anyMatch(e -> Objects.equals(e.getNameAsString(), "SdadAuditedEntity"))) {
            sql_columns.add("rowArchivedBy numeric(9)");
            sql_columns.add("rowArchivedOn datetime2(3)");
            sql_columns.add("fkRowActive " + sqlIdType + " constraint FK_" + name + "_ROW_ACTIVE references sdad." + name);
        }
        try {
            ((NormalAnnotationExpr) clazz.getAnnotations().stream().filter(a -> Objects.equals(a.getNameAsString(), "Table")).findFirst().get())
                    .getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "indexes")).map(p -> (ArrayInitializerExpr) p.getValue()).findFirst().get()
                    .getValues().stream().map(i -> (NormalAnnotationExpr) i).forEach(i -> {
                        String indexName = i.getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "name")).map(p -> ((StringLiteralExpr) p.getValue()).asString()).findFirst().get();
                        String indexColumnList = i.getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "columnList")).map(p -> ((StringLiteralExpr) p.getValue()).asString()).findFirst().get();
                        sql_indexes.add(Utils.getSqlIndex(indexName, name, indexColumnList));
                    });
        } catch (Exception e) {
            //NOOP
        }

        try {
            ((NormalAnnotationExpr) clazz.getAnnotations().stream().filter(a -> Objects.equals(a.getNameAsString(), "Table")).findFirst().get())
                    .getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "uniqueConstraints")).map(p -> (ArrayInitializerExpr) p.getValue()).findFirst().get()
                    .getValues().stream().map(i -> (NormalAnnotationExpr) i).forEach(i -> {
                        String constraintName = i.getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "name")).map(p -> ((StringLiteralExpr) p.getValue()).asString()).findFirst().get();
                        String constraintColumnList = i.getPairs().stream().filter(p -> Objects.equals(p.getNameAsString(), "columnNames"))
                                .map(p -> ((ArrayInitializerExpr) p.getValue()).getValues())
                                .flatMap(Collection::stream).map(v -> ((StringLiteralExpr) v).asString())
                                .collect(Collectors.joining(", "));
                        sql_unique_constraints.add(Utils.getSqlUniqueConstraint(constraintName, name, constraintColumnList));
                    });
        } catch (Exception e) {
            //NOOP
        }

        clazz.getMembers().stream()
                .filter(i -> i instanceof FieldDeclaration)
                .forEach(i -> addField((FieldDeclaration) i));
    }


    public void addField(FieldDeclaration field) {
        String name = field.getVariable(0).getNameAsString();
        String nameGr;
        if (field.getComment().isPresent()) {
            nameGr = field.getComment().get().asString()
                    .replace("/", "")
                    .replace("*", "").trim();
        } else {
            nameGr = name;
        }
        String javaType = field.getVariable(0).getTypeAsString();

        boolean isEnum = false;
        boolean isEntity = false;

        String sql;

        Optional<AnnotationExpr> columnA = field.getAnnotations().stream()
                .filter(a -> Objects.equals(a.getNameAsString(), "Column"))
                .findFirst();
        Optional<AnnotationExpr> joinColumnA = field.getAnnotations().stream()
                .filter(a -> Objects.equals(a.getNameAsString(), "JoinColumn"))
                .findFirst();

        if (columnA.isPresent()) {
            String columnDefinition = columnA.get().getChildNodes()
                    .stream().filter(n -> n instanceof MemberValuePair && Objects.equals(((MemberValuePair) n).getNameAsString(), "columnDefinition"))
                    .map(n -> ((StringLiteralExpr) ((MemberValuePair) n).getValue()).asString())
                    .findFirst().get();
            sql = name + " " + columnDefinition;
            isEnum = Utils.getAnnotation(field, "Enumerated") != null;
        } else if (joinColumnA.isPresent()) {
            String columnName = joinColumnA.get().getChildNodes()
                    .stream().filter(n -> n instanceof MemberValuePair && Objects.equals(((MemberValuePair) n).getNameAsString(), "name"))
                    .map(n -> ((StringLiteralExpr) ((MemberValuePair) n).getValue()).asString())
                    .findFirst().get();
            if (javaType.toUpperCase().contains("LEX")) {
                sql = columnName + " numeric(9) constraint FK_" + this.name + "_" + javaType + " references sdad." + javaType;
            } else {
                sql = columnName + " numeric(18) constraint FK_" + this.name + "_" + javaType + " references sdad." + javaType;
            }
            isEntity = true;
        } else {
            throw new RuntimeException();
        }

        if (Objects.equals(name, "id")) {
            sql_columns.add(0, sql + " not null constraint PK_" + this.name + "_ID primary key");
        } else {
            sql_columns.add(sql);

            Integer size = null;
            try {
                size = field.getAnnotations().stream().filter(a -> Objects.equals(a.getNameAsString(), "Size")).findFirst().stream()
                        .flatMap(a -> ((NormalAnnotationExpr) a).getPairs().stream()).filter(p -> Objects.equals(p.getNameAsString(), "max"))
                        .map(p -> (IntegerLiteralExpr) p.getValue()).map(IntegerLiteralExpr::getValue).map(Integer::parseInt).findFirst().orElse(null);
            } catch (Exception e) {
                // NOOP
            }
            fields.add(new JavaObjectField(name, nameGr, javaType, isEnum, isEntity, size));
            form_fields.add(new JavaObjectField(name, nameGr, javaType, isEnum, isEntity, size));
            search_fields.add(new JavaObjectField(name, nameGr, javaType, isEnum, isEntity, size));
        }

    }

    public void reloadFormFields() {
        form_fields = fields.stream().map(JavaObjectField::copy).collect(Collectors.toList());
    }

    public void reloadSearchFields() {
        search_fields = fields.stream().map(JavaObjectField::copy).collect(Collectors.toList());
    }

    public void updateFormFields() {
        form_fields.forEach(JavaObjectField::updateForForm);
    }

    public void updateSearchFields() {
        search_fields.forEach(JavaObjectField::updateForSearch);
    }
}
