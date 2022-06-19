package com.ssafy.ssastagram.controller;

import com.ssafy.ssastagram.repository.ContentRepository;
import com.ssafy.ssastagram.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/content")
@Slf4j
@CrossOrigin(origins="*", allowedHeaders="*")
public class MainController {

    ContentRepository contentRepository;

    // 이미지 목록 불러오기
    @GetMapping
    public List<Map<String,Object>> list() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Uid 내림차순으로 1000개를불러와 Map List로 반환
        contentRepository.findTop1000ByOrderByUidDesc().forEach(contentItem ->{
            Map<String, Object> obj = new HashMap<>();
            obj.put("uid", contentItem.getUid());
            obj.put("path", contentItem.getPath());
            obj.put("title", contentItem.getTitle());
            result.add(obj);
        });
        return result;
    }

    // img 태그 src로 불러오기
    @ResponseBody
    @GetMapping("/image/{filename}")
    public Resource showImage(@PathVariable String filename) throws MalformedURLException {
        String path = System.getProperty("user.dir"); // 현재 디렉토리 가져오기
        File file = new File(path + "src/main/resources/static/" + filename);
        return new UrlResource("file:"+file.getPath());
    }

    // 이미지 저장
    @PostMapping
    public Map<String,String> post(@RequestPart("picture")MultipartFile pic,
                                   @RequestParam("title") String title,
                                   @RequestParam("password") String password) throws IOException {
        log.info("## post upload mapping");

        String path = System.getProperty("user.dir"); // 현재 디렉토리 가져오기
        File file = new File(path + "src/main/resources/static/" + pic.getOriginalFilename());

        // 폴더가 없다면 폴더를 생성을 해준다.
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        // 파일을 파일 시스템 내로 이전시킨다.
        pic.transferTo(file);
        // db에 content 엔티티를 저장
        contentRepository.save(Content.builder().password(password).path(file.getName()).title(title).build()).getUid();
        return Map.of("path",file.getName()); // map 바로 초기화
    }

    // 이미지 정보 업데이트
    @PutMapping("/{uid}")
    public Map<String, String> update(@PathVariable int uid, @RequestPart("picture") MultipartFile pic,
                                      @RequestParam("title") String title,
                                      @RequestParam("password") String password) throws IOException {
        // uid에 해당하는 content를 가져온다.
        Content content  = contentRepository.findById(uid).get();
        // password를 비교한다.
        if(!password.equals(content.getPassword())) {
            // password가 다르면 에러
            return Map.of("ERROR", "Password for posting is not match");
        }
        // 새로운 title로 set
        content.setTitle(title);
        // 이미지 파일이 empty가 아니라면 새로운 image로
        if(!pic.isEmpty()) {
            // 현재 디렉토리
            String path = System.getProperty("user.dir");
            // 파일 객체 생성
            File file = new File(path + "src/main/resources/static/" +pic.getOriginalFilename());

            // 폴더가 없다면 만든다.
            if(!file.getParentFile().exists()) file.getParentFile().mkdir();
            // 파일시스템에 저장
            pic.transferTo(file);
            // content에 새로운 이미지 path를 저장
            content.setPath(file.getName());
        }

        // repository에 저장
        contentRepository.save(content);
        // Map으로 path 반환
        return Map.of("path", content.getPath());

    }


    // 이미지 삭제
    @DeleteMapping("/{uid}")
    public void delete(@PathVariable int uid, @RequestBody Map<String, Object> body) {
        // 비밀번호가 일치하면 제거
        if(body.get("password").toString().equals(contentRepository.findById(uid).get().getPassword())) {
            contentRepository.deleteById(uid);
        }
    }
}