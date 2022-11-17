package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp()

public class DriverBasedPeriod extends LinearOpMode{
    // Define motors

    DcMotor motorBackLeft;
    DcMotor motorBackRight;
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor linearSlide;
    Servo claw;


    public void runOpMode()
    {
        motorBackLeft = hardwareMap.get(DcMotor.class, "bottemLeftdrive");
        motorBackRight = hardwareMap.get(DcMotor.class, "bottemRightdrive");
        motorFrontLeft = hardwareMap.get(DcMotor.class, "topLeftdrive");
        motorFrontRight = hardwareMap.get(DcMotor.class, "topRightdrive");
        linearSlide = hardwareMap.get(DcMotor.class, "linearSlide");
        claw = hardwareMap.get(Servo.class, "claw");

        // Set the left motors to reverse so that they will go forward

        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();


        // move the robot based on the left stick
        claw.scaleRange(0.5, 0.7);
        while(opModeIsActive()) {
            //linear slide speed
            double lsSpeed = 0.75;
            if (gamepad2.right_trigger > 0){
                lsSpeed = 1.5;

            }
            //linear Slide
            if (gamepad2.dpad_up) {
                linearSlide.setPower(lsSpeed);
            }
            else if (gamepad2.dpad_down) {
                linearSlide.setPower(-lsSpeed);
            }
            else{
                linearSlide.setPower(0.1);
            }

            //claw
            if (gamepad2.x) {
                claw.setPosition(0);
            }
            if (gamepad2.y) {
                claw.setPosition(1);
            }
            double divisor = 3;
            if (gamepad1.left_trigger > 0){
                divisor = 1.5;
            }

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }
            // Send calculated power to wheels
            motorFrontLeft.setPower(leftFrontPower/divisor);
            motorFrontRight.setPower(rightFrontPower/divisor);
            motorBackLeft.setPower(leftBackPower/divisor);
            motorBackRight.setPower(rightBackPower/divisor);

            // Show the elapsed game time and wheel power.

            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.update();
        }
    }
}