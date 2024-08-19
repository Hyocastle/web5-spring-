package net.datasa.web5.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
	
	private Integer replyNum;
	private Integer boardNum;
	private String memberId;
	private String memberName;
	private String contents;
	private LocalDateTime createDate;

}
