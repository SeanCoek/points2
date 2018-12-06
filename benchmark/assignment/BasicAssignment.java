package assignment;

import basic.ObjectA;
import basic.ObjectB;
import basic.ObjectC;

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
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        ObjectC c1 = new ObjectC();
        ObjectC c2 = new ObjectC();
        a.objc = c1;
        b.objc = c2;
        a.objc = b.objc;
    }

    public void field2call() {
        ObjectB b1 = new ObjectB();
        b1.f3 = b1.getA();
    }

    public void var2param() {
        ObjectA a = new ObjectA();
        ObjectB b = new ObjectB();
        b.setA(a);
    }

    public void field2param() {
        ObjectA a = new ObjectA();
        ObjectB b1 = new ObjectB();
        ObjectB b2 = new ObjectB();

        b1.f3 = a;
        b2.setA(b1.f3);
    }

    public void call2param() {
        ObjectA a = new ObjectA();
        ObjectB b1 = new ObjectB();
        ObjectB b2 = new ObjectB();

        b1.f3 = a;
        b2.setA(b1.getA());
    }

    public void arrayAssign() {
        // local array
        ObjectA[] loc_arr = new ObjectA[2];
        ObjectA[] loc_arr2 = loc_arr;
        loc_arr[0] = new ObjectA();
        ObjectA loc_a = loc_arr[0];
        loc_arr[1] = loc_a;

        // field array
        ObjectB b = new ObjectB();
        ObjectA[] field_arr = new ObjectA[2];
        b.a_arr = field_arr;
        ObjectA[] field_arr2 = b.a_arr;

        // array as parameter

    }
}