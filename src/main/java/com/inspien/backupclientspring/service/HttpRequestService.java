package com.inspien.backupclientspring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.backupclientspring.exception.CustomException;
import com.inspien.backupclientspring.exception.ErrorCode;
import com.inspien.backupclientspring.domain.CustomFile;
import com.inspien.backupclientspring.domain.ClassifiedCustomFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HttpRequestService {

    @Autowired
    RestTemplate restTemplate;
    private String storageCreationUrl = "http://localhost:8080/storage";
    private String storageInquiryUrl = "http://localhost:8080/storage";

    public void sendNewStorageRequest(List<CustomFile> customFiles, String rootDirPath) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> param = new HashMap<>();
        param.put("files", customFiles);
        param.put("rootDirPath", rootDirPath);

        HttpEntity<Map<String, Object>> httpEntity
                = new HttpEntity<>(param, header);

        HttpStatus status;
        try {
            ResponseEntity<JsonNode> postForEntity = restTemplate.postForEntity(storageCreationUrl, httpEntity, JsonNode.class);
            throwCustomException(postForEntity.getStatusCode());
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, CustomFile> sendStorageInquiryRequest(String rootDirPath) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(storageInquiryUrl)
                    .queryParam("rootDirPath", rootDirPath);

            ResponseEntity<JsonNode> getForEntity
                    = restTemplate.getForEntity(builder.build().toString(), JsonNode.class, rootDirPath);
            throwCustomException(getForEntity.getStatusCode());

            ObjectMapper mapper = new ObjectMapper();
            CustomFile[] customFiles = mapper.readValue(getForEntity.getBody().toString(), CustomFile[].class);

            Map<String, CustomFile> remoteFileMap = new HashMap<>();
            for (CustomFile rf : customFiles) {
                remoteFileMap.put(rf.getFilename(), rf);
            }
            return remoteFileMap;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CAN_NOT_CONNECT);
        }
    }

    public void sendUpdateStorageRequest(ClassifiedCustomFiles classifiedCustomFiles) {
    public ResponseEntity<JsonNode> sendWithBody(Object body, String url, HttpMethod method) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(body, header);
        try {
            ResponseEntity<JsonNode> entity = restTemplate.exchange(url, method, httpEntity, JsonNode.class);
            throwCustomException(entity.getStatusCode());
            return entity;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CAN_NOT_CONNECT);
        }
    }

        HttpEntity<ClassifiedCustomFiles> httpEntity
                = new HttpEntity<>(classifiedCustomFiles, header);

        try {
            ResponseEntity<JsonNode> putEntity =
                    restTemplate.exchange(storageCreationUrl, HttpMethod.PUT, httpEntity, JsonNode.class);
            if (putEntity.getStatusCode() != HttpStatus.OK) {
                throw new CustomException(ErrorCode.STORAGE_UPDATE_FAILURE);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.STORAGE_UPDATE_FAILURE);
        }
    }

    public void throwCustomException(HttpStatus status) {
        switch (status) {
            case OK: return;
            case NOT_ACCEPTABLE: throw new CustomException(ErrorCode.STORAGE_IS_NOT_EXIST);
            case NO_CONTENT: throw new CustomException(ErrorCode.STORAGE_IS_ALREADY_EXIST);
            default: throw new CustomException(ErrorCode.STORAGE_CREATE_FAILURE);
        }
    }
}
