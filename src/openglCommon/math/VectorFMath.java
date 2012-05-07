package openglCommon.math;

import java.nio.FloatBuffer;
import java.util.List;

public class VectorFMath {

    /**
     * Helper method to calculate the dot product of two vectors
     * 
     * @param u
     *            The first vector.
     * @param v
     *            The second vector.
     * @return The dot product of the two vectors.
     */
    public static float dot(VecF2 u, VecF2 v) {
        return u.v[0] * v.v[0] + u.v[1] * v.v[1];
    }

    /**
     * Helper method to calculate the dot product of two vectors
     * 
     * @param u
     *            The first vector.
     * @param v
     *            The second vector.
     * @return The dot product of the two vectors.
     */
    public static float dot(VecF3 u, VecF3 v) {
        return u.v[0] * v.v[0] + u.v[1] * v.v[1] + u.v[2] * v.v[2];
    }

    /**
     * Helper method to calculate the dot product of two vectors
     * 
     * @param u
     *            The first vector.
     * @param v
     *            The second vector.
     * @return The dot product of the two vectors.
     */
    public static float dot(VecF4 u, VecF4 v) {
        return u.v[0] * v.v[0] + u.v[1] * v.v[1] + u.v[2] * v.v[2] + u.v[3] * v.v[3];
    }

    /**
     * Helper method to calculate the length of a vector.
     * 
     * @param u
     *            The vector.
     * @return The length of the vector.
     */
    public static float length(VecF2 v) {
        return (float) Math.sqrt(dot(v, v));
    }

    /**
     * Helper method to calculate the length of a vector.
     * 
     * @param u
     *            The vector.
     * @return The length of the vector.
     */
    public static float length(VecF3 v) {
        return (float) Math.sqrt(dot(v, v));
    }

    /**
     * Helper method to calculate the length of a vector.
     * 
     * @param u
     *            The vector.
     * @return The length of the vector.
     */
    public static float length(VecF4 v) {
        return (float) Math.sqrt(dot(v, v));
    }

    /**
     * Helper method to normalize a vector.
     * 
     * @param u
     *            The vector.
     * @return The normal of the vector.
     */
    public static VecF2 normalize(VecF2 v) {
        return v.div(length(v));
    }

    /**
     * Helper method to normalize a vector.
     * 
     * @param u
     *            The vector.
     * @return The normal of the vector.
     */
    public static VecF3 normalize(VecF3 v) {
        return v.div(length(v));
    }

    /**
     * Helper method to normalize a vector.
     * 
     * @param u
     *            The vector.
     * @return The normal of the vector.
     */
    public static VecF4 normalize(VecF4 v) {
        return v.div(length(v));
    }

    /**
     * Helper method to calculate the cross product of two vectors
     * 
     * @param u
     *            The first vector.
     * @param v
     *            The second vector.
     * @return The new vector, which is the cross product of the two vectors.
     */
    public static VecF3 cross(VecF3 u, VecF3 v) {
        return new VecF3(u.v[1] * v.v[2] - u.v[2] * v.v[1], u.v[2] * v.v[0] - u.v[0] * v.v[2], u.v[0] * v.v[1] - u.v[1]
                * v.v[0]);
    }

    /**
     * Helper method to calculate the cross product of two vectors
     * 
     * @param u
     *            The first vector.
     * @param v
     *            The second vector.
     * @return The new vector, which is the cross product of the two vectors.
     */
    public static VecF4 cross(VecF4 u, VecF4 v) {
        return new VecF4(u.v[1] * v.v[2] - u.v[2] * v.v[1], u.v[2] * v.v[0] - u.v[0] * v.v[2], u.v[0] * v.v[1] - u.v[1]
                * v.v[0], 0.0f);
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The array of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer toBuffer(float[] array) {
        FloatBuffer result = FloatBuffer.allocate(array.length);

        for (int i = 0; i < array.length; i++) {
            result.put(array[i]);
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The array of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer toBuffer(VecF2[] array) {
        FloatBuffer result = FloatBuffer.allocate(array.length * 2);

        for (int i = 0; i < array.length; i++) {
            result.put(array[i].asBuffer());
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The array of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer toBuffer(VecF3[] array) {
        FloatBuffer result = FloatBuffer.allocate(array.length * 3);

        for (int i = 0; i < array.length; i++) {
            result.put(array[i].asBuffer());
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The array of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer toBuffer(VecF4[] array) {
        FloatBuffer result = FloatBuffer.allocate(array.length * 4);

        for (int i = 0; i < array.length; i++) {
            result.put(array[i].asBuffer());
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The List of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer listToBuffer(List<Float> list) {
        FloatBuffer result = FloatBuffer.allocate(list.size());

        for (Float f : list) {
            result.put(f);
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The List of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer vec2ListToBuffer(List<VecF2> list) {
        FloatBuffer result = FloatBuffer.allocate(list.size() * 2);

        for (VecF2 v : list) {
            result.put(v.asBuffer());
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The List of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer vec3ListToBuffer(List<VecF3> list) {
        FloatBuffer result = FloatBuffer.allocate(list.size() * 3);

        for (VecF3 v : list) {
            result.put(v.asBuffer());
        }

        result.rewind();

        return result;
    }

    /**
     * Helper method to create a FloatBuffer from an array of vectors.
     * 
     * @param array
     *            The List of vectors.
     * @return The new FloatBuffer
     */
    public static FloatBuffer vec4ListToBuffer(List<VecF4> list) {
        FloatBuffer result = FloatBuffer.allocate(list.size() * 4);

        for (VecF4 v : list) {
            result.put(v.asBuffer());
        }

        result.rewind();

        return result;
    }

    public static VecF3[] bezierCurve(int steps, VecF3 startLocation, VecF3 startControl, VecF3 endControl,
            VecF3 endLocation) {
        VecF3[] newBezierPoints = new VecF3[steps];
        for (int i = 0; i < steps; i++) {
            newBezierPoints[i] = new VecF3();
        }

        float t = 1f / steps;
        float temp = t * t;

        for (int coord = 0; coord < 3; coord++) {
            float p[] = new float[4];
            p[0] = startLocation.get(coord);
            p[1] = (startLocation.add(startControl)).get(coord);
            p[2] = (endLocation.add(endControl.neg())).get(coord);
            p[3] = endLocation.get(coord);

            // The algorithm itself begins here ==
            float f, fd, fdd, fddd, fdd_per_2, fddd_per_2, fddd_per_6;

            // I've tried to optimize the amount of
            // multiplications here, but these are exactly
            // the same formulas that were derived earlier
            // for f(0), f'(0)*t etc.
            f = p[0];
            fd = 3 * (p[1] - p[0]) * t;
            fdd_per_2 = 3 * (p[0] - 2 * p[1] + p[2]) * temp;
            fddd_per_2 = 3 * (3 * (p[1] - p[2]) + p[3] - p[0]) * temp * t;

            fddd = fddd_per_2 + fddd_per_2;
            fdd = fdd_per_2 + fdd_per_2;
            fddd_per_6 = fddd_per_2 * (1f / 3);

            for (int loop = 0; loop < steps; loop++) {
                newBezierPoints[loop].set(coord, f);

                f = f + fd + fdd_per_2 + fddd_per_6;
                fd = fd + fdd + fddd_per_2;
                fdd = fdd + fddd;
                fdd_per_2 = fdd_per_2 + fddd_per_2;
            }
        }

        return newBezierPoints;
    }
}
