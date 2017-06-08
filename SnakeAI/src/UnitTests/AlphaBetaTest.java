package UnitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Logic.Field;
import Logic.Point;
import Util.Node;
import Util.AlphaBeta;

public class AlphaBetaTest {

	@Test
	public void testMaxFunction() {
		Field f = Field.defaultField(30, 20);
		AlphaBeta find = new AlphaBeta();
		
//		assertEquals("Point [x=2, y=10] <- Point [x=3, y=10] <- Point [x=4, y=10] <- Point [x=5, y=10] <- Point [x=6, y=10] <- Point [x=7, y=10] <- Point [x=8, y=10] <- Point [x=9, y=10] <- Point [x=10, y=10]", node.getPath());
	}

}
