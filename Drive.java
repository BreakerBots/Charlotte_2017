package org.usfirst.frc.team5104.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI.Port;

public class Drive {

	Joystick stick;
	CANTalon right1, right2, left1, left2;
	RobotDrive drive;

	ADXRS450_Gyro gyro_spi;

	DoubleSolenoid solenoidGshift, solenoidGwings;
	boolean button1reset, button2reset;
	boolean reversedirection, driveWithWingsFront;

	public Drive(Joystick myStick) {
		stick = myStick;

		left1 = new CANTalon(2);
		right1 = new CANTalon(0);
		right1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		left2 = new CANTalon(3);
		right2 = new CANTalon(1);

		drive = new RobotDrive(left1, left2, right1, right2); // four motors
		// for more options for cantalon, go to WIFI (172.22.11.2) on roborio

		gyro_spi = new ADXRS450_Gyro(Port.kOnboardCS0);

		solenoidGshift = new DoubleSolenoid(3, 2);
		solenoidGwings = new DoubleSolenoid(0, 1);
		button1reset = false;
		button2reset = false;

		reversedirection = false;
		driveWithWingsFront = true;

		solenoidGshift.set(DoubleSolenoid.Value.kForward); // kForward for low gear
		solenoidGwings.set(DoubleSolenoid.Value.kForward); // kForward for closed wings

	}

	public void teleop() {

		drive.arcadeDrive(driveWithWingsFront?-stick.getY():stick.getY(),stick.getX(), true);
//		System.out.printf("Encoder Position: %4d\t", right1.getEncPosition() - 1);
		// System.out.printf("Gyro Angle: %4f\n", gyro_spi.getAngle());

/*	
		// toggles the gears from low to high and back again
		if (stick.getRawButton(1) && !button1reset) {  // button A
			if (solenoidGshift.get() == DoubleSolenoid.Value.kForward)
				solenoidGshift.set(DoubleSolenoid.Value.kReverse);
			else
				solenoidGshift.set(DoubleSolenoid.Value.kForward);
			button1reset = true;
		}

		if (!stick.getRawButton(1))   // button A
			button1reset = false;
		// end gear toggle
*/
		
		// New GearToggle with trigger
		
		if (stick.getRawAxis(Charlotte.gearshift) < 0.5)
			solenoidGshift.set(DoubleSolenoid.Value.kReverse); // 1st gear if input is too low
		else
			solenoidGshift.set(DoubleSolenoid.Value.kForward);  // 2nd gear while trigger is pulled
		
	
		// toggles the gear wings open and closed Nov 7 b/c we have passive gear wings now
		
		// Removed code on 
/*		if (stick.getRawButton(3) && !button2reset) {  // Button X
			if (solenoidGwings.get() == DoubleSolenoid.Value.kForward)
				solenoidGwings.set(DoubleSolenoid.Value.kReverse);
			else
				solenoidGwings.set(DoubleSolenoid.Value.kForward);
			button2reset = true;

		}
		if (!stick.getRawButton(3))  // button X
			button2reset = false;
		// end gear wing toggle

*/	
		
		// toggles the direction
		if (stick.getRawButton(6/*right toggle*/) && !reversedirection) {
			driveWithWingsFront = !driveWithWingsFront;
			reversedirection = true;
		}

		if (!stick.getRawButton(6/*right toggle*/))
			reversedirection = false;
		// end gear toggle
	}

	public void openWings() {

		solenoidGwings.set(DoubleSolenoid.Value.kForward); // kForward for closed

	}

	public void lowGear() {
		solenoidGshift.set(DoubleSolenoid.Value.kForward); // kForward for low gear

	}

	public void resetEncoder() {
		right1.setEncPosition(0);
	}

	public void resetGyro() {
		gyro_spi.reset();
	}

	public void curvedDrive(double speed, double angle) {
		drive.arcadeDrive(speed, angle);
	}

	public void arcadeDrive(double speed, double angle) {
		drive.arcadeDrive(speed, angle);
	}

	public int getEncoderPosition() {
		return right1.getEncPosition();
	}

	public double getGyroAngle() {
		return gyro_spi.getAngle();
	}

}// Drive
