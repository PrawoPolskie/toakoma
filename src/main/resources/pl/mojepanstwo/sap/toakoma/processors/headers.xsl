<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:variable name="dzu"><xsl:text>Dziennik Ustaw\s+–\s+.*\s+–\s+Poz.\s+.*\s*</xsl:text></xsl:variable>
    <xsl:variable name="ks"><xsl:text>©Kancelaria Sejmu\s+s.\s+.*</xsl:text></xsl:variable>
    <xsl:variable name="dt"><xsl:text>\d\d\d\d\-\d\d\-\d\d</xsl:text></xsl:variable>
    <xsl:variable name="dzu2"><xsl:text>Dz\.U\.\s\d\d\d\d poz\.\s\d+</xsl:text></xsl:variable>

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:div[not(normalize-space(replace(
                                                      replace(
                                                      replace(
                                                      replace(text()[1], $dzu, ''),
                                                                         $ks, ''),
                                                                         $dt, ''),
                                                                         $dzu2, ''))) and not(*) and
                                   (not(preceding-sibling::*) or
                                     not(preceding-sibling::*[normalize-space(replace(
                                                                              replace(
                                                                              replace(
                                                                              replace(text()[1], $dzu, ''),
                                                                                                 $ks, ''),
                                                                                                 $dt, ''),
                                                                                                 $dzu2, ''))]))]"
                  priority="1">

    </xsl:template>

</xsl:stylesheet>

