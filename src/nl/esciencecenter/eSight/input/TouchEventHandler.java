package nl.esciencecenter.eSight.input;

interface TouchEventHandler {
    public void OnTouchPoints(double timestamp, TouchPoint[] points, int n);
}
