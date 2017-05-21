<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:pages" priority="1">
        <xsl:for-each select="html:page/child::*">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>