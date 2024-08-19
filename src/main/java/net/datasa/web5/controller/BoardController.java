package net.datasa.web5.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.BoardDTO;
import net.datasa.web5.domain.dto.ReplyDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.BoardService;

@Slf4j
@RequestMapping("board")
@Controller
@RequiredArgsConstructor
public class BoardController {

	public final BoardService service;

	// 메인화면에서 "board/list"경로를 클릭했을 때 처리하는 메소드
	// template/boardView/list.html파일로 포워딩
	// 로그인 안한 상태에서 해당 페이지의 "게시판"이라는 제목이 보여야 함 -> 시큐리티 설정
//	@GetMapping("list")
	// null 과 내용없는 문자 ""의 차이 String s = null; String s2 =""; null은 아무 값이 없다
	// s2는 힙 영역의 특정부분을 가르킨다. s의 length는 에러가 뜨고 s2는 0으로 출력된다.
	// main함수에서 list를 누르면 searchWord 에 null이 들어가기 때문에 name="", defaultValue =""를
	// 추가해줘야한다.
	// name이 일치하는 값이 넘어오지 않았다면 default로 처리한다.
//	public String list(Model model, @RequestParam(name="searchWord", defaultValue = "") String searchWord) {
//		List<BoardDTO> dtolist = service.getList(searchWord);
//		model.addAttribute("list", dtolist);
//		return "boardView/list";
//	}

	// spring 외부 설정 파일에 정의된 값을 애플리케이션 필드에 주입할 때 사용한다.
	@Value("${board.pageSize}")
	int pageSize;
	@Value("${board.linkSize}")
	int linkSize;
	// 어디다 파일이 저장될지
	@Value("${board.uploadPath}")
	String uploadPath;

