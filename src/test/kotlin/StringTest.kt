import org.junit.Test
import kotlin.test.assertEquals

class StringTest {
    @Test fun classNameCheck() {
        val name = "public class    Srinjoy {".run {
            (indexOf("class") + 5).let {
                substring(it, indexOf("{", it))
            }.trim()
        }
        assertEquals(name, "Srinjoy", message = "$name is not equal to Srinjoy")
    }
}