package pl.mojepanstwo.sap.toakoma

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

object Main {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Application])
    Cli.parseArgs(args)
    val xml = <hello>world</hello>
    println(xml)
  }
}