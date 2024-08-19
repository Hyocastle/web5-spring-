package net.datasa.web5.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
	//entity와 변수이름, 타입이 꼭 같을 필요가 없다.
	//entity에서 memberEntity로 받아온 값을 여러개의 변수에 나눠받을 수 도 있다.
	//DTO는 화면기준이고 Entity는 테이블 기준이기 때문에
	private Integer boardNum;
	private String memberId;
	private String memberName;
	private String title;
	private String contents;
	private Integer viewCount;
	private Integer likeCount;
	private String originalName;
	private String fileName;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	
	//리플 목록
	private List<ReplyDTO> replyList;
}
