package net.datasa.web5.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
/**
 * 회원정보 관련 콘트롤러
 **/
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.MemberService;

@Slf4j
@RequestMapping("/member")
@Controller
@RequiredArgsConstructor
public class MemberController {
	
	public final MemberService service;

	/**
	 * 회원가입양식으로 이동
	 **/
	@GetMapping("joinForm")
	public String joinForm() {
		return "memberView/joinForm";
	}

	@PostMapping("join")
	public String join(@ModelAttribute MemberDTO member) { //form에 입력한 값들이 dto에 들어간다.
		log.debug("전달된 회원정보 : {}", member); //전달이 된 것을 확인한다.
		//서비스로 전달하여 저장 @bean 으로 설정 후 final 변수로 생성해 주었기 때문에 객체를 따로 생성해줄 필요가 없다.
		//@requiredArg 어노테이션을 통해 final 선언한 변수의 객체를 생성해준다.
		service.join(member); //sevrvice에 dto(member)를 넘긴다.
		return "redirect:/";
	}
	@GetMapping("idCheck")
	public String idCheck() {
		return "memberView/idCheck";
	}
	
	@PostMapping("idCheck")
	public String idCheck(@RequestParam("checkId") String id, Model model) {
		//ID중복확인 폼에서 전달된 검색할 아이디를 받아서 log 출력
		log.debug("전달된 ID : {}", id);
		
		//서비스의 메소드로 검색할 아이디를 전달받아서 조회
//		MemberDTO dto = service.findId(id);
		//해당 아이드를 쓰는 회원이 있으면 false, 없으면 true를 리턴 받는다.
//		boolean result = false;
//		if(dto == null) {
//			result = true;
//		}
		boolean result = service.findId(id);
		//검색할 아이디와 조회결과를 모델에 저장
		model.addAttribute("id", id);
		model.addAttribute("result", result);
		//검색 페이지로 다시 이동
		
		return "memberView/idCheck";
	}
	
	//로그인form으로 이동한다.
	@GetMapping("loginForm")
	public String loginForm() {
		return "memberView/loginForm";
	}
	
	@GetMapping("write")
	public String write() {
		return "memberView/write";
	}
	@GetMapping("delete")
	public String delete() {
		return "memberView/delete";
	}
	
	//개인정보 수정 form으로 이동
	@GetMapping("info")
	public String info(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
		//로그인이 되어있는 상태이기 때문에 session에 id가 존재한다.(security가 넣어준다)
		//model에 담아서 이동해줘야하기 때문에 모델이 필요하다.
		
		//현재 사용자의 아이디를 서비스로 전달하여 해당 사용자 정보를 MemberDTO객체로 리턴받는다.
		//AuthenticatedUser user 로그인할 때 ID 와 PW 이름 등만 받아왔기 때문에 이 정보만 담겨있다.
		//email등의 정보는 없다.
		//MemberDTO객체를 모델에 저장하고 HTML폼으로 이동
		MemberDTO dto = service.getMemberById(user.getUsername());
		model.addAttribute("user", dto);
		
		return "memberView/infoForm";
	}
	//입력받은 값을 이용하여 db를 수정한다.	
	@PostMapping("info")
	public String info(@AuthenticationPrincipal AuthenticatedUser user, 
			@ModelAttribute MemberDTO dto) {
		//수정폼에서 전달한 값들을 memberDTO로 받는다. @ModelAttribute MemberDTO dto
		//현재 로그인한 사용자의 아이디를 MemberDTO객체에 추가한다.
		//getUsername -> authenticatedUser에서 id의 getter는 username이다.
		dto.setMemberId(user.getUsername()); 
		//MemberDTO객체를 서비스로 전달하여 DB를 수정한다.
		service.update(dto);
		//메인화면으로 리다이렉트한다.
		return "redirect:/";
}
}
