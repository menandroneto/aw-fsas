package com.algaworks.algamoneyapi.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler{

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        
        System.out.println(LocaleContextHolder.getLocale());
        
        String menagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
        // String_menagemDesenvolvedor_=_ex.getCause().toString()
        String menagemDesenvolvedor = Optional.ofNullable(ex.getCause()).orElse(ex).toString();
        List<Erro> erros = Arrays.asList(new Erro(menagemUsuario, menagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<Erro> erros = CriarListaDeErros(ex.getBindingResult());
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<Erro> CriarListaDeErros(BindingResult bindingResult){
        List<Erro> erros = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()){
            String menagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String menagemDesenvolvedor = fieldError.toString();
            erros.add(new Erro(menagemUsuario, menagemDesenvolvedor));
        }

        return erros;
    }

    // @ExceptionHandler([ EmptyResultDataAccessException.class ])
    // @ResponseStatus(HttpStatus.NOT_FOUND)
    // public_void_handleEmptyResultDataAccessException()

    @ExceptionHandler({ EmptyResultDataAccessException.class })
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request){
        String menagemUsuario = messageSource.getMessage("recurso.nao-econtrado", null, LocaleContextHolder.getLocale());
        String menagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(menagemUsuario, menagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){
        String menagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
        String menagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
        List<Erro> erros = Arrays.asList(new Erro(menagemUsuario, menagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /*     
	public static class Erro {
		
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
		
	}    
    */
}