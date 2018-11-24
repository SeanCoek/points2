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
}