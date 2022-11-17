package org.firstinspires.ftc.teamcode;

public class LinedUpRightPosition  extends AutonomousDrivePeriod{
    public void initializeRoute() throws InterruptedException {
        this.plannedRoute = new String[] {"left", "up", "left", "linear slide up", "claw", "linear slide down", "right"};

        int num = detection();
        String pos = "right";
        if (num == 1){
            pos = "left";
        }
        else if (num == 2){
            pos = "center";
        }
        else{
            pos = "right";
        }
        if(pos == "left")
        {
            this.stepRuntime = new int[]{1000, 900, 500, 1000, 1000, 500};
        }
        else if(pos == "center")
        {
            this.stepRuntime = new int[]{1000, 900, 500, 1000, 1000, 1500};
        }
        else if(pos == "right")
        {
            this.stepRuntime = new int[]{1000, 900, 500, 1000, 1000, 2500};
        }
    }
}
