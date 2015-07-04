import org.junit.Test

/**
 * author: TRYavasU
 * date: 18/04/2015
 */
class MyClass {

    @Test
    void "mapasinterfaceimpl"() {
        def map = [
                accept: { it > 5 }
        ] as Acceptable

        assert !map.accept(1)
        assert map.accept(10)
    }
}


