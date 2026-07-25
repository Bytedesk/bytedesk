local session = session

local function json_escape(value)
  value = value or ""
  value = value:gsub('\\', '\\\\')
  value = value:gsub('"', '\\"')
  return value
end

local function xml_escape(value)
  value = value or ""
  value = value:gsub('&', '&amp;')
  value = value:gsub('<', '&lt;')
  value = value:gsub('>', '&gt;')
  value = value:gsub('"', '&quot;')
  value = value:gsub("'", '&apos;')
  return value
end

local function shell_escape(value)
  value = tostring(value or "")
  return "'" .. value:gsub("'", "'\\''") .. "'"
end

local function json_string(value)
  return '"' .. json_escape(value) .. '"'
end

local function json_object(fields)
  return "{" .. table.concat(fields, ",") .. "}"
end

local function json_unescape(value)
  value = tostring(value or "")
  value = value:gsub('\\"', '"')
  value = value:gsub('\\\\', '\\')
  value = value:gsub('\\/', '/')
  value = value:gsub('\\b', '\b')
  value = value:gsub('\\f', '\f')
  value = value:gsub('\\n', '\n')
  value = value:gsub('\\r', '\r')
  value = value:gsub('\\t', '\t')
  return value
end

local function extract_json_number_field(body, field)
  local pattern = '"' .. field .. '"%s*:%s*(%-?%d+)'
  local value = string.match(body or "", pattern)
  return value and tonumber(value) or nil
end

