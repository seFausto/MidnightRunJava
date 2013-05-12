import java.awt.*;
import java.util.List;
import javax.swing.*;
import java.util.*;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.sql.*;

public class MidnightRunJava extends JFrame {

	public static int originY = 500;
	public static int multiplier = 5;

	public MidnightRunJava() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		setSize(800, 600);

		JButton button = new JButton("press");
		panel.add(button);
	}

	public void paint(Graphics g) {
		super.paint(g);

		HashMap<Object[], java.math.BigDecimal> shapes = GetShapes();

		Iterator<Entry<Object[], BigDecimal>> it = shapes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object[], java.math.BigDecimal> pairs = (Map.Entry<Object[], BigDecimal>) it
					.next();

			if (pairs.getValue().equals(java.math.BigDecimal.valueOf(2)))
				drawOracleMultiline(g, pairs);
			else if (pairs.getValue()
					.equals(java.math.BigDecimal.valueOf(1003))) {
				List<Integer> xPoints = new ArrayList<Integer>();
				List<Integer> yPoints = new ArrayList<Integer>();

				for (int i = 0; i < pairs.getKey().length; i++) {
					xPoints.add(convertOracleXToJavaX((BigDecimal) pairs.getKey()[i]));
					yPoints.add(convertOracleYToJavaY((BigDecimal) pairs.getKey()[++i]));

				}

				// for a rectangle it goes from starting point to width and
				// height
				// so do the math
				int x = xPoints.get(0);
				int y = yPoints.get(0);
				
				int width = xPoints.get(1) - xPoints.get(0) ;
				int height = yPoints.get(1) - yPoints.get(0);
				
				g.drawRect(x, y, width, height);

			}
		}

	}

	private void drawOracleMultiline(Graphics g,
			Map.Entry<Object[], java.math.BigDecimal> pairs) {
		for (int i = 0; i < pairs.getKey().length; i++) {
			int x1 = convertOracleXToJavaX((BigDecimal) pairs.getKey()[i]);
			int y1 = convertOracleYToJavaY((BigDecimal) pairs.getKey()[++i]);

			int x2 = convertOracleXToJavaX((BigDecimal) pairs.getKey()[++i]);
			int y2 = convertOracleYToJavaY((BigDecimal) pairs.getKey()[++i]);

			g.drawLine(x1, y1, x2, y2);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		MidnightRunJava mrj = new MidnightRunJava();
		mrj.setVisible(true);

	}

	private static HashMap<Object[], java.math.BigDecimal> GetShapes() {
		HashMap<Object[], java.math.BigDecimal> result = new HashMap<Object[], java.math.BigDecimal>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates from MVDEMO.MidnightRun m where id=10 or id =1 or id =2 ";
		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			con = DriverManager
					.getConnection("jdbc:oracle:thin:mvdemo/mvdemo@localhost:1521:orcl");

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				Array infoArray = rs.getArray("info");
				Object[] elemInfo = (Object[]) infoArray.getArray();

				Array ordinateArray = rs.getArray("ordinates");
				result.put((Object[]) ordinateArray.getArray(),
						(java.math.BigDecimal) elemInfo[1]);
			}

		} catch (SQLException e) {

		}

		return result;

	}

	private static int convertOracleYToJavaY(BigDecimal oracleY) {
		return convertOracleYToJavaY(oracleY.intValue());

	}

	private static int convertOracleYToJavaY(int oracleY) {
		int result;

		result = originY;

		// multiply the value for Y times the size variable

		int subtractor = (oracleY * multiplier);
		// need to subtract from the origin, as the higher up on the screen the
		// lower the Y is
		result -= subtractor;

		return result;

	}

	private static int convertOracleXToJavaX(BigDecimal oracleY) {
		return convertOracleXToJavaX(oracleY.intValue());

	}

	private static int convertOracleXToJavaX(int oracleY) {
		return oracleY * multiplier;

	}
}
