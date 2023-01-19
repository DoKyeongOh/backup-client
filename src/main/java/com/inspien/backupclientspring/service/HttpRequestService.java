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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HttpRequestService {

    @Autowired
    RestTemplate restTemplate;

    public void sendNewStorageRequest(List<CustomFile> customFiles, String rootDirPath) {
        Map<String, Object> param = new HashMap<>();
        param.put("files", customFiles);
        param.put("rootDirPath", rootDirPath);
        sendWithBody(param, ResourceUrl.STORAGE, HttpMethod.POST);
    }

    public Map<String, CustomFile> sendStorageInquiryRequest(String rootDirPath) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl.STORAGE)
                .queryParam("rootDirPath", rootDirPath);

        ResponseEntity<JsonNode> entity = send(builder.build().toString(), HttpMethod.GET);
        return convertCustomFileMap(entity.getBody());
    }

    private Map<String, CustomFile> convertCustomFileMap(JsonNode jsonBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CustomFile[] customFiles = mapper.readValue(jsonBody.toString(), CustomFile[].class);
            Map<String, CustomFile> customFileMap = new HashMap<>();
            for (CustomFile rf : customFiles) {
                customFileMap.put(rf.getFilename(), rf);
            }
            return customFileMap;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CAN_NOT_PARSE_RESPONSE_DATA);
        }
    }

    public void sendUpdateStorageRequest(ClassifiedCustomFiles classifiedCustomFiles) {
        sendWithBody(classifiedCustomFiles, ResourceUrl.STORAGE, HttpMethod.PUT);
    }

    public List<String> sendFilenameInquiryRequest(String rootDirPath) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ResourceUrl.STORAGE)
                .queryParam("rootDirPath", rootDirPath);

        ResponseEntity<JsonNode> entity = send(builder.build().toString(), HttpMethod.GET);
        return convertFilenameList(entity.getBody());
    }

    private List<String> convertFilenameList(JsonNode jsonBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String[] filenames = mapper.readValue(jsonBody.toString(), String[].class);
            List<String> filenameList = new ArrayList<>();
            for (String filename : filenameList) {
                filenameList.add(filename);
            }
            return filenameList;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CAN_NOT_PARSE_RESPONSE_DATA);
        }
    }

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

    public ResponseEntity<JsonNode> send(String url, HttpMethod method) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(header);
        try {
            ResponseEntity<JsonNode> entity = restTemplate.exchange(url, method, httpEntity, JsonNode.class);
            throwCustomException(entity.getStatusCode());
            return entity;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CAN_NOT_CONNECT);
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
