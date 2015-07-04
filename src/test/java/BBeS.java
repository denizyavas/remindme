import org.junit.Test;

import java.util.*;

/**
 * author: TRYavasU
 * date: 21/04/2015
 */
public class BBeS {

    @Test
    public void javaBeans() {
        Human human = new Human();
        human.setAge(12);
        assert human.getAge() == 12;

        Human human2 = new Human(13);
        assert human2.getAge() == 13;
    }

    class Human {
        private int age;

        Human() {
        }

        Human(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void find() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        int result = 0;
        for (int oneInt : integerList) {
            if (oneInt % 2 == 1) {
                result = oneInt;
                break;
            }
        }
        assert result == 1;
    }

    @Test
    public void findAll() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> result = new ArrayList<Integer>();
        for (int oneInt : integerList) {
            if (oneInt % 2 == 1) {
                result.add(oneInt);
            }
        }
        assert result.get(0) == 1;
        assert result.get(1) == 3;
        assert result.get(2) == 5;
    }


    @Test
    public void collect() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> result = new ArrayList<Integer>();
        for (int oneInt : integerList) {
            result.add(oneInt + 1);
        }
        assert result.get(0) == 2;
        assert result.get(1) == 3;
        assert result.get(2) == 4;
        assert result.get(3) == 5;
        assert result.get(4) == 6;
    }


    @Test
    public void collectEntries() {
        List<String> stringList = Arrays.asList("Groovy", "Rocks", "Big", "Time");
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (String oneString : stringList) {
            result.put(oneString, oneString.contains("o"));
        }
        assert result.get("Groovy");
        assert result.get("Rocks");
        assert !result.get("Big");
        assert !result.get("Time");
    }


    @Test
    public void contains() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        assert integerList.contains(1);
    }


    @Test
    public void take() {
        String hello = "Hello";
        assert hello.substring(0, hello.length() - 1).equals("Hell");
    }
}
