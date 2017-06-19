<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@class" priority="1">
        <xsl:if test="matches(., '.*(m[^\s]+).*')">
            <xsl:attribute name="m">
                <xsl:value-of select="replace(replace(., '.*(m[^\s]+).*', '$1'), 'm', '')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="matches(., '.*(x[^\s]+).*')">
            <xsl:attribute name="x">
                <xsl:value-of select="replace(replace(., '.*(x[^\s]+).*', '$1'), 'x', '')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="matches(., '.*(h[^\s]+).*')">
            <xsl:attribute name="h">
                <xsl:value-of select="replace(replace(., '.*(h[^\s]+).*', '$1'), 'h', '')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="matches(., '.*(y[^\s]+).*')">
            <xsl:attribute name="y">
                <xsl:value-of select="replace(replace(., '.*(y[^\s]+).*', '$1'), 'y', '')"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="matches(., '.*(fs[^\s]+).*')">
            <xsl:attribute name="fs">
                <xsl:value-of select="replace(replace(., '.*(fs[^\s]+).*', '$1'), 'fs', '')"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
