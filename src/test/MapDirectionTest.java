import org.junit.Test;
import static agh.cs.evolution_generator.Maps.MapDirection.*;
import static org.junit.Assert.assertEquals;

public class MapDirectionTest {
    @Test
    public void test_next(){
        assertEquals(NORTH.next(), EAST);
        assertEquals(SOUTH.next(), WEST);
        assertEquals(WEST.next(), NORTH);
        assertEquals(EAST.next(), SOUTH);
    }

    @Test
    public void test_previous(){
        assertEquals(EAST.previous(), NORTH);
        assertEquals(WEST.previous(), SOUTH);
        assertEquals(NORTH.previous(), WEST);
        assertEquals(SOUTH.previous(), EAST);

    }
}
