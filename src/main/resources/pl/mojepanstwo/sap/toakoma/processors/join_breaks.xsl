<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:variable name="class-regex">
        <xsl:text>ff.+\s|y.+\s|x.+\s</xsl:text>
    </xsl:variable>

    <xsl:template match="html:div[ends-with(text()[last()], '-')]" priority="1">
        <xsl:choose>
            <xsl:when test="replace(following-sibling::html:div[1]/@class, $class-regex, '') = replace(@class, $class-regex, '')">
                <xsl:element name="div"
                             namespace="http://www.w3.org/1999/xhtml">
                    <xsl:copy>
                        <xsl:apply-templates select="@*"/>
                    </xsl:copy>
                    <xsl:value-of select="substring(text(), 1, string-length(text()) - 1)"/><xsl:apply-templates select="following-sibling::html:div[1]" mode="copy-text"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="html:div" mode="copy-text">
        <xsl:choose>
            <xsl:when test="ends-with(text()[last()], '-') and replace(following-sibling::html:div[1]/@class, $class-regex, '') = replace(@class, $class-regex, '')">
                <xsl:value-of select="substring(text(), 1, string-length(text()) - 1)"/><xsl:apply-templates select="following-sibling::html:div[1]" mode="copy-text"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="text()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="html:div[preceding-sibling::html:div[1][ends-with(./text()[1], '-')]]" priority="2">
        <xsl:if test="replace(preceding-sibling::html:div[1]/@class, $class-regex, '') != replace(@class, $class-regex, '')">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>