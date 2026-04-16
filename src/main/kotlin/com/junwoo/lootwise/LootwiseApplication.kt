package com.junwoo.lootwise

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LootwiseApplication

fun main(args: Array<String>) {
    runApplication<LootwiseApplication>(*args)
}
