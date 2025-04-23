package org.opendatamesh.platform.pp.notification.server.services.utils.rest.http;

public enum MediaType {
    APPLICATION_JSON("application", "json"),
    APPLICATION_XML("application", "xml"),
    APPLICATION_YAML("application", "yaml"),
    APPLICATION_FORM_URLENCODED("application", "x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application", "octet-stream"),
    TEXT_PLAIN("text", "plain"),
    TEXT_HTML("text", "html"),
    TEXT_CSS("text", "css"),
    TEXT_CSV("text", "csv"),
    IMAGE_PNG("image", "png"),
    IMAGE_JPEG("image", "jpeg"),
    IMAGE_GIF("image", "gif"),
    IMAGE_SVG("image", "svg+xml"),
    AUDIO_MPEG("audio", "mpeg"),
    AUDIO_OGG("audio", "ogg"),
    VIDEO_MP4("video", "mp4"),
    VIDEO_WEBM("video", "webm"),
    MULTIPART_FORM_DATA("multipart", "form-data");

    private final String type;
    private final String subtype;

    MediaType(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getFullType() {
        return type + "/" + subtype;
    }
}
