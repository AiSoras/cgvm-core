package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Referent {

    private Descriptor descriptor;
    private Designation designation;
    private boolean isNegated;

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class Designation {
        private String additionalInfo;
        private String literal;
        private String locator;
        private String quantifier;

        @Override
        public String toString() {
            return "Designation{" +
                    "additionalInfo='" + additionalInfo + '\'' +
                    ", literal='" + literal + '\'' +
                    ", locator='" + locator + '\'' +
                    ", quantifier='" + quantifier + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class Descriptor {
        private String additionalInfo;
        private String structure;

        public List<String> getStructureMembers() {
            return Arrays.stream(Optional.ofNullable(structure).orElse("").replaceAll("[<>{}]", "")
                    .split(",")).map(String::trim).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "Descriptor{" +
                    "additionalInfo='" + additionalInfo + '\'' +
                    ", structure='" + structure + '\'' +
                    '}';
        }
    }
}