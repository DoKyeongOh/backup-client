package com.inspien.backupclientspring.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ClassifiedFilenames {
    private List<String> sameFileNames;
    private List<String> addedFileNames;
    private List<String> modifiedFilesNames;
    private List<String> deletedFileNames;

    public ClassifiedFilenames() {
        sameFileNames = new ArrayList<>();
        addedFileNames = new ArrayList<>();
        modifiedFilesNames = new ArrayList<>();
        deletedFileNames = new ArrayList<>();
    }

    public ClassifiedFilenames(Map<String, CustomFile> localFileMap, Map<String, CustomFile> remoteFileMap) {
        sameFileNames = new ArrayList<>();
        addedFileNames = new ArrayList<>();
        modifiedFilesNames = new ArrayList<>();
        deletedFileNames = new ArrayList<>();
        sortFilenames(localFileMap, remoteFileMap);
    }

    public void addSameFileNames(String filename) {
        sameFileNames.add(filename);
    }
    public void addAddedNames(String filename) {
        addedFileNames.add(filename);
    }
    public void addModifiedNames(String filename) {
        modifiedFilesNames.add(filename);
    }
    public void addDeletedNames(String filename) {
        deletedFileNames.add(filename);
    }


    public void sortFilenames(Map<String, CustomFile> localFileMap, Map<String, CustomFile> remoteFileMap) {
        for (String filename : localFileMap.keySet()) {
            if (!remoteFileMap.containsKey(filename)) {
                addAddedNames(filename);
                continue;
            }

            Long localLastModified = localFileMap.get(filename).getLastModified();
            Long remoteLastModified = remoteFileMap.get(filename).getLastModified();
            if (!remoteLastModified.equals(localLastModified)) {
                addModifiedNames(filename);
                continue;
            }

            addSameFileNames(filename);
        }

        for (String filename : remoteFileMap.keySet()) {
            if (!localFileMap.containsKey(filename)) {
                addDeletedNames(filename);
            }
        }
    }
}
