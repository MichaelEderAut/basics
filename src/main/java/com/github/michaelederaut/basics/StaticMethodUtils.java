package com.github.michaelederaut.basics;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * Makeshift workaround until 
 * <tt>{@link org.apache.commons.lang3.reflect.MethodUtils MethodUtils}</tt>
 * provides <tt>{@link org.apache.commons.lang3.reflect.MethodUtils#invokeStaticMethod(Class,String,Object[],Class[])
 * invokeStaticMethod}</tt> with forced access;
 * @author Mr. Michael Eder
 *
 */
public class StaticMethodUtils {

    static Object[] getVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length
                && args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1])) {
            // The args array is already in the canonical form for the method.
            return args;
        }

        // Construct a new array matching the method's declared parameter types.
        final Object[] newArgs = new Object[methodParameterTypes.length];

        // Copy the normal (non-varargs) parameters
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);

        // Construct a new array for the variadic parameters
        final Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        final int varArgLength = args.length - methodParameterTypes.length + 1;

        Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper(varArgComponentType), varArgLength);
        // Copy the variadic arguments into the varargs array.
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);

        if(varArgComponentType.isPrimitive()) {
            // unbox from wrapper type to primitive type
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }
	
        // Store the varargs array in the last position of the array to return
        newArgs[methodParameterTypes.length - 1] = varArgsArray;

        // Return the canonical varargs array.
        return newArgs;   
    }
    
	 public static Object[] toVarArgs(final Method method, Object[] args) {
	        if (method.isVarArgs()) {
	            final Class<?>[] methodParameterTypes = method.getParameterTypes();
	            args = getVarArgs(args, methodParameterTypes);
	        }
	        return args;
	    }
	
	 /**
     * <p>Invokes a named {@code static} method whose parameter type matches the object type.</p>
     *
     * <p>This method delegates the method search to {@link org.apache.commons.lang3.reflect.MethodUtils#getMatchingAccessibleMethod(Class, String, Class[])}.</p>
     *
     * <p>This method supports calls to methods taking primitive parameters
     * via passing in wrapping classes. So, for example, a {@code Boolean} class
     * would match a {@code boolean} primitive.</p>
     *
     * @param PI_OT_clazz invoke static method on this class
     * @param PI_B_force_access force access even if it's private or protected
     * @param PI_S_method_name get method with this name
     * @param PI_AO_args use these arguments - treat {@code null} as empty array
     * @param PI_AOT_parameter_types match these parameters - treat {@code null} as empty array
     * @return The value returned by the invoked method
     *
     * @throws NoSuchMethodException if there is no such accessible method
     * @throws InvocationTargetException wraps an exception thrown by the
     *  method invoked
     * @throws IllegalAccessException if the requested method is not accessible
     *  via reflection
     */
    public static Object invokeStaticMethod(
    		final Class<?> PI_OT_clazz, 
    		final boolean  PI_B_force_access,
    		final String   PI_S_method_name,
            Object[]       PI_AO_args, 
            Class<?>[]     PI_AOT_parameter_types)
            throws 
                NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    	
    	RuntimeException E_rt;
    	String S_msg_1, S_clazz_name;
    	Object O_retval;
    	
    	S_clazz_name = PI_OT_clazz.getName();
        PI_AO_args = ArrayUtils.nullToEmpty(PI_AO_args);
        PI_AOT_parameter_types = ArrayUtils.nullToEmpty(PI_AOT_parameter_types);
        
        final Method O_method = MethodUtils.getMatchingMethod(
        		PI_OT_clazz, 
        		PI_S_method_name,
                PI_AOT_parameter_types);
        if (O_method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + PI_S_method_name + "() on class: \'" + S_clazz_name + "\'");
            }
        if (!O_method.isAccessible()) {
        	if (PI_B_force_access) {
        		try {
					O_method.setAccessible(true);
				} catch (SecurityException PI_E_sec) {
					S_msg_1 = "Unable to grant accessibilty static of method: \"" + S_clazz_name + "." + PI_S_method_name + "\"";  
					E_rt = new RuntimeException(S_msg_1, PI_E_sec);
					throw E_rt;
				}
        	}
        }
        PI_AO_args = toVarArgs(O_method, PI_AO_args);
        try {
			O_retval = O_method.invoke(null, PI_AO_args);
		} catch (NullPointerException      | 
				 IllegalAccessException    | 
				 IllegalArgumentException  | 
				 InvocationTargetException | 
				 ExceptionInInitializerError PI_E_ill_acc) {
			S_msg_1 = "Unable to invoke static method: \"" + S_clazz_name + "." + PI_S_method_name + "\"";  
			E_rt = new RuntimeException(S_msg_1, PI_E_ill_acc);
			throw E_rt;
		}
        return O_retval;
    }

	
}
