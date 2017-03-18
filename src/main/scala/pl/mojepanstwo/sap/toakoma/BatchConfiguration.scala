package pl.mojepanstwo.sap.toakoma

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.batch.item.file.FlatFileItemReader

@Configuration
@EnableBatchProcessing
class BatchConfiguration {
  
    @Autowired
    var jobBuilderFactory: JobBuilderFactory = _

    @Autowired
    var stepBuilderFactory: StepBuilderFactory = _

    @Autowired
    var dataSource: DataSource = _
    
    @Bean
    def reader: String = {
        val reader = "ccc"
        println("Hello")
        reader
    }
}