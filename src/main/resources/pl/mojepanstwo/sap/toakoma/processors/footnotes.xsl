<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml">

    <xsl:param name="main-font_size"/>
    <xsl:param name="font_sizes"/>

    <xsl:variable name="main-font"><xsl:value-of select="replace($main-font_size, '&quot;', '')"/></xsl:variable>


    <xsl:variable name="fs"><xsl:text>.*?(fs\d+).*</xsl:text></xsl:variable>
    <xsl:variable name="d"><xsl:text>[^0-9]+</xsl:text></xsl:variable>

    <xsl:template match="@*|node()" priority="0">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:div[count(tokenize(text()[1], '\d+\)')) > 1 and
                        replace(replace(@class, $fs, '$1'), $d, '') != replace(replace(preceding-sibling::html:div[last()]/@class, $fs, '$1'), $d, '') and
                        replace(replace(@class, $fs, '$1'), $d, '') != replace(replace(following-sibling::html:div[1]/@class, $fs, '$1'), $d, '')]" priority="1">
        <xsl:element name="authorialNoteMark"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@class"/>
            <xsl:value-of select="normalize-space(text())"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="html:div[replace(replace(@class, $fs, '$1'), $d, '') != $main-font and
                         not(following-sibling::html:div/replace(replace(@class, $fs, '$1'), $d, '') != replace(replace(@class, $fs, '$1'), $d, ''))]" priority="2">
        <xsl:element name="authorialNote"
                     namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@class"/>
            <xsl:value-of select="normalize-space(text()[1])"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>