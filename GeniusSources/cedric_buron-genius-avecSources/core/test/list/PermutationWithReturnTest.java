package list;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

public class PermutationWithReturnTest {
	@Test
	public void test() {
		ImArrayList<String> source = new ImArrayList<String>(Arrays.asList(new String[] { "a", "b", "c", "d" }));
		PermutationsWithReturn<String> p = new PermutationsWithReturn<String>(source, 3);
		assertEquals(BigInteger.valueOf(64), p.size());
		System.out.println("data=" + p + "\n");
	}
}
