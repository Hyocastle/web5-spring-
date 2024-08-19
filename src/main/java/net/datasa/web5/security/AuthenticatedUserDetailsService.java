package net.datasa.web5.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.repository.MemberRepository;

//wevSecurityConfig 등에 작성된 "login" "logout"등의 url이 오면 실행된다.
//ID와 비밀번호을 가져와서 DB와 비교(이 코드에서는 생략)
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService {
	//클래스 명은 아무렇게나 지어도 상관없으나 userDetailsService를 상속받기 때문에
	//가급적이면 클래스의 목적을 표시해주면 좋다.
	//service이기 때문에 @Service 추가
	//필요에 따라 @transection

	//private final BCryptPasswordEncoder passwordEncoder;
	//라고만 적어준다면 null값이기 때문에 에러가 작동한다.
	//final이기 때문에 @RequiredArgsConstructor 추가
	//Bean으로 객체가 메모리에 만들어져 있기 때문에 생성자를 통해 초기화가 가능하다.
	private final BCryptPasswordEncoder passwordEncoder;
	
	//repository를 사용하기 위하여 변수 선언
	private final MemberRepository repository;
	
	
	@Override //login을 할 시 Username(id)을 먼저 전달해준다.
	// userdetails 타입으로 만들어서 리턴해줘야한다.(userdetail -> 인터페이스)이기 때문에
	// 하위객체를 만들어서 리턴하라는 의미 -> UserDetails를 구현한 클래스를 만들어서 이용(AuthenticatedUser)
	//입력받은 아이디를 String type으로 받아온 상태.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//전달받은 아이디로 회원정보 DB에서 조회 -> repository 객체가 있어야한다.
		//없으면 예외
		//findByid(username)   . 뒤의 메서드들을 확인해본다.
		//orElse 등
		MemberEntity entity = repository.findById(username).orElseThrow(() 
				-> new EntityNotFoundException("회원정보가 없습니다."));
		
		//있드면 그 정보로 UserDetails 객체를 생성하여 리턴 -> 실제 UserDetails객체가 아닌
		//AuthenricaltedUser (우리가 만든 userDetails를 상속받은 클래스) 객체
		//entity를 통해 db에서 값을 가져오고 로그인 값과 비교한다.
		AuthenticatedUser user = AuthenticatedUser.builder()
				.id(entity.getMemberId()) //id(username)도 가능하다.
				//WebSecurityConfig에서 암호화 객체를 @Bean으로 설정해두었기 때문에 이용한다.
				//사용자가 회원가입시 비밀번호를 입력할 떄 passwordEncoder.encode("입력한 비밀번호")
				//형식으로 들어가기 때문에 개발자도 원래 비밀번호가 뭔지 알 수 없다.
				.password(entity.getMemberPassword())
				.name(entity.getMemberName())
				.roleName(entity.getRolename())
				.enabled(entity.isEnabled()) //boolean 타입은 get set은 get이 아닌 is로 만들어준다.
				.build();
		log.debug("인증정보 : {}", user);
		
		//userDetails 객체로 만들어줘서 리턴해줘야 security가 알아볼 수 있다.
		return user;
		
	}

}
