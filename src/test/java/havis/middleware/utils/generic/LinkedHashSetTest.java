package havis.middleware.utils.generic;

import havis.middleware.utils.generic.LinkedHashSet;
import havis.middleware.utils.generic.Predicate;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class LinkedHashSetTest {

    @Test
    public void add() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        String result = set.add("a", "a");
        Assert.assertEquals("a", result);
        Assert.assertEquals("a", set.get("a"));
        Assert.assertEquals(1, set.getCount());

        result = set.add("a", "a");
        Assert.assertEquals("a", result);
        Assert.assertEquals("a", set.get("a"));
        Assert.assertEquals(1, set.getCount());
    }

    @Test
    public void update() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        String result = set.update("a");
        Assert.assertEquals("a", result);
        Assert.assertEquals("a", set.get("a"));
        Assert.assertEquals(1, set.getCount());

        result = set.update("a");
        Assert.assertEquals("a", result);
        Assert.assertEquals("a", set.get("a"));
        Assert.assertEquals(1, set.getCount());
    }

    @Test
    public void tryGetValue() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update("a");
        String result = set.tryGetValue("a", String.class);
        Assert.assertEquals("a", result);

        result = set.tryGetValue("b", String.class);
        Assert.assertEquals("", result);
    }

    @Test
    public void remove() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update("aa");
        set.update("bb");
        set.update("a");
        set.update("b");
        set.update("c");
        set.update("cc");

        Assert.assertEquals(6, set.getCount());

        set.remove(new Predicate<String>() {
            @Override
            public boolean invoke(String e) {
                return e.length() == 2;
            }
        });

        Assert.assertEquals(4, set.getCount());

        Iterator<String> iterator = set.toList().iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertEquals("c", iterator.next());
        Assert.assertEquals("cc", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeWithNull() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update("aa");
        set.update(null);
        set.update("a");
        set.update("b");
        set.update("c");
        set.update("cc");

        Assert.assertEquals(6, set.getCount());

        set.remove(new Predicate<String>() {
            @Override
            public boolean invoke(String e) {
                return e.length() == 2;
            }
        });

        Assert.assertEquals(5, set.getCount());

        Iterator<String> iterator = set.toList().iterator();
        Assert.assertEquals(null, iterator.next());
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertEquals("c", iterator.next());
        Assert.assertEquals("cc", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeFirst() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update("first");
        set.update("a");
        set.update("b");
        set.update("c");

        Assert.assertEquals(4, set.getCount());

        Assert.assertTrue(set.removeFirst());

        Assert.assertEquals(3, set.getCount());

        Iterator<String> iterator = set.toList().iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertEquals("c", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void removeFirstWithNull() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update(null);
        set.update("a");
        set.update("b");
        set.update("c");

        Assert.assertEquals(4, set.getCount());

        Assert.assertFalse(set.removeFirst());

        Assert.assertEquals(4, set.getCount());

        Iterator<String> iterator = set.toList().iterator();
        Assert.assertEquals(null, iterator.next());
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertEquals("c", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void set() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.set("a", "a");
        Assert.assertEquals(1, set.getCount());
        Assert.assertEquals("a", set.get("a"));
    }

    @Test
    public void find() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.update("a");
        set.update("b");
        set.update("c");

        Assert.assertEquals(3, set.getCount());

        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean invoke(String e) {
                return e.length() == 2;
            }
        };

        Assert.assertFalse(set.find(predicate));

        set.update("aa");
        Assert.assertEquals(4, set.getCount());

        Assert.assertTrue(set.find(predicate));
    }
}
