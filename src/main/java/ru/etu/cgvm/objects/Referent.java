package ru.etu.cgvm.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.etu.cgvm.objects.Constant.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Referent {

    // Взаимоисключающие поля
    @JsonInclude
    private Descriptor descriptor;
    @JsonInclude
    private Designation designation;
    @JacksonXmlProperty(isAttribute = true, localName = "isNegated")
    private boolean isNegated;

    public Referent(Referent referent) {
        isNegated = referent.isNegated();
        descriptor = Optional.ofNullable(referent.getDescriptor()).map(Descriptor::new).orElse(null);
        designation = Optional.ofNullable(referent.getDesignation()).map(Designation::new).orElse(null);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static final class Designation {
        @JacksonXmlProperty(isAttribute = true)
        private String additionalInfo;
        // Взаимоисключающие поля
        @JacksonXmlProperty(isAttribute = true)
        private String literal;
        @JacksonXmlProperty(isAttribute = true)
        private String locator;
        @JacksonXmlProperty(isAttribute = true)
        private String quantifier;

        private Designation(Designation designation) {
            additionalInfo = designation.getAdditionalInfo();
            literal = designation.getLiteral();
            locator = designation.getLocator();
            quantifier = designation.getQuantifier();
        }

        @Override
        public String toString() {
            return
                    (additionalInfo == null ? EMPTY : (additionalInfo + SPACE))
                            + (literal == null ? EMPTY : (literal))
                            + (locator == null ? EMPTY : (locator))
                            + (quantifier == null ? EMPTY : (quantifier));

        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static final class Descriptor {
        @JacksonXmlProperty(isAttribute = true)
        private String additionalInfo;
        @JacksonXmlProperty(isAttribute = true)
        private String structure;

        private Descriptor(Descriptor descriptor) {
            additionalInfo = descriptor.getAdditionalInfo();
            structure = descriptor.getStructure();
        }

        @JsonIgnore
        public List<String> getStructureMembers() {
            return Arrays.stream(getElementEnumeration()
                    .split(",")).map(String::trim).collect(Collectors.toList());
        }

        @JsonIgnore
        private boolean isAnyElement() {
            return getElementEnumeration()
                    .equals(Constant.ALL_SET_ELEMENTS);
        }

        @JsonIgnore
        private String getElementEnumeration() {
            return Optional.ofNullable(structure)
                    .orElse(EMPTY)
                    .replaceAll("[<>{}]", EMPTY);
        }

        @Override
        public String toString() {
            return (additionalInfo == null ? EMPTY : (additionalInfo + SPACE))
                    + (structure == null ? EMPTY : structure);
        }
    }

    @JsonIgnore
    public boolean isAny() {
        return Optional.ofNullable(descriptor).orElse(new Descriptor()).isAnyElement();
    }

    @Override
    public String toString() {
        if (isNegated) {
            return TILDA;
        } else {
            return (descriptor == null ? EMPTY : (descriptor + SPACE))
                    + (designation == null ? EMPTY : designation);
        }
    }
}