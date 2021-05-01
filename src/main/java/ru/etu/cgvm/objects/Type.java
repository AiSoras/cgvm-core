package ru.etu.cgvm.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static ru.etu.cgvm.objects.Constant.EMPTY;
import static ru.etu.cgvm.objects.Constant.TILDA;

@NoArgsConstructor
public class Type {

    @Setter
    @Getter
    @JacksonXmlText
    private String name = "";
    @JacksonXmlProperty(isAttribute = true)
    private boolean isNegated;

    public Type(Type type) {
        name = type.getName();
        isNegated = type.isNegated();
    }

    public void setNegated(String tilde) {
        isNegated = StringUtils.isNotBlank(tilde);
    }

    @JsonIgnore
    public boolean isNegated() {
        return isNegated;
    }

    @Override
    public String toString() {
        return isNegated ? TILDA : EMPTY + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var type = (Type) o;
        return isNegated == type.isNegated && StringUtils.equalsIgnoreCase(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNegated);
    }
}