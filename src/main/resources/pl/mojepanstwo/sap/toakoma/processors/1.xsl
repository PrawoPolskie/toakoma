<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="comment()" priority="1"/>

    <xsl:template match="html:head" priority="1"/>

    <xsl:template match="html:span[starts-with(@class, '_ _')]"  priority="1"/>

    <xsl:template match="html:span[@class='_ _0']" priority="2">
        <xsl:value-of select="text()"/>
    </xsl:template>

    <xsl:template match="html:span[starts-with(@class, 'ff')]"  priority="1">
        <xsl:value-of select="text()"/>
    </xsl:template>

    <xsl:template match="html:span[starts-with(@class, 'ls')]"  priority="1">
        <xsl:value-of select="text()"/>
    </xsl:template>
</xsl:stylesheet>