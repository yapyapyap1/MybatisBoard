package action;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.BoardDBBean;
import board.BoardDataBean;

public class WriteProAction implements CommandAction{
	
	public String requestPro(HttpServletRequest request, HttpServletResponse response)throws Throwable{
		request.setCharacterEncoding("utf-8");
		
		
		int num= Integer.parseInt(request.getParameter("num"));
		String writer=request.getParameter("writer");
		String email=request.getParameter("email");
		String subject=request.getParameter("subject");
		String passwd=request.getParameter("passwd");		
		int ref=Integer.parseInt(request.getParameter("ref"));
		int re_step=Integer.parseInt(request.getParameter("re_step"));
		int re_level=Integer.parseInt(request.getParameter("re_level"));
		String content=request.getParameter("content");
		String ip=request.getRemoteAddr();
		BoardDataBean article= new BoardDataBean(num,writer,subject,email,content,passwd,null,0,ip,ref,re_step,re_level);
		BoardDBBean dbPro=BoardDBBean.getInstance();
		dbPro.insertArticle(article);
		return "/MVC/writePro.jsp";
	}
}
