package action;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.BoardDBBean;

public class ListAction implements CommandAction {
	
	public String requestPro(HttpServletRequest request,HttpServletResponse response)throws Throwable{
		String pageNum =request.getParameter("pageNum");
		
		//request.getSession(); 하면 세션을 가지고 올 수 있다.
		request.setCharacterEncoding("utf-8");
		if(pageNum==null){
			pageNum="1";
		}
		int pageSize=10;//한 페이지의 글의 개수
		int currentPage=Integer.parseInt(pageNum);
		int startRow=(currentPage-1)*pageSize+1;
		int endRow=currentPage*pageSize;
		int count=0;
		int number=0;

		List articleList=null;
		BoardDBBean dbPro=BoardDBBean.getInstance();
		count= dbPro.getArticleCount();
		
		if(count>0){
			articleList =dbPro.getArticles(startRow, endRow);
		}else{
			articleList=Collections.EMPTY_LIST;
		}
		number=count-(currentPage-1)*pageSize;
		request.setAttribute("currentPage", new Integer(currentPage));
		request.setAttribute("startRow", new Integer(startRow));
		request.setAttribute("endRow", new Integer(endRow));
		request.setAttribute("count", new Integer(count));  
		
		request.setAttribute("pageSize",new Integer(pageSize));
		request.setAttribute("number", new Integer(number));
		request.setAttribute("articleList", articleList);
		
		return "/MVC/list.jsp";
	}
}
