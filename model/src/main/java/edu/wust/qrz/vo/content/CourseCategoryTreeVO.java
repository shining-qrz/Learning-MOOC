package edu.wust.qrz.vo.content;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class CourseCategoryTreeVO {
    private List<CourseCategoryTreeVO> children; // 子节点列表
    private String id; // 分类ID
    private Integer isLeaf; // 是否叶子节点
    private Integer isShow; // 是否显示
    private String label; // 分类标签
    private String name; // 分类名称
    private Integer orderby; // 排序字段
    private String parentid; // 父节点ID

    public CourseCategoryTreeVO(String id, String name, String parentid){
        this.children = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.parentid = parentid;
    }
}
