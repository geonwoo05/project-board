package com.fc.projectboard.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")

})
@EntityListeners(AuditingEntityListener.class) // 해당 Entity에도 이 옵션을 붙여야 오디팅이 된다.
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Setter를 클래스에 걸지 않는 이유는 특정 사용자가 세팅하지 못하게끔하기 위해서이다.

    @Setter
    @Column(nullable = false)
    private String title; //제목
    @Setter
    @Column(nullable = false, length = 10000)
    private String content; //본문

    @Setter
    private String hashtag; //해시태그

    @ToString.Exclude // 순환참조가 일어날 수 있기 때문에 기입
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    
    // 메타데이터
    // 이것들을 자동으로 세팅: jpa auditing

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; //생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy; //생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; //수정자


    // 모든 JPA Entity들은 Hibernate 구현체를 사용하는 경우 기본 생성자가 반드시 있어야한다.
    // 코드 바깥에서 new로 생성하지 못하도록 protected를 사용
    protected Article(){

    }

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // 도메인 Article을 생성할 때는 아래의 파라미터를 필요로 한다는 것을 알리는 목적
    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content,hashtag);
    }

    // 동일성 검사
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false; // pattern variable 검색
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
