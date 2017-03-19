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
class Pdf2XmlJobConfiguration {
  
  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Autowired
  var data: DataSource = _
    
  @Bean
	def pdf2xmlJob: Job = {
	  jobs.get("pdf2xmlJob")
		    .start(step)
		    .build()
	}
    
	def step: Step = {
	  steps.get("step")
	       .chunk(1)
	       .reader(reader)
	       .processor(processor)
	       .build()
	}
  
	def reader: ItemReader[String] = {
	  var x = new LinkedList[String]()
	  x.add("X")
		val reader = new ListItemReader[String](x)
		return reader;
	}
	
  def processor: ItemProcessor[String, String] = {
        return new ItemProcessor[String, String]() {
          @Override
	        def process(item:String): String = {
            println(item)
            ""
          }
        }
    }
}