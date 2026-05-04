package com.junwoo.lootwise.common.exception

import com.junwoo.lootwise.auth.exception.DuplicateEmailException
import com.junwoo.lootwise.auth.exception.InvalidLoginException
import com.junwoo.lootwise.user.exception.UserNotFoundException
import com.junwoo.lootwise.user.exception.WithdrawnUserException
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val message = exception.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation failed"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(message = message))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(exception: ConstraintViolationException): ResponseEntity<ApiErrorResponse> {
        val message = exception.constraintViolations.firstOrNull()?.message ?: "Validation failed"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(message = message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(exception: MethodArgumentTypeMismatchException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse(message = "Invalid request parameter: ${exception.name}"))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(exception: EntityNotFoundException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse(message = exception.message ?: "Resource not found"))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(exception: NoSuchElementException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse(message = exception.message ?: "Resource not found"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(message = exception.message ?: "Invalid request"))

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmail(exception: DuplicateEmailException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse(message = exception.message ?: "Email already exists"))

    @ExceptionHandler(InvalidLoginException::class)
    fun handleInvalidLogin(exception: InvalidLoginException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse(message = exception.message ?: "Invalid email or password"))

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(exception: DataIntegrityViolationException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse(message = "Duplicated or invalid data"))

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse(message = exception.message ?: "User not found"))

    @ExceptionHandler(WithdrawnUserException::class)
    fun handleWithdrawnUser(exception: WithdrawnUserException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiErrorResponse(message = exception.message ?: "User is withdrawn"))

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse(message = exception.message ?: "Internal server error"))
}
