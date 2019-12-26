package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CakeTest {
  private Cake cake;

  @Before
  public void before() {
    cake = new Cake();
  }

  @Test
  public void testCopyConstructor_copiesEntries() {
    Cake cake = new Cake();
    cake.publish("foo", "bar");
    cake.publish("json", 123);

    Cake copy = new Cake(cake);
    assertEquals("bar", copy.get("foo"));
    assertEquals(123, (int) copy.get("json"));
  }

  @Test(expected = RuntimeException.class)
  public void testGet_throwsOnMissingKey() {
    cake.get("foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGet_throwsOnNullKey() {
    cake.get(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGet_throwsOnEmptyString() {
    cake.get("");
  }

  @Test
  public void testGet_getsWhenSubkeyContainsSeparator() {
    cake.inNamespace(
        "foo",
        () -> {
          cake.publish("bar", 1);
        });

    int value = cake.get("foo" + Cake.SEPARATOR + "bar");
    assertEquals(1, value);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGet_throwsOnNullSubkey() {
    cake.get("foo", null, "bar");
  }

  @Test
  public void testGet_keySubstringOfOtherKeyInNamespaceNotAmbiguous() {
    cake.inNamespace(
        "foo",
        () -> {
          cake.publish("B", "B");
          cake.publish("AB", "AB");
        });

    assertEquals("B", cake.get("B"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPublish_throwsOnNullKey() {
    cake.publish(null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPublish_throwsOnEmptyStringKey() {
    cake.publish("", "value");
  }

  @Test
  public void testPublish_publishesKey() {
    cake.publish("key", "value");
    assertEquals("value", cake.get("key"));
  }

  @Test
  public void testPublish_publishesNamespacedKey() {
    cake.publish("a" + Cake.SEPARATOR + "b", "c");
    assertEquals("c", cake.get("a", "b"));
  }

  @Test
  public void testPublish_publishesMultipleKeys() {
    cake.publish("key1", "value1");
    cake.publish("key2", "value2");
    assertEquals("value1", cake.get("key1"));
    assertEquals("value2", cake.get("key2"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPublish_throwsOnEmptyStringSubkey() {
    cake.publish("a" + Cake.SEPARATOR + "", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKey_throwsOnNoInputs() {
    Cake.key();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKey_throwsOnNullSubkey() {
    Cake.key("foo", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKey_throwsOnEmptyStringSubkey() {
    Cake.key("", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKey_throwsOnSubkeyContainingSeparator() {
    Cake.key("a" + Cake.SEPARATOR + "b", "c");
  }

  @Test
  public void testKey_noSeparatorForSingleKey() {
    assertEquals("a", Cake.key("a"));
  }

  @Test
  public void testKey_returnsKeysSeparatedBySeparator() {
    assertEquals("a" + Cake.SEPARATOR + "b", Cake.key("a", "b"));
  }

  @Test
  public void testInNamespace_publishesKeyInNamespace() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("b", "c");
        });

    assertEquals("c", cake.get("a", "b"));
  }

  @Test
  public void testInNamespace_unwrapsNamespaceOnThrow() {
    try {
      cake.inNamespace(
          "a",
          () -> {
            throw new RuntimeException();
          });
    } catch (RuntimeException e) {
    }

    cake.publish("b", "c");
    assertEquals("c", cake.get("b"));
  }

  @Test
  public void testInNamespace_unwrapsOnlyLatestNamespaceOnThrow() {
    cake.inNamespace(
        "a",
        () -> {
          try {
            cake.inNamespace(
                "b",
                () -> {
                  throw new RuntimeException();
                });
          } catch (RuntimeException e) {
          }

          cake.publish("c", "d");
        });

    assertEquals("d", cake.get("a", "c"));
  }

  @Test
  public void testInNamespace_getsKeyInCurrentNamespace() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("b", "c");
          assertEquals("c", cake.get("b"));
        });
  }

  @Test
  public void testInNamespace_getPrefersKeyInCurrentNamespace() {
    cake.publish("key", "foo");
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "bar");
          assertEquals("bar", cake.get("key"));
        });
    assertEquals("foo", cake.get("key"));
  }

  @Test
  public void testInNamespsace_findsKeyInParentNamespace() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "foo");
          cake.inNamespace(
              "b",
              () -> {
                assertEquals("foo", cake.get("key"));
              });
        });
  }

  @Test
  public void testGet_findsPartialKey_fromRoot() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "foo");
        });
    assertEquals("foo", cake.get("key"));
  }

  @Test
  public void testGet_findsPartialKey_fromUnrelatedNamespace() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "foo");
        });
    cake.inNamespace(
        "b",
        () -> {
          assertEquals("foo", cake.get("key"));
        });
  }

  @Test(expected = RuntimeException.class)
  public void testGet_throwsOnAmbiguousPartialKey() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "foo");
        });
    cake.inNamespace(
        "b",
        () -> {
          cake.publish("key", "foo");
        });
    cake.get("key");
  }

  @Test(expected = RuntimeException.class)
  public void testGetPublishedKeyForValue_throwsOnNonExistentValue() {
    cake.getPublishedKeyForValue("foo", false);
  }

  @Test
  public void testGetPublishedKeyForValue_getsValue() {
    cake.publish("key", "value");
    assertEquals("key", cake.getPublishedKeyForValue("value", false));
  }

  @Test(expected = RuntimeException.class)
  public void testGetPublishedKeyForValue_throwsOnAmbiguousKey() {
    cake.publish("foo", "value");
    cake.publish("bar", "value");
    cake.getPublishedKeyForValue("value", false);
  }

  @Test
  public void testGetPublishedKeyForValue_notFullyQualified() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "value");
        });

    assertEquals("key", cake.getPublishedKeyForValue("value", false));
  }

  @Test
  public void testGetPublishedKeyForValue_fullyQualified() {
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "value");
        });

    assertEquals(Cake.key("a", "key"), cake.getPublishedKeyForValue("value", true));
  }

  @Test
  public void testGetPublishedKeyForValue_getsCorrectKey_twoObjectsOfDifferentClasses() {
    cake.publish("foo", "5");
    cake.publish("bar", 5);

    assertEquals("foo", cake.getPublishedKeyForValue("5", false));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetContext_throwsInRootNamespace() {
    cake.getContext();
  }

  @Test(expected = RuntimeException.class)
  public void testGetContext_throwsOnNoValueForNamespaceKey() {
    cake.inNamespace(
        "foo",
        () -> {
          cake.getContext();
        });
  }

  @Test
  public void testGetContext_getsValueForNamespaceKey() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          assertEquals("bar", cake.getContext());
        });
  }

  @Test
  public void testGetContext_getsValueForNamespaceKey_twoLevelsDeep() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          cake.publish("json", "bearded");
          cake.inNamespace(
              "json",
              () -> {
                assertEquals("bearded", cake.getContext());
              });
        });
  }

  @Test
  public void testHasContext_returnsFalseAtRootNamespace() {
    assertFalse(cake.hasContext());
  }

  @Test
  public void testHasContext_returnsFalseWhenNoValueForNamespaceKey() {
    cake.inNamespace(
        "foo",
        () -> {
          assertFalse(cake.hasContext());
        });
  }

  @Test
  public void testHasContext_returnsTrueWhenHasValueForNamespaceKey() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          assertTrue(cake.hasContext());
        });
  }

  @Test
  public void testGetOrGetContext_emptyInputGetsContext() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          assertEquals("bar", cake.getOrGetContext());
        });
  }

  @Test
  public void testGetOrGetContext_nullInputGetsContext() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          assertEquals("bar", cake.getOrGetContext(null));
        });
  }

  @Test
  public void testGetOrGetContext_prioritizesGetOverGetContext() {
    cake.publish("foo", "bar");
    cake.inNamespace(
        "foo",
        () -> {
          cake.publish("moo", "cow");
          assertEquals("cow", cake.getOrGetContext("moo"));
        });
  }

  @Test(expected = RuntimeException.class)
  public void testGetOrGetContext_throwsOnNonExistentKeyNoContext() {
    cake.getOrGetContext("foo");
  }

  @Test
  public void testGetOrGetContext_searchesUpNamespaceHierarchyForKey() {
    cake.publish("a", "foo");
    cake.inNamespace(
        "a",
        () -> {
          cake.publish("key", "value"); // a.key
          cake.inNamespace(
              "b",
              () -> {
                assertEquals("value", cake.getOrGetContext("key"));
              });
        });
  }
}
