package one.digitalinnovation.avengers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AvengersApiApplication

fun main(args: Array<String>) {
	runApplication<AvengersApiApplication>(*args)
}
