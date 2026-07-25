#!/bin/sh
set -e

FREESWITCH_PREFIX=${FREESWITCH_PREFIX:-/usr/local/freeswitch}
CORE_DB_MODULE=${FS_CORE_DB_MODULE:-}
CORE_DB_DSN=${FS_CORE_DB_DSN:-}
AI_BOT_BASE_URL=${FREESWITCH_AI_BOT_BASE_URL:-}
QWEN_REALTIME_MEDIA_BRIDGE_ENABLED=${FREESWITCH_QWEN_REALTIME_MEDIA_BRIDGE_ENABLED:-}
QWEN_REALTIME_MEDIA_WS_URL=${FREESWITCH_QWEN_REALTIME_MEDIA_WS_URL:-}
MRCP_SERVER_HOST=${FREESWITCH_MRCP_SERVER_HOST:-}
MRCP_SERVER_PORT=${FREESWITCH_MRCP_SERVER_PORT:-}
MRCP_RTP_IP=${FREESWITCH_MRCP_RTP_IP:-}
BAIDU_MRCP_SERVER_HOST=${FREESWITCH_BAIDU_MRCP_SERVER_HOST:-}
BAIDU_MRCP_SIP_PORT=${FREESWITCH_BAIDU_MRCP_SIP_PORT:-}
RECORDINGS_PUBLIC_BASE=${FREESWITCH_RECORDINGS_PUBLIC_BASE:-}
XML_CURL_TOKEN=${FREESWITCH_XML_CURL_TOKEN:-}
FREESWITCH_DOMAIN_OVERRIDE=${FREESWITCH_DOMAIN:-}
EXTERNAL_SIP_IP=${FREESWITCH_EXTERNAL_SIP_IP:-}
EXTERNAL_RTP_IP=${FREESWITCH_EXTERNAL_RTP_IP:-}
RTP_START_PORT=${FREESWITCH_RTP_START_PORT:-}
RTP_END_PORT=${FREESWITCH_RTP_END_PORT:-}
STUN_SERVER=${FREESWITCH_STUN_SERVER:-}
KAMAILIO_IP=${FREESWITCH_KAMAILIO_IP:-}
export CORE_DB_MODULE CORE_DB_DSN AI_BOT_BASE_URL QWEN_REALTIME_MEDIA_BRIDGE_ENABLED QWEN_REALTIME_MEDIA_WS_URL MRCP_SERVER_HOST MRCP_SERVER_PORT MRCP_RTP_IP BAIDU_MRCP_SERVER_HOST BAIDU_MRCP_SIP_PORT RECORDINGS_PUBLIC_BASE XML_CURL_TOKEN FREESWITCH_DOMAIN_OVERRIDE EXTERNAL_SIP_IP EXTERNAL_RTP_IP RTP_START_PORT RTP_END_PORT STUN_SERVER KAMAILIO_IP

echo "[BYTEDESK-ENTRYPOINT] core_db_module=${CORE_DB_MODULE}"
echo "[BYTEDESK-ENTRYPOINT] core_db_dsn=${CORE_DB_DSN}"

