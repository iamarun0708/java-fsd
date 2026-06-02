class Animal {
    public void makeSound() {
        System.out.println("Generic animal sound");
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Bark");
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Animal genericAnimal = new Animal();
        Animal myDog = new Dog();

        genericAnimal.makeSound();
        myDog.makeSound();
    }
}
