package pl.mojepanstwo.sap.toakoma.jobs

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.core.{ Job, Step }
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.item.ItemProcessor
import java.util.LinkedList

@Configuration
@EnableBatchProcessing
class Xml2DbJobConfiguration {
  
  def NAME = "xml2dbJob"
  
  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Autowired
  var data: DataSource = _
    
  @Bean
	def xml2dbJob: Job = {
	  jobs.get(NAME)
		    .start(xml2dbStep)
		    .build()
	}
    
	def xml2dbStep: Step = {
	  steps.get("step")
	       .chunk(1)
	       .reader(xml2dbReader)
	       .processor(xml2dbProcessor)
	       .build()
	}
  
	def xml2dbReader: ItemReader[String] = {
	  var x = new LinkedList[String]()
	  x.add("Y")
		val reader = new ListItemReader[String](x)
		return reader;
	}
	
  def xml2dbProcessor: ItemProcessor[String, String] = {
        return new ItemProcessor[String, String]() {
          @Override
	        def process(item:String): String = {
            println(item)
            ""
          }
        }
    }
}