package ru.etu.cgvm.objects;

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
    private String name = "";
    @Getter
    private boolean isNegated;

    public void setNegated(String tilde) {
        isNegated = StringUtils.isNotBlank(tilde);
    }

    @Override
    public String toString() {
        return isNegated ? TILDA : EMPTY + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return isNegated == type.isNegated && StringUtils.equalsIgnoreCase(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNegated);
    }
}