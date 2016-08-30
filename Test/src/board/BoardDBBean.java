package board;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

//BoardDBBean bd = BoardDBBean.getInstance()
public class BoardDBBean {
	/*
	 * private static String res= "config.xml"; private static SqlSession
	 * session=null; private static InputStream is=null; private static
	 * SqlSessionFactory factory=null;
	 */
	private static BoardDBBean instance = new BoardDBBean();

	public static BoardDBBean getInstance() {
		return instance;
	}

	private BoardDBBean() {
	}

	// mybatis 없이
	/*
	 * private Connection getConnection() throws Exception { String jdbcDriver =
	 * "jdbc:apache:commons:dbcp:/pool"; return
	 * DriverManager.getConnection(jdbcDriver); }
	 */
	// writePro.jsp
	public void insertArticle(BoardDataBean article) throws Exception {
		String res = "config.xml";
		SqlSession session = null;
		try {
			InputStream is = Resources.getResourceAsStream(res);
			SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
			session = factory.openSession();
			int num = article.getNum();
			int ref = article.getRef();
			int re_step = article.getRe_step();
			int re_level = article.getRe_level();
			Integer number = session.selectOne("board.maxNum");
			if (number == null) {
				number = new Integer(1);
			} else {
				number += 1;
			}
			if (num != 0) {
				int h = session.update("board.reUp", article);
				article.setRe_step(re_step + 1);
				article.setRe_level(re_level + 1);
				System.out.println(h);
			} else {
				article.setRef(number);
				article.setRe_step(0);
				article.setRe_level(0);
			}
			int a = session.insert("board.insertBoard", article);
			if (a == 0) {
				System.out.println("insert 실패");
			} else {
				System.out.println("insert 성공");
			}
		} catch (IOException ioe) {
			session.rollback();
		} finally {
			session.commit();
			session.close();
		}
	}

	// list.jsp : 페이징을 위해서 전체 DB에 입력된 행의수가 필요하다...!!!
	public int getArticleCount() throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		int x = 0;
		x = session.selectOne("board.countBoard");
		return x;
	}

	// list.jsp ==> Paging!!! DB로부터 여러행을 결과로 받는다.
	public List getArticles(int start, int end) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		List articleList = null;
		HashMap map = new HashMap();
		map.put("start", start);
		map.put("end", end);
		// RowBounds rowbounds=new RowBounds(start,end);
		articleList = session.selectList("board.selectList", map);
		return articleList;
	}

	// read.jsp : DB로부터 한줄의 데이터를 가져온다.
	public BoardDataBean getArticle(int num) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();

		int count = session.update("board.updateCount", num);
		if (count == 0) {
			System.out.println("못읽음");
		} else {
			System.out.println("읽음");
		}

		BoardDataBean article = session.selectOne("board.getArticle", num);
		session.commit();
		session.close();
		return article;
	}

	// updateForm.jsp : 수정폼에 한줄의 데이터를 가져올때.
	public BoardDataBean updateGetArticle(int num) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		BoardDataBean article = session.selectOne("board.getArticle", num);
		session.close();
		return article;
	}

	// updatePro.jsp : 실제 데이터를 수정하는 메소드.
	public int updateArticle(BoardDataBean article) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		String dbpasswd = "";
		String sql = "";
		int num = article.getNum();
		int x = -1;
		dbpasswd = session.selectOne("board.resultPwd", num);
		if (dbpasswd.equals(article.getPasswd())) {

			x = session.update("updateArticle");
		} else {
			x = 0;
		}
		return x;
	}

	// deletePro.jsp : 실제 데이터를 삭제하는 메소드...
	public int deleteArticle(int num, String passwd) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String dbpasswd = "";
		int x = -1;
		try {
			conn = getConnection();

			pstmt = conn.prepareStatement("select passwd from board where num = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dbpasswd = rs.getString("passwd");
				if (dbpasswd.equals(passwd)) {
					pstmt = conn.prepareStatement("delete from board where num=?");
					pstmt.setInt(1, num);
					pstmt.executeUpdate();
					x = 1; // 글삭제 성공
				} else
					x = 0; // 비밀번호 틀림
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException ex) {
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ex) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {
				}
		}
		return x;
	}
}