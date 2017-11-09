//Charlotte
package org.usfirst.frc.team5104.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
	public static final double floor_pickup_speed_intake = -0.7;
	public static final double floor_pickup_speed_ejecto = 0.8;
	public static final double floor_pickup_raise_speed = -0.1;/*NEGATIVE RAISES THE FLOOR PICKUP*/ /*DO NOT REVERSE THESE VALUES!!!*/
	public static final double floor_pickup_lower_speed =  0.2;/*POSITIVE LOWERS THE FLOOR PICKUP*/ /*SERIOUSLY, IT WON'T BE COOL!!!*/
	public static final int floor_pickup_button_cancel = 2;
	public static final int floor_pickup_button_engage = 4;
	public static final int floor_pickup_button_ejecto = 4;

	//Auto Measurements
	// robot_length			= 28.5in
	// offset_from_center	= 92.3in (to center?)
	// distance_from_back_wall_to_airship_center = 
	
	// Autonomous Mode
	public static final double auto_turn_speed = 0.5;
	public static final int auto_center_distance = 26000; // 30,000 made the robot travel 105 inches. Approx length of
															// robot is 28.5 inches.
															// Total distance to travel is 90 in
	public static final int auto_right_distance_forward = (int)(89*30000/105);
	public static final double auto_right_angle = -56.5;
	public static final int auto_right_distance_turned = (int)(44*30000/105);

	public static final int auto_left_distance_forward = (int)(84*30000/105);
	public static final int auto_left_angle = 48;
	public static final int auto_left_distance_turned = (int)(44*30000/105);

	
	public static final double gyro_kp = 0.03; // to bring the robot back to 0 degree orientation
	public static final double kGyroCompensation = 9*(Math.PI/180);

	// Winch
	public static final int winch_axis = 3; // need to determine button #
	
	// Shift Gears
	public static final int gearshift = 2;  // trigger to activate high gear

	Joystick stick = new Joystick(0);// creates controller
	// Joystick stickR = new Joystick(0);// creates controller

	Drive driver = new Drive(stick);
	Winch winch = new Winch(stick);
	FloorPickup floorPickup = new FloorPickup(stick);

	CameraServer cam1;
	
	Compressor compressor;
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */

	@Override
	public void robotInit() {


		compressor = new Compressor(50);
		compressor.setClosedLoopControl(true);


		driver.openWings();
		driver.lowGear();
		
		cam1 = CameraServer.getInstance();
		cam1.startAutomaticCapture();
		
		/*
		 * solenoidGshift.set(DoubleSolenoid.Value.kForward); // kForward for low gear
		 * solenoidGwings.set(DoubleSolenoid.Value.kForward); //kForward for closed
		 * wings
		 * 
		 */

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

	int autoMode;
	int autoStep; // each option has multiple steps

	public void autonomousInit() {
		 autoMode = (int)SmartDashboard.getNumber("DB/Slider 0",0);
		 
		// temporarily
		autoStep = 0;

		driver.resetEncoder();
		driver.resetGyro();
		
		System.out.println("Autonomous Initialization Complete");
	}// autonomousInit

	// _______________________________________________________________
	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
//		driver.drive(SmartDashboard.getNumber("DB/Slider 0", 0), SmartDashboard.getNumber("DB/Slider 1", 0));
		
		System.out.printf("Encoder Position: %4d\t", driver.getEncoderPosition() - 1);
		System.out.printf("Gyro Angle: %4f\n", driver.getGyroAngle());

		switch (autoMode) {
		case 1: // drive forward
			auto_forward(0, 0.5, auto_center_distance); // args are step, speed, and distance
			// auto_floor_pickup_lower(1);
			// auto_floor_pickup_eject(2);
			// auto_place_gear(1);
			break;
			
		case 2: // drive from the right side
			auto_forward(0, /*0.7*/0.9, auto_right_distance_forward); // step, speed, distance
			auto_turn	(1, auto_right_angle); // step, angle, degrees
			auto_forward(2, /*0.5*/0.7, auto_right_distance_turned); // step, speed, distance
			// auto_place_gear (3);
			break;
		case 3:
			// Left side
			auto_forward(0, 0.7, auto_left_distance_forward);
			auto_turn	(1, auto_left_angle);
			auto_forward(2, 0.5, auto_left_distance_turned);
			break;
		case 4:
			// Baseline
			auto_forward(0, 0.7, 90*30000/105);
			break;

		default:// case 0
			// do nothing
			break;
		}

	}// autonomousPeriodic

	// ______________________________________________________________________________

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		stick.getRawAxis(5);

		// Pneumatics:
		// button 1 for shifting gears Gshift
		// button 2 for wings Gwings

		driver.teleop();
		winch.teleop();
		floorPickup.teleop();

	}
	// _____________________________________________________________________________________

	// ------------------AUTONOMOUS MODES---------------------//
	public void auto_forward(int activeStep, double speed, double distance) {
		// Assumes encoder count to start at 0

		// If current autonomous action to account for periodic looping
		if (autoStep == activeStep) {
			System.out.printf("Running auto %d: Forward (%2f, %2f)\n", activeStep, speed, distance);
			// Until a distance, drive
			if (Math.abs(driver.right1.getEncPosition()) < Math.abs(distance)) {
				// If reversing
				if (speed < 0) {
					// Turn to gyro angle
					driver.curvedDrive(speed, gyro_kp * driver.getGyroAngle()+kGyroCompensation);
					// Timer.delay(.04); want to add to see if the jerkiness goes away
				} else
					// Turn away from gyro angle
					driver.curvedDrive(speed, -gyro_kp * driver.getGyroAngle()+kGyroCompensation);
			} else {
				System.out.println("Done");
				// Reset encoder + gyro
				driver.right1.setEncPosition(0);
				driver.resetGyro();

				// Move to next autonomous step
				autoStep++;
			}
		}
	}// end autoForward

	public void auto_turn(int activeStep, double angle) {
		// If current autonomous action to account for periodic looping
		if (autoStep == activeStep) {
			// Until an angle, turn
			if (Math.abs(driver.getGyroAngle()) < Math.abs(angle)) {
				// If turning left
				if (angle < 0)
					// turn left
					driver.arcadeDrive(0, -auto_turn_speed);
				else
					// turn right
					driver.arcadeDrive(0, auto_turn_speed);
			} else {
				// Reset encoder + gyro
				Timer.delay(1);
				SmartDashboard.putNumber("DB/Slider 2", driver.right1.getEncPosition() + 5);
				driver.resetEncoder();
				driver.resetGyro();
				// Move to next autonomous step
				autoStep++;
			}
		}
	}// auto_turn

	public void auto_lower_floor_pickup(int activeState) {

	}

}// Robot class
