package lol.cicco.ioc.core.scanner;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

@Slf4j
public class JarClassScanner implements BeanScanner {

    private JarClassScanner(){}

    private static final JarClassScanner scanner = new JarClassScanner();

    public static JarClassScanner getInstance(){
        return scanner;
    }

    @Override
    public List<ResourceMeta> doScan(URL url, String suffix) throws IOException {
        List<ResourceMeta> allResources = new LinkedList<>();
        String[] jarInfo = url.getFile().split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                // 扫描Class文件
                if (entryName.endsWith(suffix)) {
                    if (entryName.startsWith(packagePath)) {
                        // 将对应文件路径替换为class全称
                        entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                        allResources.add(ResourceMeta.of(entryName));
                    }
                }
            }
        } catch (ZipException e) {
            log.warn("Skipping invalid jar classpath entry [{}]", url);
        }
        return allResources;
    }
}
