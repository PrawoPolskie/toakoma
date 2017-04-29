package pl.mojepanstwo.sap.toakoma.writers

import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.ItemWriter
import pl.mojepanstwo.sap.toakoma.IsapModel

class IsapWriter extends ItemWriter[IsapModel] {

  var stepExecution : StepExecution = null

  @Override
  def write(items: java.util.List[_ <: IsapModel] ) = {
    val jobContext = this.stepExecution.getJobExecution.getExecutionContext
    jobContext.put("model", items.get(0))
  }

  @BeforeStep
  def saveStepExecution(stepExecution: StepExecution): Unit = {
    this.stepExecution = stepExecution
  }
}
