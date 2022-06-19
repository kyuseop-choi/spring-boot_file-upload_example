package com.ssafy.ssastagram.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.ssastagram.entity.Content;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Integer> {

    public List<Content> findTop1000ByOrderByUidDesc(); // 메서드명 명명규칙이 존재한다! (틀리면 에러)

}