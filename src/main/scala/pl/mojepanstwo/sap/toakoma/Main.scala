package pl.mojepanstwo.sap.toakoma

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder

@SpringBootApplication
class Application

object Main {
  def main(args: Array[String]): Unit = {
    if(!Cli.parseArgs(args)) {
      Cli.printHelp
      return
    }
    
    try {
      val ctx = SpringApplication.run(classOf[Application])
      val jobLauncher = ctx.getBean(classOf[JobLauncher])
      
      if(Cli.JOB.PDF_2_XML.toString == Cli.args.getOptionValue(Cli.OPT.command.toString)) {
        val pdf2XmlJob = ctx.getBean("pdf2xmlJob", classOf[Job])
        val jobParameters = new JobParametersBuilder().toJobParameters()
        val jobExecution = jobLauncher.run(pdf2XmlJob, jobParameters)
      }
      
      if(Cli.JOB.XML_2_DB.toString == Cli.args.getOptionValue(Cli.OPT.command.toString)) {
        val pdf2XmlJob = ctx.getBean("xml2dbJob", classOf[Job])
        val jobParameters = new JobParametersBuilder().toJobParameters()
        val jobExecution = jobLauncher.run(pdf2XmlJob, jobParameters)
      }
    } catch {
      case e: 
        Throwable => println(e)
        System.exit(0)
    }
  }
}