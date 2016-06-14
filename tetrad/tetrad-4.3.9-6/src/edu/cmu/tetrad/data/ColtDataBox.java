package edu.cmu.tetrad.data;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Stores a 2D array of short data. Note that the missing value marker for this
 * box is -99.
 */
public class ColtDataBox implements DataBox {
    static final long serialVersionUID = 23L;

    /**
     * The stored short data.
     */
    private DoubleMatrix2D data;

    /**
     * Constructs an 2D COLT array consisting entirely of missing values
     * (Double.NaN).
     * @param rows
     * @param cols
     */
    public ColtDataBox(int rows, int cols) {
        this.data = new DenseDoubleMatrix2D(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data.set(i, j, Double.NaN);
            }
        }
    }

    /**
     * Generates a simple exemplar of this class to test serialization.
     *
     * @see edu.cmu.TestSerialization
     * @see edu.cmu.tetradapp.util.TetradSerializableUtils
     */
    public static BoxDataSet serializableInstance() {
        return new BoxDataSet(new ShortDataBox(4, 4), null);
    }

    /**
     * Constructs a new data box using the given 2D short data array as data.
     */
    public ColtDataBox(DoubleMatrix2D data) {
        this.data = data;
    }

    /**
     * Returns the number of rows in this data box.
     */
    public int numRows() {
        return data.rows();
    }

    /**
     * Returns the number of columns in this data box.n
     */
    public int numCols() {
        return data.columns();
    }

    /**
     * Sets the value at the given row/column to the given Number value.
     * The value used is number.shortValue().
     */
    public void set(int row, int col, Number value) {
        if (value == null) {
            data.set(row, col, Double.NaN);
        } else {
            data.set(row, col, value.doubleValue());
        }
    }

    /**
     * Returns the Number value at the given row and column. If the value
     * is missing (-99), null, is returned.
     */
    public Number get(int row, int col) {
        double datum = data.get(row, col);

        if (Double.isNaN(datum)) {
            return null;
        }
        else {
            return datum;
        }
    }

    /**
     * Returns a copy of this data box.
     */
    public DataBox copy() {
        ColtDataBox box = new ColtDataBox(numRows(), numCols());

        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                box.set(i, j, get(i, j));
            }
        }

        return box;
    }

    /**
     * Returns a DataBox of type ShortDataBox, but with the given dimensions.
     */
    public DataBox like(int rows, int cols) {
        return new ColtDataBox(rows, cols);
    }
}