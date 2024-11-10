"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[88,7602,4514],{7602:function(e,t,n){n.r(t);var a=n(45817);const s=Object.freeze(JSON.parse('{"displayName":"GLSL","fileTypes":["vs","fs","gs","vsh","fsh","gsh","vshader","fshader","gshader","vert","frag","geom","f.glsl","v.glsl","g.glsl"],"foldingStartMarker":"/\\\\*\\\\*|\\\\{\\\\s*$","foldingStopMarker":"\\\\*\\\\*/|^\\\\s*\\\\}","name":"glsl","patterns":[{"match":"\\\\b(break|case|continue|default|discard|do|else|for|if|return|switch|while)\\\\b","name":"keyword.control.glsl"},{"match":"\\\\b(void|bool|int|uint|float|vec2|vec3|vec4|bvec2|bvec3|bvec4|ivec2|ivec2|ivec3|uvec2|uvec2|uvec3|mat2|mat3|mat4|mat2x2|mat2x3|mat2x4|mat3x2|mat3x3|mat3x4|mat4x2|mat4x3|mat4x4|sampler[1|2|3]D|samplerCube|sampler2DRect|sampler[1|2]DShadow|sampler2DRectShadow|sampler[1|2]DArray|sampler[1|2]DArrayShadow|samplerBuffer|sampler2DMS|sampler2DMSArray|struct|isampler[1|2|3]D|isamplerCube|isampler2DRect|isampler[1|2]DArray|isamplerBuffer|isampler2DMS|isampler2DMSArray|usampler[1|2|3]D|usamplerCube|usampler2DRect|usampler[1|2]DArray|usamplerBuffer|usampler2DMS|usampler2DMSArray)\\\\b","name":"storage.type.glsl"},{"match":"\\\\b(attribute|centroid|const|flat|in|inout|invariant|noperspective|out|smooth|uniform|varying)\\\\b","name":"storage.modifier.glsl"},{"match":"\\\\b(gl_BackColor|gl_BackLightModelProduct|gl_BackLightProduct|gl_BackMaterial|gl_BackSecondaryColor|gl_ClipDistance|gl_ClipPlane|gl_ClipVertex|gl_Color|gl_DepthRange|gl_DepthRangeParameters|gl_EyePlaneQ|gl_EyePlaneR|gl_EyePlaneS|gl_EyePlaneT|gl_Fog|gl_FogCoord|gl_FogFragCoord|gl_FogParameters|gl_FragColor|gl_FragCoord|gl_FragDat|gl_FragDept|gl_FrontColor|gl_FrontFacing|gl_FrontLightModelProduct|gl_FrontLightProduct|gl_FrontMaterial|gl_FrontSecondaryColor|gl_InstanceID|gl_Layer|gl_LightModel|gl_LightModelParameters|gl_LightModelProducts|gl_LightProducts|gl_LightSource|gl_LightSourceParameters|gl_MaterialParameters|gl_ModelViewMatrix|gl_ModelViewMatrixInverse|gl_ModelViewMatrixInverseTranspose|gl_ModelViewMatrixTranspose|gl_ModelViewProjectionMatrix|gl_ModelViewProjectionMatrixInverse|gl_ModelViewProjectionMatrixInverseTranspose|gl_ModelViewProjectionMatrixTranspose|gl_MultiTexCoord[0-7]|gl_Normal|gl_NormalMatrix|gl_NormalScale|gl_ObjectPlaneQ|gl_ObjectPlaneR|gl_ObjectPlaneS|gl_ObjectPlaneT|gl_Point|gl_PointCoord|gl_PointParameters|gl_PointSize|gl_Position|gl_PrimitiveIDIn|gl_ProjectionMatrix|gl_ProjectionMatrixInverse|gl_ProjectionMatrixInverseTranspose|gl_ProjectionMatrixTranspose|gl_SecondaryColor|gl_TexCoord|gl_TextureEnvColor|gl_TextureMatrix|gl_TextureMatrixInverse|gl_TextureMatrixInverseTranspose|gl_TextureMatrixTranspose|gl_Vertex|gl_VertexIDh)\\\\b","name":"support.variable.glsl"},{"match":"\\\\b(gl_MaxClipPlanes|gl_MaxCombinedTextureImageUnits|gl_MaxDrawBuffers|gl_MaxFragmentUniformComponents|gl_MaxLights|gl_MaxTextureCoords|gl_MaxTextureImageUnits|gl_MaxTextureUnits|gl_MaxVaryingFloats|gl_MaxVertexAttribs|gl_MaxVertexTextureImageUnits|gl_MaxVertexUniformComponents)\\\\b","name":"support.constant.glsl"},{"match":"\\\\b(abs|acos|all|any|asin|atan|ceil|clamp|cos|cross|degrees|dFdx|dFdy|distance|dot|equal|exp|exp2|faceforward|floor|fract|ftransform|fwidth|greaterThan|greaterThanEqual|inversesqrt|length|lessThan|lessThanEqual|log|log2|matrixCompMult|max|min|mix|mod|noise[1-4]|normalize|not|notEqual|outerProduct|pow|radians|reflect|refract|shadow1D|shadow1DLod|shadow1DProj|shadow1DProjLod|shadow2D|shadow2DLod|shadow2DProj|shadow2DProjLod|sign|sin|smoothstep|sqrt|step|tan|texture1D|texture1DLod|texture1DProj|texture1DProjLod|texture2D|texture2DLod|texture2DProj|texture2DProjLod|texture3D|texture3DLod|texture3DProj|texture3DProjLod|textureCube|textureCubeLod|transpose)\\\\b","name":"support.function.glsl"},{"match":"\\\\b(asm|double|enum|extern|goto|inline|long|short|sizeof|static|typedef|union|unsigned|volatile)\\\\b","name":"invalid.illegal.glsl"},{"include":"source.c"}],"scopeName":"source.glsl","embeddedLangs":["c"]}'));t.default=[...a.default,s]},70088:function(e,t,n){n.r(t),n.d(t,{default:function(){return y}});var a=n(6014),s=n(92432),i=n(96840);const l=Object.freeze(JSON.parse('{"fileTypes":["js","jsx","ts","tsx","html","vue","svelte","php","res"],"injectTo":["source.ts","source.js"],"injectionSelector":"L:source.js -comment -string, L:source.js -comment -string, L:source.jsx -comment -string,  L:source.js.jsx -comment -string, L:source.ts -comment -string, L:source.tsx -comment -string, L:source.rescript -comment -string, L:source.vue -comment -string, L:source.svelte -comment -string, L:source.php -comment -string, L:source.rescript -comment -string","injections":{"L:source":{"patterns":[{"match":"<","name":"invalid.illegal.bad-angle-bracket.html"}]}},"name":"es-tag-css","patterns":[{"begin":"(?i)(\\\\s?\\\\/\\\\*\\\\s?(css|inline-css)\\\\s?\\\\*\\\\/\\\\s?)(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.css"},{"include":"inline.es6-htmlx#template"}]},{"begin":"(?i)(\\\\s*(css|inline-css))(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.css"},{"include":"inline.es6-htmlx#template"},{"include":"string.quoted.other.template.js"}]},{"begin":"(?i)(?<=\\\\s|\\\\,|=|:|\\\\(|\\\\$\\\\()\\\\s{0,}(((\\\\/\\\\*)|(\\\\/\\\\/))\\\\s?(css|inline-css)[ ]{0,1000}\\\\*?\\\\/?)[ ]{0,1000}$","beginCaptures":{"1":{"name":"comment.line"}},"end":"(`).*","patterns":[{"begin":"(\\\\G)","end":"(`)"},{"include":"source.ts#template-substitution-element"},{"include":"source.css"}]},{"begin":"(\\\\${)","beginCaptures":{"1":{"name":"entity.name.tag"}},"end":"(})","endCaptures":{"1":{"name":"entity.name.tag"}},"patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.js"}]}],"scopeName":"inline.es6-css","embeddedLangs":["typescript","css","javascript"]}'));var r=[...a.default,...s.default,...i.default,l],m=n(7602);const o=Object.freeze(JSON.parse('{"fileTypes":["js","jsx","ts","tsx","html","vue","svelte","php","res"],"injectTo":["source.ts","source.js"],"injectionSelector":"L:source.js -comment -string, L:source.js -comment -string, L:source.jsx -comment -string,  L:source.js.jsx -comment -string, L:source.ts -comment -string, L:source.tsx -comment -string, L:source.rescript -comment -string","injections":{"L:source":{"patterns":[{"match":"<","name":"invalid.illegal.bad-angle-bracket.html"}]}},"name":"es-tag-glsl","patterns":[{"begin":"(?i)(\\\\s?\\\\/\\\\*\\\\s?(glsl|inline-glsl)\\\\s?\\\\*\\\\/\\\\s?)(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.glsl"},{"include":"inline.es6-htmlx#template"}]},{"begin":"(?i)(\\\\s*(glsl|inline-glsl))(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.glsl"},{"include":"inline.es6-htmlx#template"},{"include":"string.quoted.other.template.js"}]},{"begin":"(?i)(?<=\\\\s|\\\\,|=|:|\\\\(|\\\\$\\\\()\\\\s{0,}(((\\\\/\\\\*)|(\\\\/\\\\/))\\\\s?(glsl|inline-glsl)[ ]{0,1000}\\\\*?\\\\/?)[ ]{0,1000}$","beginCaptures":{"1":{"name":"comment.line"}},"end":"(`).*","patterns":[{"begin":"(\\\\G)","end":"(`)"},{"include":"source.ts#template-substitution-element"},{"include":"source.glsl"}]},{"begin":"(\\\\${)","beginCaptures":{"1":{"name":"entity.name.tag"}},"end":"(})","endCaptures":{"1":{"name":"entity.name.tag"}},"patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.js"}]}],"scopeName":"inline.es6-glsl","embeddedLangs":["typescript","glsl","javascript"]}'));var c=[...a.default,...m.default,...i.default,o],u=n(35582);const g=Object.freeze(JSON.parse('{"fileTypes":["js","jsx","ts","tsx","html","vue","svelte","php","res"],"injectTo":["source.ts","source.js"],"injectionSelector":"L:source.js -comment -string, L:source.js -comment -string, L:source.jsx -comment -string,  L:source.js.jsx -comment -string, L:source.ts -comment -string, L:source.tsx -comment -string, L:source.rescript -comment -string","injections":{"L:source":{"patterns":[{"match":"<","name":"invalid.illegal.bad-angle-bracket.html"}]}},"name":"es-tag-html","patterns":[{"begin":"(?i)(\\\\s?\\\\/\\\\*\\\\s?(html|template|inline-html|inline-template)\\\\s?\\\\*\\\\/\\\\s?)(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"text.html.basic"},{"include":"inline.es6-htmlx#template"}]},{"begin":"(?i)(\\\\s*(html|template|inline-html|inline-template))(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"text.html.basic"},{"include":"inline.es6-htmlx#template"},{"include":"string.quoted.other.template.js"}]},{"begin":"(?i)(?<=\\\\s|\\\\,|=|:|\\\\(|\\\\$\\\\()\\\\s{0,}(((\\\\/\\\\*)|(\\\\/\\\\/))\\\\s?(html|template|inline-html|inline-template)[ ]{0,1000}\\\\*?\\\\/?)[ ]{0,1000}$","beginCaptures":{"1":{"name":"comment.line"}},"end":"(`).*","patterns":[{"begin":"(\\\\G)","end":"(`)"},{"include":"source.ts#template-substitution-element"},{"include":"text.html.basic"}]},{"begin":"(\\\\${)","beginCaptures":{"1":{"name":"entity.name.tag"}},"end":"(})","endCaptures":{"1":{"name":"entity.name.tag"}},"patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.js"}]},{"begin":"(\\\\$\\\\(`)","beginCaptures":{"1":{"name":"entity.name.tag"}},"end":"(`\\\\))","endCaptures":{"1":{"name":"entity.name.tag"}},"patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.js"}]}],"scopeName":"inline.es6-html","embeddedLangs":["typescript","html","javascript"]}'));var d=[...a.default,...u.default,...i.default,g],p=n(37257);const x=Object.freeze(JSON.parse('{"fileTypes":["js","jsx","ts","tsx","html","vue","svelte","php","res"],"injectTo":["source.ts","source.js"],"injectionSelector":"L:source.js -comment -string, L:source.jsx -comment -string,  L:source.js.jsx -comment -string, L:source.ts -comment -string, L:source.tsx -comment -string, L:source.rescript -comment -string","injections":{"L:source":{"patterns":[{"match":"<","name":"invalid.illegal.bad-angle-bracket.html"}]}},"name":"es-tag-sql","patterns":[{"begin":"(?i)\\\\b(\\\\w+\\\\.sql)\\\\s*(`)","beginCaptures":{"1":{"name":"variable.parameter"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.ts#string-character-escape"},{"include":"source.sql"},{"include":"source.plpgsql.postgres"},{"match":"."}]},{"begin":"(?i)(\\\\s?\\\\/?\\\\*?\\\\s?(sql|inline-sql)\\\\s?\\\\*?\\\\/?\\\\s?)(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"source.ts#template-substitution-element"},{"include":"source.ts#string-character-escape"},{"include":"source.sql"},{"include":"source.plpgsql.postgres"},{"match":"."}]},{"begin":"(?i)(?<=\\\\s|\\\\,|=|:|\\\\(|\\\\$\\\\()\\\\s{0,}(((\\\\/\\\\*)|(\\\\/\\\\/))\\\\s?(sql|inline-sql)[ ]{0,1000}\\\\*?\\\\/?)[ ]{0,1000}$","beginCaptures":{"1":{"name":"comment.line"}},"end":"(`)","patterns":[{"begin":"(\\\\G)","end":"(`)"},{"include":"source.ts#template-substitution-element"},{"include":"source.ts#string-character-escape"},{"include":"source.sql"},{"include":"source.plpgsql.postgres"},{"match":"."}]}],"scopeName":"inline.es6-sql","embeddedLangs":["typescript","sql"]}'));var b=[...a.default,...p.default,x],h=n(94514);const f=Object.freeze(JSON.parse('{"fileTypes":["js","jsx","ts","tsx","html","vue","svelte","php","res"],"injectTo":["source.ts","source.js"],"injectionSelector":"L:source.js -comment -string, L:source.js -comment -string, L:source.jsx -comment -string,  L:source.js.jsx -comment -string, L:source.ts -comment -string, L:source.tsx -comment -string, L:source.rescript -comment -string","injections":{"L:source":{"patterns":[{"match":"<","name":"invalid.illegal.bad-angle-bracket.html"}]}},"name":"es-tag-xml","patterns":[{"begin":"(?i)(\\\\s?\\\\/\\\\*\\\\s?(xml|svg|inline-svg|inline-xml)\\\\s?\\\\*\\\\/\\\\s?)(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"text.xml"}]},{"begin":"(?i)(\\\\s*(xml|inline-xml))(`)","beginCaptures":{"1":{"name":"comment.block"}},"end":"(`)","patterns":[{"include":"text.xml"}]},{"begin":"(?i)(?<=\\\\s|\\\\,|=|:|\\\\(|\\\\$\\\\()\\\\s{0,}(((\\\\/\\\\*)|(\\\\/\\\\/))\\\\s?(xml|svg|inline-svg|inline-xml)[ ]{0,1000}\\\\*?\\\\/?)[ ]{0,1000}$","beginCaptures":{"1":{"name":"comment.line"}},"end":"(`).*","patterns":[{"begin":"(\\\\G)","end":"(`)"},{"include":"text.xml"}]}],"scopeName":"inline.es6-xml","embeddedLangs":["xml"]}'));var j=[...h.default,f];const _=Object.freeze(JSON.parse('{"displayName":"TypeScript with Tags","name":"ts-tags","patterns":[{"include":"source.ts"}],"scopeName":"source.ts.tags","embeddedLangs":["typescript","es-tag-css","es-tag-glsl","es-tag-html","es-tag-sql","es-tag-xml"],"aliases":["lit"]}'));var y=[...a.default,...r,...c,...d,...b,...j,_]},94514:function(e,t,n){n.r(t);var a=n(70066);const s=Object.freeze(JSON.parse('{"displayName":"XML","name":"xml","patterns":[{"begin":"(<\\\\?)\\\\s*([-_a-zA-Z0-9]+)","captures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"entity.name.tag.xml"}},"end":"(\\\\?>)","name":"meta.tag.preprocessor.xml","patterns":[{"match":" ([a-zA-Z-]+)","name":"entity.other.attribute-name.xml"},{"include":"#doublequotedString"},{"include":"#singlequotedString"}]},{"begin":"(<!)(DOCTYPE)\\\\s+([:a-zA-Z_][:a-zA-Z0-9_.-]*)","captures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"keyword.other.doctype.xml"},"3":{"name":"variable.language.documentroot.xml"}},"end":"\\\\s*(>)","name":"meta.tag.sgml.doctype.xml","patterns":[{"include":"#internalSubset"}]},{"include":"#comments"},{"begin":"(<)((?:([-_a-zA-Z0-9]+)(:))?([-_a-zA-Z0-9:]+))(?=(\\\\s[^>]*)?></\\\\2>)","beginCaptures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"entity.name.tag.xml"},"3":{"name":"entity.name.tag.namespace.xml"},"4":{"name":"punctuation.separator.namespace.xml"},"5":{"name":"entity.name.tag.localname.xml"}},"end":"(>)(</)((?:([-_a-zA-Z0-9]+)(:))?([-_a-zA-Z0-9:]+))(>)","endCaptures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"punctuation.definition.tag.xml"},"3":{"name":"entity.name.tag.xml"},"4":{"name":"entity.name.tag.namespace.xml"},"5":{"name":"punctuation.separator.namespace.xml"},"6":{"name":"entity.name.tag.localname.xml"},"7":{"name":"punctuation.definition.tag.xml"}},"name":"meta.tag.no-content.xml","patterns":[{"include":"#tagStuff"}]},{"begin":"(</?)(?:([-\\\\w\\\\.]+)((:)))?([-\\\\w\\\\.:]+)","captures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"entity.name.tag.namespace.xml"},"3":{"name":"entity.name.tag.xml"},"4":{"name":"punctuation.separator.namespace.xml"},"5":{"name":"entity.name.tag.localname.xml"}},"end":"(/?>)","name":"meta.tag.xml","patterns":[{"include":"#tagStuff"}]},{"include":"#entity"},{"include":"#bare-ampersand"},{"begin":"<%@","beginCaptures":{"0":{"name":"punctuation.section.embedded.begin.xml"}},"end":"%>","endCaptures":{"0":{"name":"punctuation.section.embedded.end.xml"}},"name":"source.java-props.embedded.xml","patterns":[{"match":"page|include|taglib","name":"keyword.other.page-props.xml"}]},{"begin":"<%[!=]?(?!--)","beginCaptures":{"0":{"name":"punctuation.section.embedded.begin.xml"}},"end":"(?!--)%>","endCaptures":{"0":{"name":"punctuation.section.embedded.end.xml"}},"name":"source.java.embedded.xml","patterns":[{"include":"source.java"}]},{"begin":"<!\\\\[CDATA\\\\[","beginCaptures":{"0":{"name":"punctuation.definition.string.begin.xml"}},"end":"]]>","endCaptures":{"0":{"name":"punctuation.definition.string.end.xml"}},"name":"string.unquoted.cdata.xml"}],"repository":{"EntityDecl":{"begin":"(<!)(ENTITY)\\\\s+(%\\\\s+)?([:a-zA-Z_][:a-zA-Z0-9_.-]*)(\\\\s+(?:SYSTEM|PUBLIC)\\\\s+)?","captures":{"1":{"name":"punctuation.definition.tag.xml"},"2":{"name":"keyword.other.entity.xml"},"3":{"name":"punctuation.definition.entity.xml"},"4":{"name":"variable.language.entity.xml"},"5":{"name":"keyword.other.entitytype.xml"}},"end":"(>)","patterns":[{"include":"#doublequotedString"},{"include":"#singlequotedString"}]},"bare-ampersand":{"match":"&","name":"invalid.illegal.bad-ampersand.xml"},"comments":{"patterns":[{"begin":"<%--","captures":{"0":{"name":"punctuation.definition.comment.xml"},"end":"--%>","name":"comment.block.xml"}},{"begin":"\x3c!--","captures":{"0":{"name":"punctuation.definition.comment.xml"}},"end":"--\x3e","name":"comment.block.xml","patterns":[{"begin":"--(?!>)","captures":{"0":{"name":"invalid.illegal.bad-comments-or-CDATA.xml"}}}]}]},"doublequotedString":{"begin":"\\"","beginCaptures":{"0":{"name":"punctuation.definition.string.begin.xml"}},"end":"\\"","endCaptures":{"0":{"name":"punctuation.definition.string.end.xml"}},"name":"string.quoted.double.xml","patterns":[{"include":"#entity"},{"include":"#bare-ampersand"}]},"entity":{"captures":{"1":{"name":"punctuation.definition.constant.xml"},"3":{"name":"punctuation.definition.constant.xml"}},"match":"(&)([:a-zA-Z_][:a-zA-Z0-9_.-]*|#\\\\d+|#x[0-9a-fA-F]+)(;)","name":"constant.character.entity.xml"},"internalSubset":{"begin":"(\\\\[)","captures":{"1":{"name":"punctuation.definition.constant.xml"}},"end":"(\\\\])","name":"meta.internalsubset.xml","patterns":[{"include":"#EntityDecl"},{"include":"#parameterEntity"},{"include":"#comments"}]},"parameterEntity":{"captures":{"1":{"name":"punctuation.definition.constant.xml"},"3":{"name":"punctuation.definition.constant.xml"}},"match":"(%)([:a-zA-Z_][:a-zA-Z0-9_.-]*)(;)","name":"constant.character.parameter-entity.xml"},"singlequotedString":{"begin":"\'","beginCaptures":{"0":{"name":"punctuation.definition.string.begin.xml"}},"end":"\'","endCaptures":{"0":{"name":"punctuation.definition.string.end.xml"}},"name":"string.quoted.single.xml","patterns":[{"include":"#entity"},{"include":"#bare-ampersand"}]},"tagStuff":{"patterns":[{"captures":{"1":{"name":"entity.other.attribute-name.namespace.xml"},"2":{"name":"entity.other.attribute-name.xml"},"3":{"name":"punctuation.separator.namespace.xml"},"4":{"name":"entity.other.attribute-name.localname.xml"}},"match":"(?:^|\\\\s+)(?:([-\\\\w.]+)((:)))?([-\\\\w.:]+)\\\\s*="},{"include":"#doublequotedString"},{"include":"#singlequotedString"}]}},"scopeName":"text.xml","embeddedLangs":["java"]}'));t.default=[...a.default,s]}}]);