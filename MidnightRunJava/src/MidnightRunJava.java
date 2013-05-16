import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.List;
import javax.swing.*;

import java.util.*;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.sql.*;

public class MidnightRunJava extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int originY = 500;
	public static int multiplier = 5;

	public static Boolean showMBR = false;
	public static Boolean showLenghtOfMountains = false;
	public static Boolean showAllShapes = true;
	public static Boolean showIntersection = false;
	public static Boolean showNN = false;

	public static Boolean moveCar = false;

	public static int pixelsToMove = 100;
	static MidnightRunJava mrj;

	static String queryAllShapes = "SELECT m.name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates from MVDEMO.cola_markets m";
	static String queryAllShapesWithoutLetters = "select  m.name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates  from mvdemo.cola_markets m where name not in ('Letter A', 'Letter E', 'Letter C')";
	private int speed = 5;

	public MidnightRunJava() {
		final JPanel panel = new JPanel();
		getContentPane().add(panel);
		setSize(800, 600);

		JButton btnShowMBR = new JButton("MBR For Tire");

		btnShowMBR.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showMBR = !showMBR;
				mrj.repaint();
			}
		});

		JButton btnShowLenghtOfMountains = new JButton("Show Mountain Lenght");

		btnShowLenghtOfMountains
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						showLenghtOfMountains = !showLenghtOfMountains;
						mrj.repaint();
					}
				});

		JButton btnShowAllShapes = new JButton("Show All Shapes");
		btnShowAllShapes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showAllShapes = !showAllShapes;
				mrj.repaint();
			}
		});

		JButton btnShowIntersection = new JButton("Show Intersection");
		btnShowIntersection
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						showIntersection = !showIntersection;
						mrj.repaint();
					}
				});

		JButton btnShowNN = new JButton("ShowNN");
		btnShowNN.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showNN = !showNN;
				mrj.repaint();
			}
		});

		JButton btnShowMoon = new JButton("ShowMoon");
		btnShowMoon.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (queryAllShapes.contains("where mkt_id <> 3"))
					queryAllShapes = queryAllShapes.replace(
							"where mkt_id <> 3", "");
				else
					queryAllShapes += " where mkt_id <> 3";
				mrj.repaint();
			}
		});

		panel.add(btnShowAllShapes);
		panel.add(btnShowMBR);
		panel.add(btnShowLenghtOfMountains);
		panel.add(btnShowIntersection);
		panel.add(btnShowNN);
		panel.add(btnShowMoon);
	}

	public void paint(Graphics g) {
		super.paint(g);

		if (moveCar) {
			showMBR = false;
			showLenghtOfMountains = false;
			showAllShapes = false;
			showIntersection = false;
			showNN = false;
		}
		
		if (pixelsToMove == 0 )
		{
			showAllShapes = true;
			moveCar = false;
		}

		if (showLenghtOfMountains) {
			g.drawString("Lenght of Mountains: "
					+ getLengthOfShape().toString(), 500, 200);
		}

		Font font = new Font("Arial", Font.PLAIN, 32);
		g.setFont(font);
		g.drawString("Midnight Run", 150, 200);

		HashMap<Object[], String> shapes = getShapes();
		Iterator<Entry<Object[], String>> it;

		if (showAllShapes) {
			it = shapes.entrySet().iterator();
			while (it.hasNext()) {
				paintAllOracleObjects(g, it);
			}
		}
		// paint nearest neighbors in red
		if (showNN) {
			shapes = getNearestNeighbor();
			g.setColor(Color.red);
			it = shapes.entrySet().iterator();
			while (it.hasNext()) {
				paintAllOracleObjects(g, it);
			}
		}

		// paint intersecting polygons green
		if (showIntersection) {
			shapes = getIntersectionBetweenBoxAndCabin();
			g.setColor(Color.green);
			it = shapes.entrySet().iterator();
			while (it.hasNext()) {
				paintAllOracleObjects(g, it);
			}
		}

		if (showMBR) {
			// paint MBR of Shape
			shapes = getMBRForShape();
			g.setColor(Color.blue);
			it = shapes.entrySet().iterator();
			while (it.hasNext()) {
				paintAllOracleObjects(g, it);
			}
		}

		if (moveCar) {

			shapes = getShapesWithoutLetters();
			HashMap<Object[], String> car = new HashMap<Object[], String>();

			it = shapes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object[], String> temp = (Map.Entry<Object[], String>) it
						.next();
				if (temp.getValue() == "Rectangle"
						|| temp.getValue() == "Polygon"
						|| temp.getValue() == "Circle") {
					car.put(temp.getKey(), temp.getValue());
				} else {

					paintAllOracleObjects(g, temp);
				}
			}

			Iterator<Entry<Object[], String>> carIterator;

			
			for (pixelsToMove = 100; pixelsToMove > 0; pixelsToMove=pixelsToMove -speed  ) {
				carIterator = car.entrySet().iterator();
				g.clearRect(200, 400, 1000, 100);
				while (carIterator.hasNext()) {
					paintCarMovement(g, carIterator, pixelsToMove);
				}
			}
		
		}
	}

	private void paintCarMovement(Graphics g,
			Iterator<Entry<Object[], String>> it, int pixelsToMove) {
		Map.Entry<Object[], String> pairs = (Map.Entry<Object[], String>) it
				.next();

		if (pairs.getValue().equals("Rectangle")) {
			drawOracleRectangleWithMove(g, pairs, pixelsToMove);
		} else if (pairs.getValue().equals("Polygon")) {
			drawOraclePolygonWithMove(g, pairs, pixelsToMove);
		} else if (pairs.getValue().equals("Circle")) {
			drawOracleCircleWithMove(g, pairs, pixelsToMove);
		}

	}

	private void drawOracleCircleWithMove(Graphics g,
			Entry<Object[], String> pairs, int pixelsToMove2) {
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

		g.drawOval(convertOracleXToJavaX(leftmostX) + pixelsToMove2,
				convertOracleYToJavaY(topmostY), width, width);
		
	}

	private void drawOraclePolygonWithMove(Graphics g,
			Entry<Object[], String> pairs, int pixelsToMove2) {
		List<Integer> xPoints = new ArrayList<Integer>();
		List<Integer> yPoints = new ArrayList<Integer>();

		for (int i = 0; i < pairs.getKey().length; i++) {
			xPoints.add(convertOracleXToJavaX((BigDecimal) pairs.getKey()[i]));
			yPoints.add(convertOracleYToJavaY((BigDecimal) pairs.getKey()[++i]));

		}

		int[] x = new int[xPoints.size()];
		int[] y = new int[yPoints.size()];

		for (int i = 0; i < xPoints.size(); i++) {
			x[i] = xPoints.get(i) + pixelsToMove2;
			y[i] = yPoints.get(i);
		}

		g.drawPolygon(x, y, x.length);
		
	}

	private void drawOracleRectangleWithMove(Graphics g,
			Entry<Object[], String> pairs, int pixelsToMove) {
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
		g.clearRect(x + pixelsToMove + 1, y, width, height);
		g.drawRect(x + pixelsToMove, y, width, height);

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HashMap<Object[], String> getShapesWithoutLetters() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			con = DriverManager
					.getConnection("jdbc:oracle:thin:mvdemo/mvdemo@localhost:1521:orcl");

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(queryAllShapesWithoutLetters);

			while (rs.next()) {

				Array infoArray = rs.getArray("info");
				Object[] elemInfo = (Object[]) infoArray.getArray();

				Array ordinateArray = rs.getArray("ordinates");
				String name = rs.getString("name");

				getOracleObjectType(result, elemInfo, ordinateArray, name);
			}

		} catch (SQLException e) {
			System.out.print(e.getMessage());
		}

		return result;

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

	private void paintAllOracleObjects(Graphics g, Entry<Object[], String> pairs) {
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

	// Operations on Oracle Objects
	// Get all shapes
	private static HashMap<Object[], String> getShapes() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			con = DriverManager
					.getConnection("jdbc:oracle:thin:mvdemo/mvdemo@localhost:1521:orcl");

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(queryAllShapes);

			while (rs.next()) {

				Array infoArray = rs.getArray("info");
				Object[] elemInfo = (Object[]) infoArray.getArray();

				Array ordinateArray = rs.getArray("ordinates");
				String name = rs.getString("name");

				getOracleObjectType(result, elemInfo, ordinateArray, name);
			}

		} catch (SQLException e) {
			System.out.print(e.getMessage());
		}

		return result;

	}

	// Get nearest Neighbor
	private static HashMap<Object[], String> getNearestNeighbor() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT  m.name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates FROM cola_markets m ";
		query += " WHERE SDO_NN(m.shape, sdo_geometry(2001, NULL, sdo_point_type(40,2,NULL), NULL,   NULL),  'sdo_num_res=2') = 'TRUE'";

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

	// Get Intersection between 2 objects
	private static HashMap<Object[], String> getIntersectionBetweenBoxAndCabin() {
		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = "select  'Polygon' as name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates ";
		query += "from (SELECT SDO_GEOM.SDO_INTERSECTION(c_a.shape, c_c.shape, 0.005) as Shape ";
		query += "FROM cola_markets c_a, cola_markets c_c ";
		query += "   WHERE c_a.name = 'Box' AND c_c.name = 'Cabin') m";

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

	// Get MBR of an object
	private static HashMap<Object[], String> getMBRForShape() {

		HashMap<Object[], String> result = new HashMap<Object[], String>();

		Statement stmt = null;
		Connection con = null;

		String query = " select  'Rectangle' as name, m.Shape.sdo_elem_info as info, m.Shape.sdo_ordinates as ordinates ";
		query += "from (SELECT SDO_AGGR_MBR(shape) as Shape FROM mvdemo.cola_markets where mkt_id = 6) m";

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

	// Get Length of Object
	private static BigDecimal getLengthOfShape() {
		BigDecimal result = BigDecimal.ZERO;

		Statement stmt = null;
		Connection con = null;

		String query = "SELECT SDO_GEOM.SDO_LENGTH(c.shape, m.diminfo) as length ";
		query += "FROM cola_markets c, user_sdo_geom_metadata m ";
		query += "WHERE m.table_name = 'COLA_MARKETS' AND m.column_name = 'SHAPE' ";
		query += "AND c.name = 'Mountains'";

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			con = DriverManager
					.getConnection("jdbc:oracle:thin:mvdemo/mvdemo@localhost:1521:orcl");

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				result = rs.getBigDecimal("length");
			}

		} catch (SQLException e) {

			System.out.println("Error: " + e.getMessage());
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

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {

		mrj = new MidnightRunJava();
		mrj.setSize(600, 600);
		mrj.setVisible(true);
	}

}
