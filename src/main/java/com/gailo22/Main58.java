package com.gailo22;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Main58 {
	
	public static void main(String[] args) {

		Car car = new Car();
		car.setName("Civic");

		Car car1 = car.toBuilder()
			.brand("Honda")
			.build();

		System.out.println(car1);

	}

}

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
class Car {
    String name;
    String brand;
}
