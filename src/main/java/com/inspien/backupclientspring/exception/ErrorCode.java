package com.inspien.backupclientspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 입력 오류
    NOT_FOUND_DIRECTORY("입력한 경로를 찾을 수 없습니다."),
    NOT_DIRECTORY("입력한 경로가 디렉토리 경로가 아닙니다."),

    // 통신 오류
    CAN_NOT_CONNECT("서버에 연결할 수 없는 상태입니다."),
    CAN_NOT_PARSE_RESPONSE_DATA("서버의 응답 데이터를 해석할 수 없습니다."),

    // 저장소 오류
    STORAGE_IS_ALREADY_EXIST("저장소가 이미 존재합니다."),
    STORAGE_CREATE_FAILURE("저장소 만들기를 실패했습니다."),
    STORAGE_IS_NOT_EXIST("해당 저장소가 존재하지 않습니다."),
    STORAGE_UPDATE_FAILURE("저장소에 저장할 수 없습니다."),
    STORAGE_ROLLBACK_FAILURE("저장소 되돌리기를 실패했습니다."),

    // 파일 오류
    CAN_NOT_SEND_FILE("파일을 전송할 수 없습니다."),
    FILE_IS_NOT_EXIST("파일이 존재하지 않습니다."),
    FILE_ACCESS_FAILURE("파일에 접근할 수 없습니다.");

    private String message;
}
