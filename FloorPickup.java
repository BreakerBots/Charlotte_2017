package org.usfirst.frc.team5104.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;

public class FloorPickup {

	CANTalon plate;

	CANTalon leftSpinner;
	CANTalon rightSpinner;
	
	DigitalInput pickupButton;
	Joystick stick;
	
	enum FloorPickupState {
		kEmpty, kEngaged, kStocked, kEjecting, kCancel;
	};
	
	FloorPickupState state;
	
	
	
	public FloorPickup(Joystick mystick) {
		plate = new CANTalon(10);
		
		leftSpinner = new CANTalon(11);
		rightSpinner = new CANTalon(12);
		rightSpinner.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightSpinner.reverseSensor(true);
		
		pickupButton = new DigitalInput(9);
		stick = mystick;
		
	}
	
	public void teleop() {
		if (stick.getRawButton(Charlotte.floor_pickup_button_cancel))
			state = FloorPickupState.kEmpty;

		switch (state) {
		case kEmpty:
			plate.set(Charlotte.floor_pickup_position_raised);
			leftSpinner.set(0);
			
			if (stick.getRawButton(Charlotte.floor_pickup_button_engage)) {
				state = FloorPickupState.kEngaged;
			}
			break;
		
		case kEngaged:
			leftSpinner.set(Charlotte.floor_pickup_speed_intake);
			
			if (pickupButton.get())
				state = FloorPickupState.kStocked;
			break;
					
		case kStocked:
			leftSpinner.set(0);
			plate.set(Charlotte.floor_pickup_position_raised);
			
			if (stick.getRawButton(Charlotte.floor_pickup_button_ejecto))
				state = FloorPickupState.kEjecting;
			break;

		case kEjecting:
			leftSpinner.set(Charlotte.floor_pickup_speed_ejecto);
			if (!stick.getRawButton(Charlotte.floor_pickup_button_ejecto))
				state = FloorPickupState.kEmpty;
			break;
		
		case kCancel:
			state = FloorPickupState.kEmpty;
			break;
		}
	}
	
	
}
