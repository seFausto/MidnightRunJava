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

		HashMap<Object[], String> shapes = GetShapes();

		Iterator<Entry<Object[], String>> it = shapes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object[], String> pairs = (Map.Entry<Object[], String>) it
					.next();

			if (pairs.getValue().equals("Line")) {
				drawOracleMultiline(g, pairs);
			} else if (pairs.getValue().equals("Rectangle")) {
				drawOracleRectangle(g, pairs);
			} else if (pairs.getValue().equals("Polygon")) {
				drawOraclePolygon(g, pairs);
			} else if (pairs.getValue().equals("Bird")) {

				List<BigDecimal> xPoints = new ArrayList<BigDecimal>();
				List<BigDecimal> yPoints = new ArrayList<BigDecimal>();

				// get points in oracle
				for (int i = 0; i < pairs.getKey().length; i++) {
					xPoints.add((BigDecimal) pairs.getKey()[i]);
					yPoints.add((BigDecimal) pairs.getKey()[++i]);

				}

				// loop through oracle points
				for (int i = 0; i < xPoints.size(); i++) {
					BigDecimal x = xPoints.get(i);
					BigDecimal y = yPoints.get(i);

					i++;
					BigDecimal highestY;
					Boolean skipNextItem = false;
					// check if the next point is the end point
					if (y.equals(yPoints.get(i))) {
						// get the the Y for the next point
						highestY = yPoints.get(i + 1);
						skipNextItem = true;
					} else {
						highestY = yPoints.get(i);
						i++;
					}

					// calculate the width and height
					int width = (xPoints.get(i).intValue() - x.intValue())
							* multiplier;
					int height = (highestY.intValue() - y.intValue())
							* multiplier;
					// convert to java values

					g.drawArc(convertOracleXToJavaX(x),
							convertOracleYToJavaY(y), width, height, 0, 180);

					if (skipNextItem)
						i++;
				}

			}

		}

	}

	private void drawOraclePolygon(Graphics g, Map.Entry<Object[], String> pairs) {
		List<Integer> xPoints = new ArrayList<Integer>();
		List<Integer> yPoints = new ArrayList<Integer>();

		for (int i = 0; i < pairs.getKey().length; i++) {
			xPoints.add(convertOracleXToJavaX((BigDecimal) pairs.getKey()[i]));
			yPoints.add(convertOracleYToJavaY((BigDecimal) pairs.getKey()[++i]));

		}

		int[] x = new int[xPoints.size()];
		int[] y = new int[yPoints.size()];

		for (int i = 0; i < xPoints.size(); i++) {
			x[i] = xPoints.get(i);
			y[i] = yPoints.get(i);
		}

		g.drawPolygon(x, y, x.length);
	}

	private void drawOracleRectangle(Graphics g,
			Map.Entry<Object[], String> pairs) {
		List<BigDecimal> xPoints = new ArrayList<BigDecimal>();
		List<BigDecimal> yPoints = new ArrayList<BigDecimal>();

		for (int i = 0; i < pairs.getKey().length; i++) {
			xPoints.add((BigDecimal) pairs.getKey()[i]);
			yPoints.add((BigDecimal) pairs.getKey()[++i]);

		}

		// for a rectangle it goes from starting point to width and
		// height so do the math
		int x = convertOracleXToJavaX(xPoints.get(0));
		int y = convertOracleYToJavaY(yPoints.get(1));

		int width = convertOracleXToJavaX(xPoints.get(1)) - x;
		int height = (yPoints.get(1).intValue() - yPoints.get(0).intValue())
				* multiplier;
		g.drawRect(x, y, width, height);
	}

	private void drawOracleMultiline(Graphics g,
			Map.Entry<Object[], String> pairs) {
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

	private static HashMap<Object[], String> GetShapes() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates from MVDEMO.MidnightRun m where id=10 or id =1 or id =2 or id=11 or id=12 or id = 4 or id = 5";
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

				String type = "";
				if (((java.math.BigDecimal) elemInfo[1])
						.equals(java.math.BigDecimal.valueOf(2))) {
					if (((java.math.BigDecimal) elemInfo[2])
							.equals(java.math.BigDecimal.valueOf(2))) {
						type = "Bird";
					} else {
						type = "Line";
					}
				} else if (((java.math.BigDecimal) elemInfo[1])
						.equals(java.math.BigDecimal.valueOf(1003))) {
					type = "Rectangle";
				} else if (((java.math.BigDecimal) elemInfo[1])
						.equals(java.math.BigDecimal.valueOf(1005))) {
					type = "Polygon";
				}

				result.put((Object[]) ordinateArray.getArray(), type);
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
