package tf;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {

    @Test
    public void testShortText() throws IOException, URISyntaxException {
        Application.main(new String[] {"/data/stop_words.txt", "/data/test.in", "test.out"});
        File actualFile = new File("test.out");
        File expectedFile = new File(Application.class.getResource("/data/test.res").toURI());
        assertThat(actualFile).hasSameContentAs(expectedFile);
    }

    @Test
    public void testLongText() throws IOException, URISyntaxException {
        Application.main(new String[] {"/data/stop_words.txt", "/data/pride-and-prejudice.in", "pride-and-prejudice.out"});
        File actualFile = new File("pride-and-prejudice.out");
        File expectedFile = new File(Application.class.getResource("/data/pride-and-prejudice.res").toURI());
        assertThat(actualFile).hasSameContentAs(expectedFile);
    }

}
