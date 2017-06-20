<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">
    <xsl:strip-space elements="*"/>
    <xsl:output omit-xml-declaration="yes" indent="yes"/>

    <xsl:param name="main-font_size"/>
    <xsl:param name="font_sizes"/>
    <xsl:param name="mode"/>

    <xsl:variable name="main-font"><xsl:value-of select="replace($main-font_size, '&quot;', '')"/></xsl:variable>

    <xsl:template match="/" priority="0">
        <xsl:choose>
            <xsl:when test="$mode = 'TEKST_OGLOSZONY'">
                <xsl:apply-templates select="node()" mode="TEKST_OGLOSZONY"/>
            </xsl:when>
            <xsl:when test="$mode = 'TEKST_AKTU'">
                <xsl:apply-templates select="node()" mode="TEKST_AKTU"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@*|node()" priority="0" mode="TEKST_OGLOSZONY">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="TEKST_OGLOSZONY"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()" priority="0" mode="TEKST_AKTU">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="TEKST_AKTU"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:div[count(tokenize(text()[1], '\d+\)')) > 1 and
                        @fs != preceding-sibling::html:div[last()]/@fs and
                        @fs != following-sibling::html:div[1]/@fs]" priority="1"
                  mode="TEKST_OGLOSZONY">
        <xsl:element name="authorialNoteMark"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="normalize-space(text())"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:div[@fs != $main-font and
                         not(following-sibling::html:div/@fs != @fs)]" priority="2"
                  mode="TEKST_OGLOSZONY">
        <xsl:element name="authorialNote"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="normalize-space(text()[1])"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:div[@fs != $main-font and
                         not(following-sibling::html:div/@fs != @fs)
                         and count(preceding-sibling::html:div) &gt; 0]" priority="2"
                  mode="TEKST_AKTU">
        <xsl:element name="authorialNote"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="normalize-space(text()[1])"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:div[count(html:div) = 1 and
                         following-sibling::html:div[1]/text() = ')' and
                         matches(html:div/text(), '\d')]" priority="2"
                  mode="TEKST_AKTU">
        <xsl:element name="authorialNoteMark"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="normalize-space(html:div/text()[1])"/><xsl:text>)</xsl:text>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:div[text() = ')' and matches(preceding-sibling::html:div[1]/html:div/text(), '\d')]" priority="2"
                  mode="TEKST_AKTU">
    </xsl:template>

</xsl:stylesheet>