package ru.etu.cgvm.objects.graphs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;

import java.util.Arrays;
import java.util.Optional;

import static ru.etu.cgvm.objects.Constant.TILDA;

@ToString
@NoArgsConstructor
public class Context extends Graph {

    @JacksonXmlProperty(isAttribute = true)
    private boolean isSpecialContext;
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name; // if null or owner is null <=> outermost graph
    @Setter
    @JacksonXmlProperty(isAttribute = true)
    private boolean isNegated;

    public Context(Context context) { // Не копируем объекты, а только саму "оболочку"
        super(context);
        isSpecialContext = context.isSpecialContext();
        name = context.getName();
        isNegated = context.isNegated();
    }

    @JsonIgnore
    public boolean isSpecialContext() {
        return isSpecialContext;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public boolean isNegated() {
        return isNegated;
    }

    public void setName(String name) {
        this.name = name;
        this.isSpecialContext = Arrays.stream(SpecialContext.values()).anyMatch(value -> value.name().equalsIgnoreCase(name));
    }

    @Override
    @JsonIgnore
    public String getStringRepresentation() {
        return isNegated ? TILDA : "" +
                Optional.ofNullable(name).orElse("");
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Context) {
            var otherContext = (Context) other;
            boolean isNameIdentical = isNegated == otherContext.isNegated()
                    && StringUtils.equalsIgnoreCase(name, otherContext.getName());
            if (isNameIdentical) {
                if (getObjects().size() < otherContext.getObjects().size()) {
                    return getObjects().stream().allMatch(thisObject -> otherContext.getObjects().stream().anyMatch(thisObject::isIdentical));
                } else {
                    return otherContext.getObjects().stream().allMatch(otherObject -> getObjects().stream().anyMatch(otherObject::isIdentical));
                }
            }
        }
        return false;
    }
}