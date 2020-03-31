package lol.cicco.ioc.core.scanner;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class FileClassScanner implements BeanScanner {

    private static final FileClassScanner scanner = new FileClassScanner();

    private FileClassScanner(){}

    @Override
    public List<ResourceMeta> doScan(URL url, String suffix) {
        return addFileFromPath(url.getPath(), suffix);
    }

    public static BeanScanner getInstance() {
        return scanner;
    }

    private List<ResourceMeta> addFileFromPath(String path, String suffix) {
        List<ResourceMeta> resourceMetas = new LinkedList<>();

        File file = new File(path);
        File[] childFiles = file.listFiles();
        if (childFiles == null) {
            return resourceMetas;
        }
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                // 搜索子目录
                resourceMetas.addAll(addFileFromPath(childFile.getPath(), suffix));
            } else {
                String childFilePath = childFile.getPath();

                if (childFilePath.endsWith(suffix)) {
                    int classesStartIdx = 0;

                    String classDir = File.separator + "classes" + File.separator;
                    String testClassDir = File.separator + "test-classes" + File.separator;

                    int classesIdx = childFilePath.indexOf(classDir);
                    if (classesIdx != -1) {
                        classesStartIdx = classesIdx + classDir.length();
                    } else {
                        int testClassesIdx = childFilePath.indexOf(testClassDir);
                        if (testClassesIdx != -1) {
                            classesStartIdx = testClassesIdx + testClassDir.length();
                        }
                    }

                    childFilePath = childFilePath.substring(classesStartIdx).replace(File.separator, ".");
                    resourceMetas.add(ResourceMeta.of(childFilePath));
                }
            }
        }
        return resourceMetas;
    }
}
