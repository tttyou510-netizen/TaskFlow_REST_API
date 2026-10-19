package com.example.TaskFlow.API.service;

import com.example.TaskFlow.API.entity.Attachment;
import com.example.TaskFlow.API.entity.Task;
import com.example.TaskFlow.API.exception.ResourceNotFoundException;
import com.example.TaskFlow.API.repository.AttachmentRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AttachmentService {
    private static final Pattern FILENAME_PATTERN =

            Pattern.compile("^[a-zA-Z0-9_\\s.-]+\\.(jpg|jpeg|png|pdf|docx|txt)$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Value("${file.upload-dir:uploads/attachments}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + uploadDir, e);
        }
    }

    @Transactional
    public Attachment uploadAttachment(Long taskId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file path sequence in file name: " + originalFileName);
        }

        // التحقق من صحة الاسم والامتداد باستخدام Regex
        if (!FILENAME_PATTERN.matcher(originalFileName).matches()) {
            throw new IllegalArgumentException("Invalid file name or unsupported format. Allowed extensions: jpg, jpeg, png, pdf, docx, txt");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        Path targetLocation = this.uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = new Attachment();
        attachment.setOriginalFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);
        attachment.setFilePath(targetLocation.toString());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setTask(task);

        return attachmentRepository.save(attachment);
    }

    public List<Attachment> getAttachmentsByTaskId(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return attachmentRepository.findByTaskId(taskId);
    }

    public Attachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
    }

    public Resource loadFileAsResource(Long attachmentId) {
        Attachment attachment = getAttachmentById(attachmentId);
        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or not readable: " + attachment.getOriginalFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading file: " + attachment.getOriginalFileName(), e);
        }
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = getAttachmentById(attachmentId);
        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
        }
        attachmentRepository.delete(attachment);
    }
}
