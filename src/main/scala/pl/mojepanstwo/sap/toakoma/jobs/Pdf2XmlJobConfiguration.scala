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
import pl.mojepanstwo.sap.toakoma.readers.PdfReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.batch.core.scope.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope

object Pdf2XmlJob {
  val NAME = "pdf2xmlJob"
}

@Configuration
@EnableBatchProcessing
class Pdf2XmlJobConfiguration {
  
  import Pdf2XmlJob.NAME
  
  @Autowired
  var jobs: JobBuilderFactory = _

  @Autowired
  var steps: StepBuilderFactory = _

  @Autowired
  var data: DataSource = _
    
  @Bean
	def pdf2xmlJob: Job = {
	  jobs.get(NAME)
		    .start(step)
		    .build()
	}
  
	def step: Step = {
	  steps.get("step")
	       .chunk(1)
	       .reader(reader(null))
	       .processor(processor)
	       .build()
	}
	
  @Bean
	@StepScope
	def reader(@Value("#{jobParameters[file]}") filePath: String): PdfReader = {
	  new PdfReader(filePath)
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