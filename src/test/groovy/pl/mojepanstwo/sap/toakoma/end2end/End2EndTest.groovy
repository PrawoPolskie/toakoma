package pl.mojepanstwo.sap.toakoma.end2end

import org.junit.Assert
import org.junit.runner.RunWith
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input

import pl.mojepanstwo.sap.toakoma.Main
import pl.mojepanstwo.sap.toakoma.jobs.Isap2AkomaJob

import spock.lang.*

import groovy.io.FileType

@SpringBootTest
@ContextConfiguration
class End2EndTest extends Specification {

  @Shared JobLauncher jobLauncher
  @Shared Job isap2AkomaJob
  @Shared def list = []

  def setupSpec() {
	def ctx = SpringApplication.run(Main.class)
	jobLauncher = ctx.getBean(JobLauncher.class)
	isap2AkomaJob = ctx.getBean(Isap2AkomaJob.NAME(), Job.class)
	new File('src/test/resources/isap').eachFileRecurse(FileType.DIRECTORIES) { dir ->
	  list << dir.name
	}
  }

  def "end_2_end"() {
    expect:
    def jobParameters = new JobParametersBuilder().addString('id', id)
                                                  .toJobParameters()
    JobExecution jobExecution = jobLauncher.run(isap2AkomaJob, jobParameters)
	while(jobExecution.getStatus().isRunning()) sleep(3000)

    def controlHtml = new File("src/test/resources/isap/$id/output.html").text
//    def testHtml = new File(System.getProperty('java.io.tmpdir') + "/" + id + "/output.html").text

//    def myDiff = DiffBuilder.compare(Input.fromString(controlHtml))
//                            .withTest(Input.fromString(testHtml))
//                            .build()

//    Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences())



    where:
	id << list
  }
}
