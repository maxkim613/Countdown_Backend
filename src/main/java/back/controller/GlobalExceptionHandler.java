package back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import back.exception.HException;
import back.util.ApiResponse;

@ControllerAdvice
//모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리할 수 있도록 해주는 어노테이션
public class GlobalExceptionHandler {

    @ExceptionHandler(HException.class) //HException이 발생했을 때 호출
    public ResponseEntity<?> handleHException(HException ex) { 
    	//ResponseEntity: HTTP 상태 코드와 데이터를 같이 보내는 데 쓰는 객체
        return ResponseEntity
                .status(ex.getStatus()) 
                //status :HTTP 응답의 상태 코드(예: 200, 400, 404 등)를 지정하는 역할
                //getStatus() : HTTP 상태코드를 리턴하는 함수
                //ex.getStatus()로 꺼낸 상태 코드(예: 400)를 ResponseEntity.status(400)처럼 전달
                .body(new ApiResponse<>(false, ex.getMessage(), null));
        		// body: 본문(body)에 어떤 데이터를 담을지 정하는 메서드
        		//        false: 성공 여부
        		//        ex.getMessage(): 예외 메시지 (예: "존재하지 않는 사용자입니다")
        		//        null: 데이터 없음
    }
}
