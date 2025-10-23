package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.service.backup.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления резервным копированием и восстановлением данных
 * Требует роль ADMIN для доступа ко всем endpoint'ам
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequestMapping("api/admin/backup")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    /**
     * Отображает страницу управления резервными копиями
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона панели администратора
     */
    @GetMapping
    public String backupPage(Model model) {
        model.addAttribute("backups", backupService.listBackups());
        return "admin/dashboard";
    }

    /**
     * Создает новую резервную копию базы данных
     *
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createBackup() {
        try {
            String result = backupService.createBackup();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Backup failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Восстанавливает базу данных из указанной резервной копии
     *
     * @param request Map с именем файла резервной копии
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/restore")
    @ResponseBody
    public ResponseEntity<Map<String, String>> restoreBackup(@RequestBody Map<String, String> request) {
        try {
            String backupFile = request.get("backupFile");
            if (backupFile == null || backupFile.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Backup file name is required");
                return ResponseEntity.badRequest().body(response);
            }

            String result = backupService.restoreBackup(backupFile);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Restore failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Возвращает список доступных резервных копий
     *
     * @return ResponseEntity со списком имен резервных копий
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<String>> listBackups() {
        try {
            List<String> backups = backupService.listBackups();
            return ResponseEntity.ok(backups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }
}