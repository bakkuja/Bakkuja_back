package com.bakooza.bakooza.Controller;

import com.bakooza.bakooza.Domain.Member;
import com.bakooza.bakooza.Service.JwtUtils;
import com.bakooza.bakooza.Service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController {

    private final MemberService ms;
    private final JwtUtils jwtUtils;

    private static int refreshTokenIndex = 0;

    public LoginController(MemberService ms, JwtUtils jwtUtils) {
        this.ms = ms;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<Object> kakaoCallback(@RequestParam(value = "code") String code) throws JsonProcessingException, UnsupportedEncodingException {

        log.info("authorizationCode={}", code);

        String accessToken = ms.getAccessToken(code);
        log.info("getAccessToken 진입 후 받아 온 accessToken = {}", accessToken);

        HashMap<String, Object> userInfo = ms.getUserInfo(accessToken);

        Member member = new Member();
        member.setId(Long.parseLong(userInfo.get("id").toString())-10000);
        member.setNickname(userInfo.get("nickname").toString());
        member.setEmail(userInfo.get("email").toString());
        member.setProfileImage(userInfo.get("profileImage").toString());

        Boolean isMemberPresent = ms.findMemberById(member.getId()).isPresent();
        log.info("ms.findMemberById(member.getId()).isPresent() = {}", member.getId());
        log.info("isMemberPresent = {}", isMemberPresent);
        if(isMemberPresent == false) {
            ms.join(member);
        }
        else {
            log.info("The member is already sign up");
        }

        String jwtAccessToken = jwtUtils.createJwt(member);
        String refreshToken = jwtUtils.createRefreshToken(member);

        AtomicInteger count = new AtomicInteger(refreshTokenIndex);
        refreshTokenIndex = count.get();

        log.info("refreshTokenIndex = {}", refreshTokenIndex);
        log.info("refreshToken = {}" + refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("jwtAccessToken", jwtAccessToken);
        headers.set("refreshTokenIndex", String.valueOf(refreshTokenIndex));
        headers.set("nickname", member.getNickname());
        headers.set("email", member.getEmail());

        log.info("headers = {}", headers);
        log.info("headers jwtAccessToken = {}", headers.get("jwtAccessToken"));
        log.info("headers refreshTokenIndex" + headers.get("refreshTokenIndex"));

        return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
    }

    @GetMapping("hi")
    public void Hello() {
        ms.test();
    }

}
