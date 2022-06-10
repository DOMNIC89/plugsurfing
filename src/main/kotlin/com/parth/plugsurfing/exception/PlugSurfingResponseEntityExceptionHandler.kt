package com.parth.plugsurfing.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class PlugSurfingResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ InvalidMBIDRequestException::class ])
    fun handleInvalidMBIDRequestException(ex: InvalidMBIDRequestException, request: WebRequest): ResponseEntity<*> {
        return handleExceptionInternal(ex,
            ApiError(
                status = HttpStatus.BAD_REQUEST.value(),
                message = ex.message,
            ),
            HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        )
    }

    @ExceptionHandler(value = [MBIDNotFoundException::class])
    fun handleMBIDNotFoundException(ex: MBIDNotFoundException, request: WebRequest): ResponseEntity<*> {
        return handleExceptionInternal(ex,
            ApiError(
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message
            ), HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(value = [SomethingWentWrongException::class])
    fun handleSomethingWentWrongException(ex: SomethingWentWrongException, request: WebRequest): ResponseEntity<*> {
        return handleExceptionInternal(
            ex,
            ApiError(
                status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
                message = ex.message,
            ),
            HttpHeaders(),
            HttpStatus.UNPROCESSABLE_ENTITY,
            request
        )
    }

    @ExceptionHandler(value = [ServerBehavedIrresponsiblyException::class])
    fun handleServerBehavedIrresponsiblyException(ex: ServerBehavedIrresponsiblyException, request: WebRequest): ResponseEntity<*> {
        return handleExceptionInternal(
            ex,
            ApiError(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = ex.message,
            ),
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        )
    }

    @ExceptionHandler(value = [AlbumCoverArtNotFound::class])
    fun handleAlbumCoverArtNotFound(ex: AlbumCoverArtNotFound, request: WebRequest): ResponseEntity<*> {
        return handleExceptionInternal(
            ex,
            ApiError(
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message
            ),
            HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        )
    }
}