	@GetMapping("list")
	public String list(Model model, @RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "searchType", defaultValue = "") String searchType,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord) {

		log.debug("propertise 의 값 : pageSize={}, linkSize={}, uploadPath={}", pageSize, linkSize, uploadPath);
		log.debug("요청 파라미터 : page={}, searchType={}, searchType={}", page, searchType, searchWord);

		// 서비스로 (페이지, 페이지당 글 수, 검색조건, 검색어) 전달하여 결과를 page객체로 받음
		Page<BoardDTO> boardPage = service.getList(page, pageSize, searchType, searchWord);

		log.debug("목록 정보 : {}", boardPage.getContent());
		log.debug("현제 페이지 : {}", boardPage.getNumber());
		log.debug("전체 개수 : {}", boardPage.getTotalElements());
		log.debug("전체 페이지수 : {}", boardPage.getTotalPages());
		log.debug("한 페이지당 글 수 : {}", boardPage.getSize());
		log.debug("이전 페이지 존재 여부 : {}", boardPage.hasPrevious());
		log.debug("현재 페이지 존재 여부 : {}", boardPage.hasNext());

		// HTML로 포워딩하기 전에 모델이 필요 정보들을 저장
		model.addAttribute("boardPage", boardPage);
		model.addAttribute("page", page);
		// 페이지를 변경해도 그전에 했던 검색등의 작업을 유지하기 위하여
		model.addAttribute("linkSize", linkSize);
		model.addAttribute("searchTypte", searchType);
		model.addAttribute("searchWord", searchWord);

		// HTML로 포워딩
		return "boardView/list";
	}

	@GetMapping("write")
	public String write() {
		return "boardView/writeForm";
	}

	// (파일 첨부)파일은 ModelAttribute로 한번에 같이 받을 수 없고 RequestParam으로 받아야한다.
	// MultipartFile로 받는다.
	@PostMapping("write")
	public String write(@ModelAttribute BoardDTO dto, @RequestParam("upload") MultipartFile upload,
			@AuthenticationPrincipal AuthenticatedUser user) {

		dto.setMemberId(user.getUsername());

		if (upload != null) {
			log.debug("파일 존재 여부 : {}", upload.isEmpty()); // 파일이 없다면 true
			log.debug("파라미터 이름 : {}", upload.getName()); // 파일의 바뀐 이름(저장을 위하여)
			log.debug("파일의 이름 : {}", upload.getOriginalFilename()); // 파일의 원래 이름
			log.debug("크기 : {}", upload.getSize()); // 파일 크기
			log.debug("파일 종류 : {}", upload.getContentType()); // 파일이 어떤 종류인지
		}

		//어디다 파일을 저장할지와 저장할 파일의 정보를 담고있는 객체를 같이 보내준다
		service.write(dto, uploadPath, upload);
		return "redirect:list";
	}

	@GetMapping("/view/{id}")
	public String view(@PathVariable("id") Integer boardNum, Model model) {
		// 에러처리 (글이 없을 때)
		try {
			BoardDTO dto = service.view(boardNum);
			dto.setViewCount(service.upView(boardNum));
			model.addAttribute("dto", dto);
			return "boardView/view";
		} catch (Exception e) {
			e.printStackTrace(); // 만약 예외가 발생했을 때 에러를 출력해주기 위하여
			return "redirect:list";
		}
	}

	/**
	 * @param boardNum 삭제할 글 번호
	 * @param user     로그인 정보
	 * @return 게시판 글정보 보기 경로
	 */
	@GetMapping("delete")
	public String delete(@RequestParam("boardNum") int boardNum, @AuthenticationPrincipal AuthenticatedUser user) {

		// 글번호와 로그인한 아이디를 서비스로 전달
		try {
			service.delete(boardNum, user.getUsername(), uploadPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:list";
	}

	/**
	 * 게시글 수정 폼으로 이동
	 * 
	 * @param boardNum 수정할 글번호
	 * @param user     로그인한 사용자 정보
	 * @return 수정 HTML
	 */
	@GetMapping("update")
	public String update(Model model, @RequestParam("boardNum") int boardNum,
			@AuthenticationPrincipal AuthenticatedUser user) {

		try {
			BoardDTO boardDTO = service.view(boardNum);
			if (!user.getUsername().equals(boardDTO.getMemberId())) {
				throw new RuntimeException("수정 권한이 없습니다.");
			}
			model.addAttribute("board", boardDTO);
			return "boardView/updateForm";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:list";
		}
	}

	/**
	 * 게시글 수정 처리
	 * 
	 * @param boardDTO 수정할 글 정보
	 * @param user     로그인한 사용자 정보
	 * @return 수정폼 HTML
	 */
	@PostMapping("update")
	public String update(@ModelAttribute BoardDTO boardDTO, @RequestParam("upload") MultipartFile upload, @AuthenticationPrincipal AuthenticatedUser user) {

		try {
			service.update(boardDTO, user.getUsername(), uploadPath, upload);
			return "redirect:view/" + boardDTO.getBoardNum();

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:list";
		}
	}

	/**
	 * 리플 쓰기
	 * 
	 * @param replyDTO 저장할 리플 정보
	 * @param user     로그인 사용자 정보
	 * @return 게시글 보기 경로로 이동
	 */

	@PostMapping("replyWrite")
	public String replyWrite(@ModelAttribute ReplyDTO replyDTO, @AuthenticationPrincipal AuthenticatedUser user) {
		replyDTO.setMemberId(user.getUsername());
		service.replyWrite(replyDTO);
		return "redirect:view/" + replyDTO.getBoardNum();
	}

	/**
	 * 리플 삭제 *
	 * 
	 * @param replyDTO 삭제할 리플번호와 본문 글번호
	 * @param user     로그인한 사용자 정보
	 * @return 게시글 상세보기 경로
	 */
	@GetMapping("replyDelete")
	public String replyDelete(

			@ModelAttribute ReplyDTO replyDTO, @AuthenticationPrincipal AuthenticatedUser user) {

		try {
			service.replyDelete(replyDTO.getReplyNum(), user.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:view/" + replyDTO.getBoardNum();
	}

	//파일 다운로드
	//지금까지는 보통 return타입이 String였다(-> return값이 보통 경로였기 때문이다)
	//파일을 return하는 방식으로 주는 것이 아니기 때문에 void이다.
	/**
	 * 첨부파일 다운로드
	 * @param boardNum 글번호
	 * @param response 응답정보
	 */
	@GetMapping("download")
	public void download(@RequestParam("boardNum") Integer boardNum,
			//응답정보를 사용한다. response 객체에 쿠키, 파일 등을 담아 보낼 수 있다.
			HttpServletResponse response
			) {
		service.download(boardNum, response, uploadPath);
	}
	
}
