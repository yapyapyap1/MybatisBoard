package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CommandAction {
	public String requestPro(HttpServletRequest request,HttpServletResponse response)throws Throwable;
	//항상 request랑 response를 가지고 다녀야됨
	//session이 필요하다면 request만 가지고 가며노딘다
}
