//Charlotte
package org.usfirst.frc.team5104.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Charlotte extends IterativeRobot {
	// GearIntakeSystem
	public static final double floor_pickup_speed_intake = 0.7;
	public static final double floor_pickup_speed_ejecto = -0.8;
	public static final int floor_pickup_position_raised = 0;
	public static final int floor_pickup_position_ground = -3000;
	public static final int floor_pickup_button_cancel = 0;
	public static final int floor_pickup_button_engage = 1;
	public static final int floor_pickup_button_ejecto = 2;
	
	//Autonomous Mode
	public static final double auto_turn_speed = 0.35;
	public static final int auto_center_distance = 26000;   // 30,000 made the robot travel 105 inches.  Approx length of robot is 28.5 inches.  
															// Total distance to travel is 90 in
	public static final int auto_right_distance_forward = 5000;
	public static final int auto_right_angle = 30;
	public static final int auto_right_distance_turned = 3000;
	
	// Constants for autonomous mode
	public static final double gyro_kp = -0.03;    // to bring the robot back to 0 degree orientation
	

	// Winch
	public static final int winch_axis = 4;  // need to determine button #
	
	
	RobotDrive drive;// set for 4 motors.
	Joystick stick = new Joystick(0);// creates controller
//	Joystick stickR = new Joystick(0);// creates controller

//	AnalogGyro gyro = new AnalogGyro(1);
	ADXRS450_Gyro gyro_spi = new ADXRS450_Gyro(Port.kOnboardCS0);  //not sure of the port
	
	Winch winch = new Winch(stick);
	FloorPickup floorPickup = new FloorPickup(stick);
	

	Compressor compressor;  
	DoubleSolenoid solenoidGshift = new DoubleSolenoid(0,1);
	DoubleSolenoid solenoidGwings = new DoubleSolenoid(2,3);
	boolean button1reset = false, button2reset = false;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization .
	 */
	// four talons for the 4 drive motors
	CANTalon right1;
	CANTalon right2;
	CANTalon left1;
	CANTalon left2;
	

	//CANTalon gearanator;  // moves the gear-intake up and down
	//CANTalon gearanotorLeft // the left spinner
	//CANTalon gearanotorRight // the right spinner
	
	@Override
	public void robotInit() {
		
		
		left1 = new CANTalon(2);
		right1 =new CANTalon(0);
		right1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		left2 = new CANTalon(3);
		right2 = new CANTalon(1);
		
		compressor = new Compressor(50);
		compressor.setClosedLoopControl(true);

		
		drive = new RobotDrive(left1, left2, right1, right2);  // four motors
		//for more options for cantalon, go to WIFI (172.22.11.2) on roborio
		
		solenoidGshift.set(DoubleSolenoid.Value.kForward);  // kForward for low gear 
		solenoidGwings.set(DoubleSolenoid.Value.kForward);   //kForward for closed wings
		


	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	
	int autoMode=1;  // chooses the autonomous option WOOOAH!!! Changed to 1 b/c I wasn'tsure I was getting input from the drive station correctly :(
	int autoStep;  //  each option has multiple steps
	public void autonomousInit() {
		//autoMode = (int)SmartDashboard.getNumber("DB/Slider 0",0);    //Commented out temporarily
		autoStep = 0;

		right1.setEncPosition(0);
		gyro_spi.reset();
		System.out.println("Autonomous Initialization Complete");
	}//autonomousInit

	//_______________________________________________________________
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
//		drive.drive(SmartDashboard.getNumber("DB/Slider 0",0),
//					SmartDashboard.getNumber("DB/Slider 1", 0));
		System.out.printf("Encoder Position: %4d\t",right1.getEncPosition()-1);
		System.out.printf("Gyro Angle: %4f\n", gyro_spi.getAngle());

		switch (autoMode) {
		case 1:  // drive forward
			auto_forward(0,0.2,auto_center_distance);  // args are step, speed, and distance
//			auto_place_gear(1);
			break;
			
		case 2:  //drive from the right side
			auto_forward	(0,0.2,9000);  //  step, speed, distance
			auto_turn		(1,-32);       // step, angle, degrees
			auto_forward	(2,0.2,5000);  // step, speed, distance
//			auto_place_gear	(3);
			break;
		case 3:
			//Left side
			auto_forward	(0,-0.2,-9000);
			break;
		case 4:
			//Baseline
			break;
			
		default://case 0
			// do nothing
			break;
		}
		
		
	}//autonomousPeriodic
	
//______________________________________________________________________________	

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		// 8/25
		stick.getRawAxis(5);
		
	
		// Pneumatics:
		// button 1 for shifting gears  Gshift
		// button 2 for wings           Gwings
//		stick.getRawButton(0); use me for pneumatics stuff l8r
//		disable both solenoids to start (?)

		
// probably don't need the while loop, since periodic is a loop		
		while (isEnabled()) {
			drive.arcadeDrive(stick);
			System.out.printf("Encoder Position: %4d\t",right1.getEncPosition()-1);
			System.out.printf("Gyro Angle: %4f\n", gyro_spi.getAngle());
	
			// toggles the gears from low to high and back again
			if (stick.getRawButton(1) && !button1reset)  {
				if (solenoidGshift.get() == DoubleSolenoid.Value.kForward) solenoidGshift.set(DoubleSolenoid.Value.kReverse);
				else solenoidGshift.set(DoubleSolenoid.Value.kForward);
				button1reset = true;
			}
			
			if(!stick.getRawButton(1)) button1reset = false;
			// end gear toggle
			
			// toggles the gear wings open and closed
			if (stick.getRawButton(2) && !button2reset)  {
				if (solenoidGwings.get() == DoubleSolenoid.Value.kForward) solenoidGwings.set(DoubleSolenoid.Value.kReverse);
				else solenoidGwings.set(DoubleSolenoid.Value.kForward);
				button2reset = true;
				
			}
			if(!stick.getRawButton(2)) button2reset = false;
			// end gear wing toggle
		
			winch.teleop();
			floorPickup.teleop();
			
		}
		
		
	}
