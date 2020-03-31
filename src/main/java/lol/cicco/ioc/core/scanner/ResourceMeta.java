package lol.cicco.ioc.core.scanner;

import lombok.Data;

@Data
public class ResourceMeta {
    /**
     * 文件名称
     */
    private String fileName;

    public static ResourceMeta of(String fileName) {
        ResourceMeta meta = new ResourceMeta();
        meta.fileName = fileName;
        return meta;
    }
}
