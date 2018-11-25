package basic;

public class ObjectB {
    public String f1;
    public String f2;
    public ObjectA f3;

    public ObjectB() {}
    public ObjectB(String f1, String f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public ObjectA getA() {
        ObjectA a = new ObjectA();
        return a;
    }
}