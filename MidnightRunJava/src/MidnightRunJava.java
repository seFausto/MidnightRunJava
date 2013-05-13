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
		final JPanel panel = new JPanel();
		getContentPane().add(panel);
		setSize(800, 600);

		JButton button = new JButton("Color Red");

		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			}
		});

		panel.add(button);
	}

	public void paint(Graphics g) {
		super.paint(g);

		HashMap<Object[], String> shapes = getShapes();

		Iterator<Entry<Object[], String>> it = shapes.entrySet().iterator();
		while (it.hasNext()) {
			paintAllOracleObjects(g, it);
		}

		// paint nearest neighbors in red
		shapes = getNearestNeighbor();
		g.setColor(Color.red);
		it = shapes.entrySet().iterator();
		while (it.hasNext()) {
			paintAllOracleObjects(g, it);
		}

	}

	private void paintAllOracleObjects(Graphics g,
			Iterator<Entry<Object[], String>> it) {
		Map.Entry<Object[], String> pairs = (Map.Entry<Object[], String>) it
				.next();

		if (pairs.getValue().equals("Line")) {
			drawOracleMultiline(g, pairs);
		} else if (pairs.getValue().equals("Rectangle")) {
			drawOracleRectangle(g, pairs);
		} else if (pairs.getValue().equals("Polygon")) {
			drawOraclePolygon(g, pairs);
		} else if (pairs.getValue().equals("Bird")) {
			drawOracleBrids(g, pairs);
		} else if (pairs.getValue().equals("Moon")) {
			drawOracleMoon(g, pairs);
		} else if (pairs.getValue().equals("Circle")) {
			drawOracleCircle(g, pairs);
		} else if (pairs.getValue().equals("LetterC")) {
			drawOraclePolygon(g, pairs);
		}
	}

	private void drawOracleCircle(Graphics g, Map.Entry<Object[], String> pairs) {
		// Get points
		List<BigDecimal> xPoints = new ArrayList<BigDecimal>();
		List<BigDecimal> yPoints = new ArrayList<BigDecimal>();

		for (int i = 0; i < pairs.getKey().length; i++) {
			xPoints.add((BigDecimal) pairs.getKey()[i]);
			yPoints.add((BigDecimal) pairs.getKey()[++i]);
		}

		// get points in oracle
		BigDecimal leftmostX = BigDecimal.valueOf(1000);
		BigDecimal topmostY = BigDecimal.ZERO;
		BigDecimal bottomY = BigDecimal.valueOf(1000);

		for (int i = 0; i < xPoints.size(); i++) {
			if (xPoints.get(i).intValue() < leftmostX.intValue()) {
				leftmostX = xPoints.get(i);
			}

			if (xPoints.get(i).intValue() > topmostY.intValue()) {
				topmostY = yPoints.get(i);
			}

			if (xPoints.get(i).intValue() < bottomY.intValue()) {
				bottomY = yPoints.get(i);
			}

		}

		int width = (topmostY.intValue() - bottomY.intValue()) * multiplier;

		g.drawOval(convertOracleXToJavaX(leftmostX),
				convertOracleYToJavaY(topmostY), width, width);
	}

	private void drawOracleMoon(Graphics g, Map.Entry<Object[], String> pairs) {
		// TODO: Fix how moon displays
		List<BigDecimal> xPoints = new ArrayList<BigDecimal>();
		List<BigDecimal> yPoints = new ArrayList<BigDecimal>();

		// get points in oracle
		for (int i = 0; i < pairs.getKey().length; i++) {
			xPoints.add((BigDecimal) pairs.getKey()[i]);
			yPoints.add((BigDecimal) pairs.getKey()[++i]);

		}

		// loop through oracle points
		for (int i = 0; i < 3; i++) {

			// get first point
			BigDecimal x = xPoints.get(i);
			BigDecimal y = yPoints.get(i);

			// get next point
			i++;
			BigDecimal rightmostX;
			Boolean skipNextItem = false;
			// check which point we have
			if (xPoints.get(i).equals(x)) {
				// if we have the same x as the first point
				// set rightmost x to the next point
				rightmostX = xPoints.get(i + 1);
				// set boolean to skip next element
				skipNextItem = true;
			} else {
				// if we have the right most point
				// set rightmostX to that
				rightmostX = xPoints.get(i);
				// skip to next point
				i++;
			}

			// calculate the width and height
			int width = (rightmostX.intValue() - x.intValue() + 2) * multiplier;
			int height = (yPoints.get(i).intValue() - y.intValue())
					* multiplier;
			// convert to java values

			g.drawArc(convertOracleXToJavaX(x), convertOracleYToJavaY(y),
					width, height, 90, -180);

			if (skipNextItem)
				i++;

		}

		for (int i = xPoints.size() - 1; i >= 3; i--) {

			// get first point
			BigDecimal x = xPoints.get(i);
			BigDecimal y = yPoints.get(i);

			// get next point
			i--;
			BigDecimal rightmostX;
			Boolean skipNextItem = false;
			// check which point we have
			if (xPoints.get(i).equals(x)) {
				// if we have the same x as the first point
				// set rightmost x to the next point
				rightmostX = xPoints.get(i - 1);
				// set boolean to skip next element
				skipNextItem = true;
			} else {
				// if we have the right most point
				// set rightmostX to that
				rightmostX = xPoints.get(i);
				// skip to next point
				i--;
			}

			// calculate the width and height
			int width = (rightmostX.intValue() - x.intValue() + 2) * multiplier;
			int height = (yPoints.get(i).intValue() - y.intValue())
					* multiplier;
			// convert to java values

			g.drawArc(convertOracleXToJavaX(x), convertOracleYToJavaY(y),
					width, height, 90, -180);

			if (skipNextItem)
				i++;

		}
	}

	private void drawOracleBrids(Graphics g, Map.Entry<Object[], String> pairs) {
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
			int width = (xPoints.get(i).intValue() - x.intValue()) * multiplier;
			int height = (highestY.intValue() - y.intValue()) * multiplier;
			// convert to java values

			g.drawArc(convertOracleXToJavaX(x), convertOracleYToJavaY(y),
					width, height, 0, 180);

			if (skipNextItem)
				i++;
		}
	}

	private void drawOraclePolygon(Graphics g, Map.Entry<Object[], String> pairs) {
		// TODO: Fix how polygon displays

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

	private static HashMap<Object[], String> getShapes() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT m.name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates from MVDEMO.cola_markets m";

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
				String name = rs.getString("name");

				getOracleObjectType(result, elemInfo, ordinateArray, name);
			}

		} catch (SQLException e) {

		}

		return result;

	}

	private static HashMap<Object[], String> getNearestNeighbor() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT  m.name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates FROM cola_markets m WHERE SDO_NN(m.shape, sdo_geometry(2001, NULL, sdo_point_type(40,2,NULL), NULL,   NULL),  'sdo_num_res=4') = 'TRUE'";

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
				String name = rs.getString("name");

				getOracleObjectType(result, elemInfo, ordinateArray, name);
			}

		} catch (SQLException e) {

		}

		return result;

	}

	private static void getOracleObjectType(HashMap<Object[], String> result,
			Object[] elemInfo, Array ordinateArray, String name)
			throws SQLException {
		String type = "";
		if (((java.math.BigDecimal) elemInfo[1]).equals(java.math.BigDecimal
				.valueOf(2))) {
			if (((java.math.BigDecimal) elemInfo[2])
					.equals(java.math.BigDecimal.valueOf(2))) {
				if (name.equals("Letter C")) {
					type = "LetterC";
				} else {
					type = "Bird";
				}
			} else {
				type = "Line";
			}
		} else if (((java.math.BigDecimal) elemInfo[1])
				.equals(java.math.BigDecimal.valueOf(1003))) {
			if (((java.math.BigDecimal) elemInfo[2])
					.equals(java.math.BigDecimal.valueOf(4))) {
				type = "Circle";
			} else if (((java.math.BigDecimal) elemInfo[2])
					.equals(java.math.BigDecimal.valueOf(3))) {
				type = "Rectangle";
			} else if (((java.math.BigDecimal) elemInfo[2])
					.equals(java.math.BigDecimal.valueOf(1))) {
				type = "Polygon";
			} else if (((java.math.BigDecimal) elemInfo[2])
					.equals(java.math.BigDecimal.valueOf(2))) {
				type = "Moon";
			}
		}
		result.put((Object[]) ordinateArray.getArray(), type);
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
