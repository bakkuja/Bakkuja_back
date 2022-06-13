package com.bakooza.bakooza.Service;

import com.bakooza.bakooza.DTO.OAuthToken;
import com.bakooza.bakooza.Domain.Member;
import com.bakooza.bakooza.Repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class MemberService {

    @Value("${kakao.clientId}")
    private String clientId;

    @Value("${kakao.redirectUri}")
    private String redirectUri;

    @Value("${kakao.reqTokenUrl}")
    private String reqTokenUrl;

    @Value("${kakao.reqUserDataUrl}")
    private String reqUserDataUrl;

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long join(Member member) {
        memberRepository.save(member);

        return member.getId();
    }

    public String getAccessToken(String authorizationCode) {
        // authorization code 가져오기
        RestTemplate rt = new RestTemplate();

        //HttpHeader 객체 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);

        log.info("parmas={}", params);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        log.info("kakaoTokenRequest={}", kakaoTokenRequest.getBody());
        // Http 요청 POST
        ResponseEntity<String> response = rt.exchange(
                reqTokenUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class //요청 시 반환되는 데이터 타입
        );

        log.info("ss={}", response);

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken.getAccess_token();
    }

    public HashMap<String, Object> getUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<String, Object>();

        // accessToken으로 회원 정보 가져오기
        RestTemplate rt2 = new RestTemplate();

        //HttpHeader 객체 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + accessToken);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

        // Http 요청 POST
        ResponseEntity<String> response2 = rt2.exchange(
                reqUserDataUrl,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class //요청 시 반환되는 데이터 타입
        );

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response2.getBody());

        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
        JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

        log.info("properties={}", properties);
        log.info("kakao_account={}", kakao_account);
        log.info("kakao_account_getObject={}", kakao_account.getAsJsonObject());

        String id = element.getAsJsonObject().get("id").getAsString();
        String nickname = properties.getAsJsonObject().get("nickname").getAsString();
        String email = kakao_account.getAsJsonObject().get("email").getAsString();
        String profileImage = properties.getAsJsonObject().get("profile_image").getAsString();

        log.info("id={}", id);
        log.info("nickname={}", nickname);
        log.info("email={}", email);
        log.info("profileImage={}", profileImage);

        userInfo.put("id", id);
        userInfo.put("nickname", nickname);
        userInfo.put("email", email);
        userInfo.put("profileImage", profileImage);

        log.info("userInfo={}", userInfo);
        return userInfo;
    }

    public Optional<Member> findMemberById(Long memberId) {

        return memberRepository.findById(memberId);
    }

    public void test(){
        memberRepository.test();
    }
}
