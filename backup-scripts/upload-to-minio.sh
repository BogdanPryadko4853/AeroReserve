#!/bin/sh

# Конфигурация - используем переменные окружения или значения по умолчанию
MINIO_ENDPOINT=${MINIO_ENDPOINT:-minio:9000}
MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY:-minioadmin}
MINIO_SECRET_KEY=${MINIO_SECRET_KEY:-minioadmin123}
MINIO_BUCKET=${MINIO_BUCKET:-backups}

if [ -z "$1" ]; then
    echo "Usage: $0 <file_path>"
    exit 1
fi

FILE_PATH=$1
FILE_NAME=$(basename "$FILE_PATH")

echo "Uploading to MinIO: ${FILE_NAME}"
echo "MinIO endpoint: ${MINIO_ENDPOINT}"

# Загружаем файл используя mc
mc cp "$FILE_PATH" "myminio/backups/${FILE_NAME}"

if [ $? -eq 0 ]; then
    echo "Successfully uploaded to MinIO: ${FILE_NAME}"
    exit 0
else
    echo "Failed to upload to MinIO"
    exit 1
fi