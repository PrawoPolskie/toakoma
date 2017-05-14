<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:div[@data-page-no]">
        <xsl:element name="page"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="nb">
                <xsl:value-of select="@data-page-no"/>
            </xsl:attribute>

            <xsl:choose>
                <xsl:when test="count(html:div) = 1">
                    <xsl:for-each select="html:div/child::*">
                        <xsl:apply-templates select="."/>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy>
                        <xsl:apply-templates select="*"/>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:img[starts-with(@src, 'bg')
                     and count(preceding-sibling::*)=0]" priority="1"/>

</xsl:stylesheet>