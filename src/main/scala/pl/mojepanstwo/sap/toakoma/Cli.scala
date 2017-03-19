package pl.mojepanstwo.sap.toakoma

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.{Option => CliOption}

object Cli {
  
  var options: Options     = new Options()
  var args:    CommandLine = _
  
  object JOB extends Enumeration {
    type JOB = Value
    val PDF_2_XML, 
        ADD_2_DB = Value
  }
  
  {
    options.addOption(CliOption.builder("C")
                               .longOpt("command")
                               .desc("What to do")
                               .hasArgs()
                               .argName(JOB.values.mkString(" | "))
                               .build())
           .addOption("f", "file",     true,  "Plik wejściowy")
           .addOption("m", "metadata", true,  "Plik informacyjny")
           .addOption("c", "config",   true,  "Plik konfiguracyjny")
           .addOption("h", "help",     false, "Ten tekst")
    
           .addOption("s", "status",            true, "Status aktu prawnego")
           .addOption("o", "data_ogloszenia",   true, "Data ogłoszenia")
           .addOption("w", "data_wydania",      true, "Data wydania")
           .addOption("z", "data_wejscia",      true, "Data wejścia w życie")
           .addOption("o", "organ_wydajacy",    true, "Organ wydający")
           .addOption("b", "organ_zobowiazany", true, "Organ zobowiązany")
  }
  
  def parseArgs(args: Array[String]): Boolean = {
    Cli.args = new DefaultParser().parse(options, args)
    if(Cli.args.hasOption("h")) return(false)
    else                        return(true)
  }
  
  def printHelp = {
    new HelpFormatter().printHelp("toakoma", options);
  }
}