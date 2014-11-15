package my;

import my.beans.impl.SimpleBean;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author kkulagin
 * @since 09.11.2014
 *
 * On an i7-4770 with 8 GB & SSD running ubuntu 12.04 prints
 *
 * Took 24.3 second to write 30,000,000 entries
 */

public class SimpleTest {

    public static void main(String[] args) throws IOException {
        option2();
    }

    private static void option2() throws IOException {
        Path file = getFile();
        Files.deleteIfExists(file);
        int entries = 30_000_000;
        try (ChronicleMap<Long, SimpleBean> map = ChronicleMapBuilder
                .of(Long.class, SimpleBean.class)
                .valueSize(8 + 8 + 8 + 4)
                .entries(entries)
                .createPersistedTo(file.toFile())) {
            long start = System.currentTimeMillis();
            SimpleBean value = new SimpleBean();
            for (long i = 0; i < entries; i += 2000000) {
                System.out.printf("put %d keys%n", i);
                for (long j = i; j < i + 2000000; j++) {
                    value.setId(j);
                    map.put(j, value);
                }
            }
            long time = System.currentTimeMillis() - start;

            for (long i = 0; i < entries; i = i + 1000000) {
                System.out.println(map.get(i));
            }
            System.out.println(map.get(entries - 1L));
            System.out.printf("Took %.1f second to write %,d entries%n", time / 1e3, map.longSize());
        }
    }

    private static Path getFile() {
        String tempDir = System.getProperty("java.io.tmpdir");

        return Paths.get(tempDir, "SimpleTest");
    }

    private static void option1() throws IOException {
//        ChronicleMap<String, ISimpleBean> chm = OffHeapUpdatableChronicleMapBuilder
//                        .of(String.class, ISimpleBean.class)
//                        .keySize(10)
//                        .create();

        Path file = getFile();
        ChronicleMapBuilder builder = ChronicleMapBuilder.of(Long.class, SimpleBean.class).
                entries(1_000_000_000).entrySize(50);

        ChronicleMap<Long, SimpleBean> map = builder.createPersistedTo(file.toFile());

        long count = 2;
        for (long i = 0; i < count; i++) {
            map.put(i, new SimpleBean(i));
        }
        System.out.println(map.get(count - 1));
        System.out.println(map.longSize());
    }


}
