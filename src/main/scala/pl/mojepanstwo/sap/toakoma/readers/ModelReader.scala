package pl.mojepanstwo.sap.toakoma.readers

import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.ItemReader
import pl.mojepanstwo.sap.toakoma.IsapModel

class ModelReader extends ItemReader[IsapModel] {

  val logger = LoggerFactory.getLogger(this.getClass())

  var stepExecution : StepExecution = null

  var executed = false

  def read : IsapModel = {
    logger.trace("read")

    if(executed) return null
    executed = true
    val jobExecution = stepExecution.getJobExecution
    val jobContext = jobExecution.getExecutionContext
    return jobContext.get("model").asInstanceOf[IsapModel]
  }

  @BeforeStep
  def retrieveInterstepData(stepExecution: StepExecution) = {
    this.stepExecution = stepExecution
  }
}
