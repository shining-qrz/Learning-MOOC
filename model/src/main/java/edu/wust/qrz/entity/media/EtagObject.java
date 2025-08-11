package edu.wust.qrz.entity.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EtagObject {
    private Integer partNumber; // 分片索引
    private String eTag; // 分片的ETag
}
