package com.inspien.backupclientspring.service;

import com.inspien.backupclientspring.exception.CustomException;
import com.inspien.backupclientspring.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DirectoryValidationService {

    public void validate(String dirPath) {
        File directoryFile = new File(dirPath);
        if (!directoryFile.exists()) {
            throw new CustomException(ErrorCode.NOT_FOUND_DIRECTORY);
        }

        if (!directoryFile.isDirectory()) {
            throw new CustomException(ErrorCode.NOT_DIRECTORY);
        }
    }

}