//_____________________________________________________________________________________
	
	//------------------AUTONOMOUS MODES---------------------//
	public void auto_forward(int activeStep, double speed, double distance) {
		//Assumes encoder count to start at 0
		
		//If current autonomous action to account for periodic looping
		if (autoStep == activeStep) {
			System.out.printf("Running auto %d: Forward (%2f, %2f)\n", activeStep, speed, distance);
			//Until a distance, drive
			if (Math.abs(right1.getEncPosition()) < Math.abs(distance)) {
				//If reversing
				if (speed < 0)   {
					//Turn to gyro angle
					drive.drive(speed, gyro_kp*gyro_spi.getAngle());
				//	Timer.delay(.04);  want to add to see if the jerkiness goes away
				}
				else
					//Turn away from gyro angle
					drive.drive(speed, -gyro_kp*gyro_spi.getAngle());
			} else {
				System.out.println("Done");
				//Reset encoder + gyro
				right1.setEncPosition(0);
				gyro_spi.reset();
				
				//Move to next autonomous step
				autoStep++;
			}
		}
	}// end autoForward
	
	public void auto_turn (int activeStep, double angle) {
		//If current autonomous action to account for periodic looping
		if (autoStep == activeStep) {
			//Until an angle, turn
			if (Math.abs(gyro_spi.getAngle()) < Math.abs(angle)) {
				//If turning left
				if (angle < 0)
					//turn left
					drive.arcadeDrive(0, -auto_turn_speed);
				else
					//turn right
					drive.arcadeDrive(0, auto_turn_speed);
			} else {
				//Reset encoder + gyro
				right1.setEncPosition(0);
				Timer.delay(1);
				SmartDashboard.putNumber("DB/Slider 2",right1.getEncPosition()+5);
				gyro_spi.reset();
				//Move to next autonomous step
				autoStep++;
			}
		}
	}//auto_turn
		
}//Robot class
