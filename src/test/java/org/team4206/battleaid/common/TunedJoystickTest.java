package org.team4206.battleaid.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.team4206.battleaid.common.TunedJoystick.ResponseCurve;

public class TunedJoystickTest {

    TunedJoystick tj = new TunedJoystick(null);

    @Test 
    void testTuneBounds() {
        tj.setDeadzone(0.1d).useResponseCurve(ResponseCurve.LINEAR);
        assertEquals(tj.tune(0.0999d), 0.0d);
        assertEquals(tj.tune(1.0001d), 1.0d);
        assertEquals(tj.tune(-0.0999d), -0.0d);
        assertEquals(tj.tune(-1.0001d), -1.0d);
    }

    @Test 
    void testMap() {
        assertEquals(TunedJoystick.map(-0.0001, 0, 1, -1, 2), -1d);
        assertEquals(TunedJoystick.map(1.0001, 0, 1, -1, 2), 2d);
        assertEquals(TunedJoystick.map(-0.0001, 0, 1, .25, .75), .25d);
        assertEquals(TunedJoystick.map(1.0001, 0, 1, .25, .75), .75d);
    }
}