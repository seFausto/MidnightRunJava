import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.sql.*;

public class MidnightRunJava extends JFrame {

	public MidnightRunJava() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		setSize(450, 450);

		JButton button = new JButton("press");
		panel.add(button);
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		
		Line2D lin = new Line2D.Float(100, 100, 250, 260);
		List<java.sql.Struct> d = GetShapes();
		
		try {
			Object[] a = d.get(0).getAttributes();
			a = a;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		MidnightRunJava mrj = new MidnightRunJava();
		mrj.setVisible(true);

	}

	private static List<java.sql.Struct> GetShapes(){
		List<java.sql.Struct> result = new ArrayList<java.sql.Struct>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT Shape from MVDEMO.MidnightRun where id=1";
		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			con = DriverManager
					.getConnection("jdbc:oracle:thin:mvdemo/mvdemo@localhost:1521:orcl");

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				result.add((java.sql.Struct) rs.getObject("Shape"));
			}

		} catch (SQLException e) {
			
		}

		return result;

	}
}
