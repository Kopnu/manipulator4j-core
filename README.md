[jitpack]: https://img.shields.io/jitpack/v/github/Kopnu/manipulator4j
# Manipulator4J

A simple DI container inspired by the Spring Framework. This is a light parody of the real functionality 🙂
Required if you want to use IoC like in Spring, but it's an overhead for you.

## Usage

##### Add repository
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

##### Add dependency

Check latest release version here: [GitHub Releases](https://github.com/Kopnu/spring-boot-starter-jda/releases)

[ ![jitpack][] ](https://jitpack.io/#Kopnu/manipulator4j)

```xml
<dependency>
    <groupId>com.github.Kopnu</groupId>
    <artifactId>manipulator4j</artifactId>
    <version>VERSION</version>
</dependency>
```

##### Create your entry Main class

###### Autoinject by field
```java
@Gear // Use this annotation for register class into DI container
public class Main {

    public static void main(String[] args) {
        Caldron caldron = Manipulator.run(Main.class, args); // Run Manipulator
        Main instance = caldron.getInstance(Main.class);     // Get Gear from DI container
        instance.sayHello();
    }

    @Autoinject // Annotation for inject your gear into a field
    private Caldron caldronInjected;
    
    public void sayHello() {
        System.out.println("Hello world!");
    }
}
```

###### Autoinject by constructor
```java
@Gear // Use this annotation for register class into DI container
public class Main {

    public static void main(String[] args) {
        Caldron caldron = Manipulator.run(Main.class, args);   // Run Manipulator
        Main instance = caldron.getGearOfType(Main.class);     // Get Gear from DI container
        instance.sayHello();
    }

    private final Caldron caldronInjected;

    @Autoinject // Or you can use it with constructors
    public Main(Caldron caldronInjected) {
        this.caldronInjected = caldronInjected;
    }

    public void sayHello() {
        System.out.println("Hello world!");
    }

}
```

###### Autoinject with interface
```java
public interface Test {
    void test();
}

@Gear // Use a default ("testimpl") Gear name
public class TestImpl implements Test {
    public void test() {
        System.out.println("Test");
    }
}

@Gear("testingGear") // Set a custom Gear name
public class Test2Impl implements Test {
    @Override
    public void test() {
        System.err.println("Test2");
    }
}

@Gear
public class Main {

    @Autoinject("testingGear") // Inject a Gear with a specific name 
    private Test test;

    public static void main(String[] args) {
        Caldron caldron = Manipulator.run(Main.class, args);
        Main instance = caldron.getGearOfType(Main.class);
        instance.sayHello();
    }

    public void sayHello() {
        test.test();
    }
}
```

###### Create a Gear from method.
> Don't try to create a class from a method inside yourself.
```java
public class Config { // This class will be a Gear automatically bcz  there is @Gear inside

    @Gear // You can register your Gear from method. 
    public Main main(Caldron caldron) { // Autoinjected parameters
        return new Main(caldron); // Your custom Gear configuration
    }

}

public class Main {

    public static void main(String[] args) {
        Caldron caldron = Manipulator.run(Main.class, args);   // Run Manipulator
        Main instance = caldron.getGearOfType(Main.class);     // Get Gear from DI container
        instance.sayHello();
    }

    private final Caldron caldronInjected;

    public Main(Caldron caldronInjected) {
        this.caldronInjected = caldronInjected;
    }

    public void sayHello() {
        System.out.println("Hello world!");
    }

}
```

