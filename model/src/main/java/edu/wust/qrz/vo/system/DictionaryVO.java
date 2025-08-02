package edu.wust.qrz.vo.system;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryVO {
    private Long id;
    private String name;
    private String code;
    private List<DictionaryItemValueVO> itemValues;
}
