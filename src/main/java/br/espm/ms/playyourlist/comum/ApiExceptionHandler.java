package br.espm.ms.playyourlist.comum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<MensagemResponse> naoEncontrado(NaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensagemResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<MensagemResponse> conflito(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MensagemResponse(ex.getMessage()));
    }

    /** Erros das regras de validacao: devolve campo -> mensagem. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> erros.putIfAbsent(e.getField(), e.getDefaultMessage()));
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("mensagem", "Dados invalidos");
        corpo.put("erros", erros);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MensagemResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new MensagemResponse("JSON invalido ou ausente"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MensagemResponse> tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(new MensagemResponse("Parametro invalido: " + ex.getName()));
    }
}
