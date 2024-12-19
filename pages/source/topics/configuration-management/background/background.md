# Background

<hr>

Consider this scenario: you're writing a simple program that rotates a servo a certain number of degrees (maybe to actuate an arm).  You don't know exactly how many degrees it will be exactly, so you initially guess 90°.  You hard code the value.

So you test it out, aaaaand... crap.  It was too much! You change it to 80° and try again.

Now it's too little! Ok, so it's somewhere in between, 85° maybe? You try this again and again until you hone in on the perfect value, which turns out to be 83.5°. 

Sound familiar?

<hr>

## Characterizing the Problem

When building robots, parameters in the software will have to be tested against reality and tweaked on the fly using a guess-and-check strategy.  However, when parameters are hard coded, making changes can be time consuming.  One has to: 

1. Locate all parameter instances in the code.
2. Make the same change for each.
3. Re-build the code.
4. Re-deploy to the robot.

As a robot project grows in complexity and size, this process becomes even more time consuming.  Making small changes will lead to time wasted waiting around for recompilation and re-deployment (which is when your compiled code is sent to the RoboRIO).

For example, if a motor is replaced and the CAN ID needs to be updated, the update should be done as quickly as possible.