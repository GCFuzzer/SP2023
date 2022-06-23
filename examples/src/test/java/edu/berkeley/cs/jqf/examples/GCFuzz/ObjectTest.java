package edu.berkeley.cs.jqf.examples.GCFuzz;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.examples.GadgetChain.ObjectGenerator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;
import static org.junit.Assert.assertTrue;

@RunWith(JQF.class)
public class ObjectTest {

    @Fuzz
    public void testWithGenerator(@From(ObjectGenerator.class) BadAttributeValueExpException bad) throws Exception {
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(bad);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();
    }
}
