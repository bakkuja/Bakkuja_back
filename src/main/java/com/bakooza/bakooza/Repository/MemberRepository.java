package com.bakooza.bakooza.Repository;

import com.bakooza.bakooza.Domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class MemberRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        Connection conn = DataSourceUtils.getConnection(dataSource);
    }

    public Member save(Member member) {
        Map<String, Object> parameters = new HashMap<>();
        log.info("repository member.getId() = {}", member.getId());
        parameters.put("member_id", member.getId());
        parameters.put("nickname", member.getNickname());
        parameters.put("email", member.getEmail());
        parameters.put("profile_image", member.getProfileImage());
        log.info("repository parameters = {}", parameters.toString());
        jdbcTemplate.update("INSERT INTO member (member_id, nickname, email, profile_image, member_reliability) VALUES(?, ?, ?, ?, ?)", member.getId(), member.getNickname(), member.getEmail(), member.getProfileImage(), 0);

        return member;
    }

    public Optional<Member> findById(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where member_id = ?", memberRowMapper(), id);
        return result.stream().findAny();
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setId(rs.getLong("member_id"));
            member.setNickname(rs.getString("nickname"));
            member.setEmail(rs.getString("email"));
            member.setProfileImage(rs.getString("profile_image"));

            return member;
        };
    }

    public void test() {
        jdbcTemplate.update("INSERT INTO member (member_id, nickname, email, profile_image, member_reliability) VALUES(?, ?, ?, ?, ?)", 2272582638L, "고동욱", "bogo1357@hanmail.net", "http://k.kakaocdn.net/dn/cP99dY/btryFMvqTWg/s2P0Pz4Ov36BMY8hlKVzLk/img_640x640.jpg", 0);
    }
}