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

    <xsl:template match="html:div[@class='loading-indicator']" priority="1"/>

    <xsl:template match="html:div[@class='pi']" priority="1"/>

    <xsl:template match="html:span" priority="1">
        <xsl:apply-templates select="child::node()"/>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>

</xsl:stylesheet>