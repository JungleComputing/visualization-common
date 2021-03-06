package nl.esciencecenter.esight.math;

public class VecS4 extends VectorS {
    /**
     * Creates a new vector, initialized to 0.
     */
    public VecS4() {
        super(4);
        this.v[0] = 0;
        this.v[1] = 0;
        this.v[2] = 0;
        this.v[3] = 0;
    }

    /**
     * Creates a new vector by copying the given vector.
     * 
     * @param v
     *            The vector to be copied.
     */
    public VecS4(VecS4 v) {
        super(4);
        this.v[0] = v.v[0];
        this.v[1] = v.v[1];
        this.v[2] = v.v[2];
        this.v[3] = v.v[3];
    }

    /**
     * Creates a new vector by copying the given vector, supplemented by the
     * scalar.
     * 
     * @param v
     *            The vector to be copied.
     * @param v3
     *            The additional value to be put into the fourth index.
     */
    public VecS4(VecS3 v, short v3) {
        super(4);
        this.v[0] = v.v[0];
        this.v[1] = v.v[1];
        this.v[2] = v.v[2];
        this.v[3] = v3;
    }

    /**
     * Creates a new vector with the given values.
     * 
     * @param x
     *            The value to be put in the first position.
     * @param y
     *            The value to be put in the second position.
     * @param z
     *            The value to be put in the third position.
     * @param w
     *            The value to be put in the fourth position.
     */
    public VecS4(short x, short y, short z, short w) {
        super(4);
        this.v[0] = x;
        this.v[1] = y;
        this.v[2] = z;
        this.v[3] = w;
    }

    /**
     * Gives the negated vector of this vector.
     * 
     * @return The new negated vector.
     */
    public VecS4 neg() {
        VecS4 result = new VecS4();
        result.v[0] = (short) -v[0];
        result.v[1] = (short) -v[1];
        result.v[2] = (short) -v[2];
        result.v[3] = (short) -v[3];
        return result;
    }

    /**
     * Adds the given vector to the current vector, and returns the result.
     * 
     * @param u
     *            The vector to be added to this vector.
     * @return The new vector.
     */
    public VecS4 add(VecS4 u) {
        VecS4 result = new VecS4();
        result.v[0] = (short) (v[0] + u.v[0]);
        result.v[1] = (short) (v[1] + u.v[1]);
        result.v[2] = (short) (v[2] + u.v[2]);
        result.v[3] = (short) (v[3] + u.v[3]);
        return result;
    }

    /**
     * Substracts the given vector from this vector.
     * 
     * @param u
     *            The vector to be substracted from this one.
     * @return The new Vector, which is a result of the substraction.
     */
    public VecS4 sub(VecS4 u) {
        VecS4 result = new VecS4();
        result.v[0] = (short) (v[0] - u.v[0]);
        result.v[1] = (short) (v[1] - u.v[1]);
        result.v[2] = (short) (v[2] - u.v[2]);
        result.v[3] = (short) (v[3] - u.v[3]);
        return result;
    }

    /**
     * Multiplies the given scalar with this vector.
     * 
     * @param n
     *            The scalar to be multiplied with this one.
     * @return The new Vector, which is a result of the multiplication.
     */
    public VecS4 mul(Number n) {
        short fn = n.shortValue();
        VecS4 result = new VecS4();
        result.v[0] = (short) (v[0] * fn);
        result.v[1] = (short) (v[1] * fn);
        result.v[2] = (short) (v[2] * fn);
        result.v[3] = (short) (v[3] * fn);
        return result;
    }

    /**
     * Multiplies the given vector with this vector.
     * 
     * @param u
     *            The vector to be multiplied with this one.
     * @return The new Vector, which is a result of the multiplication.
     */
    public VecS4 mul(VecS4 u) {
        VecS4 result = new VecS4();
        result.v[0] = (short) (v[0] * u.v[0]);
        result.v[1] = (short) (v[1] * u.v[1]);
        result.v[2] = (short) (v[2] * u.v[2]);
        result.v[3] = (short) (v[3] * u.v[3]);
        return result;
    }

    /**
     * Divides the current vector with the given scalar.
     * 
     * @param n
     *            The scalar to be divided with.
     * @return The new Vector, which is a result of the division.
     */
    public VecS4 div(Number n) {
        short f = n.shortValue();

        VecS4 result = new VecS4();
        result.v[0] = (short) (v[0] / f);
        result.v[1] = (short) (v[1] / f);
        result.v[2] = (short) (v[2] / f);
        result.v[3] = (short) (v[3] / f);
        return result;
    }

    @Override
    public VecS4 clone() {
        return new VecS4(this);
    }

    @Override
    public int hashCode() {
        int hashCode = (v[0] + 23 * 6833 + v[1] + 7 * 7207 + v[2] + 11 * 7919
                + v[3] + 3 * 3);
        return hashCode;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (this == thatObject)
            return true;
        if (!(thatObject instanceof VecS4))
            return false;

        // cast to native object is now safe
        VecS4 that = (VecS4) thatObject;

        // now a proper field-by-field evaluation can be made
        return (v[0] == that.v[0] && v[1] == that.v[1] && v[2] == that.v[2] && v[3] == that.v[3]);
    }
}
