//package com.bakooza.bakooza.Entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
//public class Member {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "member_id")
//    private Long memberId; // PK
//
//    @Column(name = "nickname")
//    private String nickname;
//
//    @Column(name = "email")
//    private String email;
//
//    @Column(name = "profile_image")
//    private String profileImage;
//
//    @Column(name = "member_reliability")
//    private float memberReliability;
//
//    @Builder
//    public Member(Long memberId, String nickname, String email, String profileImage,
//                  float memberReliability) {
//        this.memberId = memberId;
//        this.nickname = nickname;
//        this.email = email;
//        this.profileImage = profileImage;
//        this.memberReliability = memberReliability;
//    }
//}
