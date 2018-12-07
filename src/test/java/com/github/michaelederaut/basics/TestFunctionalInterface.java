package com.github.michaelederaut.basics;

import com.github.michaelederaut.basics.TriFunction;

public class TestFunctionalInterface {
	
	public static void main(final String args[]) {
		
   TriFunction<Integer, Integer, Integer, Integer> FInteger_add_args_1, FInteger_add_args_2;
//   TriFunction<int, int, int, int> Fint_add_args;
   int I_res_1, I_res_2;
   
   FInteger_add_args_1 =  (Integer i1, Integer i2, Integer i3) -> { return i1 + i2 + i3; };
   I_res_1 = FInteger_add_args_1.apply(1, 2, 5);
   System.out.println("result 1: " + I_res_1);
   
   FInteger_add_args_2 = (i1, i2, i3) -> { return i1 + (2 * i2) - i3; };
   I_res_2 = FInteger_add_args_2.apply(1, 4, 5);
   System.out.println("result 2: " + I_res_2);
   
	}
}
