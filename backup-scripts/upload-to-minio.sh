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

# Конфигурируем AWS CLI для MinIO
export AWS_ACCESS_KEY_ID=$MINIO_ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=$MINIO_SECRET_KEY

# Создаем бакет если не существует
aws --endpoint-url "http://${MINIO_ENDPOINT}" s3 mb "s3://${MINIO_BUCKET}" 2>/dev/null || true

# Загружаем файл
aws --endpoint-url "http://${MINIO_ENDPOINT}" s3 cp "$FILE_PATH" "s3://${MINIO_BUCKET}/${FILE_NAME}"

if [ $? -eq 0 ]; then
    echo "Successfully uploaded to MinIO: ${FILE_NAME}"
    exit 0
else
    echo "Failed to upload to MinIO"
    exit 1
fi