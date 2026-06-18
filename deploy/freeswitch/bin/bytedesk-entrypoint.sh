#!/bin/sh
set -e

FREESWITCH_PREFIX=${FREESWITCH_PREFIX:-/usr/local/freeswitch}
CORE_DB_MODULE=${FS_CORE_DB_MODULE:-mod_mariadb}
CORE_DB_DSN=${FS_CORE_DB_DSN:-}
export CORE_DB_MODULE

echo "[BYTEDESK-ENTRYPOINT] core_db_module=${CORE_DB_MODULE}"
echo "[BYTEDESK-ENTRYPOINT] core_db_dsn=${CORE_DB_DSN}"

for CONF_DIR in "${FREESWITCH_PREFIX}" "${FREESWITCH_PREFIX}/conf" "${FREESWITCH_PREFIX}/etc/freeswitch"; do
  if [ -f "${CONF_DIR}/autoload_configs/modules.conf.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import os
import re
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
module = os.environ.get("CORE_DB_MODULE", "mod_mariadb")
path = conf_dir / "autoload_configs" / "modules.conf.xml"
text = path.read_text(encoding="utf-8")
text = re.sub(r'^\s*<load module="mod_mariadb"\s*/>\s*\n?', '', text, flags=re.M)
text = re.sub(r'^\s*<load module="mod_pgsql"\s*/>\s*\n?', '', text, flags=re.M)
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
module = os.environ.get("CORE_DB_MODULE", "mod_mariadb")
path = conf_dir / "autoload_configs" / "pre_load_modules.conf.xml"
text = path.read_text(encoding="utf-8")
text = re.sub(r'^\s*<load module="mod_mariadb"\s*/>\s*\n?', '', text, flags=re.M)
text = re.sub(r'^\s*<load module="mod_pgsql"\s*/>\s*\n?', '', text, flags=re.M)
text = text.replace('<modules>', f'<modules>\n    <load module="{module}" />')
path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -f "${CONF_DIR}/vars.xml" ]; then
    export CONF_DIR
    python3 - <<'PY'
import os
import re
from pathlib import Path

conf_dir = Path(os.environ.get("CONF_DIR", "/usr/local/freeswitch/conf"))
module = os.environ.get("CORE_DB_MODULE", "mod_mariadb")
dsn = os.environ.get("CORE_DB_DSN", "")
path = conf_dir / "vars.xml"
text = path.read_text(encoding="utf-8")

text = re.sub(
    r'(<X-PRE-PROCESS\s+cmd="set"\s+data=")core_db_module=[^"]*("\s*/>)',
    rf'\1core_db_module={module}\2',
    text,
    flags=re.I,
)

text = re.sub(
    r'\s*<X-PRE-PROCESS\s+cmd="exec-set"\s+data="core_db_module=printenv\s+FS_CORE_DB_MODULE"\s*/>\s*\n?',
    '\n',
    text,
    flags=re.I,
)

if dsn:
    if re.search(r'core_db_dsn=', text):
        text = re.sub(
            r'(<X-PRE-PROCESS\s+cmd="set"\s+data=")core_db_dsn=[^"]*("\s*/>)',
            rf'\1core_db_dsn={dsn}\2',
            text,
            flags=re.I,
        )
    else:
        marker = f'<X-PRE-PROCESS cmd="set" data="core_db_module={module}" />'
        inject = marker + f'\n  <X-PRE-PROCESS cmd="set" data="core_db_dsn={dsn}" />'
        text = text.replace(marker, inject)

text = re.sub(
    r'\s*<X-PRE-PROCESS\s+cmd="exec-set"\s+data="core_db_dsn=printenv\s+FS_CORE_DB_DSN"\s*/>\s*\n?',
    '\n',
    text,
    flags=re.I,
)

path.write_text(text, encoding="utf-8")
PY
  fi

  if [ -n "${CORE_DB_DSN}" ] && [ -f "${CONF_DIR}/autoload_configs/switch.conf.xml" ]; then
    sed -E -i "s#(<param name=\"core-db-dsn\" value=\")[^\"]*(\" */>)#\1${CORE_DB_DSN}\2#" "${CONF_DIR}/autoload_configs/switch.conf.xml"
  fi

  if [ -f "${CONF_DIR}/autoload_configs/event_socket.conf.xml" ]; then
    sed -i 's/listen-ip" value="::"/listen-ip" value="0.0.0.0"/g' "${CONF_DIR}/autoload_configs/event_socket.conf.xml"
  fi

  echo "[BYTEDESK-ENTRYPOINT] patched conf root: ${CONF_DIR}"
  grep -n 'mod_mariadb\|mod_pgsql' "${CONF_DIR}/autoload_configs/pre_load_modules.conf.xml" 2>/dev/null || true
  grep -n 'mod_mariadb\|mod_pgsql' "${CONF_DIR}/autoload_configs/modules.conf.xml" 2>/dev/null || true
  grep -n 'core_db_module\|core_db_dsn' "${CONF_DIR}/vars.xml" 2>/dev/null || true
  grep -n 'core-db-dsn' "${CONF_DIR}/autoload_configs/switch.conf.xml" 2>/dev/null || true
done

if [ "$#" -gt 0 ]; then
  exec "$@"
else
  exec freeswitch -nf
fi
