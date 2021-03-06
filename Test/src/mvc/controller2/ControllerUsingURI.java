package mvc.controller2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.CommandAction;



public class ControllerUsingURI extends HttpServlet {
	
	private Map commandMap=new HashMap();//명령어와 명령어 처리 클래스를 쌍으로 저장
	
	//명령어와 처리클래스가 매핑되어 있는 properties 파일을 읽어서 Map객체인 commandMap에 저장
	//명령어와 처리클래스가 매핑되어 있는 properties파일은 Command.properties파일
	
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		String props= config.getInitParameter("propertyConfig");//web.xml에서 propertyConfig에 해당 init-param의 값을 읽어옴
		Properties pr= new Properties();//명령어와 처리클래스의 매핑정보를 저장할 Properties객체 생성
		FileInputStream f=null;
		try{
			f=new FileInputStream(props);//Command.properties파일의 내용을 읽어옴
			pr.load(f);
		}catch(IOException e){
			throw new ServletException(e);
		}finally{
			if(f!=null)try{f.close();}catch(IOException ex){}
		}
		Iterator keyIter=pr.keySet().iterator();//Iterator객체는 Enumeration 객체를 확장시킨 개념의 객체
		while(keyIter.hasNext()){//객체를 하나씩 꺼내서 그 객체명으로 Properties객체에 저장된 객체에 접근
			String command=(String)keyIter.next();
			String className=pr.getProperty(command);
			try{
				Class commandClass=Class.forName(className);//해당 문자열을 클래스로 만든다.
				Object commandInstance=commandClass.newInstance();//해당클래스의 객체생성
				commandMap.put(command, commandInstance);
			}catch(ClassNotFoundException e){
				   throw new ServletException(e);
            } catch (InstantiationException e) {
                throw new ServletException(e);
            } catch (IllegalAccessException e) {
                throw new ServletException(e);
            }
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestPro(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestPro(request,response);
	}  
	
	private void requestPro(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String view=null;
		CommandAction com=null;
		try{
			String command=request.getRequestURI();  
			if(command.indexOf(request.getContextPath())==0){
				command=command.substring(request.getContextPath().length());
			}
			com=(CommandAction)commandMap.get(command);
			view= com.requestPro(request,response);
		}catch(Throwable e){
			throw new ServletException(e);
		}
		RequestDispatcher dispatcher=request.getRequestDispatcher(view);
		dispatcher.forward(request, response);
	}

}
