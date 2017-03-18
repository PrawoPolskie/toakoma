package pl.mojepanstwo.sap.toakoma

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter

object Cli {
  
  var options: Options     = new Options()
  var args:    CommandLine = _
  
  {
    options.addOption("f", "file",     true,  "Plik wejściowy")
    options.addOption("m", "metadata", true,  "Plik informacyjny")
    options.addOption("c", "config",   true,  "Plik konfiguracyjny")
    options.addOption("h", "help",     false, "Ten tekst")
    
    options.addOption("s", "status",            true, "Status aktu prawnego")
    options.addOption("o", "data_ogloszenia",   true, "Data ogłoszenia")
    options.addOption("w", "data_wydania",      true, "Data wydania")
    options.addOption("z", "data_wejscia",      true, "Data wejścia w życie")
    options.addOption("o", "organ_wydajacy",    true, "Organ wydający")
    options.addOption("b", "organ_zobowiazany", true, "Organ zobowiązany")
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