package com.smarthostel.service;

import com.smarthostel.model.entity.Attendance;
import com.smarthostel.model.entity.Student;
import com.smarthostel.repository.AttendanceRepository;
import com.smarthostel.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class QRAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Value("${app.jwt.secret}")
    private String hmacSecret;

    public QRAttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    public String generateQRToken(Long blockId) {
        long timestamp = System.currentTimeMillis();
        String rawPayload = "BLOCK:" + blockId + ":TS:" + timestamp;
        String signature = sign(rawPayload);
        return Base64.getUrlEncoder().encodeToString((rawPayload + ":SIG:" + signature).getBytes(StandardCharsets.UTF_8));
    }

    public Attendance markAttendanceViaQR(Long studentId, String qrToken) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String decoded = new String(Base64.getUrlDecoder().decode(qrToken), StandardCharsets.UTF_8);
        String[] parts = decoded.split(":SIG:");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid QR Code payload");
        }

        String rawPayload = parts[0];
        String signature = parts[1];

        if (!sign(rawPayload).equals(signature)) {
            throw new RuntimeException("QR Code signature verification failed!");
        }

        String[] payloadParts = rawPayload.split(":");
        long timestamp = Long.parseLong(payloadParts[3]);
        if (System.currentTimeMillis() - timestamp > 15 * 60 * 1000) {
            throw new RuntimeException("QR Code has expired! Please scan the live code.");
        }

        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndAttendanceDate(studentId, today);

        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
            attendance.setVerificationMethod(Attendance.VerificationMethod.QR_SCAN);
            attendance.setScannedAt(LocalDateTime.now());
            return attendanceRepository.save(attendance);
        }

        Attendance newAttendance = Attendance.builder()
                .student(student)
                .attendanceDate(today)
                .status(Attendance.AttendanceStatus.PRESENT)
                .verificationMethod(Attendance.VerificationMethod.QR_SCAN)
                .scannedAt(LocalDateTime.now())
                .build();

        return attendanceRepository.save(newAttendance);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error signing QR token", e);
        }
    }
}
