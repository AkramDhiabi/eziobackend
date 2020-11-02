package com.gemalto.eziomobile.demo.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.RecordNotFoundException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.EXCEPTION_OCCURED_URL;

@ControllerAdvice
public class EziomobiledemoControllerAdvice {

	private static final LoggerUtil logger = new LoggerUtil(EziomobiledemoControllerAdvice.class);


	@ExceptionHandler(SQLException.class)
	public ResultStatus handleSQLException(HttpServletRequest request, Exception ex) {
		logger.info("SQLException Occured:: URL=" + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("SQLException Occured", HttpStatus.BAD_REQUEST);
		return resultStatus;
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResultStatus handleRecordNotFoundException(HttpServletRequest request, Exception ex) {
		logger.info("RecordNotFound Exception Occured:: URL=" + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("RecordNotFound Exception Occured", HttpStatus.NOT_FOUND);
		return resultStatus;
	}

	@ExceptionHandler(Exception.class)
	public ResultStatus handleException(HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("Exception Occured", HttpStatus.EXPECTATION_FAILED);
		return resultStatus;
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "IOException occured")
	@ExceptionHandler(IOException.class)
	public void handleIOException() {
		logger.error("IOException handler executed");
		// returning 404 error code
		// return "ezio-error";
	}

	@ExceptionHandler(ControllerException.class)
	public ResultStatus handleControllerExceptionn(HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("Exception Occured", HttpStatus.EXPECTATION_FAILED);
		return resultStatus;
	}

	@ExceptionHandler(MultipartException.class)
	public ResultStatus handleMultipartError(MultipartException e, HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("MultipartException Occured", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		return resultStatus;
	}
	
	@ExceptionHandler(InterruptedException.class)
	public ResultStatus handleInterruptedException (InterruptedException e, HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("InterruptedException Occured", HttpStatus.EXPECTATION_FAILED);
		return resultStatus;
	}
	
	@ExceptionHandler(ExecutionException.class)
	public ResultStatus handleExecutionException (ExecutionException e, HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("ExecutionException Occured", HttpStatus.EXPECTATION_FAILED);
		return resultStatus;
	}
	
	@ExceptionHandler(TimeoutException.class)
	public ResultStatus handleTimeoutException (TimeoutException e, HttpServletRequest request, Exception ex) {
		logger.info(EXCEPTION_OCCURED_URL + request.getRequestURL());
		ResultStatus resultStatus = new ResultStatus("TimeoutException Occured", HttpStatus.EXPECTATION_FAILED);
		return resultStatus;
	}

}
