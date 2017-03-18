package pl.mojepanstwo.sap.toakoma

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter

object Cli {
  
  var options: Options     = new Options()
  var args:    CommandLine = _
  
  {
    options.addOption("input", true, "Plik wejściowy")
    options.addOption("config", true, "Plik konfiguracyjny")
    
    options.addOption("status", true, "Status aktu prawnego")
    options.addOption("a", true, "Data ogłoszenia")
    options.addOption("b", true, "Data wydania")
    options.addOption("c", true, "Data wejścia w życie")
    options.addOption("d", true, "Organ wydający")
    options.addOption("e", true, "Organ zobowiązany")
  }
  
  def parseArgs(args: Array[String]): Boolean = {
    val parser = new DefaultParser()
    Cli.args = parser.parse(options, args)
    true
  }
  
  def printHelp = {
    new HelpFormatter().printHelp("toakoma", options);
  }
}