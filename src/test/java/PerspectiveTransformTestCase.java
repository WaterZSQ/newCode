import common.PerspectiveTransform;
import org.junit.Assert;
import org.junit.Test;

public final class PerspectiveTransformTestCase extends Assert {

    private static final float EPSILON = 1.0E-4f;

    @Test
    public void testSquareToQuadrilateral() {
        PerspectiveTransform pt = PerspectiveTransform.squareToQuadrilateral(
                2.0f, 3.0f, 10.0f, 4.0f, 16.0f, 15.0f, 4.0f, 9.0f);
        assertPointEquals(2.0f, 3.0f, 0.0f, 0.0f, pt);
        assertPointEquals(10.0f, 4.0f, 1.0f, 0.0f, pt);
        assertPointEquals(4.0f, 9.0f, 0.0f, 1.0f, pt);
        assertPointEquals(16.0f, 15.0f, 1.0f, 1.0f, pt);
        assertPointEquals(6.535211f, 6.8873234f, 0.5f, 0.5f, pt);
        assertPointEquals(48.0f, 42.42857f, 1.5f, 1.5f, pt);
    }

    @Test
    public void testQuadrilateralToQuadrilateral() {
        PerspectiveTransform pt = PerspectiveTransform.quadrilateralToQuadrilateral(
                2.0f, 3.0f, 10.0f, 4.0f, 16.0f, 15.0f, 4.0f, 9.0f,
                103.0f, 110.0f, 300.0f, 120.0f, 290.0f, 270.0f, 150.0f, 280.0f);
        assertPointEquals(103.0f, 110.0f, 2.0f, 3.0f, pt);
        assertPointEquals(300.0f, 120.0f, 10.0f, 4.0f, pt);
        assertPointEquals(290.0f, 270.0f, 16.0f, 15.0f, pt);
        assertPointEquals(150.0f, 280.0f, 4.0f, 9.0f, pt);
        assertPointEquals(7.1516876f, -64.60185f, 0.5f, 0.5f, pt);
        assertPointEquals(328.09116f, 334.16385f, 50.0f, 50.0f, pt);
    }

    private static void assertPointEquals(float expectedX,
                                          float expectedY,
                                          float sourceX,
                                          float sourceY,
                                          PerspectiveTransform pt) {
        float[] points = {sourceX, sourceY};
        pt.transformPoints(points);
        assertEquals(expectedX, points[0], EPSILON);
        assertEquals(expectedY, points[1], EPSILON);
    }

}