for CONF_DIR in "${FREESWITCH_PREFIX}" "${FREESWITCH_PREFIX}/conf" "${FREESWITCH_PREFIX}/etc/freeswitch"; do
  if [ -f "${CONF_DIR}/autoload_configs/modules.conf.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import os
import re
import html
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
module = os.environ.get("CORE_DB_MODULE", "").strip()
path = conf_dir / "autoload_configs" / "modules.conf.xml"
text = path.read_text(encoding="utf-8")
text = re.sub(r'^\s*<load module="mod_mariadb"\s*/>\s*\n?', '', text, flags=re.M)
text = re.sub(r'^\s*<load module="mod_pgsql"\s*/>\s*\n?', '', text, flags=re.M)
if module in {"mod_mariadb", "mod_pgsql"}:
    text = text.replace('<load module="mod_db" />', f'    <load module="{module}" />\n    <load module="mod_db" />')
path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -f "${CONF_DIR}/autoload_configs/pre_load_modules.conf.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import os
import re
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
module = os.environ.get("CORE_DB_MODULE", "").strip()
path = conf_dir / "autoload_configs" / "pre_load_modules.conf.xml"
text = path.read_text(encoding="utf-8")
text = re.sub(r'^\s*<load module="mod_mariadb"\s*/>\s*\n?', '', text, flags=re.M)
text = re.sub(r'^\s*<load module="mod_pgsql"\s*/>\s*\n?', '', text, flags=re.M)
if module in {"mod_mariadb", "mod_pgsql"}:
    text = text.replace('<modules>', f'<modules>\n    <load module="{module}" />')
path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -f "${CONF_DIR}/vars.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import html
import os
import re
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
module = os.environ.get("CORE_DB_MODULE", "").strip()
dsn = os.environ.get("CORE_DB_DSN", "")
path = conf_dir / "vars.xml"
text = path.read_text(encoding="utf-8")

text = re.sub(
  r'\s*<X-PRE-PROCESS\s+cmd="set"\s+data="core_db_module=[^"]*"\s*/>\s*\n?',
  '\n',
  text,
  flags=re.I,
)

text = re.sub(
    r'\s*<X-PRE-PROCESS\s+cmd="exec-set"\s+data="core_db_module=printenv\s+FS_CORE_DB_MODULE"\s*/>\s*\n?',
    '\n',
    text,
    flags=re.I,
)

text = re.sub(
  r'\s*<X-PRE-PROCESS\s+cmd="set"\s+data="core_db_dsn=[^"]*"\s*/>\s*\n?',
  '\n',
  text,
  flags=re.I,
)

text = re.sub(
    r'\s*<X-PRE-PROCESS\s+cmd="exec-set"\s+data="core_db_dsn=printenv\s+FS_CORE_DB_DSN"\s*/>\s*\n?',
    '\n',
    text,
    flags=re.I,
)

if module in {"mod_mariadb", "mod_pgsql"}:
  marker = '<X-PRE-PROCESS cmd="set" data="xml_curl_token=change_me_in_production" />'
  inject = f'  <X-PRE-PROCESS cmd="set" data="core_db_module={module}" />\n'
  if dsn:
    inject += f'  <X-PRE-PROCESS cmd="set" data="core_db_dsn={dsn}" />\n'
  text = text.replace(marker, inject + marker)

overrides = {
  "ai_bot_base_url": os.environ.get("AI_BOT_BASE_URL", ""),
  "qwen_realtime_media_bridge_enabled": os.environ.get("QWEN_REALTIME_MEDIA_BRIDGE_ENABLED", ""),
  "qwen_realtime_media_ws_url": os.environ.get("QWEN_REALTIME_MEDIA_WS_URL", ""),
  "ai_bot_mrcp_server_host": os.environ.get("MRCP_SERVER_HOST", ""),
  "ai_bot_mrcp_reachable_host": os.environ.get("MRCP_SERVER_HOST", ""),
  "ai_bot_mrcp_server_port": os.environ.get("MRCP_SERVER_PORT", ""),
  "ai_bot_mrcp_rtp_ip": os.environ.get("MRCP_RTP_IP", ""),
  "baidu_mrcp_server_host": os.environ.get("BAIDU_MRCP_SERVER_HOST", ""),
  "baidu_mrcp_sip_port": os.environ.get("BAIDU_MRCP_SIP_PORT", ""),
  "recordings_public_base": os.environ.get("RECORDINGS_PUBLIC_BASE", ""),
  "xml_curl_token": os.environ.get("XML_CURL_TOKEN", ""),
  "domain": os.environ.get("FREESWITCH_DOMAIN_OVERRIDE", ""),
  "domain_name": os.environ.get("FREESWITCH_DOMAIN_OVERRIDE", ""),
  "external_sip_ip": os.environ.get("EXTERNAL_SIP_IP", ""),
  "external_rtp_ip": os.environ.get("EXTERNAL_RTP_IP", ""),
  "stun_server": os.environ.get("STUN_SERVER", ""),
}

for key, value in overrides.items():
  if not value:
    continue
  escaped_value = html.escape(value, quote=True)
  text = re.sub(
    rf'(<X-PRE-PROCESS\s+cmd="set"\s+data="){re.escape(key)}=[^"]*("\s*/>)',
    lambda match, k=key, v=escaped_value: f'{match.group(1)}{k}={v}{match.group(2)}',
    text,
    flags=re.I,
  )

path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -n "${QWEN_REALTIME_MEDIA_WS_URL}" ] && [ -f "${CONF_DIR}/dialplan/default/92-ai-bot.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import html
import os
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
ws_url = os.environ.get("QWEN_REALTIME_MEDIA_WS_URL", "").strip()
if not ws_url:
    raise SystemExit(0)

path = conf_dir / "dialplan" / "default" / "92-ai-bot.xml"
text = path.read_text(encoding="utf-8")
text = text.replace("__QWEN_REALTIME_MEDIA_WS_URL__", html.escape(ws_url, quote=True))
path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -n "${CORE_DB_DSN}" ] && [ -f "${CONF_DIR}/autoload_configs/switch.conf.xml" ]; then
    sed -E -i "s#(<param name=\"core-db-dsn\" value=\")[^\"]*(\" */>)#\1${CORE_DB_DSN}\2#" "${CONF_DIR}/autoload_configs/switch.conf.xml"
  fi

  if [ -f "${CONF_DIR}/autoload_configs/switch.conf.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import os
import re
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
path = conf_dir / "autoload_configs" / "switch.conf.xml"
text = path.read_text(encoding="utf-8")

for name, value in {
  "rtp-start-port": os.environ.get("RTP_START_PORT", ""),
  "rtp-end-port": os.environ.get("RTP_END_PORT", ""),
}.items():
  if not value:
    continue
  pattern = rf'\s*<!--\s*<param\s+name="{re.escape(name)}"\s+value="[^"]*"\s*/>\s*-->'
  replacement = f'\n    <param name="{name}" value="{value}" />'
  if re.search(pattern, text, flags=re.I):
    text = re.sub(pattern, replacement, text, count=1, flags=re.I)
  elif re.search(rf'<param\s+name="{re.escape(name)}"\s+value="[^"]*"\s*/>', text, flags=re.I):
    text = re.sub(
      rf'(<param\s+name="{re.escape(name)}"\s+value=")[^"]*("\s*/>)',
      rf'\g<1>{value}\g<2>',
      text,
      count=1,
      flags=re.I,
    )
  else:
    marker = '<!-- RTP port range -->'
    text = text.replace(marker, marker + replacement)

path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -f "${CONF_DIR}/autoload_configs/event_socket.conf.xml" ]; then
    sed -i 's/listen-ip" value="::"/listen-ip" value="0.0.0.0"/g' "${CONF_DIR}/autoload_configs/event_socket.conf.xml"
  fi

  if [ -n "${KAMAILIO_IP}" ] && [ -f "${CONF_DIR}/autoload_configs/acl.conf.xml" ]; then
    python3 - <<'PY'
