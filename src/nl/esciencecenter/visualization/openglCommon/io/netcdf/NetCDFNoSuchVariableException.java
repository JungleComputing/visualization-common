package nl.esciencecenter.visualization.openglCommon.io.netcdf;

public class NetCDFNoSuchVariableException extends Exception {
    private static final long serialVersionUID = -1023661862089858626L;

    public NetCDFNoSuchVariableException(String message) {
        super(message);
    }
}