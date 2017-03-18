package pl.mojepanstwo.sap.toakoma

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

object Main {
  def main(args: Array[String]): Unit = {
    if(Cli.parseArgs(args)) SpringApplication.run(classOf[Application])
    else                    Cli.printHelp
  }
}