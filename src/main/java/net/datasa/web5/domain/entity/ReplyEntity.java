package net.datasa.web5.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
//@Data -> toString, getter, setter
//toString에서 board를 빼기 위해 각각을 분리하였다.
@ToString(exclude ="board")//board를 빼고 toStirng을 생성한다.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "web5_reply")
@EntityListeners(AuditingEntityListener.class)
public class ReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // primary key가 자동으로 생성 될때의 규칙
	@Column(name = "reply_num")
	private Integer replyNum;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="board_num", referencedColumnName = "board_num")
	BoardEntity board;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id", referencedColumnName = "member_id")
	MemberEntity member;
	
	//리플내용
	@Column(name = "contents", nullable = false, length = 2000)
	private String contents;
	
	//작성 시간
	@CreatedDate
	@Column(name = "create_date", columnDefinition = "timestamp default curerrent_timestamp")
	private LocalDateTime createDate;

	//리플을 보여주기 위해 boardEntity에 relpyList를 추가해주었다.
	//따로 읽는다면 메서드가 2개 여야하기 때문에 1개로 출력해주기 위하여 board에 추가하였다.
}
