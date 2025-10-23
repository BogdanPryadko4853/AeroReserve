#!/bin/sh

# Конфигурация
DB_HOST=${DB_HOST:-postgres}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-aeroreserve_db}
DB_USER=${DB_USER:-aeroreserve_user}
DB_PASS=${DB_PASS:-password}

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="backup_${DB_NAME}_${TIMESTAMP}.sql"
BACKUP_PATH="/backups/${BACKUP_FILE}"
COMPRESSED_FILE="${BACKUP_FILE}.gz"

echo "Starting database backup: ${BACKUP_FILE}"
echo "Database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"

# Проверяем доступность pg_dump
if ! command -v pg_dump &> /dev/null; then
    echo "ERROR: pg_dump not found!"
    exit 1
fi

# Создаем дамп PostgreSQL
export PGPASSWORD=$DB_PASS
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -F p > "$BACKUP_PATH"

if [ $? -ne 0 ]; then
    echo "Database dump failed!"
    exit 1
fi

echo "Database dump created: ${BACKUP_FILE}"

# Сжимаем бэкап
gzip "$BACKUP_PATH"
echo "Backup compressed: ${COMPRESSED_FILE}"

# Загружаем в MinIO используя AWS CLI
/bin/sh /backup-scripts/upload-to-minio.sh "/backups/${COMPRESSED_FILE}"

if [ $? -eq 0 ]; then
    echo "Backup completed successfully: ${COMPRESSED_FILE}"
    rm "/backups/${COMPRESSED_FILE}"
    echo "Local backup cleaned up"
else
    echo "Backup created but MinIO upload failed"
    exit 1
fi