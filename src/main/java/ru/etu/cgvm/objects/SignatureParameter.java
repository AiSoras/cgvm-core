package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class SignatureParameter {

    @Setter
    @Getter
    private Type type;
    @Getter
    private String variable;

    public void setVariable(String variable) {
        if (Optional.ofNullable(variable).isPresent()) {
            this.variable = variable.replace("*", "");
        }
    }
}
