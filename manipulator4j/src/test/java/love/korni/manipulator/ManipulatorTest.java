package love.korni.manipulator;

import love.korni.manipulator.core.caldron.Caldron;
import love.korni.manipulator.core.gear.args.ArgsGear;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergei_Kornilov
 */
public class ManipulatorTest {

    @Test
    public void testRunClass() {
        Caldron caldron = Manipulator.run(ManipulatorTest.class);
        Assert.assertNotNull(caldron);
    }

    @Test
    public void testRunPackage() {
        Caldron caldron = Manipulator.run("love.korni.manipulator");
        Assert.assertNotNull(caldron);
    }

    @Test
    public void testRunWithArgs() {
        Caldron caldron = Manipulator.run(ManipulatorTest.class, new String[]{"--f=argF", "args1"});
        Assert.assertNotNull(caldron);

        ArgsGear argsGear = caldron.getGearOfType(ArgsGear.class);
        Assert.assertNotNull(argsGear);
        Assert.assertNotNull(argsGear.getSourceArgs());
        Assert.assertEquals(argsGear.getSourceArgs().length, 2);
        Assert.assertEquals(argsGear.getOptionValues("f"), List.of("argF"));
        Assert.assertEquals(argsGear.getNonOptionArgs(), List.of("args1"));
    }
}