package org.team4206.battleaid.common;

import edu.wpi.first.wpilibj.XboxController;

/**
 * Utility class for retrieving and adjusting the joystick values from a controller.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * TunedJoystick joystick = new TunedJoystick(new XboxController(0));
 * 
 * jystck.setDeadzone(0.1d).useResponseCurve(ResponseCurve.QUADRATIC);
 * 
 * double leftX = jystck.getLeftX();  
 * double leftY = jystck.getLeftY();  
 * double rightX = jystck.getRightX();
 * double rightY = jystck.getRightY();
 * }</pre>
 */
public final class TunedJoystick {

    private double deadzone;
    private XboxController cntrllr;
    private ResponseCurve rc;

    /*
     * In this design, only exponential response curves
     * are implemented so as to simplify implementation.
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

    /*
     * Returns 0 if input is lower than deadzone.
     * Otherwise, deadzone is the new '0' and scales to the max value (of 1.0)
     * This design implements a square deadzone. A circular deadzone
     * requires the x AND y values to calculate vector magnitude.
     */
    double applyDeadzone(double val) {
        return val > deadzone ? map(val, deadzone, 1.0d, 0.0d, 1.0d) : 0.0d;
    }

    public TunedJoystick useResponseCurve(ResponseCurve rc) {
        this.rc = rc;
        return this;
    }

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
        double result = rc.applyCurve(applyDeadzone(Math.abs(i)));
        return (i >= 0.0d) ? result : result * -1.0d;
    }

    /**
     * @return Tuned X axis of left joystick.
     */
    public double getLeftX() {
        return tune(cntrllr.getLeftX());
    }

    /**
     * @return Tuned Y axis of left joystick.
     */
    public double getLeftY() {
        return tune(cntrllr.getLeftY());
    }

    /**
     * @return Tuned X axis of right joystick.
     */
    public double getRightX() {
        return tune(cntrllr.getRightX());
    }

    /**
     * @return Tuned Y axis of right joystick.
     */
    public double getRightY() {
        return tune(cntrllr.getRightY());
    }

}
