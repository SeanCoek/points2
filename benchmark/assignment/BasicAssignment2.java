package assignment;

import basic.ObjectA;
import basic.ObjectB;

public class BasicAssignment2 {

    public void assignInLocalInitial(ObjectA paramA, ObjectA[] paramArr) {
        // assignment : "new" operation
        ObjectA a1 = new ObjectA();
        ObjectA[] a2 = new ObjectA[2];

        // assignment : local var
        ObjectA a3 = a1;
        ObjectA[] a4 = a2;

        // assignment : field
        ObjectB b = new ObjectB();
        b.f3 = new ObjectA();
        b.a_arr = new ObjectA[2];
        ObjectA a5 = b.f3;
        ObjectA[] a6 = b.a_arr;

        // assignment : parameter
        ObjectA a7 = paramA;
        ObjectA[] a8 = paramArr;

        // assignment : method invocation
        ObjectA a9 = b.getA();
        ObjectA[] a10 = b.getArray();
    }

    public void commonAssignment(ObjectA paramA, ObjectA paramA2, ObjectA[] paramArr, ObjectA[] paramArr2) {
        ObjectA locA = null;
        ObjectA[] locArr = null;
        ObjectB objForAccessField = new ObjectB();
        // assignment : "new" operation
        locA = new ObjectA();
        locArr = new ObjectA[2];
        paramA = new ObjectA();
        paramArr = new ObjectA[2];
        objForAccessField.f3 = new ObjectA();
        objForAccessField.a_arr = new ObjectA[2];

        // assignment : local var
        ObjectA locA2 = new ObjectA();
        ObjectA[] locArr2 = new ObjectA[2];
        locA = locA2;
        locArr = locArr2;
        paramA = locA2;
        paramArr = locArr2;
        objForAccessField.f3 = locA2;
        objForAccessField.a_arr = locArr2;

        // assignment : field
        ObjectB objForAccessField2 = new ObjectB();
        locA = objForAccessField.f3;
        locArr = objForAccessField.a_arr;
        paramA = objForAccessField.f3;
        paramArr = objForAccessField.a_arr;
        objForAccessField.f3 = objForAccessField2.f3;
        objForAccessField.a_arr = objForAccessField2.a_arr;

        // assignment : parameter
        paramA2 = new ObjectA();
        paramArr2 = new ObjectA[2];
        locA = paramA2;
        locArr = paramArr2;
        paramA = paramA2;
        paramArr = paramArr2;
        objForAccessField.f3 = paramA2;
        objForAccessField.a_arr = paramArr2;

        // assignment : invocation
        locA = objForAccessField.getA();
        locArr = objForAccessField.getArray();
        paramA = objForAccessField.getA();
        paramArr = objForAccessField.getArray();
        objForAccessField.f3 = objForAccessField.getA();
        objForAccessField.a_arr = objForAccessField.getArray();
    }

    public void methodInvocation(ObjectA paramA, ObjectA[] paramArr) {
        // local var
        ObjectA locA = new ObjectA();
        ObjectA[] locArr = new ObjectA[2];
        ObjectB objForAccessMethod = new ObjectB();
        ObjectB objForAccessField = new ObjectB();
        objForAccessMethod.setA(locA);
        objForAccessMethod.setArray(locArr);

        // param
        objForAccessMethod.setA(paramA);
        objForAccessMethod.setArray(paramArr);

        // field
        objForAccessMethod.setA(objForAccessField.f3);
        objForAccessMethod.setArray(objForAccessField.a_arr);

        // method invocation
        objForAccessMethod.setA(objForAccessField.getA());
        objForAccessMethod.setArray(objForAccessField.getArray());
    }
}
