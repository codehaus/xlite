package deprecated;

import org.testng.annotations.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import si.ptb.xfast.ClassMapper;
import deprecated.ClassMapperHolder;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 6:15:04 PM
 */
public class ClasMapperHolderTest {

    @Test
    public void testSorting() {
        ClassMapper[] arr = new ClassMapper[4];
        arr[0] = new ClassMapper("aa", Object.class);
        arr[1] = new ClassMapper("bb", Object.class);

        Arrays.sort(arr, new ClassMapperHolder.ClassMapperComparer());

        assert arr[0].nodeName.equals("aa");
        assert arr[1].nodeName.equals("bb");
        assert arr[2] == null;
        assert arr[3] == null;
    }

    @Test
    public void testResize() {
        ClassMapperHolder mappers = new ClassMapperHolder();

        List<String> words = words();

        // this will internally require resizing of the array
        for (String word : words) {
            mappers.add(new ClassMapper(word, Object.class));
        }

        assert mappers.getNodeNames().size() == words.size();

    }

    @Test
    public void testSpeed() {
        String[] keywords = {"augue", "elit", "blandit", "platea", "lectus", "purus", "Mauris",
                "suscipit", "vehicula", "tincidunt"};

        Map<String, ClassMapper> cmap = new HashMap<String, ClassMapper>();
        ClassMapperHolder cholder = new ClassMapperHolder();

        List<String> keys = new ArrayList<String>();

        // setup test data
        String word;
        for (int j = 0; j < 300000; j++) {
            word = randomWord();
            ClassMapper m;
            if (j % 1000 == 0) {
                System.out.println(word + " " + (j / 1000));
                keys.add(word);
                m = new ClassMapper(word, String.class);
            } else {
                m = new ClassMapper(word, Object.class);
            }
            cmap.put(word, m);
            cholder.add(m);
        }
        System.out.println("done preparing! keys:"+keys.size());

        // benchmarking map
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            for (String key : keys) {
               assert cmap.get(key).targetClass.equals(String.class);
            }
        }
        long timeMap = System.currentTimeMillis() - start;
        System.out.println("done map, size: " + cmap.size());

        // benchmarking
        cholder.sortByNodeName();
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            for (String key : keys) {
               assert cholder.get(key).targetClass.equals(String.class);
            }
        }
        long timeHolder = System.currentTimeMillis() - start;

        System.out.println("map:    " + timeMap);
        System.out.println("holder: " + timeHolder);
    }

    /**
     * Generates a List of arbitrary words. Used for testing purposes.
     *
     * @return
     */
    public List<String> words() {
        List list = new ArrayList();
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Suspendisse aliquet erat a mauris. " +
                "Sed metus augue, aliquam in, dignissim ut, congue nec, ligula. Integer quis mi in urna lacinia dictum. " +
                "Donec tellus. Ut magna sem, ultrices elementum, placerat id, tristique pulvinar, eros. " +
                "Vestibulum commodo felis et metus convallis volutpat. Mauris eu diam eget urna porttitor egestas. " +
                "Duis ultricies risus id nibh. In massa metus, luctus in, malesuada vel, hendrerit vitae, nulla. " +
                "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; " +
                "Donec eleifend enim sit amet mi. Donec non turpis.In justo. In nec elit. In non velit eget leo " +
                "semper convallis. Nunc tempor, justo eget consectetuer suscipit, pede dui elementum urna, id ultricies" +
                " leo purus ac dolor. Pellentesque sit amet nunc quis lorem lacinia molestie. Aliquam pulvinar magna " +
                "sed ante. Vivamus adipiscing aliquam neque. Suspendisse sit amet arcu. Curabitur auctor. Nunc tellus." +
                " Vivamus orci leo, suscipit id, volutpat nec, aliquet eu, augue. Nulla ornare vulputate orci." +
                " Fusce quis odio. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos " +
                "hymenaeos. Integer et eros quis enim tristique venenatis. Aenean a urna. Sed nibh felis, porttitor at," +
                " egestas at, consectetuer vel, nisl. In pellentesque justo id sem consequat vulputate. Sed volutpat," +
                " ipsum id dictum venenatis, enim purus sagittis ante, eu blandit eros lectus nec nisl. In hac habitasse" +
                " platea dictumst. Praesent est. Nullam ipsum dolor, rhoncus ut, ultrices a, tincidunt eu, augue. Cras" +
                " vitae neque. Ut pulvinar suscipit sapien. Aliquam vehicula ipsum vel ante. Aliquam sed mi vitae orci" +
                " luctus sagittis. Nullam non quam nec eros convallis mollis. Donec varius dolor eget nunc aliquam" +
                " elementum. Integer eleifend Proin pretium augue vitae justo. Curabitur erat nunc, placerat sed, " +
                "sagittis at, fringilla ac, sem. Ut sodales tellus sed enim. Pellentesque ut enim ut orci sollicitudin " +
                "adipiscing. In hac habitasse platea dictumst. Nunc semper luctus metus. Donec tincidunt vulputate nunc" +
                ". Sed diam turpis, vulputate ac, tempus in, dictum sed, urna. Morbi vitae nisl ac nisi dignissim fringilla." +
                " Mauris consequat. Donec malesuada. Duis feugiat volutpat risus. Donec accumsan sapien quis neque" +
                "Cras ut tellus id sapien tincidunt molestie. Mauris fermentum pede nec est. Maecenas metus. Suspendisse " +
                "libero ligula, pellentesque eu, ultrices vel, dictum vel, urna. Mauris tincidunt, urna non dignissim" +
                " scelerisque, diam lacus adipiscing diam, at bibendum erat nulla eu ante. Nunc eget diam. Morbi iaculis." +
                " Nam non diam. Aliquam erat volutpat. Proin in urna sed lorem semper bibendum. Maecenas felis est, lacinia" +
                " ut, dictum id, aliquam in, mauris. Fusce facilisis eros ut urna. Etiam eu libero. Donec non massa." +
                " Duis dolor. Praesent ligula nulla, viverra iaculis, imperdiet vel, tristique eget, nisl. Aliquam " +
                "erat volutpat. Quisque sed tellus non elit accumsan pharetra. Phasellus aliquet dapibus nisi." +
                "Ut sed velit. Curabitur nunc nulla, volutpat nec, ullamcorper ac, imperdiet a, erat. Ut dictum. " +
                "Sed vel leo. Proin nisi ligula, blandit condimentum, accumsan quis, pulvinar quis, magna. Proin id mi" +
                " ac nisl pulvinar feugiat. Pellentesque velit sapien, eleifend nec, vehicula ac, dictum vulputate," +
                " dolor. Nam ac purus. Quisque gravida dolor quis dui. Morbi facilisis. Nullam nec neque in lacus" +
                " tempor luctus. Proin varius pede non diam. Donec est. Sed quis eros. Integer id dui. Aenean posuere" +
                " vehicula risus. Vivamus eu sapien. Nunc feugiat aliquam libero. ";

        // remove non-alphanumeric characters
        Pattern p = Pattern.compile("\\W");

        // extract words
        StringTokenizer parser = new StringTokenizer(loremIpsum);
        while (parser.hasMoreTokens()) {
            Matcher matcher = p.matcher(parser.nextToken());
            String output = matcher.replaceAll("");
            list.add(output);
        }

        return list;
    }

    public String randomWord() {
        Random rand = new Random();
        int c;
        char[] chars = new char[10];
        for (int i = 0; i < chars.length; i++) {
            c = rand.nextInt(123 - 97) + 97;
            chars[i] = (char) c;
        }
        return new String(chars);
    }

}
