package pl.mojepanstwo.sap.toakoma.deciders

import org.springframework.batch.core.{JobExecution, StepExecution}
import org.springframework.batch.core.job.flow.{FlowExecutionStatus, JobExecutionDecider}
import pl.mojepanstwo.sap.toakoma._

class StepText2LrDecider(pdf:Pdf.Value) extends JobExecutionDecider {

  override def decide(jobExecution : JobExecution,
                      stepExecution : StepExecution) : FlowExecutionStatus = {
    val jobContext = jobExecution.getExecutionContext
    val model = jobContext.get("model").asInstanceOf[Model]

    if(model.linksPdf.get(pdf).isDefined) return new FlowExecutionStatus("EXIST")
    else                                  return new FlowExecutionStatus("END")
  }
}
