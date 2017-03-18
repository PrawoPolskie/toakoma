package pl.mojepanstwo.sap.toakoma

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine

object Cli {
  
  def defineOptions : Options = {
    val options = new Options()
    
    options.addOption("file", true, "Plik wejściowy")
    
    options.addOption("status", true, "Status aktu prawnego")
    options.addOption("a", true, "Data ogłoszenia")
    options.addOption("b", true, "Data wydania")
    options.addOption("c", true, "Data wejścia w życie")
    options.addOption("d", true, "Organ wydający")
    options.addOption("e", true, "Organ zobowiązany")
    
    options
  }
  
  def parseArgs(args: Array[String]) : CommandLine = {
    val parser = new DefaultParser()
    parser.parse(defineOptions, args)
  }  
}