package com.inspien.backupclientspring.service;

import com.inspien.backupclientspring.domain.ClassifiedFilenames;
import com.inspien.backupclientspring.domain.CustomFile;
import com.inspien.backupclientspring.domain.ClassifiedCustomFiles;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StorageService {
    DirectoryValidationService directoryValidationService;
    HttpRequestService httpRequestService;

    public Map<String, CustomFile> getRemoteFiles(String rootDirPath) {
        directoryValidationService.validate(rootDirPath);
        Map<String, CustomFile> customFiles = httpRequestService.sendStorageInquiryRequest(rootDirPath);
        return customFiles;
    }

    public void createStorage(String rootDirPath) {
        directoryValidationService.validate(rootDirPath);
        Map<String, CustomFile> fileMap = findLocalFiles(rootDirPath, rootDirPath);
        List<CustomFile> customFiles = convertCustomFiles(fileMap);
        httpRequestService.sendNewStorageRequest(customFiles, rootDirPath);
    }

    public void updateStorage(String rootDirPath) {
        directoryValidationService.validate(rootDirPath);
        ClassifiedCustomFiles customFiles = new ClassifiedCustomFiles(
                findLocalFiles(rootDirPath, rootDirPath),
                getRemoteFiles(rootDirPath)
        );
        customFiles.setRootDirPath(rootDirPath);
        httpRequestService.sendUpdateStorageRequest(customFiles);
    }

    private List<CustomFile> convertCustomFiles(Map<String, CustomFile> fileMap) {
        List<CustomFile> customFiles = new ArrayList<>();
        for (CustomFile f : fileMap.values()) {
            customFiles.add(f);
        }
        return customFiles;
    }

    public Map<String, CustomFile> findLocalFiles(String rootDirPath, String parentPath) {
        Map<String, CustomFile> fileMap = new HashMap<>();
        for (File f : new File(parentPath).listFiles()) {
            if (f.isDirectory()) {
                fileMap.putAll(findLocalFiles(rootDirPath, f.getAbsolutePath()));
            } else {
                fileMap.put(f.getAbsolutePath(), new CustomFile(f, rootDirPath));
            }
        }
        return fileMap;
    }

    public ClassifiedFilenames getClassifiedFilenames(String rootDirPath) {
        directoryValidationService.validate(rootDirPath);
        ClassifiedFilenames sortedFilenames = new ClassifiedFilenames(
                findLocalFiles(rootDirPath, rootDirPath),
                getRemoteFiles(rootDirPath));
        return sortedFilenames;
    }

    public void rollbackStorage(String rootDirPath) {
        Map<String, CustomFile> localFiles = findLocalFiles(rootDirPath, rootDirPath);
        Map<String, CustomFile> remoteFiles = getRemoteFiles(rootDirPath);
        for (CustomFile remoteFile : remoteFiles.values()) {
            String filename = remoteFile.getFilename();
            if (!localFiles.containsKey(remoteFile.getFilename())) {
                remoteFile.writeToLocalFile();
                continue;
            }

            CustomFile localFile = localFiles.get(filename);
            Long localLastModified = localFile.getLastModified();
            Long remoteLastModified = remoteFile.getLastModified();
            if (!localLastModified.equals(remoteLastModified)) {
                remoteFile.writeToLocalFile();
            }
        }
        for (CustomFile localFile : localFiles.values()) {
            if (!remoteFiles.containsKey(localFile.getFilename())) {
                localFile.deleteLocalFile();
            }
        }
    }
}
