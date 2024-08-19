package net.datasa.web5.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//추가한 변수들을 이용하기 위하여 lombok을 이용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {
	//클래스 명은 아무렇게 설정해도 상관없다.
	//로그인했거나 로그인하려고 하는 사용자의 정보를 저장한다.
	//UserDetails interface를 상속해야한다.
	//잘못 설정한 경우 source -> override implement methods 에서 추가할 수 있다.
	//security에게 정보를 주기위하여 만든 class이다.
	
	/**
	 * serialVersionUID 우연히 겹치지 않게 긴 숫자를 적어주면 된다.
	 * 객체 직렬화 : 객체를 직렬화하여 전송 가능한 형태로 만드는 것
	 * 
	 * 		입력 출력은 stream(데이터가 흘러가는 통로(일방향으로 흐른다))을 통과하는 것
	 * 		데이터가 stream을 통해서 이동할 때 덩치가 큰 데이터가 통과하지 못하는 경우가 생긴다.
	 * 		데이터를 해체하여 통과하게 되는데 통과 후 다시 재조립하여 복구했을 때
	 * 		버전이 동일한지 확인하는 것
	 */
	private static final long serialVersionUID = 1562050567301951305L;
	//필수적으로 필요한 것 rolename : 권한이름 enabled : 멀쩡한 아이디인지 확인(차단을 당했는지 휴면인지 등의 설정)
	//변수명은 상관없이 아래 메서드를 통해서 접근한다.
	String id;
	String password;
	String roleName;
	boolean enabled;
	
	//필수적이진 않으나 가끔 확인하는 것은 추가해줄 수 있다.
	String name; //따로 설정한 변수들은 꺼내려면 get set등이 필요하기 때문에 lombok어노테이션을 이용한다.
	
	@Override //getAutorities : 권한 설정
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//"ROLE_USER", "ROLE_ADMIN" 처럼 앞에 ROLE_를 붙여서 이름을 정한다.(접근 권한등을 다르게 부여할 수 있다.
		//simpleGrantedAuthority()에 들어갈 값은 위에 설정한 권한이름 : roleName과 같아야한다.
		return Collections.singletonList(new SimpleGrantedAuthority(roleName));
	}

	//아래 3개는 지금 사용하지않기 때문에 return true로 설정
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override 
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	//아래 3개는 위에 설정한 변수들을 return 값으로 이용한다.
	@Override 
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return id;
	}

}
