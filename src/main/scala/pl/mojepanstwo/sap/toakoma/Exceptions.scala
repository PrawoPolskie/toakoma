package pl.mojepanstwo.sap.toakoma

case class NoSuchDocumentException(private val message: String = "",
                                   private val cause: Throwable = None.orNull)
                                   extends RuntimeException(message, cause)