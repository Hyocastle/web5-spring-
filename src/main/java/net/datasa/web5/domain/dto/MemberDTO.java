package net.datasa.web5.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.web5.domain.entity.MemberEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	private String memberId;		//회원 아이디
	private String memberPassword;	//비밀번호
	private String memberName;		//이름
	private String email;			//이메일
	private String phone;			//전화번호
	private String address;			//주소
	private boolean enabled;		//계정상태
	private String rolename;		//권한명
	
}
