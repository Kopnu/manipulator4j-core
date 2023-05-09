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

    @Test
    public void testCommandRunner() {
        Caldron run = Manipulator.run(CommandRunnerTest.class);

        Assert.assertNotNull(run);
    }

    private static class CommandRunner implements Runner {

        @Override
        public void run(String... args) {

        }
    }

    private static class CommandRunnerTwo implements Runner {

        @Override
        public void run(String... args) {

        }
    }

}
