<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:div[not(normalize-space(text()[1])) and not(*) and
                                   (not(following-sibling::*) or
                                     not(following-sibling::*[normalize-space(text()[1])]))]"
                  priority="1">

    </xsl:template>

</xsl:stylesheet>

