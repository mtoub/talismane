[#ftl]
[#list sentence as unit]
[#if unit.token.precedingRawOutput??]
${unit.token.precedingRawOutput}
[/#if]
${(unit.token.index+1)?c}	${unit.token.textForCoNLL}	${unit.lemmaForCoNLL}	${unit.tag.code}	${(unit.lexicalEntry.category)!"_"}	${(unit.lexicalEntry.morphologyForCoNLL)!"_"}	${unit.token.fileName}	${(unit.token.lineNumber)?c}	${(unit.token.columnNumber)?c}	
[/#list]

