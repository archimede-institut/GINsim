<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="http://www.w3.org/1998/Math/MathML"
                version="1.0">

  <xsl:template match="*">
    <xsl:if test="not(@condition) or @condition='pdf'">
      <xsl:copy>
        <xsl:copy-of select="@*" />
        <xsl:apply-templates />
      </xsl:copy>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
