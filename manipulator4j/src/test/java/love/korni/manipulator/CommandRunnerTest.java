package love.korni.manipulator;

import love.korni.manipulator.core.caldron.Caldron;
import love.korni.manipulator.core.runner.Runner;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * CommandRunnerTest
 */
public class CommandRunnerTest {

    private static int count = 0;

    @BeforeTest
    public void setUp() {
        count = 0;
    }

    @Test
    public void testCommandRunner() {
        Caldron run = Manipulator.run(CommandRunnerTest.class);

        Assert.assertEquals(count, 2);
    }

    private static class CommandRunner implements Runner {

        @Override
        public void run(String... args) {
            count++;
        }
    }

    private static class CommandRunnerTwo implements Runner {

        @Override
        public void run(String... args) {
            count++;
        }
    }

}
