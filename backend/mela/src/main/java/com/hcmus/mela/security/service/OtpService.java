package com.hcmus.mela.security.service;

import com.hcmus.mela.model.Otp;
import com.hcmus.mela.model.User;
import com.hcmus.mela.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final Random random = new Random();
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OtpRepository otpRepository;

    public String generateOtpCode(int length) {
        StringBuilder otpCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otpCode.append(this.random.nextInt(10));
        }
        return otpCode.toString();
    }

    public void cacheOtpCode(String otpCode, User user) {
        Otp otp = otpRepository.findByUser(user);
        if (otp == null) {
            otp = new Otp();
        }
        otp.setUser(user);
        otp.setOtpCode(bCryptPasswordEncoder.encode(otpCode));
        otp.setExpireAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));

        otpRepository.save(otp);
    }

    public boolean validateOtpOfUser(String otpCode, String username) {
        Otp otp = otpRepository.findByUserUsername(username);
        if (otp == null) {
            return false;
        }
        if (!bCryptPasswordEncoder.matches(otpCode, otp.getOtpCode())
                || otp.getExpireAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        otpRepository.deleteById(otp.getOtpId());
        return true;
    }
}
