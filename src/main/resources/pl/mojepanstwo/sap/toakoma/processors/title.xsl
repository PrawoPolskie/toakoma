<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">
    <xsl:strip-space elements="*"/>
    <xsl:output omit-xml-declaration="yes" indent="yes"/>

    <xsl:param name="title"/>

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:body/html:div[not(preceding-sibling::*)]" priority="2">
        <xsl:element name="title"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:element name="line"
                         namespace="http://www.w3.org/1999/xhtml">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:for-each select="following-sibling::html:*[not(preceding-sibling::html:div[string-length(text()) != 0 and
                                                                                            ends-with($title, normalize-space(text()))])]">
                <xsl:choose>
                    <xsl:when test="name() = 'div'">
		                <xsl:element name="line"
		                             namespace="http://www.w3.org/1999/xhtml">
		                    <xsl:value-of select="text()"/>
		                </xsl:element>
		            </xsl:when>
		            <xsl:otherwise>
		            	<xsl:copy-of select="."/>
		            </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:body/html:*[not(preceding-sibling::html:div[string-length(text()) != 0 and
                                                                          ends-with(normalize-space($title), normalize-space(text()))])]" priority="1"/>
</xsl:stylesheet>
