package com.junwoo.lootwise.auth.exception

class DuplicateEmailException(
    email: String,
) : RuntimeException("Email already exists: $email")
