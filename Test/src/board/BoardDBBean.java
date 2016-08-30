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

	// mybatis ����
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
				System.out.println("insert ����");
			} else {
				System.out.println("insert ����");
			}
		} catch (IOException ioe) {
			session.rollback();
		} finally {
			session.commit();
			session.close();
		}
	}

	// list.jsp : ����¡�� ���ؼ� ��ü DB�� �Էµ� ���Ǽ��� �ʿ��ϴ�...!!!
	public int getArticleCount() throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		int x = 0;
		x = session.selectOne("board.countBoard");
		return x;
	}

	// list.jsp ==> Paging!!! DB�κ��� �������� ����� �޴´�.
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

	// read.jsp : DB�κ��� ������ �����͸� �����´�.
	public BoardDataBean getArticle(int num) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();

		int count = session.update("board.updateCount", num);
		if (count == 0) {
			System.out.println("������");
		} else {
			System.out.println("����");
		}

		BoardDataBean article = session.selectOne("board.getArticle", num);
		session.commit();
		session.close();
		return article;
	}

	// updateForm.jsp : �������� ������ �����͸� �����ö�.
	public BoardDataBean updateGetArticle(int num) throws Exception {
		String res = "config.xml";
		InputStream is = Resources.getResourceAsStream(res);
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		SqlSession session = factory.openSession();
		BoardDataBean article = session.selectOne("board.getArticle", num);
		session.close();
		return article;
	}

	// updatePro.jsp : ���� �����͸� �����ϴ� �޼ҵ�.
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

	// deletePro.jsp : ���� �����͸� �����ϴ� �޼ҵ�...
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
					x = 1; // �ۻ��� ����
				} else
					x = 0; // ��й�ȣ Ʋ��
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