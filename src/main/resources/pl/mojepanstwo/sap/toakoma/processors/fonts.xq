xquery version "3.1";

declare namespace html = "http://www.w3.org/1999/xhtml";
declare namespace map = "http://www.w3.org/2005/xpath-functions/map";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:method "adaptive";

declare variable $font-sizes := //@fs[parent::html:div];

map:merge(for $x in
(
  for $font-size in distinct-values($font-sizes)
  where $font-size != ''
  order by count($font-sizes[. = $font-size]) descending
  return <x>{$font-size}</x>
)
return map:entry($x, count($font-sizes[. = $x])))
