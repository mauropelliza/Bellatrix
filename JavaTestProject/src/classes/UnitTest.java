package classes;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnitTest {

	@Test
	public final void testLogMessage() {
		JobLogger jl = new JobLogger(false, true, false, null);
		try {
			jl.LogMessage("this is a log message", "M");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
