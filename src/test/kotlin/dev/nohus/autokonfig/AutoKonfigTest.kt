package dev.nohus.autokonfig

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.util.*
import kotlin.reflect.jvm.isAccessible

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfigTest {

    private val file = File("test.conf")

    private fun String.createConfigFile() {
        file.writeText(this)
        resetDefaultAutoKonfig()
    }

    private fun String.createAutoKonfig(): AutoKonfig {
        createConfigFile()
        return AutoKonfig().withConfig(file)
    }

    private fun resetDefaultAutoKonfig() {
        DefaultAutoKonfig
            .clear()
            .withSystemProperties()
            .withEnvironmentVariables()
            .withConfigs(ConfigFileLocator().getConfigFiles())
    }

    @AfterEach
    fun tearDown() {
        file.delete()
    }

    @Test
    fun `setting can be read`() {
        val config = "setting = test".createAutoKonfig()
        val setting by config.StringSetting()
        assertEquals("test", setting)
    }

    @Test
    fun `multiple settings can be read`() {
        val config = """
            foo = abc
            bar = def
            baz = ghi
        """.trimIndent().createAutoKonfig()
        val foo by config.StringSetting()
        val baz by config.StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    @Test
    fun `multiple settings can be read from default config`() {
        """
            foo = abc
            bar = def
            baz = ghi
        """.trimIndent().createConfigFile()
        val foo by StringSetting()
        val baz by StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    @Test
    fun `keys are case-insensitive`() {
        """
            FOO = abc
            bar = DEF
        """.trimIndent().createConfigFile()
        val foo by StringSetting()
        val bar by StringSetting()
        val a by StringSetting(name = "foo")
        val b by StringSetting(name = "Bar")
        val c by StringSetting(name = "fOo")
        assertEquals("abc", foo)
        assertEquals("DEF", bar)
        assertEquals("abc", a)
        assertEquals("DEF", b)
        assertEquals("abc", c)
    }

    @Test
    fun `keys with different casing types are matched`() {
        """
            foo-bar = 5
            TEST_DATA = 4
        """.trimIndent().createConfigFile()
        val a by IntSetting(name = "foo-bar")
        val fooBar by IntSetting()
        val b by IntSetting(name = "TEST_DATA")
        val test_data by IntSetting()
        val testData by IntSetting()
        assertEquals(5, a)
        assertEquals(5, fooBar)
        assertEquals(4, b)
        assertEquals(4, test_data)
        assertEquals(4, testData)
    }

    @Test
    fun `keys can have custom names`() {
        """
            foo = abc
        """.trimIndent().createConfigFile()
        val bar by StringSetting(name = "foo")
        assertEquals("abc", bar)
    }

    @Test
    fun `multiple variables be delegated to the same setting`() {
        """
            foo = abc
        """.trimIndent().createConfigFile()
        val foo by StringSetting()
        val bar by StringSetting(name = "foo")
        assertEquals("abc", foo)
        assertEquals("abc", bar)
    }

    @Test
    fun `setting can be read from system properties`() {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    @Test
    fun `setting can be read from environment variables`() {
        val key = "unit.test.environment.variable"
        val value = "value"
        setEnvironmentVariable(key, value)
        resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    private fun setEnvironmentVariable(key: String, value: String) {
        val environment = System.getenv()
        val field = environment::class.members.first { it.name == "m" }
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.call(environment) as MutableMap<String, String>)[key] = value
    }

    @Test
    fun `setting can be read from config file in resources`() {
        val config = AutoKonfig().withResourceConfig("resource.properties")
        val setting by config.StringSetting()
        assertEquals("resource", setting)
    }

    @Test
    fun `setting can be read from command line arguments`() {
        val config = AutoKonfig().withCommandLineArguments(arrayOf("-a", "b", "-c"))
        val a by config.StringSetting()
        val c by config.BooleanSetting()
        assertEquals("b", a)
        assertTrue(c)
    }

    @Test
    fun `boolean settings can be read`() {
        """
            a = true
            b = yes
            c = 1
            d = false
            e = no
            f = 0
        """.trimIndent().createConfigFile()
        val a by BooleanSetting()
        val b by BooleanSetting()
        val c by BooleanSetting()
        val d by BooleanSetting()
        val e by BooleanSetting()
        val f by BooleanSetting()
        assertTrue(a)
        assertTrue(b)
        assertTrue(c)
        assertFalse(d)
        assertFalse(e)
        assertFalse(f)
    }

    @Test
    fun `flag settings are false by default`() {
        """
            a = true
        """.trimIndent().createConfigFile()
        val a by FlagSetting()
        val b by FlagSetting()
        assertTrue(a)
        assertFalse(b)
    }

    @Test
    fun `settings of all types can be read`() {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            boolean = false
            flag = true
        """.trimIndent().createConfigFile()
        val string by StringSetting()
        val int by IntSetting()
        val long by LongSetting()
        val float by FloatSetting()
        val double by DoubleSetting()
        val boolean by BooleanSetting()
        val flag by FlagSetting()
        assertEquals("hello", string)
        assertEquals(10, int)
        assertEquals(3000000000, long)
        assertEquals(3.14f, float)
        assertEquals(3.1415, double)
        assertFalse(boolean)
        assertTrue(flag)
    }

    object TypesGroup : Group() {
        val string by StringSetting()
        val int by IntSetting()
        val long by LongSetting()
        val float by FloatSetting()
        val double by DoubleSetting()
        val boolean by BooleanSetting()
        val flag by FlagSetting()
    }

    @Test
    fun `settings of all types can be read in a group`() {
        """
            typesGroup.string = hello
            typesGroup.int = 10
            typesGroup.long = 3000000000
            typesGroup.float = 3.14
            typesGroup.double = 3.1415
            typesGroup.boolean = false
            typesGroup.flag = true
        """.trimIndent().createConfigFile()
        assertEquals("hello", TypesGroup.string)
        assertEquals(10, TypesGroup.int)
        assertEquals(3000000000, TypesGroup.long)
        assertEquals(3.14f, TypesGroup.float)
        assertEquals(3.1415, TypesGroup.double)
        assertFalse(TypesGroup.boolean)
        assertTrue(TypesGroup.flag)
    }

    @Test
    fun `settings of all types can be read directly`() {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            boolean = false
            flag = true
        """.trimIndent().createConfigFile()
        assertEquals("hello", AutoKonfig.getString("string"))
        assertEquals(10, AutoKonfig.getInt("int"))
        assertEquals(3000000000, AutoKonfig.getLong("long"))
        assertEquals(3.14f, AutoKonfig.getFloat("float"))
        assertEquals(3.1415, AutoKonfig.getDouble("double"))
        assertFalse(AutoKonfig.getBoolean("boolean"))
        assertTrue(AutoKonfig.getFlag("flag"))
    }

    @Test
    fun `nonexistent setting throws an exception`() {
        "".createConfigFile()
        val exception = assertThrows<AutoKonfigException> {
            val nonexistent by StringSetting()
        }
        assertEquals("Required key \"nonexistent\" is missing", exception.message)
    }

    @Test
    fun `nonexistent config file throws an exception`() {
        val file = File("nonexistent")
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withConfig(file)
        }
        assertEquals("Failed to read file: ${file.normalize().absolutePath}", exception.message)
    }

    @Test
    fun `nonexistent resources config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withResourceConfig("nonexistent")
        }
        assertEquals("Failed to read resource: nonexistent", exception.message)
    }

    @Test
    fun `settings can be read from multiple files`() {
        val config = AutoKonfig().withConfigs(
            File("src/test/resources/test/multiple/5.properties"),
            File("src/test/resources/test/multiple/6.conf")
        )
        val foo by config.StringSetting()
        val bar by config.StringSetting()
        assertEquals("abc", foo)
        assertEquals("def", bar)
    }

    object groupA : Group() {
        object subgroup : Group() {
            val setting by StringSetting("")
        }
    }

    @Test
    fun `setting can be read in a group`() {
        """
            groupA.subgroup.setting = test
        """.trimIndent().createConfigFile()
        assertEquals("test", groupA.subgroup.setting)
    }

    object groupB : Group("outer") {
        object subgroup : Group("inner") {
            val setting by StringSetting(name = "key")
        }
    }

    @Test
    fun `setting can be read in a group with custom names`() {
        """
            outer.inner.key = test
        """.trimIndent().createConfigFile()
        assertEquals("test", groupB.subgroup.setting)
    }

    object groupC : Group("outer") {
        object subgroup : Group() {
            val setting by StringSetting()
        }
    }

    @Test
    fun `setting can be read in a group with some custom names`() {
        """
            outer.subgroup.setting = test
        """.trimIndent().createConfigFile()
        assertEquals("test", groupC.subgroup.setting)
    }

    @Test
    fun `wrong type of setting for value throws an exception`() {
        """
            foo = test
        """.trimIndent().createConfigFile()
        val exception = assertThrows<AutoKonfigException> {
            val a by IntSetting(name = "foo")
        }
        assertEquals("Failed to parse setting \"foo\", the value is: test", exception.message)
    }

    @Test
    fun `getAll returns all settings`() {
        """
            a = 1
            b = 2
        """.trimIndent().createConfigFile()
        AutoKonfig.clear().withConfig(file)
        assertEquals(mapOf(
            "a" to "1",
            "b" to "2"
        ), AutoKonfig.getAll())
    }

    @Test
    fun `setting can be traced to a file`() {
        """
            foo = 2
        """.trimIndent().createConfigFile()
        AutoKonfig.clear().withConfig(file)
        assertEquals("Key \"foo\" was read from config file at \"${file.normalize().absolutePath}\"", AutoKonfig.getKeySource("foo"))
    }

    @Test
    fun `setting with a fuzzy matched key can be traced to a file`() {
        """
            SERVER_PORT = 2
        """.trimIndent().createConfigFile()
        AutoKonfig.clear().withConfig(file)
        assertEquals("Key \"serverPort\" was read as \"SERVER_PORT\" from config file at \"${file.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("serverPort"))
    }

    @Test
    fun `setting can be traced to a resource file`() {
        val config = AutoKonfig.clear().withResourceConfig("resource.properties")
        assertEquals("Key \"setting\" was read from config file resource at \"resource.properties\"", config.getKeySource("setting"))
    }

    @Test
    fun `setting can be traced to environment variables`() {
        val key = "unit.test.environment.variable"
        val value = "value"
        setEnvironmentVariable(key, value)
        resetDefaultAutoKonfig()
        assertEquals("Key \"unit.test.environment.variable\" was read from environment variables", AutoKonfig.getKeySource(key))
    }

    @Test
    fun `setting can be traced to system properties`() {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        resetDefaultAutoKonfig()
        assertEquals("Key \"unit.test.system.property\" was read from system properties", AutoKonfig.getKeySource(key))
    }

    @Test
    fun `setting can be traced to command line arguments`() {
        AutoKonfig.clear().withCommandLineArguments(arrayOf("-a", "b"))
        assertEquals("Key \"a\" was read from command line parameters", AutoKonfig.getKeySource("a"))
    }

    @Test
    fun `setting can be traced to manually inserted properties`() {
        AutoKonfig.clear().withProperties(Properties().apply { put("a", "b") })
        assertEquals("Key \"a\" was read from manually inserted properties", AutoKonfig.getKeySource("a"))
    }

    @Test
    fun `setting can be traced to manually inserted map`() {
        AutoKonfig.clear().withMap(mapOf("a" to "b"))
        assertEquals("Key \"a\" was read from manually inserted map", AutoKonfig.getKeySource("a"))
    }
}
