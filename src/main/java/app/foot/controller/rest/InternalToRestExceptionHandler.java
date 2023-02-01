package app.foot.controller.rest;

import app.foot.controller.rest.model.Exception;
import app.foot.exception.BadRequestException;
import app.foot.exception.InternalServerException;
import app.foot.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class InternalToRestExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    ResponseEntity<Exception> handleBadRequest(BadRequestException e){
        log.info("Bad request", e);
        return new ResponseEntity<>(
                toRest(e, HttpStatus.BAD_REQUEST)
                , HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(value = {NotFoundException.class})
    ResponseEntity<Exception> handleNotFound(NotFoundException e){
        log.info("Not found", e);
        return new ResponseEntity<>(
                toRest(e, HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(value = {InternalServerException.class})
    ResponseEntity<Exception> handleInternalServerError(InternalServerException e){
        log.info("Internal server error", e);
        return new ResponseEntity<>(
                toRest(e, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Exception toRest(java.lang.Exception e, HttpStatus status){
        return Exception.builder()
                .error(status)
                .status(status.value())
                .message(e.getMessage())
                .build();
    }
}
