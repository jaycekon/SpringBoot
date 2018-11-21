package com.jaycekon.apollo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
//@ComponentScan(value = ["com.jaycekon.apollo"])
//@EnableApolloConfig
open class SpringBootApolloApplication

fun main(args: Array<String>) {
    runApplication<SpringBootApolloApplication>(*args)
}
