package com.ssafy.ssastagram.entity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // 엔티티임을 설정
@NoArgsConstructor // 기본 생성자
@Getter
@Setter
@ToString
public class Content {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성을 데이터베이스에 위임 (MySQL에서 사용)
    private int uid;
    private String path;
    private String title;
    private String password;

    @Builder
    public Content(String path, String title, String password) {
        super();
        this.path = path;
        this.title = title;
        this.password = password;
    }

}