<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:import href="/usr/share/xml/docbook/stylesheet/nwalsh/xhtml/profile-docbook.xsl"/>

<xsl:param name="html.stylesheet" select="'ginsim.css'"/>
<xsl:param name="navig.graphics"  select="1"/>
<xsl:param name="admon.graphics" select="1"/>
<xsl:param name="navig.graphics.path" select="'images/'"/>
<xsl:param name="admon.graphics.path" select="'images/'"/>
<xsl:param name="navig.graphics.extension" select="'.png'"/>
<xsl:param name="admon.graphics.extension" select="'.png'"/>
<xsl:param name="chunker.output.indent" select="'yes'"/>
<xsl:param name="target.database.document" select="'targetList-single.db'"/>
<!-- <xsl:param name="collect.xref.targets" select="'no'"/> -->

<!-- <xsl:param name="profile.revision" select="'2.0'"/> -->


<xsl:template name="user.head.content">
  <script type="text/javascript" src="scripts/ginsim.js"></script>
  <link rel="alternate stylesheet" title="print" type="text/css" media="print,screen,projection" href="ginsim-print.css"/>

</xsl:template>

</xsl:stylesheet>
