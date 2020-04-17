package lol.cicco.ioc.core.scanner;

import lombok.Data;

import java.net.URI;

@Data
public class ResourceMeta {
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private URI uri;


    public static ResourceMeta of(String fileName, URI uri) {
        ResourceMeta meta = new ResourceMeta();
        meta.fileName = fileName;
        meta.uri = uri;
        return meta;
    }
}
