package pl.mojepanstwo.sap.toakoma

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.{Option => CliOption}

object Cli {

  var options: Options     = new Options()
  var args:    CommandLine = _

  val ID_ALL = "ALL"

  object OPT extends Enumeration {
    type OPT = Value
    val command,
        id,
        config,
        help = Value
  }

  object JOB extends Enumeration {
    type JOB = Value
    val ISAP_TO_AKOMA,
        TO_DB = Value
  }

  {
    options.addOption(CliOption.builder("C")
                               .longOpt(OPT.command.toString)
                               .desc("What to do")
                               .hasArgs()
                               .argName(JOB.values.mkString(" | "))
                               .build())
           .addOption("i", OPT.id.toString,     true,  "ID from ISAP")
           .addOption("c", OPT.config.toString, true,  "Plik konfiguracyjny")
           .addOption("h", OPT.help.toString,   false, "Ten tekst")
  }

  def parseArgs(args: Array[String]): Boolean = {
    Cli.args = new DefaultParser().parse(options, args)
    if(Cli.args.hasOption(OPT.help.toString)) return(false)
    else                                      return(true)
  }

  def printHelp = {
    new HelpFormatter().printHelp("toakoma", options);
  }
}