local function extract_json_string_field(body, field)
  local source = body or ""
  local _, value_start = string.find(source, '"' .. field .. '"%s*:%s*"')
  if value_start then
    local chars = {}
    local escaped = false
    for index = value_start + 1, #source do
      local ch = source:sub(index, index)
      if escaped then
        chars[#chars + 1] = '\\' .. ch
        escaped = false
      elseif ch == '\\' then
        escaped = true
      elseif ch == '"' then
        return json_unescape(table.concat(chars))
      else
        chars[#chars + 1] = ch
      end
    end
  end

  local null_pattern = '"' .. field .. '"%s*:%s*null'
  if string.find(source, null_pattern) then
    return nil
  end
  return nil
end

if not session or not session:ready() then
  freeswitch.consoleLog("WARNING", "[AI-BOT-9205-REALTIME] no ready session for media bridge\n")
  return
end

local uuid = session:get_uuid()
local caller = session:getVariable("caller_id_number") or ""
local arg1 = argv and argv[1] or ""
local arg2 = argv and argv[2] or ""
local arg3 = argv and argv[3] or ""
local arg4 = argv and argv[4] or ""
local arg5 = argv and argv[5] or ""
local arg6 = argv and argv[6] or ""
local arg7 = argv and argv[7] or ""
local arg8 = argv and argv[8] or ""

local function first_non_blank(...)
  local values = {...}
  for _, value in ipairs(values) do
    if value ~= nil and value ~= "" then
      return value
    end
  end
  return ""
end

local function is_ws_url(value)
  return string.match(value or "", "^wss?://") ~= nil
end

local ws_url = first_non_blank(
  session:getVariable("voice_agent_realtime_ws_url"),
  session:getVariable("qwen_realtime_media_ws_url"),
  is_ws_url(arg1) and arg1 or "")
local bot_did = first_non_blank(
  session:getVariable("bot_did"),
  session:getVariable("voice_agent_bot_did"),
  not is_ws_url(arg1) and arg1 or "",
  arg2)
local org_uid = first_non_blank(session:getVariable("org_uid"), arg3)
local conversation_id = first_non_blank(session:getVariable("conversation_id"), arg4, uuid)
local bridge_enabled = first_non_blank(session:getVariable("voice_agent_bridge_enabled"), arg5, "true")
local fallback_destination = first_non_blank(session:getVariable("voice_agent_hotline_fallback_destination"), arg6)
local fallback_context = first_non_blank(session:getVariable("voice_agent_hotline_fallback_context"), arg7, "default")
local ai_bot_base_url = first_non_blank(
  session:getVariable("voice_agent_ai_bot_base_url"),
  session:getVariable("ai_bot_base_url"),
  arg8)
local dynamic_hotline = fallback_destination ~= "" or session:getVariable("voice_agent") == "true"

if ws_url == "" then
  ws_url = "ws://host.docker.internal:9003/visitor/api/v1/call/voice-agent/qwen-realtime/media"
end

if bot_did == "" then
  bot_did = session:getVariable("destination_number") or "9205"
end

local function enabled(value)
  value = string.lower(value or "")
  return value == "true" or value == "1" or value == "yes"
end

local function normalize_base_url(value)
  value = first_non_blank(value)
  if value == "" then
    return ""
  end
  return value:gsub("/$", "")
end

local function normalize_playback_url(value)
  value = first_non_blank(value)
  if value == "" then
    return ""
  end
  value = value:gsub("//localhost:", "//host.docker.internal:")
  value = value:gsub("//127%.0%.0%.1:", "//host.docker.internal:")

  local lower = string.lower(value)
  local lower_without_suffix = lower:gsub("[%?#].*$", "")
  if string.match(lower, "^https?://") and string.sub(lower_without_suffix, -4) == ".mp3" then
    local normalized = value:gsub("^https?://", "")
    return "shout://" .. normalized
  end

  return value
end

local function http_post_json(base_url, path, payload)
  local normalized_base_url = normalize_base_url(base_url)
  if normalized_base_url == "" then
    return nil
  end

  local body = json_object({
    '"orgUid":' .. json_string(payload and payload.orgUid or ""),
    '"did":' .. json_string(payload and payload.did or "")
  })
  local command = table.concat({
    "curl -sS --connect-timeout 5 --max-time 15",
    "-H 'Accept: application/json'",
    "-H 'Content-Type: application/json'",
    "-X POST",
    "--data", shell_escape(body),
    shell_escape(normalized_base_url .. path)
  }, " ")

  local handle = io.popen(command, "r")
  if not handle then
    freeswitch.consoleLog("WARNING", string.format("[AI-HOTLINE-REALTIME] http open failed path=%s\n", path))
    return nil
  end

  local response = handle:read("*a") or ""
  handle:close()
  if response == "" then
    freeswitch.consoleLog("WARNING", string.format("[AI-HOTLINE-REALTIME] http empty response path=%s\n", path))
    return nil
  end

  local code = extract_json_number_field(response, "code") or 500
  if code ~= 200 then
    freeswitch.consoleLog("WARNING", string.format("[AI-HOTLINE-REALTIME] http business failed path=%s code=%s message=%s\n",
      path,
      tostring(code),
      tostring(extract_json_string_field(response, "message"))))
    return nil
  end

  return {
    welcomeType = extract_json_string_field(response, "welcomeType"),
    welcomeText = extract_json_string_field(response, "welcomeText"),
    welcomeAudioUrl = extract_json_string_field(response, "welcomeAudioUrl")
  }
end

local function play_dynamic_hotline_welcome()
  local mrcp_profile = session:getVariable("mrcp_profile") or "java-mrcp"
  local default_text = "您好，我是微语智能语音助手，请问有什么可以帮您的？"
  local welcome = http_post_json(ai_bot_base_url, "/visitor/api/v1/call/voice-agent/welcome", {
    orgUid = org_uid,
    did = bot_did
  })

  local welcome_type = string.upper(first_non_blank(welcome and welcome.welcomeType, "TTS"))
  local welcome_audio_url = normalize_playback_url(welcome and welcome.welcomeAudioUrl)
  local welcome_text = first_non_blank(welcome and welcome.welcomeText, default_text)

  freeswitch.consoleLog("INFO", string.format("[AI-HOTLINE-REALTIME] welcome resolved uuid=%s did=%s type=%s rawAudioUrl=%s playbackUrl=%s\n",
    uuid,
    bot_did,
    welcome_type,
    first_non_blank(welcome and welcome.welcomeAudioUrl),
    welcome_audio_url))

  if welcome_type == "AUDIO" and welcome_audio_url ~= "" then
    session:execute("playback", welcome_audio_url)
    freeswitch.consoleLog("INFO", string.format("[AI-HOTLINE-REALTIME] played audio welcome uuid=%s did=%s audioUrl=%s\n", uuid, bot_did, welcome_audio_url))
    return
  end

  session:execute("speak", "unimrcp:" .. mrcp_profile .. "||<speak version='1.0' xml:lang='zh-CN'>" .. xml_escape(welcome_text) .. "</speak>")
  freeswitch.consoleLog("INFO", string.format("[AI-HOTLINE-REALTIME] played tts welcome uuid=%s did=%s welcomeType=%s textLength=%s\n",
    uuid,
    bot_did,
    welcome_type,
    tostring(string.len(welcome_text))))
end

local function run_fallback_entry()
  if fallback_destination == "" or ai_bot_base_url == "" then
    freeswitch.consoleLog("WARNING", string.format("[AI-HOTLINE-REALTIME] bridge disabled but fallback is incomplete uuid=%s did=%s\n", uuid, bot_did))
    return
  end
  local httapi = string.format("{url=%s/ai-bot?turn=1&mode=unlimited&bot_did=%s&org_uid=%s&voice_agent=true&conversation_id=%s,method=POST}",
    ai_bot_base_url, bot_did, org_uid, conversation_id)
  session:execute("playback", "tone_stream://%(300,1000,440);loops=1")
  session:execute("httapi", httapi)
  session:execute("sleep", "200")
  session:execute("transfer", string.format("%s XML %s", fallback_destination, fallback_context))
end

if dynamic_hotline and not enabled(bridge_enabled) then
  freeswitch.consoleLog("INFO", string.format("[AI-HOTLINE-REALTIME] bridge disabled, fallback to httapi loop uuid=%s did=%s orgUid=%s\n", uuid, bot_did, org_uid))
  run_fallback_entry()
  return
end

if dynamic_hotline then
  play_dynamic_hotline_welcome()
end

local metadata = string.format('{"uuid":"%s","caller":"%s","botDid":"%s","orgUid":"%s","conversationId":"%s"}',
  json_escape(uuid), json_escape(caller), json_escape(bot_did), json_escape(org_uid), json_escape(conversation_id))
local command = string.format("uuid_audio_stream %s start %s mono 16k %s", uuid, ws_url, metadata)
local api = freeswitch.API()
local result = api:executeString(command) or ""

freeswitch.consoleLog("INFO", string.format("[AI-HOTLINE-REALTIME] media bridge start uuid=%s did=%s orgUid=%s ws=%s result=%s\n", uuid, bot_did, org_uid, ws_url, result))

if dynamic_hotline then
  session:execute("park", "")
end