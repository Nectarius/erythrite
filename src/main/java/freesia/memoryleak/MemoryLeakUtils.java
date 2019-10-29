package freesia.memoryleak;

import java.util.Map;

public class MemoryLeakUtils {

    public static void putValueIntoProperties(String value){
        Map map = System.getProperties();
        for(int i=0; i< 1000000; i ++) {
            map.put(new BadKey("key"), value);
        }

       //StringLeaker stringLeaker = new StringLeaker();

    }
}
