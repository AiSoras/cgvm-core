package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@NoArgsConstructor
public class Type {

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private boolean isNegated;

    public void setNegated(String tilde) {
        isNegated = StringUtils.isNotBlank(tilde);
    }
}