local function trim(value)
  if not value then
    return ""
  end

  return (value:gsub("^%s+", ""):gsub("%s+$", ""))
end

local function xml_unescape(value)
  local text = value or ""

  text = text:gsub("&lt;", "<")
  text = text:gsub("&gt;", ">")
  text = text:gsub("&quot;", '"')
  text = text:gsub("&apos;", "'")
  text = text:gsub("&amp;", "&")

  return text
end

local function ssml_escape(value)
  local text = value or ""

  text = text:gsub("&", "&amp;")
  text = text:gsub("<", "&lt;")
  text = text:gsub(">", "&gt;")
  text = text:gsub('"', "&quot;")
  text = text:gsub("'", "&apos;")

  return text
end

local function strip_vendor_prefix(value)
  return (value or ""):gsub("^%[[^%]]+%]", "")
end

local function first_non_empty(...)
  for index = 1, select("#", ...) do
    local candidate = trim(select(index, ...))

    if candidate ~= "" then
      return candidate
    end
  end

  return ""
end

local function extract_from_xml(xml)
  local text = first_non_empty(
    xml:match("<input[^>]*>(.-)</input>"),
    xml:match("<speech%-to%-text[^>]*>(.-)</speech%-to%-text>")
  )

  if text == "" then
    return ""
  end

  text = text:gsub("<!%[CDATA%[(.-)%]%]>", "%1")
  text = strip_vendor_prefix(xml_unescape(trim(text)))

  return trim(text)
end

if not session or not session:ready() then
  return
end

local asr_text = trim(session:getVariable("asr_text"))
local asr_text_ssml = trim(session:getVariable("asr_text_ssml"))

if asr_text ~= "" then
  if asr_text_ssml == "" then
    session:setVariable("asr_text_ssml", ssml_escape(asr_text))
  end

  return
end

local raw_result = first_non_empty(
  session:getVariable("detect_speech_result_text"),
  session:getVariable("RECOG_RESULT"),
  session:getVariable("speech_detection_result"),
  session:getVariable("detect_speech_result"),
  session:getVariable("detect_speech_results")
)

if raw_result == "" then
  session:setVariable("asr_text", "")
  session:setVariable("asr_text_ssml", "")
  return
end

local text

if raw_result:find("<", 1, true) then
  text = extract_from_xml(raw_result)
else
  text = trim(strip_vendor_prefix(xml_unescape(raw_result)))
end

if text == "" then
  session:setVariable("asr_text", "")
  session:setVariable("asr_text_ssml", "")
  return
end

session:setVariable("asr_text", text)
session:setVariable("asr_text_ssml", ssml_escape(text))