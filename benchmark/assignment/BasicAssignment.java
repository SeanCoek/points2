package assignment;

import basic.ObjectA;
import basic.ObjectB;

public class BasicAssignment {

    public void var2var() {
        ObjectA a1 = new ObjectA();
        ObjectA a2 = new ObjectA();
        a1 = a2;
    }

    public void var2field() {
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        b.f3 = new ObjectA();
        a = b.f3;
    }

    public void var2call() {
        ObjectA a = null;
        ObjectB b = new ObjectB();
        a = b.getA();
    }

    public void field2var() {
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        b.f3 = a;
    }

    public void field2field() {
        ObjectB b1 = new ObjectB();
        ObjectB b2 = new ObjectB();
        b1.f3 = new ObjectA();
        b2.f3 = new ObjectA();
        b1.f3 = b2.f3;
    }

    public void field2call() {
        ObjectB b1 = new ObjectB();
        b1.f3 = b1.getA();
    }
}