package net.datasa.web5.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.repository.MemberRepository;

//회원정보 관련 처리 서비스

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
	// repository의 기능을 사용하기 위하여 선언
	public final MemberRepository memberRepository;

	// BCryptPasswordEncoder를 사용하기위하여 객체 선언(암호화에 이용한다.)
	private final BCryptPasswordEncoder passwordEncoder;

	// 가입 처리
	public void join(MemberDTO dto) {
		MemberEntity entity = MemberEntity.builder()
				// DTO로 전달받은 값드을 엔티티에 세팅
				.memberId(dto.getMemberId())
				// password는 암호화된 상태로 넣어줘야한다.
				.memberPassword(passwordEncoder.encode(dto.getMemberPassword())).memberName(dto.getMemberName())
				.email(dto.getEmail()).phone(dto.getPhone()).address(dto.getAddress())
				// 기타 추가 데이터를 엔티티에 세팅
				.enabled(true) // true나 admin등을 설정
				.rolename("ROLE_USER").build();
		// db에 저장
		memberRepository.save(entity);
	}

	/*
	 * public MemberDTO findId(String id) {
	 * 
	 * MemberEntity entity = memberRepository.findById(id).orElse(null);
	 * 
	 * if(entity == null) return null;
	 * 
	 * MemberDTO dto = new MemberDTO(); dto.setMemberId(entity.getMemberId());
	 * return dto; }
	 */

	public boolean findId(String checkId) {
		return !memberRepository.existsById(checkId);
		/*
		 * MemberEntity entity = memberRepository.findById(id).orElse(null);
		 * //findById(id)까지만 쓴다면 optional 객체를 준다. //내용을 알 필요 없기 때문에 isPresent, isEmpty를
		 * 사용해도 된다. -> boolean 값이 나오기 때문에 //return
		 */
	}

	public MemberDTO getMemberById(String Id) {
		MemberEntity entity = memberRepository.findById(Id)
				// optional객체로 받아오기 때문에 뒤에 .get()등을 붙여줘야한다.
				// 예외를 던지는게 좋다.
				.orElseThrow(() -> new EntityNotFoundException("해당 ID가 존재하지않습니다"));

		// entity를 서비스 이후에까지 넘겨주는 것은 좋지않기 때문에 dto에 담아서 넘겨준다.
		MemberDTO dto = new MemberDTO().builder().memberId(entity.getMemberId())
				.memberPassword(entity.getMemberPassword()).memberName(entity.getMemberName()).email(entity.getEmail())
				.phone(entity.getPhone()).address(entity.getAddress()).build();
		return dto;
	}

//개인정보 수정
	public void update(MemberDTO dto) {
		//entity의 생명주기 : 
		//가입할 때 만든 entity -> db랑 연동되어있지않다.(new 또는 builder로 만든 것)
		//repository를 통해 findbyid로 가져온 entity -> db와 연동되어있다.
		//entity매니저는 영속성 context에 데이터를 두고 사용한다.
		//findbyId를 통해서 가져온 것은 데이터를 영속성 context에 담아둔다(하나의 트랜잭션까지 살아있다.)
		//-> @Transactional이 관리해준다.(중간에 종료되면 rollback하는 등의 작업)
		//트랜잭션(commit과 commit사이) -> 하나의 메서드(return될 대 commit)
		//데이터를 가져오면 캐시에 저장된다.(한 번 더 findbyid를 하면 이 캐시에서 값을 가져온다)
		//하나의 트랜잭션동안 캐시에서 작업을 하다가 commit 할 때 db에 실제 반영한다.(지연반영)
		MemberEntity entity = memberRepository.findById(dto.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("해당 ID가 존재하지않습니다"));
		//이 작업을 해야 테이블 수정이 된다.
		//비밀번호는 전달된 값이 있을 때만 수정한다.
		//값이 들어오면 암호화해서 수정한다.
		if(dto.getMemberPassword() != null && !dto.getMemberPassword().isEmpty()) {
			entity.setMemberPassword(passwordEncoder.encode(dto.getMemberPassword()));
		}
		entity.setMemberName(dto.getMemberName());
		entity.setEmail(dto.getEmail());
		entity.setPhone(dto.getPhone());
		entity.setAddress(dto.getAddress());
		//리턴될 때 db에 저장된다.
	}
}
