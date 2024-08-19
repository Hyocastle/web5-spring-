package net.datasa.web5.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "web5_board")
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // primary key가 자동으로 생성 될때의 규칙
	@Column(name = "board_num")
	private Integer boardNum;
	
//	@Column(name = "member_id", length = 30)
//	private String memberId;
	//board table 이 아니 member table의 내용을 읽어온다
	@ManyToOne(fetch = FetchType.LAZY)//Lazy -> 지연시킨다(쓸 때만 읽어온다)
	@JoinColumn(name="member_id", referencedColumnName = "member_id")
	//name(기존 컬럼명) referencedColumName(참조할 컬럼명)은 같을 필요는 없다)
	//상대방의 테이블은 MemberEntity에 명시돼있다.
	MemberEntity member;

	@Column(name = "title", nullable = false, length = 1000)
	private String title;
	
	@Column(name = "contents", nullable = false, columnDefinition ="text")
	private String contents;
	
	@Column(name = "view_count", columnDefinition = "default 0")
	private Integer viewCount = 0;
	
	@Column(name = "like_count", columnDefinition = "default 0")
	private Integer likeCount = 0;
	
	@Column(name = "original_name", length = 300)
	private String originalName;
	
	@Column(name = "file_name", length = 100)
	private String fileName;
	
	// [작성시간]
	//자동으로 시간이 설정되게 하기 위해서는 @EnableJpaAuditing를 main(web4Applicaion.java)나
	//다른 설정들을 모아둔 곳에 붙여 활성화 시켜줘야한다. entity와 해당 column에도 listener과 createdate를 붙여줘야한다.
	@CreatedDate // 3단계
	@Column(name = "create_date", columnDefinition = "timestamp default curerrent_timestamp")
	private LocalDateTime createDate;//Date로 했다면 LocalDate, Timestamp로 했다면 LocalDateTime
	
	@LastModifiedDate
	@Column(name = "update_date", columnDefinition = "timestamp default curerrent_timestamp")
	private LocalDateTime updateDate;
	
	//reply를 출력하기 위한 항목
	//해당 글에 달린 리플들 목록
	//mappedBy board(reply에 있는 board를 통해서 참조한다.)
	//board 하나에 리플 여러개 이기 때문에 OneToMany
	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReplyEntity> replyList;
}
