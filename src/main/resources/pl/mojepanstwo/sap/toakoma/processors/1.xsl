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

    <xsl:template match="html:span" priority="1">
        <xsl:apply-templates select="child::node()"/>
        <!--<xsl:for-each select="child::text()">-->
            <!--&lt;!&ndash;<xsl:if test="self::text()">&ndash;&gt;-->
                <!--&lt;!&ndash;<xsl:value-of select="."/>&ndash;&gt;-->
            <!--&lt;!&ndash;</xsl:if>&ndash;&gt;-->

        <!--</xsl:for-each>-->
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>

    <!--<xsl:template match="html:span[starts-with(@class, '_ _') and number(substring(@class,4,4))]" priority="1"/>-->

    <!--<xsl:template match="html:span[@class='_ _0']" priority="2">-->
        <!--<xsl:value-of select="text()"/>-->
    <!--</xsl:template>-->

    <!--<xsl:template match="html:span[starts-with(@class, 'ff')]" priority="1">-->
        <!--<xsl:value-of select="text()"/>-->
    <!--</xsl:template>-->

    <!--<xsl:template match="html:span[starts-with(@class, 'ls')]" priority="1">-->
        <!--<xsl:value-of select="text()"/>-->
    <!--</xsl:template>-->
</xsl:stylesheet>