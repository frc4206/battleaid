package org.team4206.battleaid.common;

import edu.wpi.first.wpilibj.XboxController;

/**
 * Utility class for scaling and tuning joystick values from a controller.
 */
public final class TunedJoystick {

    private double deadzone;
    private XboxController cntrllr;
    private ResponseCurve rc;

    private int periodMilliseconds = 20;
    private long t1_lft = 0; // timestamp of previous check
    private long t1_rght = 0;

    private double lx = 0.0d;
    private double ly = 0.0d;
    private double rx = 0.0d;
    private double ry = 0.0d;

    /**
     * Helper class for applying exponential transformations.
     */
    public static enum ResponseCurve {
        LINEAR(1.0d),
        VERYSOFT(1.48d),
        SOFT(1.64d),
        QUADRATIC(2.0d),
        CUBIC(3.0d);

        private double exponent;

        private ResponseCurve(double d) {
            this.exponent = d;
        }

        /*
         * This function assumes that the input is
         * an absolute value since fractional exponents,
         * and non-odd integer exponents, do not preserve the
         * sign of the input. That is, not all exponents are
         * reflective on both the x and y axis.
         */
        double applyCurve(double val) {
            return Math.pow(val, exponent);
        }
    }

    public TunedJoystick(XboxController c) {
        this.cntrllr = c;
        this.rc = ResponseCurve.LINEAR; // why default to anything except regular?
        this.deadzone = 0.1d; // default should be pretty small, in Christian's opinion
    }

    /* Epic math that scales one domain to a new domain */
    static double map(double val, double in_min, double in_max, double out_min, double out_max) {
        if (val < in_min) {
            return out_min;
        }
        if (val > in_max) {
            return out_max;
        }
        return ((val - in_min) * (out_max - out_min) / (in_max - in_min)) + out_min;
    }

    /**
     * Sets the period on which TunedJoystick performs
     * deadzone and scaling operations. Default is 20ms.
     * Only use this if you know what you're doing.
     */
    void setPeriodMilliseconds(int milliseconds) {
        this.periodMilliseconds = milliseconds;
    }

    /*
     * Returns 0 if input is lower than deadzone.
     * Otherwise, deadzone is the new '0' and scales to the max value (of 1.0)
     * This design implements a square deadzone. A circular deadzone
     * requires the x AND y values to calculate vector magnitude.
     */
    double applyDeadzone(double val) {
        return val > deadzone ? map(val, deadzone, 1.0d, 0.0d, 1.0d) : 0.0d;
    }

    /**
     * Set the type of response curve to be used.
     */
    public TunedJoystick useResponseCurve(ResponseCurve rc) {
        this.rc = rc;
        return this;
    }

    /**
     * The expected deadzone is between 0.0 and 1.0.
     */
    public TunedJoystick setDeadzone(double d) {
        // Check for negative just in case
        this.deadzone = Math.abs(d);
        return this;
    }

    /*
     * Note that the deadzone must be applied BEFORE the
     * response curve. This is a critical order of operations,
     * so it deserves it's own function for scrutiny.
     * Note that the parameter is an absolute value
     * in order to reduce internal branching.
     */
    double tune(double i) {
        return (i < 0.0d) ? 0.0 : rc.applyCurve(applyDeadzone(i));
    }

    void tuneLeftStick() {
        long t2 = System.currentTimeMillis();

        if (t2 - t1_lft < periodMilliseconds)
            return;

        t1_lft = t2;

        double _lx = cntrllr.getLeftX();
        double _ly = cntrllr.getLeftY();

        // convert to polar
        double r = Math.sqrt((_lx * _lx) + (_ly * _ly));
        double theta = Math.atan2(_ly, _lx);

        r = tune(r);

        this.lx = r * Math.cos(theta);
        this.ly = r * Math.sin(theta);
    }

    void tuneRightStick() {
        long t2 = System.currentTimeMillis();

        if (t2 - t1_rght < periodMilliseconds)
            return;

        t1_rght = t2;

        double _rx = cntrllr.getRightX();
        double _ry = cntrllr.getRightY();

        // convert to polar
        double r = Math.sqrt((_rx * _rx) + (_ry * _ry));
        double theta = Math.atan2(_ry, _rx);

        r = tune(r);

        this.rx = r * Math.cos(theta);
        this.ry = r * Math.sin(theta);
    }

    /**
     * @return Tuned X axis of left joystick.
     */
    public double getLeftX() {
        tuneLeftStick();
        return lx;
    }

    /**
     * @return Tuned Y axis of left joystick.
     */
    public double getLeftY() {
        tuneLeftStick();
        return ly;
    }

    /**
     * @return Tuned X axis of right joystick.
     */
    public double getRightX() {
        tuneRightStick();
        return rx;
    }

    /**
     * @return Tuned Y axis of right joystick.
     */
    public double getRightY() {
        tuneRightStick();
        return ry;
    }

}