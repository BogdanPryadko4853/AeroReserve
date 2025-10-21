#!/bin/sh

# Конфигурация - используем переменные окружения или значения по умолчанию
DB_HOST=${DB_HOST:-postgres}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-aeroreserve_db}
DB_USER=${DB_USER:-aeroreserve_user}
DB_PASS=${DB_PASS:-password}
MINIO_ENDPOINT=${MINIO_ENDPOINT:-minio:9000}
MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY:-minioadmin}
MINIO_SECRET_KEY=${MINIO_SECRET_KEY:-minioadmin123}
MINIO_BUCKET=${MINIO_BUCKET:-backups}

if [ -z "$1" ]; then
    echo "Usage: $0 <backup_file_name>"
    echo "Available backups:"
    aws --endpoint-url http://${MINIO_ENDPOINT} s3 ls s3://${MINIO_BUCKET}/ || exit 1
    exit 1
fi

BACKUP_FILE=$1
LOCAL_PATH="/backups/${BACKUP_FILE}"
LOCAL_DECOMPRESSED_PATH="/backups/${BACKUP_FILE%.gz}"

echo "Restoring from backup: ${BACKUP_FILE}"
echo "Database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"

# Скачиваем из MinIO
export AWS_ACCESS_KEY_ID=$MINIO_ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=$MINIO_SECRET_KEY

aws --endpoint-url http://${MINIO_ENDPOINT} s3 cp "s3://${MINIO_BUCKET}/${BACKUP_FILE}" $LOCAL_PATH

if [ $? -ne 0 ]; then
    echo "Failed to download backup from MinIO"
    exit 1
fi

echo "Backup downloaded from MinIO"

# Распаковываем если нужно
if echo "$BACKUP_FILE" | grep -q "\.gz$"; then
    echo "Decompressing backup..."
    gunzip $LOCAL_PATH
    LOCAL_PATH=$LOCAL_DECOMPRESSED_PATH
    BACKUP_FILE="${BACKUP_FILE%.gz}"
fi

# Восстанавливаем базу данных
export PGPASSWORD=$DB_PASS

echo "Terminating existing connections..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '${DB_NAME}' AND pid <> pg_backend_pid();" 2>/dev/null || true

echo "Dropping existing database..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "DROP DATABASE IF EXISTS ${DB_NAME};"

echo "Creating new database..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "CREATE DATABASE ${DB_NAME};"

echo "Restoring data..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $LOCAL_PATH

if [ $? -eq 0 ]; then
    echo "Database restored successfully from: ${BACKUP_FILE}"
    rm -f $LOCAL_PATH
else
    echo "Database restore failed!"
    exit 1
fi