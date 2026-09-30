#!/usr/bin/env bash
# 启动项目自用的本机 MySQL（端口 3307），不占用系统已有的 3306。
# 账号 root / 密码 campus，库名 campus_ershou。
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DATA="$ROOT/.data/mysql"
SOCK="$ROOT/.data/mysql.sock"
PID="$ROOT/.data/mysqld.pid"
LOG="$ROOT/.data/mysqld.err"
MYSQLD="${MYSQLD:-/usr/local/mysql/bin/mysqld}"
MYSQL="${MYSQL:-/usr/local/mysql/bin/mysql}"
PORT="${PORT:-3307}"

if [[ ! -x "$MYSQLD" ]]; then
  echo "找不到 mysqld: $MYSQLD" >&2
  exit 1
fi

mkdir -p "$ROOT/.data"

if [[ -f "$PID" ]] && kill -0 "$(cat "$PID")" 2>/dev/null; then
  echo "本机项目 MySQL 已在运行 pid=$(cat "$PID") port=$PORT"
  exit 0
fi

if [[ ! -d "$DATA/mysql" ]]; then
  echo "初始化数据目录 $DATA"
  "$MYSQLD" --initialize-insecure --basedir=/usr/local/mysql --datadir="$DATA"
fi

echo "启动 mysqld 127.0.0.1:$PORT"
nohup "$MYSQLD" \
  --basedir=/usr/local/mysql \
  --datadir="$DATA" \
  --bind-address=127.0.0.1 \
  --port="$PORT" \
  --socket="$SOCK" \
  --pid-file="$PID" \
  --log-error="$LOG" \
  --mysqlx=0 \
  >/dev/null 2>&1 &
disown || true

ready=0
for _ in $(seq 1 60); do
  if "$MYSQL" --protocol=SOCKET -uroot -S "$SOCK" -e "SELECT 1" >/dev/null 2>&1 \
    || "$MYSQL" --protocol=SOCKET -uroot -pcampus -S "$SOCK" -e "SELECT 1" >/dev/null 2>&1; then
    ready=1
    break
  fi
  sleep 0.25
done

if [[ "$ready" -ne 1 ]]; then
  echo "MySQL 启动失败，日志：$LOG" >&2
  tail -40 "$LOG" >&2 || true
  exit 1
fi

mysql_root() {
  if "$MYSQL" --protocol=SOCKET -uroot -pcampus -S "$SOCK" -e "SELECT 1" >/dev/null 2>&1; then
    "$MYSQL" --protocol=SOCKET -uroot -pcampus -S "$SOCK" "$@"
  else
    "$MYSQL" --protocol=SOCKET -uroot -S "$SOCK" "$@"
  fi
}

mysql_root <<'SQL'
CREATE DATABASE IF NOT EXISTS campus_ershou CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'campus';
CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY 'campus';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SQL

echo "就绪：jdbc:mysql://127.0.0.1:${PORT}/campus_ershou  root/campus"
