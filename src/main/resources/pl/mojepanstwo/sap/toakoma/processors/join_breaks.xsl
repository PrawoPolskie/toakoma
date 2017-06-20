<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">
    <xsl:strip-space elements="*"/>
    <xsl:output omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:*[(local-name() = 'div' or
                                 local-name() = 'line') and
                                ends-with(text()[last()], '-')]" priority="1">
        <xsl:choose>
            <xsl:when test="following-sibling::*[./local-name() = local-name()][1]/@h = @h and
                            following-sibling::*[./local-name() = local-name()][1]/@fs = @fs">
                <xsl:element name="{local-name()}"
                             namespace="http://www.w3.org/1999/xhtml">
                    <xsl:apply-templates select="@*"/>
                    <xsl:value-of select="substring(text(), 1, string-length(text()) - 1)"/><xsl:apply-templates select="following-sibling::*[./local-name() = local-name()][1]" mode="copy-text"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="html:*[local-name() = 'div' or
                                local-name() = 'line']" mode="copy-text">
        <xsl:choose>
            <xsl:when test="ends-with(text()[last()], '-') and
                            following-sibling::*[./local-name() = local-name()][1]/@h = @h and
                            following-sibling::*[./local-name() = local-name()][1]/@fs = @fs">
                <xsl:value-of select="substring(text(), 1, string-length(text()) - 1)"/><xsl:apply-templates select="following-sibling::*[./local-name() = local-name()][1]" mode="copy-text"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="text()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="html:*[(local-name() = 'div' or
                                 local-name() = 'line') and
                                preceding-sibling::*[./local-name() = local-name()][1][ends-with(./text()[1], '-')]]" priority="2">
        <xsl:if test="preceding-sibling::*[./local-name() = local-name()][1]/@h != @h and
                      preceding-sibling::*[./local-name() = local-name()][1]/@fs != @fs">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>