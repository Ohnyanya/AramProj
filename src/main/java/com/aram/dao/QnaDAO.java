package com.aram.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.aram.dto.NoticeDTO;
import com.aram.dto.QnaDTO;

public class QnaDAO {
	private BasicDataSource bds;
	
	public QnaDAO() {
		try {
			Context iCtx = new InitialContext();
			Context envCtx = (Context)iCtx.lookup("java:comp/env");
			bds = (BasicDataSource)envCtx.lookup("jdbc/bds");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//QnA 삭제 
	public int deleteByQnA_no(int qna_no) throws Exception{
		String sql = "delete from tbl_qna where qna_no = ?";
						
		 try(Connection con = bds.getConnection();
			 PreparedStatement pstmt =  con.prepareStatement(sql)){
			    	
			    pstmt.setInt(1, qna_no);
			    	
			    return pstmt.executeUpdate();
			    
		 }
			
	}
	
	
	
	// qna 게시글 작성
	public int write(QnaDTO dto)throws Exception {
		String sql = "INSERT into tbl_qna values(seq_qna.nextval, ?, ?, ?, sysdate, default, default, sysdate)";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			pstmt.setString(1, dto.getUser_id());
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3, dto.getContent());
			
			int rs = pstmt.executeUpdate();
			
			return rs;
		}
	}
	
	// qna 게시글 삭제
	public int delete(int qna_no)throws Exception {
		String sql = "DELETE FROM tbl_qna WHERE qna_no = ?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			pstmt.setInt(1, qna_no);
			
			int rs = pstmt.executeUpdate();
			return rs;
		}
	}
	
	
	// qna 게시글 수정
	public int modify(QnaDTO dto)throws Exception{
		String sql = "UPDATE tbl_qna SET title = ?, content = ? WHERE qna_no = ?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getQna_no());
			
			int rs = pstmt.executeUpdate();
			return rs;
		}
	}
	
	
	// qna 게시글 조회 _ 전체
	public ArrayList<QnaDTO> qnaSelectAll() throws Exception{ 
		String sql =  "select * from tbl_qna order by 1 desc";
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			ResultSet rs = pstmt.executeQuery();
			
			ArrayList<QnaDTO> list = new ArrayList<>();
			while(rs.next()) {
				int notice_no = rs.getInt("qna_no");
				String user_id = rs.getString("user_id");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String write_date = getStringDate(rs.getDate("write_date"));
				boolean answer_yn = rs.getBoolean("answer_yn");
				String answer = rs.getString("answer");
				String answer_date = getStringDate(rs.getDate("answer_date"));
				

				
				list.add(new QnaDTO(notice_no, user_id, title, content, write_date, answer_yn, answer, answer_date));
				
			}
			return list;
		}
	}
	
	//qna 게시글 조회 _ 부분 (detailview)
	public QnaDTO selectByNo(int qna_no) throws Exception{
		String sql = "SELECT * FROM tbl_qna Where qna_no = ?";
		
		try(Connection con = bds.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql)){
			
			pstmt.setInt(1, qna_no);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String user_id = rs.getString("user_id");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String write_date = getStringDate(rs.getDate("write_date"));
				Boolean answer_yn = rs.getBoolean("answer_yn");
				String answer = rs.getString("answer");
				String answer_date = getStringDate(rs.getDate("answer_date"));
				QnaDTO dto = new QnaDTO(qna_no, user_id, title, content, write_date, answer_yn, answer, answer_date);
				return dto;
			}
			return null;
		}
	}
	
	//날짜 String으로 변환
	public String getStringDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(date);
	}
	
	
	// Qna 게시판 게시글 검색
		// id(글쓴이)로 검색
		public ArrayList<QnaDTO> searchByUserId(String searchByUserId) throws Exception{
			String sql = "select * from tbl_qna where user_id = ? order by 1 desc";
			try(Connection con = bds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql)){
				
				pstmt.setString(1, searchByUserId);
				
				ResultSet rs = pstmt.executeQuery();
				ArrayList<QnaDTO> list = new ArrayList<>();
				while(rs.next()) {
					int qna_no = rs.getInt("qna_no");
					String user_id = rs.getString("user_id");
					String title = rs.getString("title");
					String content = rs.getString("content");
					String write_date = getStringDate(rs.getDate("write_date"));
					boolean answer_yn = rs.getBoolean("answer_yn");
					String answer = rs.getString("answer");
					String answer_date = getStringDate(rs.getDate("answer_date"));

					
					
					list.add(new QnaDTO(qna_no, user_id, title, content, write_date, answer_yn, answer, answer_date));
					
					System.out.println(list);
				}
				return list;
			}
		}
		
		// 제목으로 검색
		public ArrayList<QnaDTO> searchByTitle(String searchTitle) throws Exception{
			String sql = "select * from tbl_qna where title like '%'||?||'%' order by 1 desc";
			try(Connection con = bds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql)){
				
				pstmt.setString(1, searchTitle);
				
				ResultSet rs = pstmt.executeQuery();
				ArrayList<QnaDTO> list = new ArrayList<>();
				while(rs.next()) {
					int qna_no = rs.getInt("qna_no");
					String user_id = rs.getString("user_id");
					String title = rs.getString("title");
					String content = rs.getString("content");
					String write_date = getStringDate(rs.getDate("write_date"));
					boolean answer_yn = rs.getBoolean("answer_yn");
					String answer = rs.getString("answer");
					String answer_date = getStringDate(rs.getDate("answer_date"));

					
					
					list.add(new QnaDTO(qna_no, user_id, title, content, write_date, answer_yn, answer, answer_date));
					
					System.out.println(list);
				}
				return list;
			}
		
		}
		
		
		//관리자페이지 - Q&A관리 내용으로 검색
		public ArrayList<QnaDTO> searchByContent(String contents) throws Exception{
			String sql = "select * from tbl_qna where content like '%'||?||'%' order by 1 desc";
			
			try(Connection con = bds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql)){
				
				
				pstmt.setString(1, contents);
				
				ResultSet rs = pstmt.executeQuery();
				ArrayList<QnaDTO> list = new ArrayList<>();
				while(rs.next()) {
					int qna_no = rs.getInt("qna_no");
					String user_id = rs.getString("user_id");
					String title = rs.getString("title");
					String content = rs.getString("content");
					String write_date = getStringDate(rs.getDate("write_date"));
					boolean answer_yn = rs.getBoolean("answer_yn");
					String answer = rs.getString("answer");
					String answer_date = getStringDate(rs.getDate("answer_date"));

					
					
					list.add(new QnaDTO(qna_no, user_id, title, content, write_date, answer_yn, answer, answer_date));
					
					System.out.println(list);
				}
				return list;
			}
			
			
		}
		

		
}
