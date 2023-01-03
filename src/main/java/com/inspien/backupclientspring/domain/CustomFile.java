package com.inspien.backupclientspring.domain;

import com.inspien.backupclientspring.exception.CustomException;
import com.inspien.backupclientspring.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomFile {
    private String filename;
    private Long lastModified;
    private String rootPath;
    private byte[] fileData;

    public CustomFile(File file, String rootPath) {
        this.filename = file.getAbsolutePath();
        this.lastModified = file.lastModified();
        this.rootPath = rootPath;
        try {
            this.fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.CAN_NOT_SEND_FILE);
        }
    }

    public void writeToLocalFile() {
        try {
            FileOutputStream fos = new FileOutputStream(filename, false);
            fos.write(fileData);
            File file = new File(filename);
            file.setLastModified(lastModified);
        } catch (FileNotFoundException e) {
            throw new CustomException(ErrorCode.FILE_IS_NOT_EXIST);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_ACCESS_FAILURE);
        }
    }

    public void deleteLocalFile() {
        File file = new File(filename);
        if (!file.delete()) {
            throw new CustomException(ErrorCode.STORAGE_ROLLBACK_FAILURE);
        }
    }

}
