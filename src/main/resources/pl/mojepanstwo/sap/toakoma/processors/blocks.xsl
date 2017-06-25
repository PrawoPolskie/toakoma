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

    <xsl:template match="html:body" priority="2">
		<xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="html:div[preceding-sibling::*[1]/local-name() = 'title']" priority="2">
        <xsl:element name="main"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:element name="line"
                         namespace="http://www.w3.org/1999/xhtml">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:for-each select="following-sibling::*[local-name() != 'authorialNotes' and
                                                       preceding-sibling::*[local-name() != 'authorialNotes']]">
                <xsl:choose>
                    <xsl:when test="local-name() = 'div'">
                        <xsl:element name="line"
                                     namespace="http://www.w3.org/1999/xhtml">
                            <xsl:value-of select="text()"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy>
                            <xsl:apply-templates select="@*|node()"/>
                        </xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:*[preceding-sibling::*/local-name() = 'title' and
                                local-name() != 'authorialNotes']" priority="1"/>

    <xsl:template match="@m|@x|@h|@y|@fs" priority="2"/>

</xsl:stylesheet>