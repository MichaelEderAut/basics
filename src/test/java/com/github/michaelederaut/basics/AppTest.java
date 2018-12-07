package com.github.michaelederaut.basics;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        Assert.assertEquals(1, 1);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    
   private boolean B_kill;
    
    public static	Consumer <ProcessHandle> F_proc_handle = (ProcessHandle ph) -> {
		ProcessHandle.Info O_ph_info;
		Optional<String> O_cmd;
		String S_cmd;
		long P_pid;
		
		if (ph.isAlive()) {
			O_ph_info = ph.info();
			O_cmd = O_ph_info.command();
			S_cmd = null;
			synchronized (ph) {
			   if (O_cmd.isPresent()) {
				  try {
					S_cmd = O_cmd.get();
				} catch (NoSuchElementException PI_E_no_such_eleme) {
					S_cmd = null;
				    }
			      }
			    }
			System.out.println(S_cmd);
		    }
		
		};
    
    public void testApp()
    {
    	
        assertTrue( true );
        List<Long> AL_retval_pid = new Stack<Long>();
		Stream<ProcessHandle> AO_proc_handle;
		
		AO_proc_handle = ProcessHandle.allProcesses();
		AO_proc_handle.forEach(F_proc_handle);
		return;
        
    }
}
