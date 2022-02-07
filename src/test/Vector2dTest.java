import static org.junit.Assert.*;

import agh.cs.evolution_generator.Maps.Vector2d;
import org.junit.Test;

public class Vector2dTest {
    @Test
    public void test_equals(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(1, 2);
        position2 = new Vector2d(1, 2);
        position3 = new Vector2d(2, 3);
        position4 = new Vector2d(2, 3);

        assertTrue(position1.equals(position2));
        assertFalse(position1.equals(position3));
        assertFalse(position1.equals(position4));

        assertFalse(position2.equals(position3));
        assertFalse(position2.equals(position4));

        assertTrue(position3.equals(position4));

    }

    @Test
    public void test_toString(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 2);
        position2 = new Vector2d(5, 2);
        position3 = new Vector2d(2, 4);
        position4 = new Vector2d(6, 3);


        assertEquals(position1.toString(), "(-1 , 2)");
        assertEquals(position2.toString(), "(5 , 2)");
        assertEquals(position3.toString(), "(2 , 4)");
        assertEquals(position4.toString(), "(6 , 3)");

    }

    @Test
    public void test_precedes(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 2);
        position2 = new Vector2d(5, 2);
        position3 = new Vector2d(2, 4);
        position4 = new Vector2d(6, 3);


        assertTrue(position1.precedes(position2));
        assertTrue(position1.precedes(position3));
        assertTrue(position1.precedes(position4));

        assertFalse(position2.precedes(position3));
        assertTrue(position2.precedes(position4));

        assertFalse(position3.precedes(position4));
    }

    @Test
    public void test_follows(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 2);
        position2 = new Vector2d(5, 4);
        position3 = new Vector2d(2, 4);
        position4 = new Vector2d(4, 2);

        assertFalse(position1.follows(position2));
        assertFalse(position1.follows(position3));
        assertFalse(position1.follows(position4));

        assertTrue(position2.follows(position3));
        assertTrue(position2.follows(position4));

        assertFalse(position3.follows(position4));

    }

    @Test
    public void test_upperRight(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 6);
        position2 = new Vector2d(5, 3);
        position3 = new Vector2d(-2, 0);
        position4 = new Vector2d(7, -2);


        assertEquals(position1.upperRight(position2), new Vector2d(5, 6));
        assertEquals(position1.upperRight(position3), new Vector2d(-1, 6));
        assertEquals(position1.upperRight(position4), new Vector2d(7, 6));

        assertEquals(position2.upperRight(position3), new Vector2d(5, 3));
        assertEquals(position2.upperRight(position4), new Vector2d(7, 3));

        assertEquals(position3.upperRight(position4), new Vector2d(7, 0));
    }

    @Test
    public void test_lowerLeft(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 6);
        position2 = new Vector2d(-3, 10);
        position3 = new Vector2d(-9, 2);
        position4 = new Vector2d(7, -2);

        assertEquals(position1.lowerLeft(position2), new Vector2d(-3, 6));
        assertEquals(position1.lowerLeft(position3), new Vector2d(-9, 2));
        assertEquals(position1.lowerLeft(position4), new Vector2d(-1, -2));

        assertEquals(position2.lowerLeft(position3), new Vector2d(-9, 2));
        assertEquals(position2.lowerLeft(position4), new Vector2d(-3, -2));

        assertEquals(position3.lowerLeft(position4), new Vector2d(-9, -2));
    }

    @Test
    public void test_add(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 6);
        position2 = new Vector2d(-3, 10);
        position3 = new Vector2d(-9, 2);
        position4 = new Vector2d(7, -2);

        assertEquals(position1.add(position2), new Vector2d(-4, 16));
        assertEquals(position1.add(position3), new Vector2d(-10, 8));
        assertEquals(position1.add(position4), new Vector2d(6, 4));

        assertEquals(position2.add(position3), new Vector2d(-12, 12));
        assertEquals(position2.add(position4), new Vector2d(4, 8));

        assertEquals(position3.add(position4), new Vector2d(-2, 0));
    }

    @Test
    public void test_subtract(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(1, 6);
        position2 = new Vector2d(-3, 10);
        position3 = new Vector2d(-2, 2);
        position4 = new Vector2d(7, -1);

        assertEquals(position1.subtract(position2), new Vector2d(4, -4));
        assertEquals(position1.subtract(position3), new Vector2d(3, 4));
        assertEquals(position1.subtract(position4), new Vector2d(-6, 7));

        assertEquals(position2.subtract(position3), new Vector2d(-1, 8));
        assertEquals(position2.subtract(position4), new Vector2d(-10, 11));

        assertEquals(position3.subtract(position4), new Vector2d(-9, 3));

    }

    @Test
    public void test_opposite(){
        Vector2d position1, position2, position3, position4;
        position1 = new Vector2d(-1, 2);
        position2 = new Vector2d(5, 2);
        position3 = new Vector2d(2, 4);
        position4 = new Vector2d(6, 3);

        assertEquals(position1.opposite(), new Vector2d(1, -2));
        assertEquals(position2.opposite(), new Vector2d(-5, -2));
        assertEquals(position3.opposite(), new Vector2d(-2, -4));
        assertEquals(position4.opposite(), new Vector2d(-6,-3));
    }
}
