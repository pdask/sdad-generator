package gr.petros;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.Objects;
import java.util.Optional;

public class Utils {

    public static String getSimpleSqlField(String type, String name) {
        return "";
    }

    public static String getSqlIndex(String indexName, String table, String columnList) {
        return String.format("create index %s on sdad.%s (%s);", indexName, table, columnList);
    }

    public static String getSqlUniqueConstraint(String name, String table, String columnList) {
        return String.format("create unique index %s on sdad.%s (%s);", name, table, columnList);
    }


    public static AnnotationExpr getAnnotation(FieldDeclaration field, String annotationName) {
        Optional<AnnotationExpr> annotationOptional =
                field.getAnnotations().stream()
                        .filter(a -> Objects.equals(a.getNameAsString(), annotationName))
                        .findFirst();

        return annotationOptional.orElse(null);
    }

    public static String getAnnotationPropertyValue(NodeList<AnnotationExpr> annotations, String annotationName, String propertyName) {
        Optional<AnnotationExpr> annotationOptional =
                annotations.stream()
                        .filter(a -> Objects.equals(a.getNameAsString(), annotationName))
                        .findFirst();

        return annotationOptional.map(annotationExpr -> annotationExpr.getChildNodes()
                .stream().filter(n -> n instanceof MemberValuePair && Objects.equals(((MemberValuePair) n).getNameAsString(), propertyName))
                .map(n -> ((StringLiteralExpr) ((MemberValuePair) n).getValue()).asString())
                .findFirst().get()).orElse(null);

    }
}
