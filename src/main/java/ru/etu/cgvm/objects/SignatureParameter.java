package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@ToString
@NoArgsConstructor
public class SignatureParameter {

    @Setter
    @Getter
    private Type type;
    @Getter
    private String variable;

    public void setVariable(String variable) {
        if (Optional.ofNullable(variable).isPresent()) {
            this.variable = variable.replace("*", ""); //Всегда определяющие метки
        }
    }
}
