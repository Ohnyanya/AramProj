package com.aram.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.aram.dao.ItemDAO;
import com.aram.dto.ItemViewDTO;

import com.aram.dao.BlacklistDAO;
import com.aram.dto.BlacklistDTO;
import com.aram.dto.UserDTO;

import com.google.gson.Gson;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;


@WebServlet("*.admin")
public class AdminController extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String uri = request.getRequestURI();
		System.out.println("요청 uri" + uri);
		
		
		if(uri.equals("/itemUpload.admin")) { // 상품등록
			System.out.println("파일 업로드 요청");

		String realPath = request.getServletContext().getRealPath("");	
		
		String filePath = request.getServletContext().getRealPath("files");
			System.out.println(realPath + " : " +filePath);
		
		File dir = new File(filePath);	
		if(!dir.exists()) {
			dir.mkdirs();	
		}
		
		int maxSize = 1024*1024*10;
		
		
		try {
			
			MultipartRequest multi = new MultipartRequest(request,filePath,maxSize,"utf-8",new DefaultFileRenamePolicy());
			
			String ori_name = multi.getOriginalFileName("img_title");
			String sys_name	= multi.getFilesystemName("img_title");
			
			System.out.println("업로드할 파일명"+ori_name+" : "+sys_name);
			String item_name = multi.getParameter("item_name");
			String category_name = multi.getParameter("category_name");
			int price = Integer.parseInt(multi.getParameter("price"));
			int item_stock = Integer.parseInt(multi.getParameter("item_stock"));
			String item_comment = multi.getParameter("item_comment");
			
			System.out.println("파일등록 : "+item_name+" : "+category_name+" : "+price+" : "+item_stock+" : "+item_comment);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		} else if (uri.equals("/toItemPage.admin")) { // 관리자 페이지(상품관리) 이동 요청

			ItemDAO itemDao = new ItemDAO();
			
			try {
				ArrayList<ItemViewDTO> mngItemList = itemDao.mngItemList();
				System.out.println(mngItemList);
				
				int count = itemDao.countAllItems();
				
				request.setAttribute("count", count);
				request.setAttribute("mngItemList", mngItemList);
				request.getRequestDispatcher("/admin/itemManagePage.jsp").forward(request, response);
				
			} catch(Exception e) {
				e.printStackTrace();
			}

		} else if (uri.equals("/toUserManage.admin")) { //관리자 페이지(고객관리) 이동 요청
	         response.sendRedirect("/admin/blacklist.jsp");
	         
		} else if (uri.equals("/toNoticeManage.admin")) { // 관리자 페이지 (공지사항 관리) 이동 요청
			response.sendRedirect("/board/notice.jsp");
			
		} else if (uri.equals("/toModifyItem.admin")) { // 개별상품 수정버튼 눌렀을 때
			int item_no = Integer.parseInt(request.getParameter("item_no"));
			System.out.println(item_no);
			
			ItemDAO itemdao = new ItemDAO();
			try {
				ItemViewDTO dto = itemdao.selectByItemNo(item_no);
				request.setAttribute("dto", dto);
				System.out.println(dto);
				
				// RealPath로 쓰면 맨 앞에 슬래시가 2개가 더 생겨서 취소
//				String filePath = request.getServletContext().getRealPath("/resources/images/items");
//				System.out.println(filePath);
//				request.setAttribute("filePath", filePath);
				
				
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			request.getRequestDispatcher("/admin/itemModify.jsp").forward(request, response);

		} else if(uri.equals("/toUserManage.admin")) { //관리자 페이지(고객관리) 이동 요청
			
			BlacklistDAO BlacklistDAO = new BlacklistDAO();
			
			try {
				
				ArrayList<UserDTO> list = BlacklistDAO.ManageUserList();
				request.setAttribute("userList", list);
				
				request.getRequestDispatcher("/admin/blacklist.jsp").forward(request, response);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else if(uri.equals("/addBlacklist.admin")) { //블랙리스트 추가요청
			String[] user_id = request.getParameterValues("user_id");
			String black_detail = request.getParameter("black_detail");
			
			
			
			BlacklistDAO BlacklistDAO = new BlacklistDAO();
			
			try {
				int rs = 0;
				for(int i= 0; i <user_id.length; i++) {
					System.out.println(user_id[i]);		
					
					 rs = BlacklistDAO.insertBlacklist(user_id[i],black_detail);
					}if(rs > 0 ) {
						System.out.println("블랙리스트 추가 성공");
						
						ArrayList<BlacklistDTO> list = BlacklistDAO.selectAllBlacklist();
						request.setAttribute("blacklist", list);
						request.getRequestDispatcher("/toUserManage.admin").forward(request, response);
					
					}else {
						System.out.println("블랙리스트 추가 실패");
					}

				
			}catch(Exception e) {
				e.printStackTrace();
			}
	
		}else if(uri.equals("/showBlacklist.admin")) { //블랙리스트 출력 요청
			
			BlacklistDAO BlacklistDAO = new BlacklistDAO();
			
			try {
				ArrayList<BlacklistDTO> list =  BlacklistDAO.selectAllBlacklist();
				
				Gson gson = new Gson();
				String data = gson.toJson(list);
				response.setCharacterEncoding("utf-8");
				response.getWriter().append(data);	
			
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}else if(uri.equals("/noMoreBlacklist.admin")) { //블랙리스트 해제 요청
			String[] user_id = request.getParameterValues("user_id");
			System.out.println(user_id);
			
			BlacklistDAO BlacklistDAO = new BlacklistDAO();
			
			try {
				int rs = 0;
				for(int i = 0; i<user_id.length;i++) {
					System.out.println(user_id[i]);
					rs = BlacklistDAO.deleteFromBlacklist(user_id[i]);
					
				}
				if(rs > 0 ) {
					System.out.println("블랙리스트 해제 성공");
					response.sendRedirect("/toUserManage.admin");
					
				}else {
					System.out.println("블랙리스트 해제 실패");
				}
				
				
			}catch(Exception e) {
				e.printStackTrace();
			}

		}else if(uri.equals("/searchBlacklist.admin")) { //블랙리스트 검색 - 아이디 , 사유 , 등록일
			
			String user_id = request.getParameter("user_id");
			String black_detail = request.getParameter("black_detail");
			String black_date = request.getParameter("black_date");
			
			System.out.println(user_id +" : "+ black_detail +" : "+black_date);
			
			if(user_id != null && black_detail == null && black_date == null) {
				System.out.println("아이디만으로 검색 : "+user_id);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<BlacklistDTO> list = BlacklistDAO.selectBlacklistById(user_id);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}else if(user_id == null && black_detail != null && black_date == null) {
				System.out.println("블랙리스트 사유로 검색 : "+black_detail);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<BlacklistDTO> list = BlacklistDAO.selectBlacklistByDetail(black_detail);
					System.out.println(list);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else if(user_id == null && black_detail == null && black_date != null) {
				System.out.println("블랙리스트 등록일로 검색 : "+black_date);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<BlacklistDTO> list = BlacklistDAO.selectBlacklistByDate(black_date);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}else if(uri.equals("/searchUserlist.admin")) {
			String user_id = request.getParameter("user_id");
			String username = request.getParameter("username");
			String email = request.getParameter("email");
			
			System.out.println(user_id +" : "+username+" : "+email);
			
			if(user_id != null && username == null && email == null) { //id로검색
				System.out.println("아이디만으로 검색 : "+user_id);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<UserDTO> list = BlacklistDAO.selectUserlistById(user_id);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}else if(user_id == null && username != null && email == null) { //username으로 검색
				System.out.println("이름으로 검색 : "+username);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<UserDTO> list = BlacklistDAO.selectUserlistByUserName(username);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}else if(user_id == null && username == null && email != null) { //email으로 검색
				System.out.println("이메일로 검색: "+email);
				BlacklistDAO BlacklistDAO = new BlacklistDAO();
				
				try {
					
					ArrayList<UserDTO> list = BlacklistDAO.selectUserlistByEmail(email);
					Gson gson = new Gson();
					String data = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(data);	
					
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

			
		}else if(uri.equals("/toQnAManagePage.admin")) {//QnA 관리 페이지 요청
			response.sendRedirect("/admin/qna.jsp");
		}else if(uri.equals("/toReviewManage.admin")) {//리뷰 관리 페이지 요청
			response.sendRedirect("/admin/review.jsp");

		}

	}
}
