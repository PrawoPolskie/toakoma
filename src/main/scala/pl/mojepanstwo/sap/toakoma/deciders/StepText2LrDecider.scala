package pl.mojepanstwo.sap.toakoma.deciders

import org.springframework.batch.core.{JobExecution, StepExecution}
import org.springframework.batch.core.job.flow.{FlowExecutionStatus, JobExecutionDecider}
import pl.mojepanstwo.sap.toakoma.{IsapModel, Pdf}

class StepText2LrDecider(pdf:Pdf.Value) extends JobExecutionDecider {

  override def decide(jobExecution : JobExecution,
                      stepExecution : StepExecution) : FlowExecutionStatus = {
    val jobContext = jobExecution.getExecutionContext
    val model = jobContext.get("model").asInstanceOf[IsapModel]

    if(model.texts.contains(pdf)) return new FlowExecutionStatus("EXIST")
    else                          return new FlowExecutionStatus("END")
  }
}
