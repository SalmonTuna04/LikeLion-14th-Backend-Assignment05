package com.likelion.likelioncrud.common.exception;

import com.likelion.likelioncrud.common.response.code.ErrorCode;
import com.likelion.likelioncrud.common.template.ApiResTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@Slf4j // 로깅을 위한 Logger를 생성
@RestControllerAdvice // REST API 컨트롤러에 대한 예외 처리 어드바이스임을 나타내는 어노테이션
@Component // 클래스를 Spring 컴포넌트로 등록
@RequiredArgsConstructor
public class CustomExceptionAdvice {

    /**
     * 500 Internal Server Error
     * 원인 모를 이유의 예외 발생 시
     * 모든 종류의 Exception 처리
     */
    @ExceptionHandler(Exception.class)  // 모든 종류의 Exception 처리
    public ResponseEntity<ApiResTemplate<Void>> handleServerException(final Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e); // 로그 출력

        ApiResTemplate<Void> response = ApiResTemplate.errorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * custom error
     * 내부 커스텀 에러
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResTemplate<Void>> handleCustomException(BusinessException e) {
        log.error("CustomException: {}", e.getMessage(), e); // 로그 출력

        //조심
        ApiResTemplate<Void> apiResponse =ApiResTemplate.errorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus()) //http 응답 코드 설정
                .body(apiResponse);//http 응답 body값 설정
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResTemplate<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        // 에러 메시지 생성
        // 에러 메세지를 저장할 errorMap 생성
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // 응답 생성
        return new ResponseEntity<>(
                ApiResTemplate.errorResponse(
                        ErrorCode.VALIDATION_EXCEPTION,
                        ErrorCode.VALIDATION_EXCEPTION.getMessage() + convertMapToString(errorMap)
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    private String convertMapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append(", ");
        }
        sb.deleteCharAt(sb.length() - 1); // 마지막 띄어쓰기 제거
        sb.deleteCharAt(sb.length() - 1); // 마지막 쉼표 제거
        return sb.toString();
    }


}

