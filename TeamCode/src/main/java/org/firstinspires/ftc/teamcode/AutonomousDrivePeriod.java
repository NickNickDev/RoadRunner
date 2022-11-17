package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name="FTC 15337 Alpha Intelligence")

public class AutonomousDrivePeriod extends LinearOpMode {
    DcMotor motorBackLeft;
    DcMotor motorBackRight;
    DcMotor motorFrontLeft;
    DcMotor motorFrontRight;
    DcMotor linearSlide;
    Servo claw;
    private long wastedTicks = 0;
    private ElapsedTime runTime = new ElapsedTime();

    private void safeSleep(int milliseconds) {
        runTime.reset();
        while (opModeIsActive() && runTime.milliseconds() < milliseconds) {
            wastedTicks++;
        }
    }

     private int initializeRoute() throws InterruptedException {

        this.plannedRoute = new String[] {"right", "up", "right", "up","linear slide up", "claw", "down", "linear slide down", "down", "left"};
        this.stepRuntime = new int[]{2200, 2400, 1400, 200, 3500, 500, 200, 3500, 120, 1000};
        int num = 1; //detection();
        String pos;
        if (num == 1){
            pos = "left";
        }
        else if (num == 2){
            pos = "center";
        }
        else{
            pos = "right";
        }
        switch (pos) {
            case "left":
                this.stepRuntime[this.stepRuntime.length - 1] = 500;
                break;
            case "center":
                this.stepRuntime[this.stepRuntime.length - 1] = 1500;
                break;
            case "right":
                this.stepRuntime[this.stepRuntime.length - 1] = 2000;
                break;
        }
        return num;
    }

