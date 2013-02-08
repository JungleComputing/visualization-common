package nl.esciencecenter.visualization.openglCommon.input;

interface TouchEventHandler {
    public void OnTouchPoints(double timestamp, TouchPoint[] points, int n);
}