import os, re
from pathlib import Path
conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
kamailio_ip = os.environ.get("KAMAILIO_IP", "").strip()
if not kamailio_ip:
    raise SystemExit(0)
path = conf_dir / "autoload_configs" / "acl.conf.xml"
text = path.read_text(encoding="utf-8")
# Replace the Kamailio IP in kamailio_only list
text = re.sub(
    r'(<list\s+name="kamailio_only"[^>]*>.*?)<node\s+type="allow"\s+cidr="[0-9.]+/32"\s*/>',
    rf'\1<node type="allow" cidr="{kamailio_ip}/32" />',
    text,
    count=1,
    flags=re.DOTALL,
)
path.write_text(text, encoding="utf-8")
PY
  fi

  echo "[BYTEDESK-ENTRYPOINT] patched conf root: ${CONF_DIR}"
  grep -n 'mod_mariadb\|mod_pgsql' "${CONF_DIR}/autoload_configs/pre_load_modules.conf.xml" 2>/dev/null || true
  grep -n 'mod_mariadb\|mod_pgsql' "${CONF_DIR}/autoload_configs/modules.conf.xml" 2>/dev/null || true
  grep -n 'core_db_module\|core_db_dsn' "${CONF_DIR}/vars.xml" 2>/dev/null || true
  grep -n 'domain=\|domain_name=\|external_sip_ip\|external_rtp_ip' "${CONF_DIR}/vars.xml" 2>/dev/null || true
  grep -n 'core-db-dsn' "${CONF_DIR}/autoload_configs/switch.conf.xml" 2>/dev/null || true
  grep -n 'rtp-start-port\|rtp-end-port' "${CONF_DIR}/autoload_configs/switch.conf.xml" 2>/dev/null || true
done

if [ "$#" -gt 0 ]; then
  exec "$@"
else
  exec freeswitch -nf
fi
