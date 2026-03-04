#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

detect_platform() {
  arch="$(uname -m 2>/dev/null || echo unknown)"
  case "$arch" in
    arm64|aarch64) echo "linux/arm64" ;;
    x86_64|amd64) echo "linux/amd64" ;;
    *) echo "linux/amd64" ;;
  esac
}

# Allow:
# - EC_DEMO_PLATFORM=linux/amd64 (explicit)
# - EC_DEMO_PLATFORM=linux/arm64 (explicit)
# - EC_DEMO_PLATFORM=auto (recommended for sharing)
if [ -z "${EC_DEMO_PLATFORM:-}" ] || [ "${EC_DEMO_PLATFORM:-}" = "auto" ]; then
  EC_DEMO_PLATFORM="$(detect_platform)"
  export EC_DEMO_PLATFORM
fi

cd "$SCRIPT_DIR"
exec docker compose --env-file .env -f docker-compose-env.yml "$@"
