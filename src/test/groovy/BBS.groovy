import org.junit.Test

/**
 * author: TRYavasU
 * date: 17/04/2015
 */
class BBS {


    @Test
    void "typeInference"() {
        assert add(1, 2) == 3
        assert add(1.9, 2.2) == 4.1
        assert add(1.9d, 2.2d) == 4.1d
        assert add("Deniz ", "Yavas") == "Deniz Yavas"
    }

    def add(def x, def y) {
        x + y
    }


    @Test
    void "groovyBeans"() {
        def human = new Human()
        human.age = 12
        assert human.age == 12

        def human2 = new Human(age: 13)
        assert human2.age == 13
    }

    @Test
    void "metaProgramming"() {
        String.metaClass {
            findNumbers = {
                delegate.findAll(/\d+/)*.toInteger()
            }
        }

        String test = "D3N1Z"
        assert [3, 1] == test.findNumbers()
    }

    @Test
    void "noParanthesesNoSemicolon"() {
        Human human = new Human()
        human.say "hello"
    }

    @Test
    void "closure"() {
        def oneClosure = {
            "1"
        }
        def oneClosure2 = {
            it + "2"
        }
        def oneClosure3 = { def something ->
            something + "3"
        }
        def oneClosure4 = { def foo, def bar ->
            foo + "4" + bar
        }

        assert oneClosure() == "1"
        assert oneClosure2("a") == "a2"
        assert oneClosure3("a") == "a3"
        assert oneClosure4("a", "b") == "a4b"
    }

    @Test
    void "closure2"() {
        def square = {
            it * it
        }

        def process = { number, method ->
            method(number)
        }

        assert 9 == process(3, square)
    }

    @Test
    void "variableDefinition"() {
        def someInt = 3
        def someString = "some string"
        def someList = [1, 2, 3]
        def someMap = [1: "some", 2: "map"]
    }

    @Test
    void "defaultGroovyMethods"() {
        assert [1, 2, 3, 4, 5].find({ it % 2 }) == 1
        assert [1, 2, 3, 4, 5].findAll({ it % 2 }) == [1, 3, 5]
        assert [1, 2, 3, 4, 5].collect({ it + 1 }) == [2, 3, 4, 5, 6]


        def result = ['Groovy', 'Rocks', 'Big', 'Time'].collectEntries {
            [(it): it.contains('o')]
        }
        assert result == [Groovy: true, Rocks: true, Big: false, Time: false]
        assert result.Groovy && result.Rocks
        assert !result.Big && !result.Time

        assert [1, 2, 3, 4, 5].contains(1)
        assert ![1, 2, 3, 4, 5].contains(6)

        def hello = "Hello"
        assert hello.take(hello.size() - 1) == "Hell"
    }

    @Test
    void "defaultGroovyMethods2"() {

        def newCollection = []
        [1, 2, 3, 4, 5].each({
            newCollection << it
        })
        assert newCollection == [1, 2, 3, 4, 5]

        assert [1, 2, 3, 4, 5].every({ it < 6 })

        assert [1, [2, 3], [[4]], [5]].flatten() == [1, 2, 3, 4, 5]

        assert [1, 2, 3, 4, 5].join(':') == '1:2:3:4:5'

        assert [1, 2, 3, 4, 5].minus([2, 4]) == [1, 3, 5]

        assert [1, 2, 3, 4, 5].plus([7, 9]) == [1, 2, 3, 4, 5, 7, 9]

        assert [3, 2, 4, 1, 5].sort() == [1, 2, 3, 4, 5]

    }

    @Test
    void "withMethod"() {
        Human human = new Human()
        human.with {
            walk()
            turn "right"
            say "hello world"
        }
    }

    class Human {
        def age
        def walk = {
            println "go 10 miles"
        }

        def turn = { direction ->
            println "turn " + direction
        }
        //This is a closure
        def say = { something ->
            println something
        }
    }

    @Test
    void "curryAndOptionalParameter"() {
        def joinTwoWordsWithSymbol = { symbol, first, second -> first + symbol + second }
        assert joinTwoWordsWithSymbol('#', 'Hello', 'World') == 'Hello#World'

        def concatWords = joinTwoWordsWithSymbol.curry(' ')
        assert concatWords('Hello', 'World') == 'Hello World'

        def prependHello = concatWords.curry('Hello')
        assert prependHello('World') == 'Hello World'

        def joinTwoWords = { word1, joiner = ' ', word2 ->
            word1 + joiner + word2
        }

        assert joinTwoWords("Hello", "+", "World") == "Hello+World"
        assert joinTwoWords("Hello", "World") == "Hello World"
    }

    @Test
    void "xmlBuilder"() {
        def writer = new StringWriter()
        def builder = new groovy.xml.MarkupBuilder(writer)
        builder.languages {
            language(year: 1995) {
                name "java"
                paradigm "object oriented"
                typing "static"
            }
            language(year: 1995) {
                name "ruby"
                paradigm "object oriented, functional"
                typing "dynamic, duck typing"
            }
            language(year: 2003) {
                name "groovy"
                paradigm "object oriented, functional"
                typing "dynamic, static, duck typing"
            }
        }
        def languages = new XmlSlurper().parseText(writer.toString())

        assert languages.language[2].name == "groovy"
        assert languages.language[2].paradigm == "object oriented, functional"
    }


    @Test
    void "gstring"() {
        assert "hello" == "hello"

        def hello = "Hello"

        assert "Hello World!" == "$hello World!"
        assert "Hello World!" == "${hello + " Wor"}ld!"

    }

    @Test
    void "thisownerdelegate"() {
        def closure = new MyClass().outerClosure
        closure()
    }


    class MyClass {

        def outerClosure = {
            println this.class.name    // outputs BBS$MyClass
            println owner.class.name    // outputs BBS$MyClass
            println delegate.class.name  //outputs BBS$MyClass
            def nestedClosure = {
                println this.class.name
                // outputs BBS$MyClass
                println owner.class.name
                // outputs BBS$MyClass$_closure1
                println delegate.class.name
                // outputs BBS$MyClass$_closure1

            }
            nestedClosure()

        }
    }

    @Test
    void "operators"() {
        //Conditional Operator
        def expression = false
        assert !expression

        //Spread Operator
        assert ['cat', 'elephant']*.size() == [3, 8]

        //Elvis Oprerator
        def user = [name: "Deniz"]
        def displayName = user.name ? user.name : "Anonymous" //traditional ternary operator usage
        def displayName2 = user.name ?: "Anonymous" // more compact Elvis operator - does same as above

        assert displayName == displayName2

        //Safe Navigation
        def streetName = user?.address?.street
        //streetName will be null if user or user.address is null - no NPE thrown

        assert !streetName
    }

}
