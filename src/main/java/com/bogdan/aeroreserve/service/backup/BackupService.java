package com.bogdan.aeroreserve.service.backup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BackupService {

    /**
     * Создание бэкапа через Docker
     */
    public String createBackup() {
        try {
            log.info("🔄 Starting database backup creation");

            // Команда выполняется на локальной машине, обращаясь к Docker контейнеру
            String command = "docker exec backup-service /backup-scripts/backup.sh";

            Process process = Runtime.getRuntime().exec(command);
            String result = readProcessOutput(process, "Backup");

            log.info("✅ Backup completed: {}", result);
            return result;

        } catch (Exception e) {
            log.error("❌ Error creating backup", e);
            return "Error creating backup: " + e.getMessage();
        }
    }

    /**
     * Восстановление из бэкапа
     */
    public String restoreBackup(String backupFileName) {
        try {
            log.info("🔄 Starting database restore from: {}", backupFileName);

            String command = String.format(
                    "docker exec backup-service /backup-scripts/restore.sh %s",
                    backupFileName
            );

            Process process = Runtime.getRuntime().exec(command);
            String result = readProcessOutput(process, "Restore");

            log.info("✅ Restore completed: {}", result);
            return result;

        } catch (Exception e) {
            log.error("❌ Error restoring backup", e);
            return "Error restoring backup: " + e.getMessage();
        }
    }

    /**
     * Получение списка бэкапов из MinIO
     */
    public List<String> listBackups() {
        List<String> backups = new ArrayList<>();
        try {
            String command = "docker exec backup-service aws --endpoint-url http://minio:9000 s3 ls s3://backups/";

            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        String fileName = parts[3];
                        if (fileName.startsWith("backup_") && (fileName.endsWith(".sql") || fileName.endsWith(".gz"))) {
                            backups.add(fileName);
                        }
                    }
                }
            }

            backups.sort((a, b) -> b.compareTo(a));
            log.info("📋 Found {} backup files", backups.size());

        } catch (Exception e) {
            log.error("❌ Error listing backups", e);
        }
        return backups;
    }

    private String readProcessOutput(Process process, String operation) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            log.info("{}: {}", operation, line);
        }

        while ((line = errorReader.readLine()) != null) {
            output.append("ERROR: ").append(line).append("\n");
            log.error("{} ERROR: {}", operation, line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            return operation + " completed successfully:\n" + output;
        } else {
            return operation + " failed with exit code " + exitCode + ":\n" + output;
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledBackup() {
        log.info("🔄 Starting scheduled database backup");
        String result = createBackup();
        log.info("📋 Scheduled backup result: {}", result);
    }
}