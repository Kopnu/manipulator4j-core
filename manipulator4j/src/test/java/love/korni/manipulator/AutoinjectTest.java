package love.korni.manipulator;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.Caldron;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sergei_Kornilov
 */
public class AutoinjectTest {

    @Test(priority = 1)
    public void testAutoinjectAnnotation() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        AutoinjectClassTest gearOfType = caldron.getGearOfType(AutoinjectClassTest.class);

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.rabbit);
    }

    @Test(priority = 2)
    public void testAutoinjectConstructor() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        ConstructorClassTest gearOfType = caldron.getGearOfType(ConstructorClassTest.class);

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.rabbit);
    }

    //ToDo: https://github.com/Kopnu/manipulator4j-core/issues/9
    //  @Test(priority = 3)
    public void testAutoinjectConstructorWithoutAnnotation() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        ConstructorWithoutAnnotationClassTest gearOfType = caldron.getGearOfType(ConstructorWithoutAnnotationClassTest.class);

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.rabbit);
    }

    @Test(priority = 4)
    public void testAutoinjectByAbstractClass() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        AbstractClassTest gearOfType = caldron.getGearOfType(AbstractClassTest.class);

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.abstractClass);
        Assert.assertTrue(gearOfType.abstractClass instanceof Rabbit);
    }

    @Test(priority = 5)
    public void testAutoinjectList() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        ListClassTest gearOfType = caldron.getGearOfType(ListClassTest.class);

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.interfaceClasses);
        Assert.assertEquals(gearOfType.interfaceClasses.size(), 2);
    }

    @Test(priority = 6)
    public void testGetGearOfTypeWithArgs() {
        Caldron caldron = Manipulator.run(AutoinjectTest.class);

        Rabbit3 gearOfType = caldron.getGearOfType(Rabbit3.class, new Rabbit(), "s");

        Assert.assertNotNull(gearOfType);
        Assert.assertNotNull(gearOfType.rabbit);
        Assert.assertNotNull(gearOfType.s);
    }


    @Gear
    public static class Rabbit extends AbstractClass implements InterfaceClass {
    }

    @Gear
    public static class Rabbit2 implements InterfaceClass {
    }

    @Gear
    public static class Rabbit3 {

        private Rabbit rabbit;
        private String s;

        public Rabbit3(Rabbit rabbit, String s) {
            this.rabbit = rabbit;
            this.s = s;
        }

    }

    public static abstract class AbstractClass {
    }

    public interface InterfaceClass {
    }

    @Gear
    public static class AutoinjectClassTest {
        @Autoinject
        private Rabbit rabbit;
    }

    @Gear
    public static class ConstructorClassTest {
        private final Rabbit rabbit;

        @Autoinject
        public ConstructorClassTest(Rabbit rabbit) {
            this.rabbit = rabbit;
        }
    }

    @Gear
    public static class ConstructorWithoutAnnotationClassTest {
        private final Rabbit rabbit;

        public ConstructorWithoutAnnotationClassTest(Rabbit rabbit) {
            this.rabbit = rabbit;
        }
    }

    @Gear
    public static class AbstractClassTest {
        private final AbstractClass abstractClass;

        @Autoinject
        public AbstractClassTest(AbstractClass abstractClass) {
            this.abstractClass = abstractClass;
        }
    }

    @Gear
    public static class ListClassTest {
        @Autoinject
        private List<InterfaceClass> interfaceClasses;
    }

}