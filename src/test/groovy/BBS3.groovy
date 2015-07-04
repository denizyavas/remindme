import org.junit.Test

/**
 * author: TRYavasU
 * date: 17/04/2015
 */
class BBS3 {
    class MyOtherClass {
        String myString = "I am over in here in myOtherClass"
    }

    class MyOtherClass2 {
        String myString = "I am over in here in myOtherClass2"
    }

    class MyClass {
        def closure = {
            println myString
        }
    }


    @Test
    void "closureDelegate"() {
        MyClass myClass = new MyClass()
        def closure = new MyClass().closure
        closure.delegate = new MyOtherClass()
        closure() // outputs: I am over in here in myOtherClass

        closure = new MyClass().closure
        closure.delegate = new MyOtherClass2()
        closure() // outputs: I am over in here in myOtherClass2
    }
}
