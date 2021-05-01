package ru.etu.cgvm.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

@ToString
@NoArgsConstructor
public class SignatureParameter {

    @Setter
    @Getter
    @JsonInclude
    private Type type;
    @Getter
    @JacksonXmlProperty(isAttribute = true)
    private String variable;

    public void setVariable(String variable) {
        if (Optional.ofNullable(variable).isPresent()) {
            this.variable = variable.replace("*", ""); //Всегда определяющие метки
        }
    }

    public String getStringRepresentation() {
        return type.getName() + (variable == null ? "" : " *" + variable);

    }

    public boolean isIdentical(SignatureParameter other) {
        if (other == null) return false;
        return Objects.equals(type, other.getType());
    }
}