#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PID="$ROOT/.data/mysqld.pid"
if [[ -f "$PID" ]] && kill -0 "$(cat "$PID")" 2>/dev/null; then
  kill "$(cat "$PID")"
  echo "已停止项目 MySQL pid=$(cat "$PID")"
else
  echo "项目 MySQL 未在运行"
fi
