package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class Type {

    @Setter
    @Getter
    private String name = "";
    @Setter
    @Getter
    private boolean isNegated;

    public void setNegated(String tilde) {
        isNegated = StringUtils.isNotBlank(tilde);
    }

    @Override
    public String toString() {
        return isNegated ? "~" : "" + name;
    }
}