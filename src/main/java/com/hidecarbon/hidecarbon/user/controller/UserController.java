package com.hidecarbon.hidecarbon.user.controller;

import com.hidecarbon.hidecarbon.user.model.Member;
import com.hidecarbon.hidecarbon.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @Data
    public static class SignUpRequest {
        private String email;
        private String password;
        private String username;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class EmailRequest {
        private String email;
    }

    @Data
    public static class UpdateProfileRequest {
        private String newUsername;
        private String newPassword;
    }

    @Data
    public static class VerificationRequest {
        private String email;
        private String code;
    }

    @Data
    public static class TokenRequest {
        private String refreshToken;
    }

    // 회원가입
    // Todo : 예외처리 세분화
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws Exception{
        try {
            if (!userService.isVerified(signUpRequest.getEmail())) {
                throw new RuntimeException("Unverified email address");
            }
            Member member = userService.signUp(signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getUsername());
            return ResponseEntity.ok(member);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400
        }
    }


    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {

            Map<String, Object> userInfo = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            log.info("토큰: " + userInfo.get("token"));

            // Return token and user info
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("Login error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        SecurityContextHolder.clearContext();
        request.logout();
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("로그아웃 성공: " + response.getStatus());
    }

    // 프로필 조회
    @GetMapping("/profile")
    @Operation(summary = "프로필 조회")
    public ResponseEntity<Member> getProfile(@RequestParam String userId) {
        try {
            Member member = userService.getProfile(userId);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // todo : 프로필 업데이트 시 사용자 정보 추가 업데이트
    // 프로필 업데이트
//    @PutMapping("/profile")
//    @ApiOperation(value = "프로필 업데이트", notes = "프로필을 업데이트 합니다.")
//    public ResponseEntity<Member> updateProfile(@RequestParam String userEmail, @RequestBody UpdateProfileRequest updateProfileRequest) {
//        try {
//            Member updatedMember = userService.updateProfile(userEmail, updateProfileRequest.getNewUsername(), updateProfileRequest.getNewPassword());
//            return ResponseEntity.ok(updatedMember);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    //Todo : 이미지 테이블에 저장하는걸로 바꿈
    @PostMapping("/profile/update")
    @Operation(summary = "프로필 업데이트")
    public ResponseEntity<?> updateProfile(@RequestPart String userEmail,
                                           @RequestPart String userName,
                                           @RequestPart String userPhn,
                                           @RequestPart(value = "file") MultipartFile file) {
        try {
//            String storedFileUrl = storageService.storeFile(file);
//            userService.updateProfile(userEmail, userName, userPhn, storedFileUrl);
            return ResponseEntity.ok("200");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회원탈퇴
    @PostMapping("/deleteProfile")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<Member> deleteProfile(@RequestParam String userEmail) {
        try {
            Member deletedMember = userService.deleteProfile(userEmail);
            return ResponseEntity.ok(deletedMember);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update-token")
    @Operation(summary = "토큰 업데이트")
    public ResponseEntity<?> updateAccessToken(@RequestBody TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();

        try {
            // 액세스 토큰 업데이트
            String newAccessToken = userService.updateAccessToken(refreshToken);
            return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
    }

    // 이메일로 인증 코드 보내기
    @PostMapping("/send-verification-email")
    @Operation(summary = "이메일 인증 보내기")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody EmailRequest request) {
        try {
            userService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/verify-email")
    @Operation(summary = "이메일 인증 확인")
    public ResponseEntity<?> verifyEmail(@RequestBody VerificationRequest verificationRequest) {
        String userEmail = verificationRequest.getEmail();
        String code = verificationRequest.getCode();

        try {
            userService.verifyEmail(userEmail, code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


//    // 쿠키 사용방법
//    @GetMapping("/loginSuccess")
//    public void getLoginInfo(HttpServletResponse response, OAuth2AuthenticationToken token) throws IOException {
//        String userEmail = token.getPrincipal().getAttribute("email");
//        String jwt = userService.simpleLogin(userEmail);
//
//        // Create a new cookie with the JWT token
//        Cookie jwtCookie = new Cookie("jwt", jwt);
//
//        response.addCookie(jwtCookie);
//
//        log.info("jwtCookie={}", jwtCookie);
//        log.info("jwt={}", jwt);
//        // Redirect to the client app
//        response.sendRedirect("http://localhost:3000/loginSuccess");
//    }

    // url 사용방법
    @GetMapping("/loginSuccess")
    @Operation(summary = "간편 로그인")
    public void getLoginInfo(HttpServletResponse response, OAuth2AuthenticationToken token) throws IOException {
        String userEmail = token.getPrincipal().getAttribute("email");
        String userImg = token.getPrincipal().getAttribute("picture");
        String jwt = userService.simpleLogin(userEmail);

        // Redirect to the client app with the JWT token as a URL parameter
        response.sendRedirect("http://localhost:5173/loginSuccess?token=" + URLEncoder.encode(jwt, "UTF-8") + "&email=" + userEmail + "&img=" + userImg);
    }

    @GetMapping("/admin/login")
    @Operation(summary = "관리자 로그인")
    public ResponseEntity<?> getAdminLogin() {
        try {
            log.info("관리자 두두 등장");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}
