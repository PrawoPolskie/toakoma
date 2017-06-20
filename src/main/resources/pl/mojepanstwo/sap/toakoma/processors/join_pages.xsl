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

    <xsl:template match="html:pages" priority="1">
        <xsl:for-each select="html:page/child::*">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <xsl:element name="authorialNotes"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:for-each select="html:page/html:authorialNote[preceding-sibling::*[1][local-name() = 'authorialNoteMark']]">
                <xsl:variable name="first-line" select="."/>
                <xsl:element name="authorialNote"
                             namespace="http://www.w3.org/1999/xhtml">
                    <xsl:attribute name="marker">
                        <xsl:value-of select="preceding-sibling::html:authorialNoteMark[last()]/text()"/>
                    </xsl:attribute>
                    <xsl:element name="line"
                                 namespace="http://www.w3.org/1999/xhtml">
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                    <xsl:for-each select="following-sibling::html:authorialNote">
                        <xsl:element name="line"
                                     namespace="http://www.w3.org/1999/xhtml">
                            <xsl:value-of select="text()"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:authorialNote" priority="2"/>

    <xsl:template match="html:authorialNoteMark[following-sibling::*[1][local-name() = 'authorialNote']]"  priority="2"/>


</xsl:stylesheet>
