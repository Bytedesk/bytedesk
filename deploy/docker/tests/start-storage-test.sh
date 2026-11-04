#!/usr/bin/env bash
set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "${TEST_DIR}"' EXIT
mkdir -p "${TEST_DIR}/compose" "${TEST_DIR}/bin"
cp "${DEPLOY_DIR}/start.sh" "${TEST_DIR}/start.sh"
touch "${TEST_DIR}/.env"
for component in redis elasticsearch mysql artemis bytedesk minio rustfs; do
  touch "${TEST_DIR}/compose/compose-${component}.yaml"
done

cat > "${TEST_DIR}/bin/docker" <<'EOF'
#!/usr/bin/env bash
printf '%s\n' "$*" >> "${DOCKER_TEST_LOG}"
EOF
chmod +x "${TEST_DIR}/bin/docker"
export PATH="${TEST_DIR}/bin:${PATH}"
export DOCKER_TEST_LOG="${TEST_DIR}/docker.log"
unset BYTEDESK_STORAGE_PROVIDER

bash "${TEST_DIR}/start.sh" > "${TEST_DIR}/output.log"
grep -q '^BYTEDESK_STORAGE_PROVIDER=local$' "${TEST_DIR}/.env.app"
if grep -Eq 'compose-(minio|rustfs)\.yaml' "${DOCKER_TEST_LOG}"; then
  echo "FAIL: default startup selected object storage containers" >&2
  exit 1
fi

for component in minio rustfs; do
  : > "${DOCKER_TEST_LOG}"
  bash "${TEST_DIR}/start.sh" all "${component}" > "${TEST_DIR}/output.log"
  grep -q "compose-${component}.yaml" "${DOCKER_TEST_LOG}"
  grep -q '^BYTEDESK_STORAGE_PROVIDER=local$' "${TEST_DIR}/.env.app"
done

BYTEDESK_STORAGE_PROVIDER=s3 bash "${TEST_DIR}/start.sh" > "${TEST_DIR}/output.log"
grep -q '^BYTEDESK_STORAGE_PROVIDER=s3$' "${TEST_DIR}/.env.app"

printf '%s\n' 'BYTEDESK_STORAGE_PROVIDER=s3' > "${TEST_DIR}/.env"
bash "${TEST_DIR}/start.sh" > "${TEST_DIR}/output.log"
if grep -q '^BYTEDESK_STORAGE_PROVIDER=' "${TEST_DIR}/.env.app"; then
  echo "FAIL: generated defaults override explicit .env configuration" >&2
  exit 1
fi
echo "PASS: default local storage, opt-in containers, and explicit storage overrides"
