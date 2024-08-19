package net.datasa.web5.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.BoardDTO;
import net.datasa.web5.domain.dto.ReplyDTO;
import net.datasa.web5.domain.entity.BoardEntity;
import net.datasa.web5.domain.entity.MemberEntity;
import net.datasa.web5.domain.entity.ReplyEntity;
import net.datasa.web5.repository.BoardRepository;
import net.datasa.web5.repository.MemberRepository;
import net.datasa.web5.repository.ReplyRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

	private final BoardRepository repository;
	// 외래키가 있어서 memberRepo도 사용해야하기 때문에 선언해야한다.
	private final MemberRepository memberRepository;

	private final ReplyRepository replyRepository;

	public void write(BoardDTO dto, String uploadPath, MultipartFile upload) {
		/*
		 * BoardEntity entity = BoardEntity.builder() .memberId(dto.getMemberId())
		 * .title(dto.getTitle()) .contents(dto.getContents()) .build();
		 */

		// 애를 안써줘도 되는 이유는? getList()등에서
		MemberEntity memberEntity = memberRepository.findById(dto.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("아이디가 없습니다."));

		BoardEntity entity = new BoardEntity();
//		entity.setMemberId(dto.getMemberId());
		entity.setMember(memberEntity);
		entity.setTitle(dto.getTitle());
		entity.setContents(dto.getContents());

		// 첨부파일 처리하기
		// 첨부파일이 null, isEmpty를 확인한다.
		if (upload != null && !upload.isEmpty()) {
			// 저장할 경로의 디렉토리가 실제 있는지 확인 -> 없다면 생성한다.
			// java.io패키지의 File class를 이용하여 파일이나 디렉토리를 다룬다.
			File directoryPath = new File(uploadPath);
			if (!directoryPath.isDirectory()) {
				directoryPath.mkdirs();
			}
			// 저장할 파일명 생성
			// 이름은 아무렇게나 만들 수 있지만
			// 맨 앞에 오늘 날짜를 사용하고 뒤에는 UUID 로 128비트의 랜덤한 숫자로 만들어준다.
			// 예시 : 내 이력서.doc -> 20240806_6156df53-a49b-4419-a336-cb6da0fa9640.doc
			// 올라온 파일 이름
			String originalName = upload.getOriginalFilename();
			// 확장자 붙이기
			// 파일의 확장자를 판단하는 방법 -> 마지막 . 이 나온 것을 기준으로 뒤를 읽어준다.
			String extension = originalName.substring(originalName.lastIndexOf("."));
			// 생성 날짜 붙이기
			String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			// UUID 클래스 -> randomUUID(static method) -> 클래스 뒤에 바로.으로 호출해줬기 떄문
			// 랜덤한 숫자열을 생성해준다.
			String uuidString = UUID.randomUUID().toString();
			String fileName = dateString + "_" + uuidString + extension;

			// 파일 이동
			// 1.파일 객체 생헝
			// 2.실패했을 때 어떻게할지 에러처리를 해야한다.(입출력관련 메서드는 대부분 에러처리를 해줘야한다)
			// -> 파일 에러가 떴을 때 글 자체를 못쓰게 하려면 write 메서드 자체에 throw
			// -> 저장은 하게하려면 지금처럼
			try {
				File file = new File(uploadPath, fileName);
				// transferTO 메서드를 이용하면 바로 이동시켜준다.
				upload.transferTo(file);
				// 엔티티에 원래 파일명, 저장된파일명 추가
				entity.setOriginalName(originalName);
				entity.setFileName(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		repository.save(entity);
	}

	// DB의 데이터를 가져옴 단, 역순(작성한 순서)으로
//	public List<BoardDTO> getList(String searchWord) { // List<BoardDTO> 타입을 리턴 하는 list()
	// 1번 DB에서 받아온 Entity를 담을 Entity리스트 생성,받아옴(역순)
	// 2번 DTO를 담을 DTO리스트 생성
	// 3번 Entity리스트의 Entity를 -> DTO리스트의 DTO로 이동(반복문 사용)
	// 4번 값이 모두 담긴 DTO리스트를 리턴

	// [값 받아오는 방법 종류]
	// find~(): select로 데이터를 가져올때 사용
	// findAll(): 모든 데이터 가져올때 사용
	// findAll(sort): 모든 데이터 순서 정해 가져옴(desc)

	// BoardRepository의 메소드를 호출하여 게시판의 모든 글 정보를 조회
	// 순서를 정렬한다. (boardNum -> 엔티티에 적힌 값을 적어줘야한다)
//		Sort sort = Sort.by(Sort.Direction.DESC, "boardNum"); // select * from web5_board order by boardNum desc;(조건 1개)
//		List<BoardEntity> entitylist = repository.findAll(sort);
//		List<BoardEntity> entitylist = repository.findByTitleContaining(searchWord, sort);
//		ArrayList<BoardDTO> dtolist = new ArrayList<>();

	// (빌더사용)
	// 엔티티의 개수만큼 반복하면서 엔팅티의 값을 entity객체.member에서 id를 통해 꺼내고 BoardDTO객체를 생성하여 저장
	// 생성된 BoardDTO객체를 ArrayList에 저장
	// 최종 완성된 ArrayList를 리턴
//		for (BoardEntity entity : entitylist) {
//			BoardDTO dto = new BoardDTO().builder().boardNum(entity.getBoardNum())
	// .memberId(entity.getMemberId())
//					.memberId(entity.getMember().getMemberId()).memberName(entity.getMember().getMemberName())
//					.title(entity.getTitle()).contents(entity.getContents()).viewCount(entity.getViewCount())
//					.likeCount(entity.getLikeCount()).originalName(entity.getOriginalName())
	// .fileName(entity.getFileName())
//					.createDate(entity.getCreateDate()).updateDate(entity.getUpdateDate()).build();
//			dtolist.add(dto);
//		}
//		return dtolist;
//	}

	/**
	 * 검색 결과 글목록을 지정한 판페이지 분량의 Page 객체로 리턴
	 * 
	 * @param page       현재 페이지
	 * @param pageSize   페이지당 글 수 (property에 값을 설정해주었다)
	 * @param searchType 검색조건 (제목검색 : title, 본문검색 : contents, 작성자검색 : id)
	 * @param searchWord 검색어
	 * @return 게시글 목록 정보
	 */
	public Page<BoardDTO> getList(int page, int pageSize, String searchType, String searchWord) {
		// page와 pageSize는 필수적으로 받아와야함 / searchType, searchWord는 없을 수도 있다
		// 조회 조건을 담은 Pageable 객체 생성
		// page -> 맨 앞을 0부터 시작한다(page를 1부터 시작하게 했기 때문에 -1을 해줘야한다)
		// 몇 번째 부분을 가져올지 표시한다.
		// pageSize -> 몇개씩 자를지, sort -> 정렬 boardNum을 DESC로 정렬한다.
		// boardNum 자리에는 table과 연동되는 entity 객체의 이름을 적어줘야한다.
		// pageable 페이지를 어떻게 자르고 정렬하고 등의 정보가 들어있다.
		Pageable p = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "boardNum");

		// repository의 메소드로 Pageable 전달하여 조회. Page 리턴받음
		// 이전에는 boardRepository.findAll() : 전부 다 가져온다 ()내에 sort 객체를 넣으면 정렬해서 가져온다.
		// findAll()에 Pageable 객체를 넣으면 조건에 맞게 정렬해서 가져올 수 있다.
		Page<BoardEntity> entityPage = null;

		switch (searchType) {
		case "title":
			entityPage = repository.findByTitleContaining(searchWord, p);
			break;
		case "contents":
			entityPage = repository.findByContentsContaining(searchWord, p);
			break;
		case "id":
			entityPage = repository.findByMember_memberIdContaining(searchWord, p);
			break;
		default:
			entityPage = repository.findAll(p);
		}
		log.debug("조회된 결과 엔티티페이지 : {}", entityPage.getContent());
		// List는 ArrayList에 담아갔지만 이번에는 BoardDTO가 들어있는 Page객체에 담아가야한다(이래야 Page등의 값을 가져갈 수
		// 있다)

		// Page 안에 map 메서드를 이용한다. -> list의 foreach와 유사하다.
		// this 객체를 통해 convertToDTO를 호출한다.
		// 반복할 때 마다 this(service)가 가르키는 값이 새로운 DTO에 들어간다.
		// a::b a의 b 메서드를 실행한다.
		// entityPage 객체 내의 엔티티들을 DTO객체로 변환하여 새로운 Page 객체 생성
		Page<BoardDTO> boardPage = entityPage.map(this::convertToDTO);
		// entityPage.map(entity -> converToDTO(entity)); 같은 의미이다.

		return boardPage;
	}

//service 내에서만 쓸 것이기 때문에 private
//entity의 값을 DTO에 담아준다.
	private BoardDTO convertToDTO(BoardEntity entity) {
		return BoardDTO.builder().boardNum(entity.getBoardNum()).memberId(entity.getMember().getMemberId())
				.memberName(entity.getMember().getMemberName()).title(entity.getTitle()).contents(entity.getContents())
				.viewCount(entity.getViewCount()).likeCount(entity.getLikeCount())
				.originalName(entity.getOriginalName()).fileName(entity.getFileName())
				.createDate(entity.getCreateDate()).updateDate(entity.getUpdateDate()).build();
	}

	public int upView(Integer boardNum) {
		BoardEntity entity = repository.findById(boardNum).orElseThrow(() -> new EntityNotFoundException("글이 없습니다."));
		entity.setViewCount(entity.getViewCount() + 1);
		return entity.getViewCount();
	}

	public BoardDTO view(Integer boardNum) {

		BoardEntity entity = repository.findById(boardNum).orElseThrow(() -> new EntityNotFoundException("글이 없습니다."));

		log.debug("조회한 엔티티 : {}", entity);
		// log.debug 등으로 객체를 부르면 toString 으로 부른다 ->boardEntity는 Data로 toString이 만들어져있다.
		// log.debug("조회한 엔티티 : {}", entity);
		// 에러가 뜨는 이유는 순환 참조 문제 (무한 호출) -> 서로 부르기 때문에
		// boardEntity의 toStirng를 호출하는데 안에 있는 내용인 Member를 읽기 위해 memberEntity를 호출하고
		// replyList를 읽기 위해 arrayList의 toStirng을 호출하고 arrayList는 안의 객체들의 toStirng을
		// 반복하여 호출하기 때문에 replyEntity를 호출한다.
		// replyEntity는 board를 참조하고 있기 때문에 replyEntity의 board를 읽을 때 boardEntity를 호출한다
		// 이 과정이 반복되기 때문에 무한으로 순환한다.
		// 순환 중간에 출력할 필요가 없는 부분을 끊어준다.

		BoardDTO dto = convertToDTO(entity);

		// 엔티티 내의 replyList를 ArrayList<ReplyDTO>로 변환해서 dto에 추가한다.
		// one to Many Many to One 으로 인하여 엔티티 내에는 replyList가 존재한다.
		List<ReplyDTO> replyList = new ArrayList<>();
		for (ReplyEntity replyEntity : entity.getReplyList()) {
			ReplyDTO replyDTO = ReplyDTO.builder().replyNum(replyEntity.getReplyNum())
					.boardNum(replyEntity.getBoard().getBoardNum()).memberId(replyEntity.getMember().getMemberId())
					.memberName(replyEntity.getMember().getMemberName()).contents(replyEntity.getContents())
					.createDate(replyEntity.getCreateDate()).build();
			replyList.add(replyDTO);
		}
		dto.setReplyList(replyList);
		return dto;
	}

	public void delete(int boardNum, String username, String uploadPath) {

		BoardEntity entity = repository.findById(boardNum).orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		if (!entity.getMember().getMemberId().equals(username)) {
			throw new RuntimeException("권한이 없습니다.");
		}
		// 글을 삭제하기전에 필요없는 파일인지 확인하고 첨부파일부터 삭제해야한다.
		if (entity.getFileName() != null && !entity.getFileName().isEmpty()) {
			File file = new File(uploadPath, entity.getFileName());
			file.delete(); // 지우면 true 리턴하고 못지우면 false를 리턴한다
		}
		repository.delete(entity);

	}

	public void update(BoardDTO boardDTO, String username, String uploadPath, MultipartFile upload) {

		BoardEntity entity = repository.findById(boardDTO.getBoardNum())
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		if (!entity.getMember().getMemberId().equals(username)) {
			throw new RuntimeException("권한이 없습니다.");
		}

		// 첨부파일 처리
		// 수정하면서 새로 첨부한 피일이 있으면
		if (upload != null && !upload.isEmpty()) {
			File directoryPath = new File(uploadPath);
			if (!directoryPath.isDirectory()) {
				directoryPath.mkdirs();
			}
		// 그전에 업로드한 기존 파일이 있으면 먼저 파일 삭제
		if (entity.getOriginalName() != null && !entity.getOriginalName().isEmpty()) {
			File file = new File(uploadPath, entity.getFileName());
			file.delete(); // 지우면 true 리턴하고 못지우면 false를 리턴한다
		}
		// 새로운 파일의 이름을 바꿔서 복사
			String originalName = upload.getOriginalFilename();
			String extension = originalName.substring(originalName.lastIndexOf("."));
			String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			String uuidString = UUID.randomUUID().toString();
			String fileName = dateString + "_" + uuidString + extension;
			try {
				File file = new File(uploadPath, fileName);
				// transferTO 메서드를 이용하면 바로 이동시켜준다.
				upload.transferTo(file);
				// 엔티티에 원래 파일명, 저장된파일명 추가
				entity.setOriginalName(originalName);
				entity.setFileName(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 전달된 정보 수정
		entity.setTitle(boardDTO.getTitle());
		entity.setContents(boardDTO.getContents());
	}

	public void replyWrite(ReplyDTO replyDTO) {
		MemberEntity memberEntity = memberRepository.findById(replyDTO.getMemberId())
				.orElseThrow(() -> new EntityNotFoundException("사용자 아이디가 없습니다."));

		BoardEntity boardEntity = repository.findById(replyDTO.getBoardNum())
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		ReplyEntity entity = ReplyEntity.builder().board(boardEntity).member(memberEntity)
				.contents(replyDTO.getContents()).build();

		replyRepository.save(entity);

	}

	public void replyDelete(Integer replyNum, String username) {
		ReplyEntity replyEntity = replyRepository.findById(replyNum)
				.orElseThrow(() -> new EntityNotFoundException("리플이 없습니다."));

		if (!replyEntity.getMember().getMemberId().equals(username)) {
			throw new RuntimeException("삭제 권한이 없습니다.");
		}
		replyRepository.delete(replyEntity);
	}

	/**
	 * 파일 다운로드
	 * @param boardNum 게시글 번호
	 * @param response 응답정보
	 * @param uploadPath 파일 저장 경로
	 */
	public void download(Integer boardNum, HttpServletResponse response, String uploadPath) {
		//전달받은 글 번호로 게시글 정보를  DB에서 조회
		//boardEntity로 값을 받아오고 없다면 에러를 던져준다.
		BoardEntity entity = repository.findById(boardNum)
				.orElseThrow(()-> new EntityNotFoundException("파일이 없습니다."));
		//원래의 파일명
		try {
			//response객채에 헤더를 생성한다.(content-disposition 설정할 헤더의 이름, 
			//attachment : filename= 의 파일을 다운로드 하라고 지시한다)
			//한글파일명들을 깨지지 않게하기 위하여 URLEncoder를 사용한다.
			response.setHeader("Content-Disposition", "attachment;filename="
					+URLEncoder.encode(entity.getOriginalName(),"UTF-8"));
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//저장된 파일 경로 ( C:/upload/240806_asdfasdf.jpg)와 같은 형식으로 받아온다.
		String fullPath = uploadPath + "/" + entity.getFileName();
				
		//서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
		FileInputStream filein = null; //하드디스크 내의 파일과 프로그램의 연결통로
		ServletOutputStream fileout = null; //프로그램과 클라이언트의 연결통로
		
		try {
			//fullPath를 통해 파일의 내용을 읽어올 입력 스트림을 생성하고(아직 파일을 읽어오진 않는다)
			filein = new FileInputStream(fullPath);
			//받아올 파일을 보내주기위해 출력스트림을 받아온다.
			fileout = response.getOutputStream();
			
			//원래는 반복문으로 filein 으로 읽고 fileout으로 출력하는 것을 1byte씩 반복해야했지만
			//Spring의 파일 관련 유틸을 이용하여 출력할 수 있다.
			FileCopyUtils.copy(filein, fileout);
			
			//데이터가 목적지까지 다 도착할 수 있도록 close를 해줘야한다.
			//스트림 닫기(스트림을 안 닫아주면 파일이 손실될 수 있기 때문에 꼭 닫아줘야한다)
			//중간에 데이터가 가기 전에 통로에 이상이 생겨도 스트림을 닫아주면 밀려있던 데이터를 다 보내고
			//스트림을 닫는다.
			filein.close();
			fileout.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
