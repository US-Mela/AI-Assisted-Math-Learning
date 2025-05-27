package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.dto.EmailDetailsDto;
import com.hcmus.mela.auth.dto.request.ForgotPasswordRequest;
import com.hcmus.mela.auth.dto.request.OtpConfirmationRequest;
import com.hcmus.mela.auth.dto.request.ResetPasswordRequest;
import com.hcmus.mela.auth.dto.response.ForgotPasswordResponse;
import com.hcmus.mela.auth.dto.response.OtpConfirmationResponse;
import com.hcmus.mela.auth.dto.response.ResetPasswordResponse;
import com.hcmus.mela.auth.exception.ForgotPasswordException;
import com.hcmus.mela.auth.exception.InvalidTokenException;
import com.hcmus.mela.auth.exception.UserNotFoundException;
import com.hcmus.mela.auth.model.User;
import com.hcmus.mela.auth.security.jwt.JwtTokenForgotPasswordService;
import com.hcmus.mela.shared.utils.ExceptionMessageAccessor;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final ExceptionMessageAccessor exceptionMessageAccessor;

    private final GeneralMessageAccessor generalMessageAccessor;

    private final AuthService authService;

    private final OtpService otpService;

    private final EmailService emailService;

    private final JwtTokenForgotPasswordService jwtTokenForgotPasswordService;

    @Override
    public ForgotPasswordResponse sendOtpCodeByEmail(ForgotPasswordRequest forgotPasswordRequest) {
        User user = authService.findByUsername(forgotPasswordRequest.getUsername());
        if (user == null) {
            throw new UserNotFoundException(
                    "Sending otp via email failed! "
                            + exceptionMessageAccessor.getMessage(
                            null,
                            "username_not_found",
                            forgotPasswordRequest.getUsername()
                    )
            );
        }

        String otpCode = otpService.generateOtpCode(6);
        String otpMessage = emailService.generateOtpNotify(user.getUsername(), otpCode);
        EmailDetailsDto details = EmailDetailsDto.builder()
                .recipient(user.getUsername())
                .subject("Xác thực OTP - Quên mật khẩu")
                .msgBody(otpMessage)
                .build();

        otpService.cacheOtpCode(otpCode, user);
        emailService.sendSimpleMail(details);
        return new ForgotPasswordResponse(
                generalMessageAccessor.getMessage(
                        null,
                        "send_email_success",
                        forgotPasswordRequest.getUsername()
                )
        );
    }

    @Override
    public OtpConfirmationResponse validateOtp(OtpConfirmationRequest otpConfirmationRequest) {
        User user = authService.findByUsername(otpConfirmationRequest.getUsername());
        if (otpService.validateOtpOfUser(otpConfirmationRequest.getOtpCode(), user.getUserId())) {
            String token = jwtTokenForgotPasswordService.generateToken(otpConfirmationRequest.getUsername());
            OtpConfirmationResponse otpConfirmationResponse = new OtpConfirmationResponse();
            otpConfirmationResponse.setUsername(otpConfirmationRequest.getUsername());
            otpConfirmationResponse.setToken(token);
            otpConfirmationResponse.setMessage(
                    generalMessageAccessor.getMessage(
                            null,
                            "otp_validation_success"
                    )
            );
            return otpConfirmationResponse;
        }
        throw new ForgotPasswordException(
                exceptionMessageAccessor.getMessage(
                        null,
                        "otp_validation_fail",
                        otpConfirmationRequest.getOtpCode()
                )
        );
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (jwtTokenForgotPasswordService.validateToken(
                resetPasswordRequest.getToken(),
                resetPasswordRequest.getUsername()
        )) {
            authService.updatePassword(resetPasswordRequest.getUsername(), resetPasswordRequest.getNewPassword());
            return new ResetPasswordResponse(
                    generalMessageAccessor.getMessage(null, "reset_pw_success")
            );
        }
        throw new InvalidTokenException(
                exceptionMessageAccessor.getMessage(null, "reset_pw_fail")
        );
    }
}