    private void initializeBot(){
        motorBackRight = hardwareMap.get(DcMotor.class, "bottemRightdrive");
        motorFrontLeft = hardwareMap.get(DcMotor.class, "topLeftdrive");
        motorFrontRight = hardwareMap.get(DcMotor.class, "topRightdrive");
        linearSlide = hardwareMap.get(DcMotor.class, "linearSlide");
        claw = hardwareMap.get(Servo.class, "claw");
        motorBackLeft = hardwareMap.get(DcMotor.class, "bottemLeftdrive");

        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        claw.scaleRange(0.3, 0.7);
    }
    private void initializeCam(){
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }
    }

    protected String[] plannedRoute;
    protected  int[] stepRuntime;

    public void startAutonomous() throws InterruptedException {
        for (int i = 0; i < this.plannedRoute.length; i++) {
            runCommand(this.plannedRoute[i], this.stepRuntime[i]);
       }
    }

    private void runCommand(String command, int runTime) throws InterruptedException {
        telemetry.addData("command", command);
        telemetry.addData("runtime", runTime);
        telemetry.update();
        if (command.equals("right")) {
            strafe(runTime, 1);
        } else if (command.equals("left")) {
            strafe(runTime, 0);
        }
        if (command.equals("up")) {
            moveForward(runTime);
        }
        if (command.equals("linear slide up")) {
            linearSlide(0, runTime);
        } else if (command.equals("linear slide down")) {
            linearSlide(1, runTime);
        }
        if (command.equals("claw")) {
            claw(runTime);
        }
        if(command.equals("down"))
        {
            moveBackward(runTime);
        }
    }

    public void moveForward(int runTime) {
        motorBackLeft.setPower(0.25);
        motorBackRight.setPower(0.25);
        motorFrontLeft.setPower(0.25);
        motorFrontRight.setPower(0.25);
        safeSleep(runTime);
        stopBot();
    }

    public void rotate(int runTime, int direction) throws InterruptedException {
        // 0 for left, 1 for right

        if(direction == 0)
        {
            motorBackLeft.setPower(-0.25);
            motorBackRight.setPower(0.25);
            motorFrontLeft.setPower(-0.25);
            motorFrontRight.setPower(0.25);
        }
        else if(direction == 1)
        {
            motorBackLeft.setPower(0.25);
            motorBackRight.setPower(-0.25);
            motorFrontLeft.setPower(0.25);
            motorFrontRight.setPower(-0.25);
        }
        safeSleep(runTime);
        stopBot();
    }

    public void moveBackward(int runTime) throws InterruptedException {
        motorBackLeft.setPower(-0.25);
        motorBackRight.setPower(-0.25);
        motorFrontLeft.setPower(-0.25);
        motorFrontRight.setPower(-0.25);
        safeSleep(runTime);
        stopBot();
    }

    private void stopBot() {
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
    }

    public void strafe(int runTime, int direction) throws InterruptedException {
        // 0 for left, 1 for right
        if (direction == 0) {
            motorBackLeft.setPower(0.25);
            motorBackRight.setPower(-0.25);
            motorFrontLeft.setPower(-0.25);
            motorFrontRight.setPower(0.25);
        } else if (direction == 1) {
            motorBackLeft.setPower(-0.25);
            motorBackRight.setPower(0.25);
            motorFrontLeft.setPower(0.25);
            motorFrontRight.setPower(-0.25);
        }
        safeSleep(runTime);
        stopBot();
    }

    public void linearSlide(int direction, int runTime) throws InterruptedException {
        // Safety limit of the linear slide to avoid overshoot.
        if (runTime > 4000) {
            runTime = 3500;
        }
        // 0 for up, 1 for down
        if(direction == 0)
        {
            linearSlide.setPower(0.75);
        }
        else if(direction == 1)
        {
            linearSlide.setPower(-0.75);
        }
        safeSleep(runTime);
        linearSlide.setPower(0);
    }

    public void claw(int runTime) throws InterruptedException {
        // 0 for open, 1 for close

        claw.setPosition(0);
        safeSleep(runTime);
        claw.setPosition(1);
    }

    //@Override
    public void runOpMode() throws InterruptedException {
        try {
            telemetry.setAutoClear(false);
            telemetry.addData("Initialize bot", "started");
            telemetry.update();
            initializeBot();
            telemetry.addData("Initialize cam", "started");
            telemetry.update();
            //initializeCam();
            telemetry.addData("initialize route", "started");
            telemetry.update();

            waitForStart();

            int startPosition = initializeRoute();
            telemetry.addData("destination", startPosition);
            telemetry.update();

            if (opModeIsActive()) {
                while(opModeIsActive()) {
                    startAutonomous();
                    safeSleep(1000);
                    break;
                }
            }
            telemetry.addData("Completed, wasted ticks", this.wastedTicks);
            telemetry.update();
        } catch (Exception ex) {
            telemetry.addData("Fatal error.", ex.toString());
            telemetry.update();
        } finally {
            stopBot();
        }
    }

    /*
     * Specify the source for the Tensor Flow Model.
     * If the TensorFlowLite object model is included in the Robot Controller App as an "asset",
     * the OpMode must to load it using loadModelFromAsset().  However, if a team generated model
     * has been downloaded to the Robot Controller's SD FLASH memory, it must to be loaded using loadModelFromFile()
     * Here we assume it's an Asset.    Also see method initTfod() below .
     */
    private static final String TFOD_MODEL_ASSET = "old yolo.tflite";

    private static final String[] LABELS = {
            "1 car",
            "2 stop sign",
            "3 zebra",
    };

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AUg7+iX/////AAABmbLlfAfcrk1BpwewYJOrqPIyTpx/OJuZ7/eB7Q5Uv+2XWqEVXgbEtqX8YOKEr1mHDcxhANT4+Wqnju9+Zhat5G6xLF+TcIODg6NPKCbbvIPJ79lOj+ypL2LvCTgHQocK9YkTNi29KJIRt5jOvLMgAW/2YAXEJuGOShq/E5uDa/MoI1b9Z8LS78Q1p/b2yraoQRyZJcv7Hy0CVV0TGkdLHRR2Ywd1+K2Tp6mc30MZAxPN1VMRCj6ZsTAY5NlIp+Pzb7w+7rle960llt6NOhhlKDfa7qhjKgHZpf6xH9Zfkqy5cjTxhOXTO8bKhqwTMPzwmHE3VrTqz20cR5q3pkic6IRgoc6KE0XvShPDHaV1X0Gl";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;
    //1=left
    //2=center
    //3=right
    public int detection() throws InterruptedException {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        if (opModeIsActive()) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Objects Detected", updatedRecognitions.size());
                    telemetry.update();
                    // step through the list of recognitions and display image position/size information for each one
                    // Note: "Image number" refers to the randomized image orientation/number
                    for (Recognition recognition : updatedRecognitions) {
                        double col = (recognition.getLeft() + recognition.getRight()) / 2;
                        double row = (recognition.getTop() + recognition.getBottom()) / 2;
                        double width = Math.abs(recognition.getRight() - recognition.getLeft());
                        double height = Math.abs(recognition.getTop() - recognition.getBottom());

                        telemetry.addData("", " ");
                        telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
                        telemetry.addData("- Position (Row/Col)", "%.0f / %.0f", row, col);
                        telemetry.addData("- Size (Width/Height)", "%.0f / %.0f", width, height);
                    }
                    telemetry.update();
                }
            }
        }
        return (int) (Math.random() * 3) + 1;
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.2f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 640;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        telemetry.addData("loading", TFOD_MODEL_ASSET);
        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        telemetry.addData("loaded", TFOD_MODEL_ASSET);
        telemetry.update();
    }
}