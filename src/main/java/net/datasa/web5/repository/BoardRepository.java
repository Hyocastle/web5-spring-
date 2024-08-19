package net.datasa.web5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.web5.domain.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity , Integer> {
	
	//메서드 이름을 잘 만들면 jpa가 자동으로 완성시켜준다.
	//아래처럼 정의한다면 추상메서드이지만 jpa가 대신 구현해준다(규칙을 준수해야함)
	//메서드 명을 정의하기 힘들 때는 위에 쿼리를 적어주거나 
	//다른 곳에 쿼리를 적어두고 파일을 실행하여 쿼리를 실행하게 할 수 도 있다.
	//find : select를 만들어줘라 By 다음 조건을 준다(where의 느낌)
	//완전히 일치하는 경우 findByTitle(=) 
	//특정 단어를 포함하는 경우(like) Contating
	//s 를 포함하는 Title을 찾아줘라.
//	List<BoardEntity> findByTitleContaining(String s, Sort sort);
	Page<BoardEntity> findByTitleContaining(String s, Pageable pageable);
	
	Page<BoardEntity> findByContentsContaining(String s, Pageable pageable);

	Page<BoardEntity> findByMember_memberIdContaining(String s, Pageable pageable);

}
