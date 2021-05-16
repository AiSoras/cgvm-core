package ru.etu.cgvm.ui.controllers;

import javafx.stage.FileChooser;
import lombok.Getter;

@Getter
public enum Notation {
    CGIF("CGIF files", "*.cgif"),
    XML("XML files", "*.xml");

    private final String fileType;
    private final String extension;

    Notation(String fileType, String extension) {
        this.fileType = fileType;
        this.extension = extension;
    }

    public FileChooser.ExtensionFilter getFileFilter() {
        return new FileChooser.ExtensionFilter(fileType, extension);
    }
}