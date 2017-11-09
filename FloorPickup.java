package org.usfirst.frc.team5104.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;

public class FloorPickup {

	CANTalon plate;
	DigitalInput upperLimitSwitch;
	DigitalInput lowerLimitSwitch;

	CANTalon leftSpinner;
	CANTalon rightSpinner;

	DigitalInput pickupButton;
	Joystick stick;

	enum FloorPickupState {
		kEmpty, kEngaged, kStocked, kEjecting, kCancel;
	};

	FloorPickupState state;

	public FloorPickup(Joystick mystick) {
		plate = new CANTalon(7);
		upperLimitSwitch = new DigitalInput(8);
		lowerLimitSwitch = new DigitalInput(7);

		leftSpinner = new CANTalon(35); 
		rightSpinner = new CANTalon(31); 
		rightSpinner.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightSpinner.set(35/*leftSpinner*/);
		rightSpinner.reverseOutput(true);

		pickupButton = new DigitalInput(9); // check number
		stick = mystick;
		
		state = FloorPickupState.kEmpty;

	}

	public void teleop() {
		if (stick.getRawButton(Charlotte.floor_pickup_button_cancel)) {
			state = FloorPickupState.kEmpty;
		}

		System.out.printf("State: %s   Upper Limit: %b    Lower Limit: %b    Pickup: %b\n", getStateName(state), upperLimitSwitch.get(), lowerLimitSwitch.get(), pickupButton.get());
		
		switch (state) {
		case kEmpty:
			// plate.set(Charlotte.floor_pickup_position_raised);
			raisePickup();
			leftSpinner.set(0);

			if (stick.getRawButton(Charlotte.floor_pickup_button_engage)) {
				state = FloorPickupState.kEngaged;
			}
			break;

		case kEngaged:

			lowerPickup();
			leftSpinner.set(Charlotte.floor_pickup_speed_intake);

			if (!pickupButton.get())
				state = FloorPickupState.kStocked;
			break;

		case kStocked:
			
			raisePickup();
			leftSpinner.set(0);

			if (stick.getRawButton(Charlotte.floor_pickup_button_ejecto))
				state = FloorPickupState.kEjecting;
			break;

		case kEjecting:
			
			raisePickup();
			leftSpinner.set(Charlotte.floor_pickup_speed_ejecto);
			
			if (!stick.getRawButton(Charlotte.floor_pickup_button_ejecto))
				state = FloorPickupState.kEmpty;
			break;

		case kCancel:
			state = FloorPickupState.kEmpty;
			break;
			
		default:
			plate.set(0);
			leftSpinner.set(0);
		}
	}//teleop
	
	private void raisePickup() {
		if (upperLimitSwitch.get())
			plate.set(Charlotte.floor_pickup_raise_speed);
		else
			plate.set(0);
	}//raisePickup
	private void lowerPickup() {
		if (lowerLimitSwitch.get())
			plate.set(Charlotte.floor_pickup_lower_speed);
		else
			plate.set(0);
	}//lowerPickup

	private String getStateName(FloorPickupState testState) {
		switch (testState) {
		case kEmpty:
			return "Empty";
		case kEngaged:
			return "Engaged";
		case kStocked:
			return "Stocked";
		case kEjecting:
			return "Ejecting";
		default:
			return "Null";
		}
	}//getStateName
}//FloorPickup
