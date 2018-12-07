package com.github.michaelederaut.basics;

@FunctionalInterface
public interface TriFunction<T1, T2, T3, TResult> {
	
	TResult apply(T1 arg1, T2 arg2, T3 arg3); 

}
