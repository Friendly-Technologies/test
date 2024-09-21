package com.friendly.services.infrastructure.base;

import com.friendly.commons.exceptions.BaseFriendlyException;
import com.friendly.commons.errors.ErrorApi;
import com.friendly.commons.exceptions.FriendlyPermissionException;
import com.friendly.commons.exceptions.FriendlyUnauthorizedException;
import com.friendly.commons.exceptions.FriendlyUnauthorizedUserException;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.settings.alerts.AlertsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;

@Slf4j
public class BaseController {

    @NonNull
    private final AlertProvider alertProvider;
    @Autowired
    private AlertsService alertsService;

    public BaseController(AlertProvider alertProvider) {
        this.alertProvider = alertProvider;
    }
    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Object> handleAll(Throwable ex, WebRequest request) {
        log.warn("Error [{}({})] occurred while handling request [{}]", ex.getMessage(), ex.getClass().getName(), ((ServletWebRequest) request).getRequest().getRequestURI());
        if (ex instanceof IOException) {
            if (!StringUtils.isEmpty(ex.getMessage()) && ex.getMessage().contains("[CLOSED_RST_RX]")) {
                return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.DESTINATION_LOCKED);
            }
        }
        if (ex instanceof FriendlyUnauthorizedUserException
                || ex instanceof FriendlyPermissionException) {
            final ErrorApi error = new ErrorApi(ex.getMessage(), ((BaseFriendlyException) ex).getCode());
            return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.FORBIDDEN);
        } else if (ex instanceof FriendlyUnauthorizedException) {
            final ErrorApi error = new ErrorApi(ex.getMessage(), 401);
            return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } else if (((ServletWebRequest) request).getRequest().getRequestURI().contains("alerts/events")
                || ex instanceof WebServiceException) {
            alertProvider.setAcsIsDown();
            return new ResponseEntity<>(alertsService.getAlertEvents(request.getHeader(IOT_AUTH_HEADER), true), HttpStatus.OK);
        } else if (ex instanceof BaseFriendlyException) {
            log.error(ex.getMessage(), ex);
            final ErrorApi error = new ErrorApi(ex.getMessage(), ((BaseFriendlyException) ex).getCode());
            return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (ex instanceof JDBCConnectionException
                || ex instanceof DataAccessResourceFailureException
            /*|| ex instanceof InvalidDataAccessResourceUsageException*/) {
            final ErrorApi error = new ErrorApi("Can not connect to DB", 9001);
            alertProvider.setDbIsDown();
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (ex instanceof ClientAbortException) {
            return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (ex instanceof DataIntegrityViolationException
                || ex instanceof ConstraintViolationException
                || ex instanceof SQLIntegrityConstraintViolationException) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getCause().getCause().getCause().getMessage(),
                                        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getClass().getName() + ": " + ex.getMessage(),
                                        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
