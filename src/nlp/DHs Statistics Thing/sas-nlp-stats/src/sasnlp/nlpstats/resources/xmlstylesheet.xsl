<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" method="html"/><xsl:output method="html" doctype-system="http://www.w3.org/TR/html4/strict.dtd" doctype-public="-//W3C//DTD HTML 4.01//EN" indent="yes" />

  <xsl:template match="/">
    <html>
      <head>
        <title>SANLP - nlp - bigram PMI's</title><meta http-equiv="X-UA-Compatible" content="IE=100"/>
        <style type="text/css">
        	body{
        	font-family:"Palatino", serif;
        	}
        	th{
        	font-family:"Avenir Next Condensed", serif;
        	font-weight:100;
        	padding-right:50px;
        	text-transform:uppercase;
        	text-align:left;
        	}
        	h1, h2{
        	font-weight:700;
        	font-family:"Avenir Next Condensed", serif;
        	}
        	td{
        	border-bottom:1px dotted #eaeaea;
        	padding:3px 1px;
        	width:200px;
        	}
        	tr:hover{
        	background:#eaeaea;
        	}
        </style>
      </head>
      <body>
      
      <div style="position:fixed;top:0;left:20px;background:white;padding:5px">
      <a href="#pmi" style="display:block;margin-right:20px;float:left">PMI</a>
      <a href="#ngramtfidf" style="display:block;margin-right:20px;float:left">n-gram TF-IDF</a>
      <a href="#ugramtfidf" style="display:block;margin-right:20px;float:left">word TF-IDF</a>
      </div>
      
      <h1 style="padding-top:50px;">Pointwise Mutual Information for <em>n</em>-grams</h1>
      
      <table name="pmi" id="pmi">
      	<tr>
      		<th></th><th>first word freq.</th><th>second word freq.</th><th>bigram freq.</th><th>PMI</th>
      	</tr>
      	<xsl:for-each select="pmi/bigram">
          <xsl:sort select="@pmi" order="descending" data-type="number" />
          <xsl:if test="@bigramFreq > 5">
            <tr>
            	<td><xsl:value-of select="@bigram"/></td>
            	<td><xsl:value-of select="@firstFreq"/></td>
            	<td><xsl:value-of select="@secondFreq"/></td>
            	<td><xsl:value-of select="@bigramFreq"/></td>
            	<td><xsl:value-of select="@pmi"/></td>
            </tr>
            </xsl:if>
        </xsl:for-each>
      </table>
      
      <h1 name="ngramtfidf" id="ngramtfidf"><em>n</em>-gram Term-Frequency Inverse-Document Frequency (TF-IDF)</h1>
      
      
      	<xsl:for-each select="/pmi/docsNGrams/doc">
      	
      	<div style="float:left;width:46%">
      	<h2>Document: <xsl:value-of select="@name"/></h2>
      	<table>
      	<tr>
      		<th>words</th><th>TF-IDF</th><th>domain spec</th>
      	</tr>
      	
      	<xsl:for-each select="tdidfs/tdidf">
      	
          <xsl:sort select="@tdidf" order="descending" data-type="number" />
            <tr>
            	<td><xsl:value-of select="@word"/></td>
            	<td><xsl:value-of select="@tdidf"/></td>
            	<td><xsl:value-of select="@domainspecificity"/></td>
            </tr>
            
        </xsl:for-each>
        </table>
        
       </div>
        </xsl:for-each>
        
        <div style="clear:both"></div>
     <h1 name="ugramtfidf" id="ugramtfidf" style="margin-top:100px">Single Word Term-Frequency Inverse-Document Frequency (TF-IDF)</h1>
        
        
     	<xsl:for-each select="/pmi/docs/doc">
      	
      	<div style="float:left;width:46%">
      	
      	<h2>Document: <xsl:value-of select="@name"/></h2>
      	<table>
      	<tr>
      		<th>words</th><th>TF-IDF</th><th>domain spec</th>
      	</tr>
      	
      	<xsl:for-each select="tdidfs/tdidf">
      	
          <xsl:sort select="@tdidf" order="descending" data-type="number" />
            <tr>
            	<td><xsl:value-of select="@word"/></td>
            	<td><xsl:value-of select="@tdidf"/></td>
            	<td><xsl:value-of select="@domainspecificity"/></td>
            </tr>
            
        </xsl:for-each>
        </table>
        
       </div>
        </xsl:for-each>
      
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>