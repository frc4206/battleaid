# Implementation

<hr>

Battleaid offers `TunedJoystick` to make using deadzones and response curves intuitive with minimal performance overhead.

```{code-block} java
:linenos:

// create a controller like normal
CommandXboxController controller = new CommandXboxController(0);

// then use the same controller handle
TunedJoystick tunedJoystick = new TunedJoystick(controller.getHID())
            .useResponseCurve(ResponseCurve.SOFT)
            .setDeadzone(0.1d);
```

Then, you can use the object to retrieve the joystick values:

```{code-block} java
:linenos:
:lineno-start: 8

// these values are tuned
tunedJoystick.getRightX();
tunedJoystick.getRightY();
tunedJoystick.getLeftX();
tunedJoystick.getLeftY();
```
