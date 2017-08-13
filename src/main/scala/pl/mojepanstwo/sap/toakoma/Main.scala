package pl.mojepanstwo.sap.toakoma

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import pl.mojepanstwo.sap.toakoma.jobs.Isap2AkomaJob
import pl.mojepanstwo.sap.toakoma.jobs.Files2BasexJob

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

      if(Cli.args.getOptionValue(Cli.OPT.command.toString) contains Cli.JOB.ISAP_TO_AKOMA.toString) {
        val isap2AkomaJob = ctx.getBean(Isap2AkomaJob.NAME, classOf[Job])
        val jobParameters = new JobParametersBuilder().addString(Cli.OPT.id.toString,
                                                                 Cli.args.getOptionValue(Cli.OPT.id.toString))
                                                      .toJobParameters()
        val jobExecution = jobLauncher.run(isap2AkomaJob, jobParameters)

        if(Cli.args.getOptionValue(Cli.OPT.command.toString) contains Cli.JOB.TO_DB.toString) {
          val files2BasexJob = ctx.getBean(Files2BasexJob.NAME, classOf[Job])
          jobLauncher.run(files2BasexJob, jobParameters)
        }
      }


    } catch {
      case e:
        Throwable => e.printStackTrace()
        System.exit(0)
    }
  }
}