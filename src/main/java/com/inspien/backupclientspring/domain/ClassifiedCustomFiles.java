package com.inspien.backupclientspring.domain;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ClassifiedCustomFiles {
    private String rootDirPath;
    private List<CustomFile> addedFiles;
    private List<CustomFile> modifiedFiles;
    private List<CustomFile> deletedFiles;

    public ClassifiedCustomFiles(String rootDirPath, List<CustomFile> addedFiles, List<CustomFile> modifiedFiles, List<CustomFile> deletedFiles) {
        this.rootDirPath = rootDirPath;
        this.addedFiles = addedFiles;
        this.modifiedFiles = modifiedFiles;
        this.deletedFiles = deletedFiles;
    }

    public ClassifiedCustomFiles() {
        this.addedFiles = new ArrayList<>();
        this.modifiedFiles = new ArrayList<>();
        this.deletedFiles = new ArrayList<>();
    }

    public ClassifiedCustomFiles(Map<String, CustomFile> localMap, Map<String, CustomFile> remoteMap) {
        this.addedFiles = new ArrayList<>();
        this.modifiedFiles = new ArrayList<>();
        this.deletedFiles = new ArrayList<>();
        sortFiles(localMap, remoteMap);
    }

    public void addAddedFiles(CustomFile file) {
        if (this.addedFiles == null) {
            this.addedFiles = new ArrayList<>();
        }
        this.addedFiles.add(file);
    }

    public void addModifiedFiles(CustomFile file) {
        if (this.modifiedFiles == null) {
            this.modifiedFiles = new ArrayList<>();
        }
        this.modifiedFiles.add(file);
    }

    public void addDeletedFiles(CustomFile file) {
        if (this.deletedFiles == null) {
            this.deletedFiles = new ArrayList<>();
        }
        this.deletedFiles.add(file);
    }

    public void sortFiles(Map<String, CustomFile> localMap, Map<String, CustomFile> remoteMap) {
        for (CustomFile localFile : localMap.values()) {
            if (!remoteMap.containsKey(localFile.getFilename())) {
                addAddedFiles(localFile);
                continue;
            }

            CustomFile remoteFile = remoteMap.get(localFile.getFilename());
            Long localLastModified = localFile.getLastModified();
            Long remoteLastModified = remoteFile.getLastModified();
            if (!localLastModified.equals(remoteLastModified)) {
                addModifiedFiles(localFile);
                continue;
            }
        }
        for (String filename : remoteMap.keySet()) {
            if (!localMap.containsKey(filename)) {
                addDeletedFiles(remoteMap.get(filename));
            }
        }
    }
}
